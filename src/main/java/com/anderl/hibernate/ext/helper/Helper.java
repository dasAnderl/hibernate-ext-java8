package com.anderl.hibernate.ext.helper;

import com.anderl.hibernate.ext.wrappers.OrFilter;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public static Class getGenericInterfaceType(Class clazz, int index) {
        return (Class<?>) ((ParameterizedTypeImpl) clazz.getGenericInterfaces()[index]).getActualTypeArguments()[0];
    }

    public static boolean fieldExistsRecursive(Class clazz, String fieldPath) throws IllegalArgumentException{
        Class currentClass = clazz;
        ArrayList<String> fieldNames = Lists.newArrayList(fieldPath.split("\\."));
        for(String fieldName : fieldNames)    {
            Field field = ReflectionUtils.findField(currentClass, fieldName);
            if (field != null) {
                currentClass = field.getType();
                if (Collection.class.isAssignableFrom(currentClass)) {
                    currentClass = (Class) ((ParameterizedTypeImpl)field.getGenericType()).getActualTypeArguments()[0];
                }
            } else throw new IllegalArgumentException(String.format("%s is not a field on %s. complete path was %s on %s)", fieldName, currentClass.getSimpleName(), fieldPath, clazz.getSimpleName()));
        }
        return true;
    }
}
