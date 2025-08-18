package com.bonss.iot.service;

import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.system.domain.ConsumableType;

import java.util.List;

public interface IConsumableService {
    void bindConnection(Long productId, ConsumableType consumableType);

    TableDataInfo listConsumables(Long productId, PageQuery pageQuery,ConsumableType consumableType);

    void updateConsumables(Long productId, ConsumableType consumableType);

    void deleteConsumables(Long productId, List<Long> ids);
}
