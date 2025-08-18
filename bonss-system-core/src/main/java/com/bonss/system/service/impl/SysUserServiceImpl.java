package com.bonss.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.jws.WebParam;
import javax.validation.Validator;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.domain.entity.SysUser;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.utils.file.FileUploadUtils;
import com.bonss.system.domain.*;
import com.bonss.system.domain.DTO.DeviceDetailDTO;
import com.bonss.system.domain.vo.DeviceDetailVO;
import com.bonss.system.domain.vo.UserDeviceVO;
import com.bonss.system.mapper.*;
import com.bonss.system.param.ResetPasswordParam;
import com.bonss.system.param.SmsCodeParam;
import com.bonss.system.param.SmsCodeValidateParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.bonss.common.constant.UserConstants;
import com.bonss.common.core.domain.entity.SysRole;
import com.bonss.common.exception.ServiceException;
import com.bonss.common.utils.SecurityUtils;
import com.bonss.common.utils.StringUtils;
import com.bonss.common.utils.bean.BeanValidators;
import com.bonss.common.utils.spring.SpringUtils;
import com.bonss.system.service.ISysConfigService;
import com.bonss.system.service.ISysUserService;
import org.springframework.web.multipart.MultipartFile;

import static com.bonss.common.utils.SecurityUtils.getUserId;

