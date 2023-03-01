package org.newcih.service;

import java.util.Iterator;
import java.util.stream.Stream;

public class ClassExample {

    public ClassExample() {

    }

    public static void main(String[] args) {
        test1();
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
}
