package org.newcih.galoisdemo.service.abs;

import org.newcih.galoisdemo.controller.DemoController;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDemoService<T> {

    @Autowired
    private DemoController demoController;

    public T getName() {
        DemoController demoController1 = new DemoController();
        return (T) ("uiiiia  uih aaaaaaa b a" + demoController1.getTest());
    }
}
