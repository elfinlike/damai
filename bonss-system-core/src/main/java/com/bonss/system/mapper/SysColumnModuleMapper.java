package com.bonss.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonss.system.domain.SysColumnModule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysColumnModuleMapper extends BaseMapper<SysColumnModule> {

    int deleteBatchIdsNoLogic(@Param("columnIds") List<Long> columnIds);
}
