package org.newcih.galoisdemo.dao;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuguangsheng
 * @since 1.0.0
 **/
@RestController
@RequestMapping("test2")
public class Test2 {

  @GetMapping("fstr")
  public String str() {
    return "f3str";
  }

}
