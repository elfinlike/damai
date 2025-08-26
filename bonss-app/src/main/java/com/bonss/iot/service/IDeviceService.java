package com.bonss.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.system.domain.DTO.DeviceDetailDTO;
import com.bonss.system.domain.Device;

import java.util.List;

public interface IDeviceService extends IService<Device>{

    TableDataInfo listDevices(PageQuery pageQuery, DeviceDetailDTO deviceDetailDTO);

    void updateDevice(Device device);

    TableDataInfo deviceConsumablesDetail(Long deviceId, PageQuery pageQuery);

    Device getDeviceDetail(Long id);

    List<Device> listDevicesByUserId(Long userId);
}
