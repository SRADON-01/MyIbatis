package com.gxa.myIbatis.model;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结果集解析类
 */
public class ResultParser {

    /**
     * 返回类型枚举
     */
    public enum returnType {
        List,
        Bean,
        Map,
        Single // 代表所有除上面以外的
    }

    /**
     * 解析结果集到对象中
     * @param rs 结果集
     * @param clazz 要封装对象的字节码对象
     * @return 对象列表或单个对象
     */
    public static Object parseResultSet(ResultSet rs, Class<?> clazz, returnType type, boolean isConvertNaming) {
        try {

            // 判断结果集大小
            rs.last();  // 移动到最后一行, 用于获取结果集大小
            if (type == returnType.Bean) {
                // 结果集大小为0表示什么都没查到, 直接返回null
                if (rs.getRow() == 0) return null;

                // 准备对象实例
                Object bean = clazz.newInstance();

                // 准备对象属性列表 用于结果集封装
                Field[] fields = clazz.getDeclaredFields();

                // 实例属性赋值
                rs.first();  // 移动到第一行, 方便直接取出数据
                setBean(fields, bean, rs, isConvertNaming);  // 将查询结果封装到对象中

                return bean;

            } else if (type == returnType.Map) {
                // 结果集大小为0表示什么都没查到, 直接返回空Map
                if (rs.getRow() == 0) return new HashMap<>();

                // 准备MAP实例
                Map<String, Object> bean = new HashMap<>();

                // 实例属性赋值
                rs.first();  // 移动到第一行, 方便直接取出数据
                setBean(bean, rs);  // 将查询结果封装到对象中

                return bean;

            } else if (type == returnType.List){
                // 多行结果的情况, 需要一个列表来存放多个对象
                List<Object> beans = new ArrayList<>();
                // 准备对象属性列表 用于结果集封装
                Field[] fields = clazz.getDeclaredFields();
                rs.beforeFirst();   // 游标移动到最初
                while (rs.next()) {
                    Object bean = clazz.newInstance(); // 每行数据都准备一个新的对象实例
                    /**
                     * TODO List<Map>
                     */
                    setBean(fields, bean, rs, isConvertNaming);  // 将查询结果封装到对象中
                    //                for (Field field : fields) {
                    //                    // 利用BeanUtils把结果集中的值都依次封装到对象对应的的属性中
                    //                    try {
                    //                        Object columnValue = rs.getObject(field.getName());
                    //                        BeanUtils.copyProperty(bean, field.getName(), columnValue);
                    //                        // 如果结果集中找不到这个字段就会抛出异常, 就证明没查这个属性, 于是跳过
                    //                    } catch (Exception e) {
                    //                        BeanUtils.copyProperty(bean, field.getName(), null);
                    //                    }
                    //                }

                    beans.add(bean);    // 赋值完后把实例添加到集合中
                }
                return beans;

            } else {
                // 如果以上都不是那就是基本数据类型
                // 结果集大小为0表示什么都没查到, 直接返回null
                if (rs.getRow() == 0) return null;
                rs.first();
                return rs.getObject(1);     // 直接返回
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[ERR] 解析结果集失败");
        }
    }

    /**
     * 将结果集中的行的数据映射到Bean对象的属性上
     * @param fields Bean对象的属性列表
     * @param bean 要封装的Bean对象实例
     * @param rs 结果集对象
     */
    public static void setBean(Field[] fields, Object bean, ResultSet rs, boolean isConvertNaming) {
        // 把结果集中的字段一一对应到Bean属性中, 根据Bean属性名到结果集中取值
        for (Field field : fields) {
            // 获取属性名称, 对应结果集中的列
            String fieldName = field.getName();

            try {
                // 通过对象属性取出(映射)结果集中的字段值
                Object columnValue = rs.getObject(
                        // 如果设置了驼峰转换就传入驼峰属性名, 否则传入属性名, 不然在数据库中找不到该列
                        isConvertNaming ? SqlParser.mapUnderscoreToCamelCase(fieldName) : fieldName
                );

                // 利用BeanUtils把结果集中的值都依次封装到对象对应的的属性中
                BeanUtils.copyProperty(bean, fieldName, columnValue);

                // 如果结果集中找不到这个字段就会抛出异常, 就证明没查这个属性, 于是跳过
            } catch (Exception e) {
                // BeanUtils.copyProperty(bean, fieldName, null);
            }
        }
    }
    /**
     * 重载: 将结果集中的行的数据映射到Map
     * @param map 要装数据的Map对象
     * @param rs 结果集对象
     */
    public static void setBean(Map<String, Object> map, ResultSet rs) {
        try {
            // 获取结果集中的元数据
            ResultSetMetaData metaData = rs.getMetaData();
            // 字段的个数
            int count = metaData.getColumnCount();
            for (int i = 0; i < count; i++) {
                String columnName = metaData.getColumnName(i + 1);
                map.put(columnName, rs.getObject(columnName));
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[ERR] 将结果集封装到MAP时失败");
        }
    }

    /**
     * 解析返回自增主键的结果集, 并将主键赋值给Bean的ID字段
     *
     * @param rs   自增主键结果
     * @param bean 插入用到的Bean对象
     * @return Bean对象
     */
    public static void parseResultSet(ResultSet rs, Object bean) {
        try {
            // 目前没想到获取id字段名的手段, 只能指定"id"
            rs.next();
            BeanUtils.copyProperty(
                    bean, "id", rs.getObject(1)
            );
            // 用这个需要处理暴力破解, 这里直接用BeanUtils偷懒了
            // bean.getClass().getDeclaredField("id").set(bean, rs.getObject(1));
            // return bean;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[ERR] 获取自增结果或赋值操作失败");
        }
        // return bean;
    }
}
