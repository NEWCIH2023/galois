package org.newcih.galoisdemo.controller;

import org.newcih.galoisdemo.dao.TestTemp2Mapper;
import org.newcih.galoisdemo.model.TestTemp2Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("temp")
@RestController
public class TempController {

//    @Autowired
//    private TestTemp2Mapper mapper;

    @GetMapping("temp")
    public String getTemp() {
//        TestTemp2Example example = new TestTemp2Example();
//        example.createCriteria().andIdIsNotNull();
//        return mapper.selectByExample(example).get(0).getName();
        return "";
    }
}
