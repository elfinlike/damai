package com.bonss.iot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonss.common.constant.CacheConstants;
import com.bonss.common.exception.ServiceException;
import com.bonss.common.utils.file.ImageUtils;
import com.bonss.iot.mapper.SysColumnMapper;
import com.bonss.iot.param.SysColumnParam;
import com.bonss.iot.service.IColumnService;
import com.bonss.system.domain.SysColumn;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bonss.common.constant.ColumnConstants.COLUMN_VIDEO_FLAG_FALSE;
import static com.bonss.common.constant.ColumnConstants.COLUMN_VIDEO_FLAG_TRUE;


/**
 * 系统栏目相干操作 服务层处理
 *
 * @author bonss
 */
@CacheConfig(cacheNames = {CacheConstants.COLUMN_KEY})
@Service
@RequiredArgsConstructor
public class ColumnServiceImpl extends ServiceImpl<SysColumnMapper, SysColumn> implements IColumnService {

    private final static Logger log = LoggerFactory.getLogger(ColumnServiceImpl.class);

    private final SysColumnMapper sysColumnMapper;

    private final ImageUtils imageUtils;

    /**
     * @param sysColumnParam 接收前端发送的修改栏目信息
     * @return 修改结果
     */
    @CacheEvict(allEntries = true)
    @Override
    public SysColumn modify(SysColumnParam sysColumnParam, MultipartFile file, MultipartFile productFile) {
        Long columnId = sysColumnParam.getColumnId();

        LambdaQueryWrapper<SysColumn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(SysColumn::getColumnId, columnId);
        queryWrapper.eq(SysColumn::getColumnName, sysColumnParam.getColumnName());
        if (sysColumnMapper.selectCount(queryWrapper) > 0) throw new ServiceException("该栏目名称已存在!");
        SysColumn sysColumnFromDB = sysColumnMapper.selectById(columnId);
        SysColumn sysColumn = BeanUtil.toBean(sysColumnParam, SysColumn.class);
        if (file != null) {
//            FileUploadUtils.deleteFile(sysColumnFromDB.getIconUrl());
//            sysColumn.setIconUrl(imageUtils.transformWebpUpload(file));
        }
        if (productFile != null) {
//            FileUploadUtils.deleteFile(sysColumnFromDB.getProductImgUrl());
//            sysColumn.setProductImgUrl(imageUtils.transformWebpUpload(productFile));
        }
        if (sysColumn.getVideoFlag().equals(COLUMN_VIDEO_FLAG_FALSE)) {
            sysColumn.setProductImgUrl("");
        }
        sysColumnMapper.updateById(sysColumn);
        return sysColumn;
    }

    /**
     * @param ids 待删除栏目的id列表
     *            含有活动或章节的栏目不能删除
     */
    @CacheEvict(allEntries = true)
    @Override
    public void delete(Long[] ids) {
        ArrayList<Long> deleteList = new ArrayList<>();
        ArrayList<String> errorList = new ArrayList<>();
        for (Long columnId : ids) {
//            List<String> liveSysColumnList = liveSysColumnService.selectLiveSysColumnDependency(columnId);

//            if (CollUtil.isNotEmpty(liveSysColumnList)) {
//                    errorList.addAll(liveSysColumnList);
//                continue;
//            }

            deleteList.add(columnId);
        }

        if (CollUtil.isNotEmpty(deleteList)) {
            sysColumnMapper.deleteBatchIds(deleteList);
        }

        if (CollUtil.isNotEmpty(errorList)) {
            throw new ServiceException("部分栏目无法删除: " + String.join(", ", errorList) + "下含有活动或章节或电子彩页，无法删除");
        }
    }


    /**
     * 处理新添加栏目
     *
     * @param sysColumnParam 接收新添栏目信息对象
     * @return
     */
    @CacheEvict(allEntries = true)
    @Override
    public SysColumn addColumn(SysColumnParam sysColumnParam, MultipartFile file, MultipartFile productFile) {
        isExist(sysColumnParam);
        SysColumn sysColumn = BeanUtil.copyProperties(sysColumnParam, SysColumn.class);
//        sysColumn.setIconUrl(imageUtils.transformWebpUpload(file));
//        if (productFile != null) {
//            sysColumn.setProductImgUrl(imageUtils.transformWebpUpload(productFile));
//        }
        sysColumnMapper.insert(sysColumn);
        return sysColumn;
    }

