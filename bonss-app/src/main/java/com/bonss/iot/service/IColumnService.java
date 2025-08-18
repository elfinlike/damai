package com.bonss.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bonss.iot.param.SysColumnParam;
import com.bonss.system.domain.SysColumn;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 为解决模块拆分时产生的循环依赖, 此处使用门面模式解决
 */
public interface IColumnService extends IService<SysColumn> {

    SysColumn modify(SysColumnParam sysColumnParam, MultipartFile file, MultipartFile productFile);

    void delete(Long[] ids);

    List<SysColumn> listAllColumnInfo();

    List<SysColumn> listAllVideoColumnInfo();

    SysColumn addColumn(SysColumnParam sysColumnParam, MultipartFile file, MultipartFile productFile);

    SysColumn selectColumnById(Long id);

    List<SysColumn> listAllWechatLiveColumn();

    List<SysColumn> listAllWechatVideoColumn();

    List<String> getColumnNames(String medicalDept);

    List<String> getColumnIdByName(String medicalDept);

    void updateImgUrl(Long columnId, String iconUrl, String productImgUrl);
}
