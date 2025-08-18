package com.bonss.iot.service;

import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.system.domain.Product;

import java.util.List;

public interface IProductService {
    TableDataInfo getList(PageQuery query, Product product);

    void addProduct(Product product);

    void updateProduct(Product product);

    void deleteProduct(List<Long> ids);

    Product getDetail(Long id);
}