    private void isExist(SysColumnParam sysColumnParam) {
        // 判断是否存在相同的栏目
        LambdaQueryWrapper<SysColumn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysColumn::getColumnName, sysColumnParam.getColumnName());
        if (sysColumnMapper.selectCount(queryWrapper) > 0) {
            throw new ServiceException("栏目名称:" + sysColumnParam.getColumnName() + "已存在");
        }
    }

    @Override
    public List<SysColumn> listAllWechatLiveColumn() {
        // 通过动态代理调用缓存方法
        ColumnServiceImpl sysColumnService = (ColumnServiceImpl) AopContext.currentProxy();
        List<SysColumn> sysColumns = sysColumnService.listAllColumnInfo();
//        return sysColumns.stream()
//                .filter(column -> liveSysColumnService.checkWechatLiveColumn(column.getColumnId()))
//                .collect(Collectors.toList());
        return null;
    }

    @Override
    public List<SysColumn> listAllWechatVideoColumn() {
        // 通过动态代理调用缓存方法
        ColumnServiceImpl sysColumnService = (ColumnServiceImpl) AopContext.currentProxy();
        List<SysColumn> sysColumns = sysColumnService.listAllVideoColumnInfo();
//        return sysColumns.stream()
//                .filter(column -> liveSysColumnService.checkWechatVideoColumn(column.getColumnId()))
//                .collect(Collectors.toList());
        return null;
    }

    /**
     * 查询单个栏目信息通过id
     *
     * @param id 待查询栏目的id
     * @return 查询的栏目信息
     */
    @Cacheable(key = "#id")
    @Override
    public SysColumn selectColumnById(Long id) {
        return sysColumnMapper.selectById(id);
    }

    /**
     * @return 返回查询所有栏目的信息
     */
    @Cacheable(key = "'allColumn'")
    @Override
    public List<SysColumn> listAllColumnInfo() {
        return sysColumnMapper.selectList(new LambdaQueryWrapper<>(SysColumn.class).orderByDesc(SysColumn::getSort));
    }

    @Cacheable(key = "'allVideoColumn'")
    @Override
    public List<SysColumn> listAllVideoColumnInfo() {
        return sysColumnMapper.selectList(new LambdaQueryWrapper<>(SysColumn.class).orderByDesc(SysColumn::getSort).eq(SysColumn::getVideoFlag, COLUMN_VIDEO_FLAG_TRUE));
    }

    @Override
    public List<String> getColumnNames(String medicalDept) {
        if (StrUtil.isEmpty(medicalDept)) return null;

        ColumnServiceImpl sysColumnService = (ColumnServiceImpl) AopContext.currentProxy();
        List<SysColumn> sysColumnList = sysColumnService.listAllColumnInfo();
        if (CollUtil.isEmpty(sysColumnList)) return null;

        List<String> columnIdStrList = StrUtil.split(medicalDept, ",");
        List<Long> columnIdList = columnIdStrList.stream().map(Long::valueOf).collect(Collectors.toList());
        return sysColumnList.stream().filter(item -> columnIdList.contains(item.getColumnId())).map(SysColumn::getColumnName).collect(Collectors.toList());
    }

    @Override
    public List<String> getColumnIdByName(String medicalDept) {
        if (StrUtil.isEmpty(medicalDept)) return null;

        ColumnServiceImpl sysColumnService = (ColumnServiceImpl) AopContext.currentProxy();
        List<SysColumn> sysColumnList = sysColumnService.listAllColumnInfo();
        if (CollUtil.isEmpty(sysColumnList)) return null;
        // 分割输入的科室名称转换为字符串集合
        String[] parts = medicalDept.split("\\s*[.,，]\\s*");
        List<String> departmentNames = Arrays.asList(parts);

        // 构建栏目名称到ID的映射关系
        Map<String, Long> columnMap = sysColumnList.stream()
                .collect(Collectors.toMap(SysColumn::getColumnName, SysColumn::getColumnId));

        // 过滤出匹配的科室名称并提取对应的ID
        return departmentNames.stream()
                .filter(columnMap::containsKey)
                .map(name -> columnMap.get(name).toString())
                .collect(Collectors.toList());
    }

    @CacheEvict(allEntries = true)
    @Override
    public void updateImgUrl(Long columnId, String iconUrl, String productImgUrl) {
        LambdaUpdateWrapper<SysColumn> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(SysColumn::getColumnId, columnId);
        wrapper.set(SysColumn::getIconUrl, iconUrl);
        wrapper.set(SysColumn::getProductImgUrl, productImgUrl);
        sysColumnMapper.update(null, wrapper);
    }
}
