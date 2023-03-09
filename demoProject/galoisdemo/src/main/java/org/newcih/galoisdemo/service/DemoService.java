package org.newcih.galoisdemo.service;

import org.newcih.galoisdemo.service.abs.AbstractDemoService;
import org.springframework.stereotype.Service;

@Service
public class DemoService extends AbstractDemoService {

    @Override
    public String getName() {
        return super.getName();
    }

}
