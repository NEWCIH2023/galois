package org.newcih.galoisdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuguangsheng
 * @since 1.0.0
 **/
@RestController
@RequestMapping("test2")
public class TestController {

  @GetMapping("star")
  public String getStr() {
    return "str";
  }

}
