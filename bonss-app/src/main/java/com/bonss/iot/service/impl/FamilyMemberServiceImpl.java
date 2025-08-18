package com.bonss.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonss.common.core.domain.entity.SysUser;
import com.bonss.common.enums.FamilyRole;
import com.bonss.common.enums.FamilyStatus;
import com.bonss.common.exception.ServiceException;
import com.bonss.iot.domain.Family;
import com.bonss.iot.domain.FamilyMember;
import com.bonss.iot.domain.vo.FamilyMemberVo;
import com.bonss.iot.domain.vo.FamilyVo;
import com.bonss.iot.eventbus.event.FamilyMessageEvent;
import com.bonss.iot.eventbus.service.EventBusService;
import com.bonss.iot.mapper.FamilyMapper;
import com.bonss.iot.mapper.FamilyMemberMapper;
import com.bonss.iot.service.IFamilyMemberService;
import com.bonss.iot.util.QRCodeUtil;
import com.bonss.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.bonss.common.utils.SecurityUtils.getLoginUser;

@Service
public class FamilyMemberServiceImpl extends ServiceImpl<FamilyMemberMapper, FamilyMember> implements IFamilyMemberService {
    @Autowired
    private FamilyMemberMapper familyMemberMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private EventBusService eventBusService;

    @Override
    public List<FamilyMemberVo> getFamilyMemberList(Long familyId) {
        List<FamilyMemberVo> result = null;
        List<FamilyMember> familyMembers = familyMemberMapper.selectList(
                new LambdaQueryWrapper<FamilyMember>()
                        .eq(FamilyMember::getFamilyId, familyId)
                        .eq(FamilyMember::getStatus, FamilyStatus.APPROVED.getCode())
        );

        if (CollectionUtils.isEmpty(familyMembers)) {
            return Collections.emptyList();
        }

        result = new ArrayList<>();

        for (FamilyMember member : familyMembers) {
            SysUser user = sysUserMapper.selectUserById(member.getUserId());

            FamilyMemberVo vo = new FamilyMemberVo();
            vo.setUserId(member.getUserId());
            vo.setFamilyRole(FamilyRole.getByCode(member.getRole()));
            vo.setAvatar(user.getAvatar());
            vo.setPhone(user.getPhonenumber());
            vo.setUserName(user.getUserName());
            vo.setNickName(user.getNickName());
            vo.setSex(user.getSex());
            vo.setJoinTime(member.getCreateTime());
            vo.setStatus(member.getStatus());
            result.add(vo);
        }

        return result;
    }

    @Override
    public boolean addFamilyMember(Long familyId, String phone) {
        add(familyId, phone, FamilyStatus.APPROVED.getCode());
        return true;
    }

    private FamilyMember add(Long familyId, String phone, int status) {
        SysUser user = sysUserMapper.selectUserByPhone(phone);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        // 检查用户是否已经加入任何家庭
        LambdaQueryWrapper<FamilyMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FamilyMember::getUserId, user.getUserId());
        FamilyMember familyMember = familyMemberMapper.selectOne(queryWrapper);

        if (familyMember != null) {
            if (familyMember.getFamilyId().equals(familyId)) {
                // 是同一个家庭
                if (familyMember.getStatus() == FamilyStatus.APPROVED.getCode()) {
                    throw new ServiceException("用户已在家庭中");
                }
                if (familyMember.getStatus() == FamilyStatus.PENDING.getCode()) {
                    throw new ServiceException("用户加入请求待审核");
                }
                familyMember.setStatus(status);
                familyMemberMapper.updateById(familyMember);
                return familyMember;
            } else {
                // 是其他家庭
                throw new ServiceException("用户已加入其他家庭");
            }
        }

