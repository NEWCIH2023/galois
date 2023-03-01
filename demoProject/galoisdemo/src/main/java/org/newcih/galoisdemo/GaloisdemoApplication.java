package org.newcih.galoisdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@MapperScan(basePackages = "org.newcih.galoisdemo.dao")
public class GaloisdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaloisdemoApplication.class, args);
    }

}
