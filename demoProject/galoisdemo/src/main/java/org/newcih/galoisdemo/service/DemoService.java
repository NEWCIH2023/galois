package org.newcih.galoisdemo.service;

import org.newcih.galoisdemo.dao.TestTemp2Mapper;
import org.newcih.galoisdemo.model.TestTemp2Example;
import org.newcih.galoisdemo.service.abs.AbstractDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemoService extends AbstractDemoService<String> {

    @Autowired
    private TestTemp2Mapper testTemp2Mapper;

    static {
        try {
            Class.forName(com.comtop.corm.session.Configuration.class.getName());
        } catch (Exception ignored) {

        }

        System.out.println("加载corm驱动完成!");

    }

    public String getName() {
        TestTemp2Example example = new TestTemp2Example();
        example.createCriteria().andIdIsNotNull();
        return testTemp2Mapper.selectByExample(example).get(0).getName();
    }

}
