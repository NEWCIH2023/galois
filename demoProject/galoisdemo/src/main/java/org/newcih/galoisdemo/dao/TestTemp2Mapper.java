package org.newcih.galoisdemo.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.newcih.galoisdemo.model.TestTemp2;
import org.newcih.galoisdemo.model.TestTemp2Example;

public interface TestTemp2Mapper {
    int countByExample(TestTemp2Example example);

    int deleteByExample(TestTemp2Example example);

    int insert(TestTemp2 record);

    int insertSelective(TestTemp2 record);

    List<TestTemp2> selectByExample(TestTemp2Example example);

    int updateByExampleSelective(@Param("record") TestTemp2 record, @Param("example") TestTemp2Example example);

    int updateByExample(@Param("record") TestTemp2 record, @Param("example") TestTemp2Example example);
}