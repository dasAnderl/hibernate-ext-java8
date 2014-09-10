package com.anderl.hibernate.ext.helper;

import com.google.common.collect.Lists;
import org.springframework.util.ReflectionUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ga2unte on 8.9.2014.
 */
public class Helper {

    public static <T> List<T> invokeGettersByReturnType(Class<T> clazz, Object object) {
        List<T> list = new ArrayList<>();
        if (object == null) {
            return list;
        }
        for (Method method : object.getClass().getMethods()) {
            if (method.getName().startsWith("get")
                    && method.getReturnType().getName().equals(clazz.getName())
                    && object.getClass() != method.getClass()) {
                try {
                    Object result = method.invoke(object);
                    if (result != null) {
                        list.add((T) result);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public static Class getGenericInterfaceType(Class clazz) {
        return (Class<?>) ((ParameterizedTypeImpl) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    public static boolean fieldExists(Class currentClass, String fieldName) {
        return ReflectionUtils.findField(currentClass, fieldName) != null;
    }
}
