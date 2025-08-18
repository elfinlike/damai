package com.bonss.iot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonss.common.core.domain.entity.SysUser;
import com.bonss.common.enums.*;
import com.bonss.common.exception.ServiceException;
import com.bonss.common.utils.SecurityUtils;
import com.bonss.iot.domain.Family;
import com.bonss.iot.domain.FamilyMember;
import com.bonss.iot.domain.vo.FamilyTableVO;
import com.bonss.iot.domain.vo.FamilyVo;
import com.bonss.iot.eventbus.event.FamilyMessageEvent;
import com.bonss.iot.eventbus.service.EventBusService;
import com.bonss.iot.service.IFamilyService;
import com.bonss.iot.mapper.FamilyMapper;
import com.bonss.iot.mapper.FamilyMemberMapper;
import com.bonss.iot.util.QRCodeUtil;
import com.bonss.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FamilyServiceImpl extends ServiceImpl<FamilyMapper, Family> implements IFamilyService {
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private FamilyMemberMapper familyMemberMapper;
    @Autowired
    private EventBusService eventBusService;

    private final SysUserMapper sysUserMapper;

    public FamilyServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    @Transactional
    public FamilyVo createFamily(String familyName) {
        Long userId = SecurityUtils.getUserId();

        // 检查是否已存在同名家庭由该用户创建
        boolean exists = lambdaQuery()
                .eq(Family::getName, familyName)
                .eq(Family::getCreatorId, userId)
                .exists();

        if (exists) {
            throw new ServiceException("该家庭已存在");
        }
        LambdaQueryWrapper<Family> familyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        familyLambdaQueryWrapper.eq(Family::getCreatorId, userId);
        if (familyMapper.selectOne(familyLambdaQueryWrapper) != null) {
            throw new ServiceException("你已经创建过一个家庭，不能继续创建");
        }

        Family family = new Family();
        family.setName(familyName);
        family.setCreatorId(userId);
        this.save(family);
        // 生成二维码（示例：Base64 内容为家庭ID）
        String joinUrl = String.valueOf(family.getId());
        String qrCodeData = QRCodeUtil.generateBase64QRCode(joinUrl);
        family.setQrCode(qrCodeData);
        // 更新二维码字段
        this.updateById(family);

        FamilyMember familyMember = new FamilyMember();
        familyMember.setFamilyId(family.getId());
        familyMember.setUserId(userId);
        familyMember.setRole(FamilyRole.ADMIN.getCode());
        familyMember.setStatus(FamilyStatus.APPROVED.getCode());
        familyMemberMapper.insert(familyMember);


        FamilyVo familyVo = new FamilyVo();
        familyVo.setFamilyId(family.getId());
        familyVo.setQrCode(qrCodeData);
        return familyVo;
    }

    @Override
    @Transactional
    public boolean dismissFamily(Long familyId) {
        Long userId = SecurityUtils.getAdminUserId();
        //是否是该家庭管理员
        LambdaQueryWrapper<FamilyMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FamilyMember::getFamilyId, familyId);
        queryWrapper.eq(FamilyMember::getUserId, userId);
        FamilyMember familyMember = familyMemberMapper.selectOne(queryWrapper);
        if (familyMember == null || familyMember.getRole() != FamilyRole.ADMIN.getCode()) {
            throw new ServiceException("您不是家庭管理员,无法解散家庭");
        }
        LambdaQueryWrapper<Family> qq = new LambdaQueryWrapper<>();
        qq.eq(Family::getId, familyId);
        Family family = familyMapper.selectOne(qq);
        // 删除家庭
        this.removeById(familyId);

        // 删除该家庭下的所有成员
        LambdaQueryWrapper<FamilyMember> q = new LambdaQueryWrapper<>();
        q.eq(FamilyMember::getFamilyId, familyId);

        List<FamilyMember> members = familyMemberMapper.selectList(q);
        List<Long> userIds = members.stream()
                .map(FamilyMember::getUserId)
                .collect(Collectors.toList());

        familyMemberMapper.delete(q);

        // 使用事件总线推送事件
        String format = String.format("管理员解散了家庭【%s】", family.getName());
        FamilyMessageEvent event = new FamilyMessageEvent(familyId, family.getName(), 400, 402, "解散家庭通知", format, null, userIds, true);
        eventBusService.sendFamilyMessageEvent(event);


        return true;

    }

    @Override
    public List<FamilyTableVO> selectFamilyList() {
        return familyMapper.selectFamilyWithMemberCount().stream().map(family -> {
            FamilyTableVO bean = BeanUtil.toBean(family, FamilyTableVO.class);
            bean.setId(String.valueOf(family.getId()));
            return bean;
        }).collect(Collectors.toList());
    }

    @Override
    public List<FamilyTableVO> selectFamilyListByUserId() {
        Long userId = SecurityUtils.getUserId();
        // 查询当前用户的所有家庭成员
        LambdaQueryWrapper<FamilyMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FamilyMember::getUserId, userId);
        List<FamilyMember> familyMembers = familyMemberMapper.selectList(queryWrapper);

        if (CollectionUtils.isNotEmpty(familyMembers)) {
            // 对每个家庭成员进行映射，查询家庭信息，并设置成员数量
            return familyMembers.stream().map(familyMember -> {
                // 获取家庭信息
                Family family = familyMapper.selectById(familyMember.getFamilyId());
                FamilyTableVO bean = BeanUtil.toBean(family, FamilyTableVO.class);
                bean.setId(String.valueOf(family.getId()));

                // 获取该家庭的成员数量
                Long memberCount = familyMemberMapper.selectCount(
                        new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getFamilyId, family.getId())
                );
                bean.setMemberCount(memberCount); // 设置家庭成员数量

                return bean;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
