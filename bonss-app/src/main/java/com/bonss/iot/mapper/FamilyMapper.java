package com.bonss.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonss.iot.domain.Family;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FamilyMapper extends BaseMapper<Family> {

    @Select("SELECT f.*, COUNT(m.id) as memberCount FROM family f " +
            "LEFT JOIN family_member m ON f.id = m.family_id " +
            "GROUP BY f.id")
    List<Family> selectFamilyWithMemberCount();
}
