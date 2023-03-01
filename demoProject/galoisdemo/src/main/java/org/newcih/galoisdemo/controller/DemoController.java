package org.newcih.galoisdemo.controller;

import org.newcih.galoisdemo.dao.TestTemp2Mapper;
import org.newcih.galoisdemo.model.TestTemp2;
import org.newcih.galoisdemo.model.TestTemp2Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("demo")
public class DemoController {

    private int hash = 1000;

    @Autowired
    private TestTemp2Mapper mapper;

    public DemoController() {
    }

    public DemoController(int hash) {
        this.hash = hash;
    }

    @GetMapping("/getString")
    public String getString() {
        TestTemp2Example example = new TestTemp2Example();
        example.createCriteria().andIdIsNotNull();
        TestTemp2 testTemp2 = mapper.selectByExample(example).get(0);
        return "" + testTemp2.getName();
    }

    @GetMapping("/getInt")
    public int getInt() {
        return hash + (int) getTemp() % 1000;
    }

    public long getTemp() {
        return System.currentTimeMillis();
    }

    private final String temp = "tehuihui";

    @GetMapping("/getS")
    public String getS() {
        return String.join(temp, temp, temp);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof DemoController) {
            DemoController temp = (DemoController) obj;
            return temp.hash == this.hash;
        }

        return false;
    }

    @Override
    public String toString() {
        return "DemoController{" +
                "hash=" + hash +
                '}';
    }
}
