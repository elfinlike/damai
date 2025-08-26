package com.bonss.system.controller;

import com.bonss.common.annotation.Log;
import com.bonss.common.core.controller.BaseController;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.common.core.domain.entity.SysAdmin;
import com.bonss.common.core.domain.entity.SysRole;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.enums.BusinessType;
import com.bonss.common.utils.SecurityUtils;
import com.bonss.common.utils.StringUtils;
import com.bonss.common.utils.poi.ExcelUtil;
import com.bonss.system.service.ISysAdminService;
import com.bonss.system.service.ISysRoleService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员信息
 *
 * @author hzx
 */
@RestController
@RequestMapping("/system/admin")
public class SysAdminController extends BaseController {

    @Autowired
    private ISysAdminService adminService;

    @Autowired
    private ISysRoleService roleService;

    /**
     * 获取管理员列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysAdmin user) {
        startPage();
        List<SysAdmin> list = adminService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "管理员管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysAdmin user) {
        List<SysAdmin> list = adminService.selectUserList(user);
        ExcelUtil<SysAdmin> util = new ExcelUtil<SysAdmin>(SysAdmin.class);
        util.exportExcel(response, list, "管理员数据");
    }

    @Log(title = "管理员管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysAdmin> util = new ExcelUtil<SysAdmin>(SysAdmin.class);
        List<SysAdmin> userList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = adminService.importUser(userList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<SysAdmin> util = new ExcelUtil<SysAdmin>(SysAdmin.class);
        util.importTemplateExcel(response, "管理员数据");
    }

    /**
     * 根据管理员编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        AjaxResult ajax = AjaxResult.success();
        if (StringUtils.isNotNull(userId)) {
            adminService.checkUserDataScope(userId);
            SysAdmin sysUser = adminService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        }
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", roles);
        return ajax;
    }

    /**
     * 新增管理员
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "管理员管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysAdmin user) {
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!adminService.checkUserNameUnique(user)) {
            return error("新增管理员'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !adminService.checkPhoneUnique(user)) {
            return error("新增管理员'" + user.getUserName() + "'失败，手机号码已存在");
        }
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(adminService.insertUser(user));
    }

    /**
     * 修改管理员
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "管理员管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysAdmin user) {
        adminService.checkUserAllowed(user);
        adminService.checkUserDataScope(user.getAdminId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!adminService.checkUserNameUnique(user)) {
            return error("修改管理员'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !adminService.checkPhoneUnique(user)) {
            return error("修改管理员'" + user.getUserName() + "'失败，手机号码已存在");
        }
        user.setUpdateBy(getUsername());
        return toAjax(adminService.updateUser(user));
    }

    /**
     * 删除管理员
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "管理员管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        if (ArrayUtils.contains(userIds, getAdminId())) {
            return error("当前管理员不能删除");
        }
        return toAjax(adminService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "管理员管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysAdmin user) {
        adminService.checkUserAllowed(user);
        adminService.checkUserDataScope(user.getAdminId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return toAjax(adminService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "管理员管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysAdmin user) {
        adminService.checkUserAllowed(user);
        adminService.checkUserDataScope(user.getAdminId());
        user.setUpdateBy(getUsername());
        return toAjax(adminService.updateUserStatus(user));
    }

    /**
     * 根据管理员编号获取授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId) {
        AjaxResult ajax = AjaxResult.success();
        SysAdmin user = adminService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", roles);
        return ajax;
    }

    /**
     * 管理员授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "管理员管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds) {
        adminService.checkUserDataScope(userId);
        roleService.checkRoleDataScope(roleIds);
        adminService.insertUserAuth(userId, roleIds);
        return success();
    }
}
