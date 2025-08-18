package com.bonss.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bonss.system.domain.SysModule;
import com.bonss.system.domain.vo.SysModuleVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IModuleService extends IService<SysModule> {
    List<SysModule> listAll();

    void updateModule(SysModule param, MultipartFile file);

    void deleteModule(Long[] ids);

    void addModule(SysModule param, MultipartFile file);

    List<SysModuleVo> selectAllModule(Long columnId);

    List<SysModule> selectModuleByColumnId(Long columnId);
}
