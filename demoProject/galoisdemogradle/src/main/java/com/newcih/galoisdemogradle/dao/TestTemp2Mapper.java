package com.newcih.galoisdemogradle.dao;

import com.newcih.galoisdemogradle.model.TestTemp2;
import com.newcih.galoisdemogradle.model.TestTemp2Example;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestTemp2Mapper {
    int countByExample(TestTemp2Example example);

    int deleteByExample(TestTemp2Example example);

    int insert(TestTemp2 record);

    int insertSelective(TestTemp2 record);

    List<TestTemp2> selectByExample(TestTemp2Example example);

    int updateByExampleSelective(@Param("record") TestTemp2 record, @Param("example") TestTemp2Example example);

    int updateByExample(@Param("record") TestTemp2 record, @Param("example") TestTemp2Example example);
}