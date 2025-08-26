package com.bonss.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonss.system.domain.SysColumn;

import java.util.List;

/**
 * 栏目相关操作 数据层
 *
 * @author bonss
 */
public interface SysColumnMapper extends BaseMapper<SysColumn> {

    /**
     * 查询栏目列表
     *
     * @param column 栏目查询条件
     * @return 栏目列表
     */
    List<SysColumn> selectColumnList(SysColumn column);
}
