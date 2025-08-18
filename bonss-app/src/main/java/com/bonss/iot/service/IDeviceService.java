package com.bonss.iot.service;

import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.system.domain.DTO.DeviceDetailDTO;
import com.bonss.system.domain.Device;

public interface IDeviceService {
    TableDataInfo listDevices(PageQuery pageQuery, DeviceDetailDTO deviceDetailDTO);

    void updateDevice(Device device);

    TableDataInfo deviceConsumablesDetail(Long deviceId, PageQuery pageQuery);

    Device getDeviceDetail(Long id, Device device);
}
