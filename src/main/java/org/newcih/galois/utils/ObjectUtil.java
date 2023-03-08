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

package org.newcih.galois.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectUtil {

    private static final GaloisLog logger = GaloisLog.getLogger(ObjectUtil.class);

    private ObjectUtil() {
    }

    public static Object invokeGetMethod(Object instance, String methodName) {
        try {
            Class<?> clazz = instance.getClass();
            Method getMethod = clazz.getMethod(methodName);
            return getMethod.invoke(instance);
        } catch (Exception e) {
            logger.error("invokeGetMethod failed", e);
            return null;
        }
    }

    public static Object invokeStaticMethod(String className, String methodName, Class<?>[] parameterTypes,
                                            Object[] args) {
        try {
            Class<?> clazz = Class.forName(className);
            return invokeStaticMethod(clazz, methodName, parameterTypes, args);
        } catch (Exception e) {
            logger.error("invokeStaticMethod faeled", e);
            return null;
        }
    }

    public static Object invokeStaticMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes,
                                            Object[] args) {
        try {
            Method method = clazz.getMethod(methodName, parameterTypes);
            return method.invoke(null, args);
        } catch (Exception e) {
            logger.error("invokeStaticMethod failed", e);
            return null;
        }
    }

    public static Object invokeMethod(Object instance, String methodName, Class<?>[] parameterTypes, Object[] args) {
        try {
            Class<?> clazz = instance.getClass();
            Method method = clazz.getMethod(methodName, parameterTypes);
            return method.invoke(instance, args);
        } catch (Exception e) {
            logger.error("invokeMethod failed", e);
            return null;
        }
    }

    public static Object assessField(Object instance, String fieldName) {
        try {
            Class<?> clazz = instance.getClass();
            Field field = clazz.getField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (Exception e) {
            logger.error("accessField failed", e);
            return null;
        }
    }

}
