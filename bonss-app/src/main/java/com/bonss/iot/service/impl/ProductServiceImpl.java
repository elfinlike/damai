package com.bonss.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.exception.ServiceException;
import com.bonss.common.utils.SecurityUtils;
import com.bonss.system.domain.Product;
import com.bonss.system.domain.SysOperLog;
import com.bonss.system.mapper.SysProductMapper;
import com.bonss.iot.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProductServiceImpl implements IProductService {

    @Autowired
    private SysProductMapper sysProductMapper;



    /**
     *  分页条件查询产品列表
     * @param query 分页实体
     * @param product 产品实体
     * @return 返回分页查询结果
     */
    @Override
    public TableDataInfo getList(PageQuery query, Product product) {
        // 开启分页
        Page<Product> page = query.build();
        // 查询数据（调用mapper方法）
        LambdaQueryWrapper<Product> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(product.getProductCode()!=null, Product::getProductCode, product.getProductCode())
                .like(product.getName()!=null, Product::getName, product.getName())
                .eq(product.getShelfStatus()!=null,Product::getShelfStatus,product.getShelfStatus())
                .eq(product.getCategoryId()!=null,Product::getCategoryId,product.getCategoryId())
                .eq(Product::getDelFlag,"0");
        Page<Product> productPage = sysProductMapper.selectPage(page, wrapper);
        SysOperLog sysOperLog=new SysOperLog();
        sysOperLog.setTitle("产品模块");
        return new TableDataInfo(productPage.getRecords(), productPage.getTotal());
    }

    /**
     * 添加产品
     * @param product 产品实体类
     */
    @Override
    public void addProduct(Product product) {
        if (product.getProductCode()==null||product.getName()==null){
            log.error("编码和名称不能重复");
            throw new ServiceException("编码和名称不能为空");
        }
        LambdaQueryWrapper<Product> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Product::getProductCode,product.getProductCode())
                .or().eq(Product::getName,product.getName());
        List<Product> products = sysProductMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(products)){
            log.error("编码和名称不能重复");
            throw new ServiceException("编码和名称不能重复");
        }

        product.setDelFlag("0");
        product.setUpdateBy(SecurityUtils.getUsername());
        product.setCreateBy(SecurityUtils.getUsername());
        int insert = sysProductMapper.insert(product);
        if (insert<=0){
            log.error("添加失败");
            throw new ServiceException("添加失败");
        }
    }


    /**
     * 修改产品
     * @param product 产品
     */
    @Override
    public void updateProduct(Product product) {
        //  对新插入的数据进行唯一性校验
        if (product.getProductCode()!=null || product.getName()!=null){
            LambdaQueryWrapper<Product> wrapper=new LambdaQueryWrapper<>();
            wrapper.eq(product.getProductCode()!=null,Product::getProductCode,product.getProductCode())
                    .or().eq(product.getName()!=null,Product::getName,product.getName());
            List<Product> products = sysProductMapper.selectList(wrapper);
            if (products.size()>1){
                log.error("修改的编码和名称不能重复");
                throw new ServiceException("修改的编码和名称不能重复");
            } else if (products.size()==1&& !Objects.equals(products.get(0).getId(), product.getId())) {
                log.error("修改的编码和名称不能重复");
                throw new ServiceException("修改的编码和名称不能重复");
            }
        }

        product.setUpdateBy(SecurityUtils.getUsername());
        //然后进行插入
        int update = sysProductMapper.updateById(product);
        if (update<=0){
            log.error("修改产品失败");
            throw new ServiceException("修改产品失败");
        }


    }

    /**
     * 逻辑删除产品
     * @param ids 产品id
     */
    @Override
    public void deleteProduct(List<Long> ids) {
        UpdateWrapper<Product> wrapper=new UpdateWrapper<>();
        wrapper.lambda().in(Product::getId,ids)
                .set(Product::getDelFlag,"2");
        int update = sysProductMapper.update(null, wrapper);
        if (update<=0){
            log.error("删除失败");
            throw new ServiceException("删除失败");
        }
    }

    /**
     * 获取产品详情
     * @param id 产品id
     * @return 返回具体结果
     */
    @Override
    public Product getDetail(Long id) {
        Product product=sysProductMapper.selectById(id);
        if (product==null){
            log.error("查询不到对应记录");
            throw new ServiceException("查询失败");
        }
        return product;
    }
}
