package com.bonss.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bonss.common.annotation.Log;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.domain.entity.SysUser;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.exception.ServiceException;
import com.bonss.common.web.domain.server.Sys;
import com.bonss.system.domain.*;
import com.bonss.system.domain.DTO.DeviceDetailDTO;
import com.bonss.system.domain.vo.DeviceConsumableVO;
import com.bonss.system.domain.vo.DeviceDetailVO;
import com.bonss.system.mapper.*;
import com.bonss.iot.service.IDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeviceServiceImpl implements IDeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceConsumableMapper deviceConsumableMapper;

    @Autowired
    private ConsumableMapper consumableMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SysProductMapper sysProductMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 分页查询设备信息
     *
     * @param pageQuery       分页
     * @param deviceDetailDTO 查询条件
     * @return 返回分页结果
     */
    @Override
    public TableDataInfo listDevices(PageQuery pageQuery, DeviceDetailDTO deviceDetailDTO) {
        // 构建设备查询条件
        LambdaQueryWrapper<Device> deviceWrapper = new LambdaQueryWrapper<>();
        deviceWrapper.like(deviceDetailDTO.getDeviceName() != null, Device::getName, deviceDetailDTO.getDeviceName())
                .eq(deviceDetailDTO.getUserId() != null, Device::getUserId, deviceDetailDTO.getUserId())
                .eq(deviceDetailDTO.getDeviceId() != null, Device::getId, deviceDetailDTO.getDeviceId())
                .eq(Device::getDelFlag, "0");

        // 如果指定了productId，需要筛选对应modelId
        if (deviceDetailDTO.getProductId() != null) {
            LambdaQueryWrapper<ProductModel> modelWrapper = new LambdaQueryWrapper<>();
            modelWrapper.eq(ProductModel::getProductId, deviceDetailDTO.getProductId());
            List<ProductModel> productModels = modelMapper.selectList(modelWrapper);

            if (CollectionUtils.isEmpty(productModels)) {
                return new TableDataInfo(new ArrayList<>(), 0);
            }
            List<Long> modelIds = productModels.stream().map(ProductModel::getId).collect(Collectors.toList());
            deviceWrapper.in(Device::getModelId, modelIds);
        }

        // 查询设备列表
        Page<Device> devicePages = deviceMapper.selectPage(pageQuery.build(), deviceWrapper);

        // 获取用户信息（如果指定了userId）
        SysUser specifiedUser = null;
        if (deviceDetailDTO.getUserId() != null) {
            specifiedUser = sysUserMapper.selectUserById(deviceDetailDTO.getUserId());
        }

        // 转换为VO对象
        SysUser finalSpecifiedUser = specifiedUser;
        List<DeviceDetailVO> deviceDetailVOS = devicePages.getRecords().stream()
                .map(record -> convertToDeviceDetailVO(record, finalSpecifiedUser, deviceDetailDTO.getProductId()))
                .collect(Collectors.toList());

        return new TableDataInfo(deviceDetailVOS, devicePages.getTotal());
    }

    /**
     * 将Device转换为DeviceDetailVO
     */
    private DeviceDetailVO convertToDeviceDetailVO(Device record, SysUser specifiedUser, Long productId) {
        DeviceDetailVO deviceDetailVO = new DeviceDetailVO();
        deviceDetailVO.setDeviceId(record.getId());
        deviceDetailVO.setDeviceName(record.getName());
        deviceDetailVO.setBindTime(record.getBindTime());
        deviceDetailVO.setLastOnlineTime(record.getLastOnlineTime());
        deviceDetailVO.setOnlineStatus(record.getOnlineStatus());
        deviceDetailVO.setIpAddress(record.getIpAddress());

        // 设置用户信息
        if (specifiedUser != null) {
            deviceDetailVO.setUserName(specifiedUser.getUserName());
            deviceDetailVO.setUserId(specifiedUser.getUserId());
        } else {
            SysUser sysUser = sysUserMapper.selectUserById(record.getUserId());
            deviceDetailVO.setUserName(sysUser.getUserName());
            deviceDetailVO.setUserId(sysUser.getUserId());
        }

        // 设置产品型号信息
        ProductModel productModel = modelMapper.selectById(record.getModelId());
        deviceDetailVO.setModelName(productModel.getName());
        deviceDetailVO.setModelCode(productModel.getModelCode());

        // 设置产品信息
        if (productId != null) {
            Product product = sysProductMapper.selectById(productId);
            deviceDetailVO.setProductCode(product.getProductCode());
            deviceDetailVO.setProductName(product.getName());
        } else {
            Product product = sysProductMapper.selectById(productModel.getProductId());
            deviceDetailVO.setProductCode(product.getProductCode());
            deviceDetailVO.setProductName(product.getName());
        }

        return deviceDetailVO;
    }


    /**
     * 更新设备信息
     *
     * @param device 设备实体
     */
    @Override
    public void updateDevice(Device device) {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Device::getDeviceCode, device.getDeviceCode());
        Device deviceOne = deviceMapper.selectOne(wrapper);
        if (deviceOne != null && !Objects.equals(deviceOne.getId(), device.getId())) {
            log.error("设备编号已存在");
            throw new ServiceException("设备编号已存在");
        }
        int update = deviceMapper.updateById(device);
        if (update <= 0) {
            log.error("更新设备信息失败");
            throw new ServiceException("更新设备信息失败");
        }
    }

    /**
     * 查询设备耗品信息
     *
     * @param deviceId  设备id
     * @param pageQuery 分页参数
     * @return 耗品信息
     */
    @Override
    public TableDataInfo deviceConsumablesDetail(Long deviceId, PageQuery pageQuery) {
        // 查询设备耗品类型信息
        LambdaQueryWrapper<DeviceConsumableType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceConsumableType::getDeviceId, deviceId);
        Page<DeviceConsumableType> deviceConsumableTypePage = deviceConsumableMapper.selectPage(pageQuery.build(), wrapper);
        List<DeviceConsumableType> records = deviceConsumableTypePage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return new TableDataInfo();
        }
        List<DeviceConsumableVO> deviceConsumableVOs = new ArrayList<>();
        // 组装数据
        for (DeviceConsumableType record : records) {
            ConsumableType consumableType = consumableMapper.selectById(record.getProductConsumableId());
            DeviceConsumableVO deviceConsumableVO = new DeviceConsumableVO();
            deviceConsumableVO.setName(consumableType.getName());
            deviceConsumableVO.setType(consumableType.getType());
            deviceConsumableVO.setSpec(consumableType.getSpec());
            deviceConsumableVO.setDefaultRemindDays(record.getDefaultRemindDays());
            if (record.getBindTime() == null) {
                deviceConsumableVO.setRemainDays(record.getDefaultRemindDays());
            } else {
                Integer days = Math.toIntExact(ChronoUnit.DAYS.between(record.getBindTime(), LocalDateTime.now()));
                if (deviceConsumableVO.getDefaultRemindDays() >= days) {
                    deviceConsumableVO.setRemainDays(deviceConsumableVO.getDefaultRemindDays() - days);
                } else {
                    log.info("当前耗品已过期");
                    deviceConsumableVO.setRemainDays(-1);
                }
            }
            deviceConsumableVOs.add(deviceConsumableVO);
        }
        return new TableDataInfo(deviceConsumableVOs, deviceConsumableTypePage.getTotal());
    }

    /**
     * 获取设备详情
     *
     * @param id 设备id
     * @return 设备详情
     */
    @Override
    public Device getDeviceDetail(Long id, Device device) {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getId, id);
        Device deviceOne = deviceMapper.selectOne(wrapper);
        if (deviceOne == null) {
            log.error("设备不存在");
            throw new ServiceException("设备不存在");
        }
        return deviceOne;
    }
}
