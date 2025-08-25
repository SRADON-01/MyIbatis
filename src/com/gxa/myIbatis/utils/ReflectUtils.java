package com.gxa.myIbatis.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射工具类
 */
public class ReflectUtils {
    /**
     * 获取指定属性的值
     * @param obj 要获取值的实例对象
     * @param fieldNames 要获取值的属性名称 列表
     * @return 属性值 列表
     */
    public static List<Object> getFieldValue(Object obj, List<String> fieldNames) {
        // 准备一个列表来存放所有属性值
        List<Object> fieldValues = new ArrayList<>();

        // 获取属性值
        Class<?> clazz = obj.getClass();
        for (String fieldName : fieldNames) {
            try {
                // 通过拼接属性名称和"get"的方式来获取Getter
                Method method = clazz.getMethod(
                        "get"
                                + fieldName.substring(0, 1).toUpperCase()
                                + fieldName.substring(1)
                );
                // System.out.println( method);
                // System.out.println( method.invoke(obj));
                fieldValues.add(method.invoke(obj));        // 执行get方法获取属性值, 并放入属性值列表中
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("[ERR] GET FieldValues FROM Bean ERR");
            }
        }
        return fieldValues;
    }

    /**
     * 判断一个对象是否是符合 JavaBean 规范的对象
     * @param obj 要判断的对象
     * @return true 表示是 JavaBean，false 表示不是
     */
    public static boolean isBean(Object obj) {
        if (obj == null) {
            return false;
        }
        Class<?> clazz = obj.getClass();

        // 检查是否有无参构造方法
        try {
            clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            return false;
        }

        // 检查是否包含getter和setter方法
        Method[] methods = clazz.getMethods();
        boolean hasGetter = false, hasSetter = false;
        for (Method method : methods) {
            if (method.getName().startsWith("get") && method.getParameterCount() == 0)
                hasGetter = true;
            if (method.getName().startsWith("set") && method.getParameterCount() == 1)
                hasSetter = true;
        }

        return hasGetter && hasSetter;
    }



}
