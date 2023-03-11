/*
 * MIT License
 * Copyright (c) [2023] [liuguangsheng]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.newcih.galoisdemo.controller;

import org.newcih.galoisdemo.dao.TestTemp2Mapper;
import org.newcih.galoisdemo.service.DemoService;
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

    @Autowired
    private DemoService demoService;

    public DemoController() {
    }

    public DemoController(int hash) {
        this.hash = hash;
    }

    @GetMapping("/getString")
    public String getString() {
        return getTest();
    }

    public String getTest() {
        System.out.println(1);
        return "test";
    }

    @GetMapping("/t7")
    public String t2() {
        return "t";
    }


    @GetMapping("/t")
    public String t() {
        return "t2";
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
