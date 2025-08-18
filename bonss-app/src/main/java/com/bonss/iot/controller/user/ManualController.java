package com.bonss.iot.controller.user;


import com.bonss.common.annotation.Log;
import com.bonss.common.config.BonssConfig;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.enums.BusinessType;
import com.bonss.common.enums.OperatorType;
import com.bonss.system.config.ServerConfig;
import com.bonss.system.domain.Manual;
import com.bonss.iot.service.IManualService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/system/product/{productId}")
@Slf4j
public class ManualController {

    @Autowired
    private IManualService manualService;

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 上传文件
     *
     * @param productId      产品id
     * @param multipartFiles 上传文件说明书
     * @return 返回上传结果
     */
    @PostMapping("/manual/upload")
    @Log(title = "上传产品说明书", businessType = BusinessType.INSERT, operatorType = OperatorType.MANAGE)
    public AjaxResult uploadManual(@PathVariable Integer productId,
                                   @RequestParam("files") List<MultipartFile> multipartFiles) {
        String prefix = serverConfig.getUrl();
        manualService.addManuals(productId, multipartFiles, prefix);
        return AjaxResult.success();
    }

    /**
     * 删除说明书
     *
     * @param ids 说明书id集合
     * @return 返回删除结果
     */
    @DeleteMapping("/{ids}")
    @Log(title = "删除产品说明书", businessType = BusinessType.DELETE, operatorType = OperatorType.MANAGE)
    public AjaxResult deleteManuals(@PathVariable Integer productId,
                                    @PathVariable List<Long> ids) {
        String profileName = BonssConfig.getProfile();
        manualService.deleteManuals(productId, ids, profileName);
        return AjaxResult.success();
    }


    /**
     * 分页查询说明书
     *
     * @param productId 产品Id
     * @param query     分页参数
     * @return 返回分页数据
     */
    @GetMapping("/manuals")
    public TableDataInfo listManuals(@PathVariable Integer productId, PageQuery query, Manual manual) {
        return manualService.listPage(productId, query, manual);
    }
}
