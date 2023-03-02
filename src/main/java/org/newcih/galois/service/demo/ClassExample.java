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

package org.newcih.galois.service.demo;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.stream.Stream;

public class ClassExample {

    private String name;

    public ClassExample() {

    }

    public static void setProperty(ClassExample example) throws Exception {
        PropertyDescriptor descriptor = new PropertyDescriptor("name", ClassExample.class);
        Method setName = descriptor.getWriteMethod();
        setName.invoke(example, "newcih");
    }

    public static void getProperty(ClassExample example) throws Exception {
        PropertyDescriptor descriptor = new PropertyDescriptor("name", ClassExample.class);
        Method getName = descriptor.getReadMethod();
        System.out.println(getName.invoke(example));
    }

    public static void main(String[] args) throws Exception {
        ClassExample example = new ClassExample();
        setProperty(example);
        getProperty(example);

        BeanInfo beanInfo = Introspector.getBeanInfo(ClassExample.class);
        System.out.println(beanInfo);
    }

    public static void test1() {
        Stream<Integer> is = Stream.of(12, 33, 89);
        Iterator iterator = is.iterator();
        while (iterator.hasNext()) {
            if (33 == (int) iterator.next()) {
                iterator.remove();
            }
        }

        System.out.println(is.count());
    }

    public static void test2() {
    }

    public String test() {
        System.out.println("test method");
        return "test";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
