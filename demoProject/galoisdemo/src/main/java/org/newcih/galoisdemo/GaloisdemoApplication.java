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

package org.newcih.galoisdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
@MapperScan(basePackages = "org.newcih.galoisdemo.dao")
public class GaloisdemoApplication {

    public static void main(String[] args) {
        ClassLoader appClassLoader = GaloisdemoApplication.class.getClassLoader();
        ClassLoader springClassLoader = GaloisdemoApplication.class.getClassLoader();

        try {
            Class<?> app = appClassLoader.loadClass("org.newcih.galois.service.SpringBootLifeCycle");
            Class<?> spring = springClassLoader.loadClass("org.newcih.galois.service.SpringBootLifeCycle");
            System.out.println(appClassLoader);
            System.out.println(springClassLoader);
            System.out.println(app);
            System.out.println(spring);
        } catch (Exception e) {

        }


        SpringApplication.run(GaloisdemoApplication.class, args);
    }

}
