package com.bonss.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonss.system.domain.ConsumableType;
import com.bonss.system.domain.ProductModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ModelMapper extends BaseMapper<ProductModel> {

    List<ConsumableType> selectConsumables(@Param("modelId") Long modelId);


}
