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
