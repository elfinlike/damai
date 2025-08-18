package com.bonss.system.domain.DTO;

import com.bonss.system.domain.Device;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDetailDTO {

    private Long userId;
    //设备Id
    private Long deviceId;
    //设备名称
    private String deviceName;
    //所属产品Id
    private Long productId;
}
