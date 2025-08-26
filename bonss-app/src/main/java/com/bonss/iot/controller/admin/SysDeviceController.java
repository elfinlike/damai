package com.bonss.iot.controller.admin;


import com.bonss.common.annotation.Log;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.enums.BusinessType;
import com.bonss.common.enums.OperatorType;
import com.bonss.iot.service.IDeviceService;
import com.bonss.system.domain.DTO.DeviceDetailDTO;
import com.bonss.system.domain.Device;
import com.bonss.system.domain.vo.DeviceDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/system/device")
public class SysDeviceController {

    @Autowired
    private IDeviceService deviceService;

    /**
     * 分页查询设备
     *
     * @param pageQuery 分页查询参数
     * @return 返回分页结果
     */
    @GetMapping("/list")
    @Log(title = "查询设备列表", businessType = BusinessType.GET, operatorType = OperatorType.MANAGE)
    public TableDataInfo listDevices(PageQuery pageQuery, DeviceDetailDTO deviceDetailDTO) {
        return deviceService.listDevices(pageQuery, deviceDetailDTO);
    }


    /**
     * 查看设备详情
     */
    @GetMapping("/{id}")
    @Log(title = "查询设备详情", businessType = BusinessType.GET, operatorType = OperatorType.MANAGE)
    public AjaxResult detail(@PathVariable Long id) {
        return AjaxResult.success(deviceService.getDeviceDetail(id));
    }


    /**
     * 修改设备
     *
     * @param device 设备实体
     * @return 返回操作结果
     */
    @PutMapping
    @Log(title = "修改设备", businessType = BusinessType.UPDATE, operatorType = OperatorType.MANAGE)
    public AjaxResult changeBind(Device device) {
        deviceService.updateDevice(device);
        return AjaxResult.success();
    }

    /**
     * 查看设备的耗材情况
     */
    @GetMapping("/{deviceId}/consumables/detail")
    @Log(title = "查看设备耗材信息", businessType = BusinessType.GET, operatorType = OperatorType.MANAGE)
    public TableDataInfo getDeviceConsumables(@PathVariable Long deviceId,PageQuery pageQuery){
        return deviceService.deviceConsumablesDetail(deviceId,pageQuery);
    }
}
