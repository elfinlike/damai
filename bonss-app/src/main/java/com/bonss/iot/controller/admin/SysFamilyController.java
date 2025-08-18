package com.bonss.iot.controller.admin;

import com.bonss.common.core.controller.BaseController;
import com.bonss.common.core.domain.entity.SysUser;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.iot.domain.Family;
import com.bonss.iot.domain.FamilyMember;
import com.bonss.iot.domain.vo.FamilyTableVO;
import com.bonss.iot.service.IFamilyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 家庭管理
 *
 * @author ct
 */
@RestController
@RequestMapping("/system/family")
public class SysFamilyController extends BaseController {

    private final IFamilyService familyService;

    public SysFamilyController(IFamilyService familyService) {
        this.familyService = familyService;
    }

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list() {
        startPage();
        List<FamilyTableVO> list = familyService.selectFamilyList();
        return getDataTable(list);
    }
}
