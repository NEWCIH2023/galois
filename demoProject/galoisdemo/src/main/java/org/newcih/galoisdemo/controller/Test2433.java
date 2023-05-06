package org.newcih.galoisdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuguangsheng
 * @since 1.0.0
 **/
@RestController
@RequestMapping("f3strf3str")
public class Test2433 {

  @GetMapping("fstr")
  public String str() {
    return "f3sf3strf3strtr";
  }

}
