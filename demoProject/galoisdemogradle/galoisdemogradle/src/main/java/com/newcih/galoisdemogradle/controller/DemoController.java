package com.newcih.galoisdemogradle.controller;

import com.newcih.galoisdemogradle.dao.TestTemp2Mapper;
import com.newcih.galoisdemogradle.model.TestTemp2Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("demo")
public class DemoController {

    public static final String A = "AA";

    @Autowired
    private TestTemp2Mapper mapper;

    @GetMapping("demo")
    public String demo() {
        TestTemp2Example example = new TestTemp2Example();
        example.createCriteria().andIdIsNotNull();
        return "em" + mapper.selectByExample(example).get(0).getName();
    }
}
