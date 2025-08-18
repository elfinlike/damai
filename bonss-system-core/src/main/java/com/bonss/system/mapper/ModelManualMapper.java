package com.bonss.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonss.system.domain.ModelManual;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ModelManualMapper extends BaseMapper<ModelManual> {
    Integer batchInsert(@Param("id") Long id, @Param("manualIds") List<Long> manualIds);
}
