package com.bonss.iot.controller.user;


import com.bonss.common.annotation.Log;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.enums.BusinessType;
import com.bonss.common.enums.OperatorType;
import com.bonss.system.domain.Product;
import com.bonss.iot.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/product")
public class ProductController {

    @Autowired
    private IProductService productService;

    /**
     * 分页查询产品
     */
    @GetMapping("/list")
    public TableDataInfo listProducts(PageQuery query, Product product) {
        return productService.getList(query, product);
    }

    /**
     * 新增产品
     */
    @PostMapping
    @Log(title = "新增产品", businessType = BusinessType.INSERT, operatorType = OperatorType.MANAGE)
    public AjaxResult add(@RequestBody Product product) {
        productService.addProduct(product);
        return AjaxResult.success();
    }

    /**
     * 修改产品
     */
    @PutMapping
    @Log(title = "修改产品", businessType = BusinessType.UPDATE, operatorType = OperatorType.MANAGE)
    public AjaxResult update(@RequestBody Product product) {
        productService.updateProduct(product);
        return AjaxResult.success();
    }

    /**
     * 逻辑删除产品
     */
    @DeleteMapping("/{ids}")
    @Log(title = "删除产品", businessType = BusinessType.DELETE, operatorType = OperatorType.MANAGE)
    public AjaxResult delete(@PathVariable List<Long> ids) {
        productService.deleteProduct(ids);
        return AjaxResult.success();
    }

    /**
     * 查看详情
     */
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable Long id) {

        return AjaxResult.success(productService.getDetail(id));
    }

}
