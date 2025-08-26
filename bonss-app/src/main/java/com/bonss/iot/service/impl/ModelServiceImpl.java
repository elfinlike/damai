package com.bonss.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.exception.ServiceException;
import com.bonss.common.utils.SecurityUtils;
import com.bonss.system.domain.*;
import com.bonss.system.domain.DTO.ModelDTO;
import com.bonss.system.domain.vo.ModelVO;
import com.bonss.system.mapper.*;
import com.bonss.iot.service.IModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ModelServiceImpl implements IModelService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ModelConsumableTypeMapper modelConsumableTypeMapper;

    @Autowired
    private ConsumableMapper consumableMapper;

    @Autowired
    private ModelManualMapper modelManualMapper;

    @Autowired
    private ModelTutorialMapper modelTutorialMapper;

    @Autowired
    private ManualMapper manualMapper;

    @Autowired
    private TutorialMapper tutorialMapper;

    @Autowired
    private SysProductMapper sysProductMapper;

    /**
     * 添加产品型号
     *
     * @param modelDTO 产品型号实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addModel(ModelDTO modelDTO) {

        //属性拷贝
        ProductModel productModel = new ProductModel();
        BeanUtils.copyProperties(modelDTO, productModel);
        log.info(modelDTO.toString());
        // 对型号和编码做唯一性校验
        if (productModel.getModelCode() == null || productModel.getName() == null) {
            log.error("型号编码或者名称不能为空");
            throw new ServiceException("型号编码或名称不能为空");
        }

        // 检查编码和名称的唯一性
        LambdaQueryWrapper<ProductModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductModel::getName, productModel.getName())
                .or()
                .eq(ProductModel::getModelCode, productModel.getModelCode());

        List<ProductModel> productModels = modelMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(productModels)) {
            log.error("型号编码或者名称不能重复");
            throw new ServiceException("型号编码或名称不能重复");
        }
        productModel.setCreateBy(SecurityUtils.getAdminUserName());
        productModel.setUpdateBy(SecurityUtils.getAdminUserName());
        int insert = modelMapper.insert(productModel);
        if (!CollectionUtils.isEmpty(modelDTO.getManualIds())) {
            modelManualMapper.batchInsert(productModel.getId(), modelDTO.getManualIds());
        }
        if (!CollectionUtils.isEmpty(modelDTO.getConsumableIds())) {
            modelConsumableTypeMapper.batchInsert(productModel.getId(), modelDTO.getConsumableIds());
        }
        if (!CollectionUtils.isEmpty(modelDTO.getTutorialIds())) {
            modelTutorialMapper.batchInsert(productModel.getId(), modelDTO.getTutorialIds());
        }
        if (insert <= 0) {
            log.error("添加产品型号失败");
            throw new ServiceException("添加产品型号失败");
        }
        log.info("产品型号添加成功");
        Product product = sysProductMapper.selectById(productModel.getProductId());
        // 产品型号数量+1
        int update = sysProductMapper.update(null, new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, productModel.getProductId())
                .set(Product::getModelCount, product.getModelCount() + 1));

        if (update <= 0) {
            log.error("产品型号数量添加失败");
            throw new ServiceException("产品型号数量添加失败");
        }
        log.info("产品型号添加成功");
    }

    /**
     * 查询型号具体信息
     *
     * @param modelId 型号ID
     * @return 返回型号实体
     */
    @Override
    public ModelVO getDetail(Long modelId) {
        ProductModel productModel = modelMapper.selectById(modelId);
        if (productModel == null) {
            log.error("查询不到对应记录");
            throw new ServiceException("查询不到对应记录");
        }
        ModelVO modelVO = new ModelVO();
        BeanUtils.copyProperties(productModel, modelVO);
        log.info(modelVO.toString());
        //添加耗材列表
        LambdaQueryWrapper<ModelConsumableType> consumableWrapper = new LambdaQueryWrapper<>();
        consumableWrapper.eq(ModelConsumableType::getModelId, modelId)
                .eq(ModelConsumableType::getDelFlag, "0");
        List<ModelConsumableType> modelConsumableTypes = modelConsumableTypeMapper.selectList(consumableWrapper);
        //查询不到对应记录就直接设置空集合
        if (CollectionUtils.isEmpty(modelConsumableTypes)) {
            modelVO.setConsumables(Collections.EMPTY_LIST);
        } else {
            List<Long> consumableIds = modelConsumableTypes.stream().map(ModelConsumableType::getConsumableTypeId)
                    .collect(Collectors.toList());
            List<ConsumableType> consumableTypes = consumableMapper.selectBatchIds(consumableIds);
            modelVO.setConsumables(consumableTypes);
        }
        //添加说明书列表
        LambdaQueryWrapper<ModelManual> manualWrapper = new LambdaQueryWrapper<>();
        manualWrapper.eq(ModelManual::getModelId, modelId)
                .eq(ModelManual::getDelFlag, "0");
        List<ModelManual> modelManuals = modelManualMapper.selectList(manualWrapper);
        //查询不到对应记录就直接设置空集合
        if (CollectionUtils.isEmpty(modelManuals)) {
            modelVO.setManuals(Collections.EMPTY_LIST);
        } else {
            List<Long> manualIds = modelManuals.stream().map(ModelManual::getManualId)
                    .collect(Collectors.toList());
            List<Manual> manuals = manualMapper.selectBatchIds(manualIds);
            modelVO.setManuals(manuals);
        }


        //添加图文教材列表
        LambdaQueryWrapper<ModelTutorial> tutorialWrapper = new LambdaQueryWrapper<>();
        tutorialWrapper.eq(ModelTutorial::getModelId, modelId)
                .eq(ModelTutorial::getDelFlag, "0");
        List<ModelTutorial> modelTutorials = modelTutorialMapper.selectList(tutorialWrapper);
        //查询不到对应记录就直接设置空集合
        if (CollectionUtils.isEmpty(modelTutorials)) {
            modelVO.setManuals(Collections.EMPTY_LIST);
        } else {
            List<Long> tutorialIds = modelTutorials.stream().map(ModelTutorial::getTutorialId)
                    .collect(Collectors.toList());
            List<Tutorial> tutorials = tutorialMapper.selectBatchIds(tutorialIds);
            modelVO.setTutorials(tutorials);
        }
        return modelVO;
    }

    /**
     * 分页查询型号
     *
     * @param pageQuery
     * @param productModel
     * @return
     */
    @Override
    public TableDataInfo listModels(PageQuery pageQuery, ProductModel productModel) {
        Page<ProductModel> page = pageQuery.build();
        LambdaQueryWrapper<ProductModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(productModel.getModelCode() != null, ProductModel::getModelCode, productModel.getModelCode())
                .like(productModel.getName() != null, ProductModel::getName, productModel.getName())
                .eq(productModel.getProductId() != null, ProductModel::getProductId, productModel.getProductId())
                .eq(productModel.getStatus() != null, ProductModel::getStatus, productModel.getStatus())
                .eq(ProductModel::getDelFlag, "0");
        Page<ProductModel> productModelPage = modelMapper.selectPage(page, wrapper);
        return new TableDataInfo(productModelPage.getRecords(), productModelPage.getTotal());
    }

    /**
     * 修改产品型号
     *
     * @param modelDTO 产品型号实体
     */
    @Override
    public void updateModel(ModelDTO modelDTO) {
        ProductModel productModel = new ProductModel();
        BeanUtils.copyProperties(modelDTO, productModel);
        List<Long> consumableIds = modelDTO.getConsumableIds();
        List<Long> manualIds = modelDTO.getManualIds();
        List<Long> tutorialIds = modelDTO.getTutorialIds();


        //  对新插入的数据进行唯一性校验
        if (productModel.getModelCode() != null || productModel.getName() != null) {
            LambdaQueryWrapper<ProductModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(productModel.getModelCode() != null, ProductModel::getModelCode, productModel.getModelCode())
                    .or().eq(productModel.getName() != null, ProductModel::getName, productModel.getName());
            List<ProductModel> productModels = modelMapper.selectList(wrapper);
            if (productModels.size() > 1) {
                log.error("修改的产品编码和产品名称不能重复");
                throw new ServiceException("修改的产品编码和产品名称不能重复");
            } else if (productModels.size() == 1 && !Objects.equals(productModels.get(0).getId(), productModel.getId())) {
                log.error("修改的产品编码和产品名称不能重复");
                throw new ServiceException("修改的产品编码和产品名称不能重复");
            }
        }

        productModel.setUpdateBy(SecurityUtils.getAdminUserName());
        int update = modelMapper.updateById(productModel);
        Long modelId = productModel.getId();
        //修改耗材关系表
        UpdateWrapper<ModelConsumableType> wrapper = new UpdateWrapper<>();
        wrapper.lambda().eq(ModelConsumableType::getModelId, modelId)
                .set(ModelConsumableType::getDelFlag, "2");
        modelConsumableTypeMapper.update(null, wrapper);
        if (!CollectionUtils.isEmpty(consumableIds)) {
            modelConsumableTypeMapper.batchInsert(modelId, consumableIds);
        }
        //修改产品说明书关系表
        UpdateWrapper<ModelManual> manualWrapper = new UpdateWrapper<>();
        manualWrapper.lambda().eq(ModelManual::getModelId, modelId)
                .set(ModelManual::getDelFlag, "2");
        modelManualMapper.update(null, manualWrapper);
        if (!CollectionUtils.isEmpty(manualIds)) {
            modelManualMapper.batchInsert(modelId, manualIds);
        }
        //修改图文教程关系表
        UpdateWrapper<ModelTutorial> tutorialWrapper = new UpdateWrapper<>();
        tutorialWrapper.lambda().eq(ModelTutorial::getModelId, modelId)
                .set(ModelTutorial::getDelFlag, "2");
        modelTutorialMapper.update(null, tutorialWrapper);
        if (!CollectionUtils.isEmpty(tutorialIds)) {
            modelTutorialMapper.batchInsert(modelId, tutorialIds);
        }

        if (update <= 0) {
            log.error("修改产品型号失败");
            throw new ServiceException("修改产品型号失败");
        }
    }

    /**
     * 逻辑删除产品型号
     *
     * @param ids 产品型号id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModels(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("删除ID不能为空");
        }

        // 先查询要删除的型号对应的产品ID
        List<ProductModel> models = modelMapper.selectList(new LambdaQueryWrapper<ProductModel>()
                .select(ProductModel::getId, ProductModel::getProductId)
                .in(ProductModel::getId, ids));

        if (models.isEmpty()) {
            throw new ServiceException("未找到对应的型号数据");
        }

        // 逻辑删除产品型号
        UpdateWrapper<ProductModel> wrapper = new UpdateWrapper<>();
        wrapper.lambda()
                .set(ProductModel::getDelFlag, "2")
                .set(ProductModel::getUpdateBy, SecurityUtils.getAdminUserName())
                .set(ProductModel::getUpdateTime, LocalDateTime.now())
                .in(ProductModel::getId, ids);

        int update = modelMapper.update(null, wrapper);
        if (update <= 0) {
            log.error("产品型号删除失败, ids:{}", ids);
            throw new ServiceException("删除失败");
        }

        // 统计每个产品被删掉多少个型号
        Map<Long, Long> reduceCountMap = models.stream()
                .filter(m -> m.getProductId() != null)
                .collect(Collectors.groupingBy(ProductModel::getProductId, Collectors.counting()));

        // 批量更新产品的 modelCount 字段
        reduceCountMap.forEach((productId, reduceCount) -> {
            UpdateWrapper<Product> productWrapper = new UpdateWrapper<>();
            productWrapper.lambda()
                    .eq(Product::getId, productId)
                    .setSql("model_count = CASE WHEN model_count >= " + reduceCount +
                            " THEN model_count - " + reduceCount +
                            " ELSE 0 END");
            int updated = sysProductMapper.update(null, productWrapper);
            if (updated <= 0) {
                log.error("产品型号减少失败, productId:{}", productId);
                throw new ServiceException("减少失败");
            }
        });
    }
}
