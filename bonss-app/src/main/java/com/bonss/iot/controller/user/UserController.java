package com.bonss.iot.controller.user;

import com.bonss.common.annotation.Log;
import com.bonss.common.core.controller.BaseController;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.common.core.domain.entity.SysRole;
import com.bonss.common.core.domain.entity.SysUser;
import com.bonss.common.core.domain.model.LoginBody;
import com.bonss.common.core.domain.model.LoginUser;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.enums.BusinessType;
import com.bonss.common.utils.SecurityUtils;
import com.bonss.common.utils.StringUtils;
import com.bonss.common.utils.poi.ExcelUtil;
import com.bonss.common.web.service.AppTokenService;
import com.bonss.common.web.service.RefreshTokenService;
import com.bonss.system.param.ResetPasswordParam;
import com.bonss.system.param.SmsCodeParam;
import com.bonss.system.param.SmsCodeValidateParam;
import com.bonss.system.param.UpdateUserProfileParam;
import com.bonss.system.service.ISysRoleService;
import com.bonss.system.service.ISysUserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户信息
 *
 * @author hzx
 */
@RestController
@RequestMapping("/app/user")
public class UserController extends BaseController {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private AppTokenService tokenService;

    @Autowired
    private RefreshTokenService refreshService;

    /**
     * 发送短信验证码
     */
    @PostMapping("/sms/code")
    public AjaxResult sendSingleSms(@RequestBody @Validated SmsCodeParam param) {
        userService.sendSmsCode(param);
        return AjaxResult.success();
    }

    /**
     * 校验短信验证码
     */
    @PostMapping("/sms/validate")
    public AjaxResult validateSmsCode(@RequestBody @Validated SmsCodeValidateParam param) {
        SysUser sysUser = userService.handRegisterAndLogin(param);
        LoginUser user = new LoginUser();
        user.setUser(sysUser);
        user.setLoginTime(System.currentTimeMillis());
        user.setUserId(sysUser.getUserId());
        // 2) 生成AT + 初始RT
        String accessToken = tokenService.createAccessToken(user);
        String refreshToken = refreshService.createInitial(user);

        return AjaxResult.success()
                .put("accessToken", accessToken)
                .put("refreshToken", refreshToken)
                .put("expiresIn", 60 * tokenServiceExpireMinutes()) // 返回秒
                .put("sessionId", user.getToken());
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public AjaxResult getUserInfo(HttpServletRequest request) {
        // 获取当前登录用户
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }

        // 查询完整的用户信息
        SysUser user = userService.selectUserById(loginUser.getUserId());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("userName", user.getUserName());
        userInfo.put("nickName", user.getNickName());
        userInfo.put("sex", user.getSex());
        userInfo.put("birthday", user.getBirthday());
        userInfo.put("phonenumber", user.getPhonenumber());
        userInfo.put("email", user.getEmail());
        userInfo.put("avatar", user.getAvatar());

        return AjaxResult.success(userInfo);
    }

    /**
     * 修改用户信息
     */
    @PutMapping("/update")
    public AjaxResult updateProfile(@RequestBody @Validated UpdateUserProfileParam param, HttpServletRequest request) {
        // 获取当前登录用户
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        SysUser user = new SysUser();
        user.setUserId(loginUser.getUserId());
        user.setNickName(param.getNickName());
        user.setSex(param.getSex());
        user.setBirthday(param.getBirthday());
        user.setPhonenumber(param.getPhonenumber());
        userService.updateUserProfile(user);
        return AjaxResult.success("用户信息更新成功");
    }


    /**
     * 校验短信验证码
     */
    @PostMapping("/sms/reset")
    public AjaxResult resetPassword(@RequestBody @Validated ResetPasswordParam param) {
        userService.resetPwd(param);
        return AjaxResult.success();
    }


    // 你的登录逻辑里应校验账号密码，并返回 LoginUser
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody body) {
        // 1) 认证：略（从DB查用户 -> 校验密码 -> 组装 LoginUser）
        LoginUser user = doAuthenticate(body.getUsername(), body.getPassword());

        // 2) 生成AT + 初始RT
        String accessToken = tokenService.createAccessToken(user);
        String refreshToken = refreshService.createInitial(user);

        return AjaxResult.success()
                .put("accessToken", accessToken)
                .put("refreshToken", refreshToken)
                .put("expiresIn", 60 * tokenServiceExpireMinutes()) // 返回秒
                .put("sessionId", user.getToken());
    }

    @PostMapping("/refresh")
    public AjaxResult refresh(@RequestParam("refreshToken") String refreshTokenJti,
                              HttpServletRequest request) {
        // 1) 解析当前请求的AT，拿到会话LoginUser（若AT已过期，也可通过前端回传sid来找Redis会话，这里从AT取）
        LoginUser cur = tokenService.getLoginUser(request);
        if (cur == null) {
            return AjaxResult.error("invalid_or_expired_access_token");
        }

        // 2) RT轮换（含复用检测）
        String newRt = refreshService.rotate(cur, refreshTokenJti);

        // 3) 发新AT（并滑动续期Redis会话）
        String newAt = tokenService.createAccessToken(cur);

        return AjaxResult.success()
                .put("accessToken", newAt)
                .put("refreshToken", newRt)
                .put("expiresIn", 60 * tokenServiceExpireMinutes())
                .put("sessionId", cur.getToken());
    }

    @PostMapping("/logout")
    public AjaxResult logout(HttpServletRequest request) {
        LoginUser cur = tokenService.getLoginUser(request);
        if (cur != null) {
            refreshService.revokeSessionFamily(cur.getUserId(), cur.getToken());
            tokenService.deleteSessionBySid(cur.getToken());
        }
        return AjaxResult.success();
    }

    @PostMapping("/logout-all")
    public AjaxResult logoutAll(HttpServletRequest request) {
        LoginUser cur = tokenService.getLoginUser(request);
        if (cur != null) {
            refreshService.revokeAll(cur.getUserId());
            tokenService.deleteSessionBySid(cur.getToken()); // 当前这个也清一下
        }
        return AjaxResult.success();
    }

    // ==== 你自己的实现（示意） ====
    private LoginUser doAuthenticate(String username, String password) {
        // TODO: 从DB获取用户，校验密码，加盐、状态等 -> 组装 LoginUser
        LoginUser u = new LoginUser();
        SysUser sysUser = userService.selectUserById(1L);
        u.setUser(sysUser);
        u.setUserId(sysUser.getUserId());
        return u;
    }

    private int tokenServiceExpireMinutes() {
        return 30;
    } // 与配置一致

    /**
     * 后台用户头像上传
     */
    @PostMapping("/avatar")
    public AjaxResult updateAvatar(@RequestParam("avatarFile") MultipartFile avatarFile) {
        AjaxResult success = AjaxResult.success();
        success.put("fileName", userService.updateAvatar(avatarFile));
        return success;
    }

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        AjaxResult ajax = AjaxResult.success();
        if (StringUtils.isNotNull(userId)) {
            SysUser sysUser = userService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        }
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user)) {
            return error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user)) {
            return error("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        if (ArrayUtils.contains(userIds, getAdminId())) {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId) {
        AjaxResult ajax = AjaxResult.success();
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds) {
        roleService.checkRoleDataScope(roleIds);
        userService.insertUserAuth(userId, roleIds);
        return success();
    }
}
