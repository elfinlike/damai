package com.bonss.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bonss.system.domain.SysColumnModule;

import java.util.List;


public interface IColumnModuleService extends IService<SysColumnModule> {
    List<SysColumnModule> listByModuleId(Long moduleId);
}
