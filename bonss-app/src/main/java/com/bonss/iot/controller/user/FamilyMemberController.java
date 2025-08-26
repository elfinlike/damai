package com.bonss.iot.controller.user;

import com.bonss.common.annotation.Log;
import com.bonss.common.core.controller.BaseController;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.common.enums.BusinessType;
import com.bonss.iot.domain.vo.FamilyMemberVo;
import com.bonss.iot.domain.vo.FamilyVo;
import com.bonss.iot.service.IFamilyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家庭成员管理
 */
@RestController
@RequestMapping("/app/family/member")
public class FamilyMemberController extends BaseController {
    @Autowired
    private IFamilyMemberService familyMemberService;

    /**
     * 获取家庭成员列表
     */
    @GetMapping("/list")
    public AjaxResult list(Long familyId) {
        List<FamilyMemberVo> list = familyMemberService.getFamilyMemberList(familyId);
        return AjaxResult.success(list);
    }

    /**
     * 添加家庭成员(管理员)
     */
    @PostMapping("/add")
    public AjaxResult add(Long familyId, String phone) {
        boolean result = familyMemberService.addFamilyMember(familyId, phone);
        return result ? AjaxResult.success() : AjaxResult.error();
    }


    /**
     * 移除家庭成员(管理员)
     */
    @DeleteMapping("/remove")
    public AjaxResult remove(Long userId, Long familyId) {
        boolean result = familyMemberService.removeFamilyMember(userId, familyId);
        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 获取分享家庭
     */
    @GetMapping("/share")
    public AjaxResult share(Long familyId) {
        FamilyVo result = familyMemberService.shareFamily(familyId);
        return AjaxResult.success(result);
    }

    /**
     * 邀请成员加入家庭
     */
    @PostMapping("/invite")
    @Log(title = "邀请成员加入家庭", businessType = BusinessType.INSERT)
    public AjaxResult invite(Long familyId, String phone) {
        boolean result = familyMemberService.inviteFamilyMember(familyId, phone);
        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 退出家庭
     */
    @DeleteMapping("/quit")
    @Log(title = "退出家庭", businessType = BusinessType.DELETE)
    public AjaxResult quit(Long familyId) {
        boolean result = familyMemberService.quitFamily(familyId);
        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 管理员审批成员加入家庭
     */
    @PostMapping("/approve")
    @Log(title = "管理员审批成员加入家庭", businessType = BusinessType.UPDATE)
    public AjaxResult approve(Long familyId, Long userId, Integer status, String reason) {
        boolean result = familyMemberService.approveFamilyMember(familyId, userId, status, reason);
        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 加入家庭
     */
    @PostMapping("/join")
    @Log(title = "加入家庭", businessType = BusinessType.INSERT)
    public AjaxResult join(Long familyId) {
        boolean result = familyMemberService.joinFamily(familyId);
        return result ? AjaxResult.success() : AjaxResult.error();
    }

}
