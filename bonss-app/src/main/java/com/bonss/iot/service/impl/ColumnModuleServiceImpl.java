package com.bonss.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonss.iot.mapper.SysColumnModuleMapper;
import com.bonss.iot.service.IColumnModuleService;
import com.bonss.system.domain.SysColumnModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统栏目活动与栏目映像表操作
 *
 * @author bonss
 */
@Service
public class ColumnModuleServiceImpl extends ServiceImpl<SysColumnModuleMapper, SysColumnModule> implements IColumnModuleService {

    @Autowired
    private SysColumnModuleMapper sysColumnModuleMapper;

    @Override
    public List<SysColumnModule> listByModuleId(Long moduleId) {
        LambdaQueryWrapper<SysColumnModule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysColumnModule::getModuleId, moduleId);
        return sysColumnModuleMapper.selectList(queryWrapper);
    }
}
