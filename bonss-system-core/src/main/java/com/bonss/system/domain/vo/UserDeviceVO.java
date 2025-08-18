package com.bonss.system.domain.vo;

import com.bonss.common.core.domain.entity.SysUser;
import com.bonss.system.domain.Device;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDeviceVO extends SysUser {
    // 用户设备列表
    private List<Device> devices;
}
