package com.bonss.iot.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bonss.common.config.BonssConfig;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.exception.ServiceException;
import com.bonss.common.utils.SecurityUtils;
import com.bonss.common.utils.StringUtils;
import com.bonss.common.utils.file.FileUploadUtils;
import com.bonss.common.utils.file.FileUtils;
import com.bonss.system.domain.Manual;
import com.bonss.system.mapper.ManualMapper;
import com.bonss.iot.service.IManualService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ManualServiceImpl implements IManualService {

    @Autowired
    private ManualMapper manualMapper;


    /**
     * 上传说明书
     *
     * @param productId 产品id
     */
    @Override
    public void addManuals(Integer productId, List<MultipartFile> multipartFiles, String prefix) {
        if (CollectionUtils.isEmpty(multipartFiles)) {
            throw new ServiceException("文件不能为空");
        }
        // 上传文件路径
        String filePath = BonssConfig.getUploadPath();
        List<Manual> list = new ArrayList<>();
        try {
            for (MultipartFile file : multipartFiles) {

                String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                log.info(type);
                if (!type.equals(".pdf")) {
                    throw new ServiceException("上传文件格式有问题");
                }
                // 上传并返回新文件名称
                Manual manual = new Manual();
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = prefix + fileName;
                String orginalName = file.getOriginalFilename();
                long size = file.getSize();
                String fileSize = String.format("%.2f", (double) size / (1024 * 1024)) + " MB";
                manual.setFileName(fileName);
                manual.setUrl(url);
                manual.setOriginalName(orginalName);
                manual.setNewFileName(FileUtils.getName(fileName));
                manual.setFileForm(type);
                manual.setFileSize(fileSize);
                list.add(manual);
            }
        } catch (Exception e) {
            log.error("上传失败");
            throw new ServiceException("上传失败" + e.getMessage());
        }
        LocalDateTime now = LocalDateTime.now();
        String userId = SecurityUtils.getLoginUser().getUserId().toString();
        //对集合每个元素设置提交时间和提交者
        list.stream().forEach(manual -> {
            manual.setUploadTime(now);
            manual.setUploadBy(userId);
            manual.setDelFlag("0");
        });
        //通过判空就进行批量插入
        for (Manual manual : list) {
            manualMapper.insert(manual);
        }
        //获取id集合
        List<Long> ids = list.stream().map(Manual::getId).collect(Collectors.toList());
        //插入联表数据
        manualMapper.insertManualProduct(productId, ids);
    }

    /**
     * 分页查询说明书列表
     *
     * @param productId 产品id
     * @param query     分页
     * @param manual    说明书实体
     * @return 返回分页结果
     */
    @Override
    public TableDataInfo listPage(Integer productId, PageQuery query, Manual manual) {
        Page<Manual> page = query.build();
        List<Long> manualIds = manualMapper.selectByProductId(productId);
        LambdaQueryWrapper<Manual> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(!CollectionUtils.isEmpty(manualIds), Manual::getId, manualIds)
                .eq(manual.getFileName() != null, Manual::getFileName, manual.getFileName())
                .eq(manual.getNewFileName() != null, Manual::getNewFileName, manual.getNewFileName())
                .eq(manual.getOriginalName() != null, Manual::getOriginalName, manual.getOriginalName());
        Page<Manual> manualPage = manualMapper.selectPage(page, wrapper);
        return new TableDataInfo(manualPage.getRecords(), manualPage.getTotal());
    }


    /**
     * 批量删除产品说明书
     *
     * @param productId 产品id
     * @param ids       产品说明书id集合
     */
    @Override
    public void deleteManuals(Integer productId, List<Long> ids, String profileName) {
        if (CollectionUtils.isEmpty(ids)) {
            log.error("传入的产品说明书id不能为空");
            throw new ServiceException("传入的产品说明书id不能为空");
        }
        LambdaQueryWrapper<Manual> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Manual::getId, ids);
        List<Manual> manuals = manualMapper.selectList(wrapper);
        try {
            //删除本地文件
            for (Manual manual : manuals) {
                deleteFile(manual.getFileName());
            }
        } catch (Exception e) {
            log.error("删除产品说明书失败");
            throw new ServiceException("删除产品说明书失败");
        }
        UpdateWrapper<Manual> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(Manual::getId, ids)
                .set(Manual::getDelFlag, "2");
        int update = manualMapper.update(null, updateWrapper);
        if (update <= 0) {
            log.error("逻辑作废产品说明书失败");
            throw new ServiceException("删除产品说明书失败");
        }
    }

    public void deleteFile(String fileName) {
        try {
            if (!FileUtils.checkAllowDownload(fileName)) {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许删除。 ", fileName));
            }
            String filePath = BonssConfig.getProfile() + FileUtils.stripPrefix(fileName);
            log.info(filePath);
            FileUtils.deleteFile(filePath);
        } catch (Exception e) {
            log.error("删除文件失败", e);
            throw new ServiceException("删除文件失败");
        }
    }
}
