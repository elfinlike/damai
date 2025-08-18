package com.bonss.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonss.system.domain.Manual;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface ManualMapper extends BaseMapper<Manual> {

    void batchInsert(List<Manual> manuals);

    void insertManualProduct(@Param("productId") Integer productId, @Param("ids") List<Long> ids);

    @Select("select manual_id from product_manual where product_id=#{productId}")
    List<Long> selectByProductId(@RequestParam("productId") Integer productId);
}