        // 新建记录
        FamilyMember member = new FamilyMember();
        member.setFamilyId(familyId);
        member.setUserId(user.getUserId());
        member.setStatus(status);
        member.setRole(FamilyRole.COMMON.getCode());
        familyMemberMapper.insert(member);
        return member;
    }


    @Override
    @Transactional
    public boolean removeFamilyMember(Long userId, Long familyId) {
        FamilyMember member = getFamilyMember();
        if (member.getRole() != FamilyRole.ADMIN.getCode()) {
            throw new ServiceException("没有权限");
        }
        LambdaQueryWrapper<FamilyMember> delete = new LambdaQueryWrapper<>();
        delete.eq(FamilyMember::getUserId, userId);
        delete.eq(FamilyMember::getFamilyId, familyId);
        delete.eq(FamilyMember::getStatus, FamilyStatus.APPROVED.getCode());
        FamilyMember user = familyMemberMapper.selectOne(delete);
        if (user == null) {
            throw new ServiceException("该用户不存在");
        }
        if (user.getRole() == FamilyRole.ADMIN.getCode()) {
            throw new ServiceException("不能删除管理员");
        }

        familyMemberMapper.delete(delete);

        //通知成员被移除
        Family family = getFamily(familyId);
        String format = String.format("你已移除【%s】", family.getName());
        FamilyMessageEvent event = new FamilyMessageEvent(familyId, family.getName(), 400, 403, "移除家庭通知", format, userId, null, true);
        eventBusService.sendFamilyMessageEvent(event);
        return true;
    }

    @Override
    public FamilyVo shareFamily(Long familyId) {
        FamilyVo result = new FamilyVo();
        result.setFamilyId(familyId);

        // 生成跳转链接二维码
        String joinUrl = String.valueOf(familyId);
        String qrCodeData = QRCodeUtil.generateBase64QRCode(joinUrl);
        result.setQrCode(qrCodeData);

        return result;
    }


    @Override
    @Transactional
    public boolean inviteFamilyMember(Long familyId, String phone) {
        Family family = getFamily(familyId);
        if (family == null) {
            throw new ServiceException("家庭不存在");
        }
        FamilyMember member = getFamilyMember();
        // 管理员邀请不用审核
        if (member.getRole() == FamilyRole.ADMIN.getCode()) {
            add(familyId, phone, FamilyStatus.APPROVED.getCode());
            return true;
        }
        if (member.getStatus() != FamilyStatus.APPROVED.getCode()) {
            throw new ServiceException("你尚未在该家庭中，无法邀请其他成员");
        }
        FamilyMember add = add(familyId, phone, FamilyStatus.PENDING.getCode());

        // 1. 一次性查询所需用户信息，避免重复访问数据库
        SysUser applicant = sysUserMapper.selectUserById(member.getUserId());
        SysUser invitee = sysUserMapper.selectUserByPhone(phone);

        FamilyMessageEvent event = new FamilyMessageEvent(familyId, family.getName(), 400, 401, "加入家庭申请", String.format("%s 邀请 %s 加入家庭", applicant.getUserName(), invitee.getUserName()), getAdmin(familyId), null, true);
        eventBusService.sendFamilyMessageEvent(event);
        return true;
    }

    @Override
    @Transactional
    public boolean quitFamily(Long familyId) {
        FamilyMember familyMember = getFamilyMember();
        if (familyMember.getRole() == FamilyRole.ADMIN.getCode()) {
            throw new ServiceException("管理员不能退出家庭");
        }
        LambdaQueryWrapper<FamilyMember> delete = new LambdaQueryWrapper<>();
        delete.eq(FamilyMember::getUserId, familyMember.getUserId());
        delete.eq(FamilyMember::getFamilyId, familyId);
        delete.eq(FamilyMember::getStatus, FamilyStatus.APPROVED.getCode());
        int delete1 = familyMemberMapper.delete(delete);
        if (delete1 == 1) {
            String userName = sysUserMapper.selectUserById(familyMember.getUserId()).getUserName();
            String format = String.format("%s退出了家庭【%s】", userName, getFamily(familyId).getName());
            FamilyMessageEvent event = new FamilyMessageEvent(familyId, getFamily(familyId).getName(), 400, 407, "退出家庭通知", format, getAdmin(familyId), null, true);
            eventBusService.sendFamilyMessageEvent(event);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean approveFamilyMember(Long familyId, Long userId, Integer status, String reason) {
        FamilyMember member = getFamilyMember();
        if (member.getRole() != FamilyRole.ADMIN.getCode()) {
            throw new ServiceException("没有权限");
        }
        LambdaQueryWrapper<FamilyMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FamilyMember::getFamilyId, familyId);
        queryWrapper.eq(FamilyMember::getUserId, userId);
        FamilyMember familyMember = familyMemberMapper.selectOne(queryWrapper);
        if (familyMember.getStatus() == FamilyStatus.APPROVED.getCode()) {
            throw new ServiceException("用户已存在");
        }

        familyMember.setStatus(status);
        familyMember.setApprovedTime(new Date());
        familyMemberMapper.updateById(familyMember);

        Long adminId = getAdmin(familyId);
        SysUser admin = sysUserMapper.selectUserById(adminId);
        //通知用户审核结果
        String format = String.format("审核结果为:%s，理由为:%s", status == 1 ? "通过" : "拒绝", reason);
        FamilyMessageEvent event = new FamilyMessageEvent(familyId, getFamily(familyId).getName(), 400, status == 1 ? 405 : 406, "审核结果", format, userId, null, true);
        eventBusService.sendFamilyMessageEvent(event);
        return true;
    }


    @Override
    @Transactional
    public boolean joinFamily(Long familyId) {
        Family family = familyMapper.selectById(familyId);
        if (family == null) {
            throw new ServiceException("家庭不存在");
        }

        SysUser user = sysUserMapper.selectUserById(getLoginUser().getUserId());

        // 判断用户是否已经加入了任何家庭
        LambdaQueryWrapper<FamilyMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FamilyMember::getUserId, user.getUserId());
        FamilyMember existingMember = familyMemberMapper.selectOne(queryWrapper);

        if (existingMember != null) {
            if (existingMember.getFamilyId().equals(familyId)) {
                // 是当前家庭，重复申请
                if (existingMember.getStatus() == FamilyStatus.APPROVED.getCode()) {
                    throw new ServiceException("你已申请加入该家庭或已在该家庭中");
                } else if (existingMember.getStatus() == FamilyStatus.REJECTED.getCode()) {
                    throw new ServiceException("你已被管理员拒绝加入该家庭");
                }
            } else {
                // 已加入其他家庭
                throw new ServiceException("你已加入其他家庭，无法再次申请");
            }
        }

        // 插入新的家庭成员申请
        FamilyMember member = new FamilyMember();
        member.setFamilyId(family.getId());
        member.setUserId(user.getUserId());
        member.setStatus(FamilyStatus.PENDING.getCode());
        member.setRole(FamilyRole.COMMON.getCode());
        member.setRequestTime(new Date());
        int insert = familyMemberMapper.insert(member);

        FamilyMessageEvent event = new FamilyMessageEvent(familyId, family.getName(), 400, 401, "有新成员申请加入家庭", String.format("%s 申请加入家庭 %s", user.getUserName(), family.getName()), getAdmin(family.getId()), null, true);
        eventBusService.sendFamilyMessageEvent(event);
        return insert == 1;
    }

    private Long getAdmin(Long familyId) {
        LambdaQueryWrapper<Family> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Family::getId, familyId);
        Family family = familyMapper.selectOne(queryWrapper);
        return family.getCreatorId();
    }

    private Family getFamily(Long familyId) {
        LambdaQueryWrapper<Family> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Family::getId, familyId);
        return familyMapper.selectOne(queryWrapper);
    }

    private FamilyMember getFamilyMember() {
        Long LoginUserId = getLoginUser().getUserId();
        LambdaQueryWrapper<FamilyMember> q = new LambdaQueryWrapper<>();
        q.eq(FamilyMember::getUserId, LoginUserId);
        FamilyMember member = familyMemberMapper.selectOne(q);
        if (member == null) {
            throw new ServiceException("用户不存在");
        }
        return member;
    }

}
