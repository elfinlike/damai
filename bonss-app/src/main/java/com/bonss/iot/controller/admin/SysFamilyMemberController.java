package com.bonss.iot.controller.admin;

import com.bonss.common.annotation.Log;
import com.bonss.common.core.controller.BaseController;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.common.enums.BusinessType;
import com.bonss.iot.domain.vo.FamilyMemberVo;
import com.bonss.iot.service.IFamilyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * 家庭成员管理
 */
@RestController
@RequestMapping("/system/family/member")
public class SysFamilyMemberController  extends BaseController {
    @Autowired
    private IFamilyMemberService familyMemberService;

    /**
     * 获取家庭成员列表
     */
    @GetMapping("/list/{familyId}")
    public AjaxResult list(@PathVariable Long familyId) {
        List<FamilyMemberVo> list = familyMemberService.getFamilyMemberList(familyId);
        return AjaxResult.success(list);
    }

    /**
     * 添加家庭成员(管理员)
     */
    @PostMapping("/add")
    @Log(title = "添加家庭成员(管理员)", businessType = BusinessType.INSERT)
    public AjaxResult add(Long familyId, String phone) {
        boolean result = familyMemberService.addFamilyMember(familyId, phone);
        return result ? AjaxResult.success() : AjaxResult.error();
    }


    /**
     * 移除家庭成员(管理员)
     */
    @DeleteMapping("/remove")
    @Log(title = "移除家庭成员(管理员)", businessType = BusinessType.DELETE)
    public AjaxResult remove(Long userId,Long familyId) {
        boolean result = familyMemberService.removeFamilyMember(userId,familyId);
        return result ? AjaxResult.success() : AjaxResult.error();
    }

}
