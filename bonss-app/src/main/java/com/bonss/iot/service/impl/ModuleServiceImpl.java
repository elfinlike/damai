package com.bonss.iot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonss.common.exception.ServiceException;
import com.bonss.common.utils.file.ImageUtils;
import com.bonss.iot.mapper.SysColumnModuleMapper;
import com.bonss.iot.mapper.SysModuleMapper;
import com.bonss.iot.service.IModuleService;
import com.bonss.system.domain.SysColumnModule;
import com.bonss.system.domain.SysModule;
import com.bonss.system.domain.vo.SysModuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModuleServiceImpl extends ServiceImpl<SysModuleMapper, SysModule> implements IModuleService {

    @Autowired
    private SysModuleMapper sysModuleMapper;

    @Autowired
    private SysColumnModuleMapper sysColumnModuleMapper;

    @Autowired
    private ImageUtils imageUtils;

    @Override
    public List<SysModule> listAll() {
        LambdaQueryWrapper<SysModule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(SysModule::getSort, SysModule::getCreateTime);
        return sysModuleMapper.selectList(queryWrapper);
    }

    @Override
    public void updateModule(SysModule param, MultipartFile file) {
        SysModule sysModule = sysModuleMapper.selectById(param.getModuleId());
        if (file != null) {
//            FileUploadUtils.deleteFile(sysModule.getUrl());
//            String upload = imageUtils.transformWebpUpload(file);
//            param.setUrl(upload);
        }
        LambdaQueryWrapper<SysColumnModule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysColumnModule::getModuleId, param.getModuleId());
        sysColumnModuleMapper.delete(queryWrapper);
        for (Long columnId : param.getColumnIds()) {
            SysColumnModule sysColumnModule = new SysColumnModule();
            sysColumnModule.setModuleId(param.getModuleId());
            sysColumnModule.setColumnId(columnId);
            sysColumnModuleMapper.insert(sysColumnModule);
        }
        sysModuleMapper.updateById(param);
    }

    @Override
    @Transactional
    public void deleteModule(Long[] ids) {
        List<String> errorList = new LinkedList<>();
        for (Long id : ids) {
            SysModule sysModule = sysModuleMapper.selectById(id);
//            FileUploadUtils.deleteFile(sysModule.getUrl());
            sysModuleMapper.deleteById(id);
            LambdaQueryWrapper<SysColumnModule> queryWrapperColumnModule = new LambdaQueryWrapper<>();
            queryWrapperColumnModule.in(SysColumnModule::getModuleId, ids);
//            sysColumnModuleMapper.delete(queryWrapperColumnModule);
        }
        if (CollUtil.isNotEmpty(errorList)) {
            throw new ServiceException("部分活动无法删除: " + String.join(", ", errorList) + "下含有直播，无法删除");
        }
    }

    @Override
    public void addModule(SysModule param, MultipartFile file) {
//        String upload = imageUtils.transformWebpUpload(file);
//        param.setUrl(upload);
//        sysModuleMapper.insert(param);
//        for (Long columnId : param.getColumnIds()) {
//            SysColumnModule sysColumnModule = new SysColumnModule();
//            sysColumnModule.setModuleId(param.getModuleId());
//            sysColumnModule.setColumnId(columnId);
//            sysColumnModuleMapper.insert(sysColumnModule);
//        }
    }

    @Override
    public List<SysModuleVo> selectAllModule(Long columnId) {
        LinkedList<SysModuleVo> result = new LinkedList<>();
        // 直接查询实体而不是只查询ID
        LambdaQueryWrapper<SysColumnModule> queryWrapperColumnModule = new LambdaQueryWrapper<>();
        queryWrapperColumnModule.eq(SysColumnModule::getColumnId, columnId);
        List<SysColumnModule> columnModules = sysColumnModuleMapper.selectList(queryWrapperColumnModule);

        // 提取 moduleIds
        List<Long> moduleIds = columnModules.stream()
                .map(SysColumnModule::getModuleId)
                .collect(Collectors.toList());

        if (!moduleIds.isEmpty()) {
            LambdaQueryWrapper<SysModule> queryWrapperModule = new LambdaQueryWrapper<>();
            queryWrapperModule.in(true, SysModule::getModuleId, moduleIds);
            queryWrapperModule.orderByDesc(SysModule::getSort, SysModule::getCreateTime);
            List<SysModule> sysModules = sysModuleMapper.selectList(queryWrapperModule);
            for (SysModule sysModule : sysModules) {
                result.add(BeanUtil.copyProperties(sysModule, SysModuleVo.class));
            }
        }
        return result;
    }

    @Override
    public List<SysModule> selectModuleByColumnId(Long columnId) {
        LambdaQueryWrapper<SysColumnModule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysColumnModule::getColumnId, columnId).select(SysColumnModule::getModuleId);
        // 类型转换
        List<Long> moduleIds = sysColumnModuleMapper.selectObjs(queryWrapper)
                .stream()
                .map(obj -> (Long) obj)
                .collect(Collectors.toList());

        if (moduleIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SysModule> queryWrapperSysModule = new LambdaQueryWrapper<>();
        queryWrapperSysModule.in(SysModule::getModuleId, moduleIds);
        return sysModuleMapper.selectList(queryWrapperSysModule);
    }

}
