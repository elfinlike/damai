package com.bonss.iot.controller.user;

import com.bonss.common.annotation.Log;
import com.bonss.common.core.controller.BaseController;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.enums.BusinessType;
import com.bonss.iot.domain.vo.FamilyTableVO;
import com.bonss.iot.domain.vo.FamilyVo;
import com.bonss.iot.service.IFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家庭管理
 */
@RestController
@RequestMapping("/app/family")
public class FamilyController extends BaseController {

    @Autowired
    private IFamilyService familyService;

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public TableDataInfo list() {
        startPage();
        List<FamilyTableVO> list = familyService.selectFamilyListByUserId();
        return getDataTable(list);
    }

    /**
     * 创建家庭
     */
    @PostMapping("/create")
    @Log(title = "创建家庭", businessType = BusinessType.INSERT)
    public AjaxResult create(String familyName) {
        FamilyVo familyVo = familyService.createFamily(familyName);
        return AjaxResult.success(familyVo);
    }

    /**
     * 解散家庭
     */
    @DeleteMapping("/dismiss")
    @Log(title = "解散家庭", businessType = BusinessType.DELETE)
    public AjaxResult dismiss(Long familyId) {
        boolean result = familyService.dismissFamily(familyId);
        if (!result) {
            return AjaxResult.error("解散失败");
        }
        return AjaxResult.success();
    }

}
