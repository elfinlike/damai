package com.bonss.iot.service;

import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.system.domain.DTO.ModelDTO;
import com.bonss.system.domain.ProductModel;
import com.bonss.system.domain.vo.ModelVO;

import java.util.List;

public interface IModelService {
    void addModel(ModelDTO modelDTO);

    ModelVO getDetail(Long modelId);

    TableDataInfo listModels(PageQuery pageQuery, ProductModel productModel);

    void updateModel(ModelDTO modelDTO);

    void deleteModels(List<Long> ids);
}
