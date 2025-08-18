package com.bonss.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonss.system.domain.ModelTutorial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ModelTutorialMapper extends BaseMapper<ModelTutorial> {
    Integer batchInsert(@Param("id") Long id, @Param("tutorialIds") List<Long> tutorialIds);
}