/**
 * 用户 业务层处理
 *
 * @author hzx
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    protected Validator validator;

    @Autowired
    private SmsService smsService;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SysProductMapper sysProductMapper;

    /**
     * 短信验证码
     */
    @Override
    public void sendSmsCode(SmsCodeParam smsCodeParam) {
        smsService.sendCodeSingleSms(smsCodeParam);
    }

    /**
     * 短信验证码校验
     *
     * @param param 验证参数
     */
    @Override
    public SysUser handRegisterAndLogin(SmsCodeValidateParam param) {
        smsService.validateSmsCode(param.getPhoneNumber(), param.getSmsCode(), ServiceException.class);
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhonenumber, param.getPhoneNumber());
        SysUser user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            user = new SysUser();
            user.setPhonenumber(param.getPhoneNumber());
            user.setStatus("0"); // 正常状态
            user.setDelFlag("0"); // 未删除
            user.setCreateBy("system");
            user.setCreateTime(new Date());
            user.setUserName(param.getPhoneNumber());
            user.setNickName(param.getPhoneNumber());
            userMapper.insert(user);
        }
        return user;
    }

    @Override
    public String updateAvatar(MultipartFile avatarFile) {
        String avatar = FileUploadUtils.upload(avatarFile);
        updateAvatar(avatar);
        return avatar;
    }


    private void updateAvatar(String avatar) {
        Long userId = SecurityUtils.getUserId();
        SysUser userFromDB = userMapper.selectUserById(userId);
        if (StrUtil.isNotBlank(userFromDB.getAvatar())) {
            FileUploadUtils.deleteFile(userFromDB.getAvatar());
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setAvatar(avatar);
        userMapper.updateUser(sysUser);
    }


    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return userMapper.selectUserList(user);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectAllocatedList(SysUser user) {
        return userMapper.selectAllocatedList(user);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUnallocatedList(SysUser user) {
        return userMapper.selectUnallocatedList(user);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName) {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkUserNameUnique(user.getUserName());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public boolean checkPhoneUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public boolean checkEmailUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertUser(SysUser user) {
        // 新增用户信息
        int rows = userMapper.insertUser(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean registerUser(SysUser user) {
        return userMapper.insertUser(user) > 0;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUser(SysUser user) {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPostByUserId(userId);
        return userMapper.updateUser(user);
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds) {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户头像
     *
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(Long userId, String avatar) {
        return userMapper.updateUserAvatar(userId, avatar) > 0;
    }

    /**
     * 重置用户密码（通过手机号）
     */
    @Override
    public int resetPwd(ResetPasswordParam param) {
        if (!param.getNewPassword().equals(param.getConfirmNewPassword())) {
            throw new ServiceException("新密码与确认密码不一致");
        }

        // 验证短信验证码
        smsService.validateSmsCode(param.getPhoneNumber(), param.getSmsCode(), ServiceException.class);

        // 根据手机号查找用户
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhonenumber, param.getPhoneNumber());
        SysUser user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new ServiceException("未找到该手机号对应的用户");
        }

        // 更新密码
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getUserId, user.getUserId());
        updateWrapper.set(SysUser::getPassword, SecurityUtils.encryptPassword(param.getNewPassword()));

        return userMapper.update(null, updateWrapper);
    }


    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(Long userId, String password) {
        return userMapper.resetUserPwd(userId, password);
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        this.insertUserRole(user.getUserId(), user.getRoleIds());
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds) {
        if (StringUtils.isNotEmpty(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>(roleIds.length);
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleMapper.batchUserRole(list);
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserById(Long userId) {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        userPostMapper.deleteUserPostByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(new SysUser(userId));
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPost(userIds);
        return userMapper.deleteUserByIds(userIds);
    }

    /**
     * 导入用户数据
     *
     * @param userList        用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(userList) || userList.size() == 0) {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (SysUser user : userList) {
            try {
                // 验证是否存在这个用户
                SysUser u = userMapper.selectUserByUserName(user.getUserName());
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, user);
                    String password = configService.selectConfigByKey("sys.user.initPassword");
                    user.setPassword(SecurityUtils.encryptPassword(password));
                    user.setCreateBy(operName);
                    userMapper.insertUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
                } else if (isUpdateSupport) {
                    BeanValidators.validateWithException(validator, user);
                    checkUserAllowed(u);
                    user.setUserId(u.getUserId());
                    user.setUpdateBy(operName);
                    userMapper.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、账号 " + user.getUserName() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /**
     * 查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public TableDataInfo getUserDevices(Long userId, PageQuery pageQuery, DeviceDetailDTO deviceDetailDTO) {
        // 获取用户信息
        SysUser sysUser = userMapper.selectUserById(userId);
        if (sysUser == null) {
            log.error("用户不存在");
            throw new ServiceException("用户不存在");
        }

        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(deviceDetailDTO.getDeviceName() != null, Device::getName, deviceDetailDTO.getDeviceName())
                .eq(deviceDetailDTO.getDeviceId() != null, Device::getId, deviceDetailDTO.getDeviceId())
                .eq(Device::getUserId, userId)
                .eq(Device::getDelFlag, "0");
        Page<Device> devicePages = deviceMapper.selectPage(pageQuery.build(), wrapper);
        if (CollectionUtils.isEmpty(devicePages.getRecords())) {
            return new TableDataInfo();
        }
        List<DeviceDetailVO> collect = devicePages.getRecords().stream().map(record -> {
            DeviceDetailVO deviceDetailVO = new DeviceDetailVO();
            deviceDetailVO.setDeviceId(record.getId());
            deviceDetailVO.setDeviceName(record.getName());
            deviceDetailVO.setBindTime(record.getBindTime());
            deviceDetailVO.setLastOnlineTime(record.getLastOnlineTime());
            deviceDetailVO.setOnlineStatus(record.getOnlineStatus());
            deviceDetailVO.setIpAddress(record.getIpAddress());

            ProductModel productModel = modelMapper.selectById(record.getModelId());
            deviceDetailVO.setModelCode(productModel.getModelCode());
            deviceDetailVO.setModelName(productModel.getName());

            Product product = sysProductMapper.selectById(productModel.getProductId());
            deviceDetailVO.setProductName(product.getName());
            deviceDetailVO.setProductCode(product.getProductCode());

            return deviceDetailVO;
        }).collect(Collectors.toList());
        return new TableDataInfo(collect, devicePages.getTotal());
    }
}
