package com.bonss.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonss.system.domain.ModelConsumableType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ModelConsumableTypeMapper extends BaseMapper<ModelConsumableType> {
    Integer batchInsert(@Param("id") Long id, @Param("consumableIds") List<Long> consumbleIds);
}
