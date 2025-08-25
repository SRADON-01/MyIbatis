package com.gxa.myIbatis.utils;
import com.gxa.myIbatis.anno.Param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 已实现功能 :
 * 1. 插入
 *     将Bean对象中的属性值封装成SQL参数直接插入数据库
 *     - 参数类型: Bean x1
 *     - 返回类型: int
 *     ↗ 返回自增主键到参数Bean对象中
 * 2. 更新
 *     将Bean对象中的属性值封装成SQL参数直接插入数据库
 *     * 更新的条件和更新的值都必须在Bean中, 且一个属性不能同时作为更新条件和更新值
 *     - 参数类型: Bean x1
 *     - 返回类型: int
 * 3. 删除
 *     a. 只有一个条件的情况, 参数是单个简单数据类型 (WHERE id = #{})
 *         - 参数类型: Object x1
 *         - 返回类型: int
 *     b. 有一个或多个条件(条件参数封装于Bean), 参数是Bean(DTO) (WHERE id = #{} AND xx = #{} AND ...)
 *         - 参数类型: Bean x1
 *         - 返回类型: int
 *     ×c. 有多个条件, 而且参数都是单个数据类型, 参数不封装到Bean中, 是零散的多个数据类型
 *         - 参数类型: Object1, Object2, ...
 *         - 返回类型: int
 *         ↗ 需要用@Param注解指定参数名称并封装到Map中当作是单个Map(Bean)参数传入
 *     ×d. 批量删除, 参数是列表(List<ids>)
 *         - 参数类型: List<Object>
 *         - 返回类型: int
 * 4. 查询
 *     ↗ 驼峰命名转换为下划线功能
 *     x. 有映射值和无映射值(SELECT A, B / SELECT *)
 *         a. 无查询条件的情况(直接SELECT xx FROM tb)
 *             - 参数类型: 无
 *             - 返回类型: List<Bean>
 *         b. 有查询条件的情况(WHERE id = #{} AND xx = #{} AND ...)
 *             i. 只有一个条件, 同删除.a
 *                 - 参数类型: Object x1
 *                 - 返回类型:
 *                     - 单行 Bean、Map、八大基本数据类型(或其他)
 *                     - 多行 List<Bean>、×List<Map> 、×List<SingleType>
 *             ii. 多个条件, 同删除.b
 *                 - 参数类型: Bean x1
 *                 - 返回类型:
 *                     - 单行 Bean、Map、八大基本数据类型(或其他)
 *                     - 多行 List<Bean>、×List<Map> 、×List<SingleType>
 * 5. 其他功能
 *     - 一级缓存
 *     - 二级缓存 ×
 *     - 事务 ×
 */

/**
 * SQL语句解析类
 */
public class SqlParser {

    /**
     * 存放SQL解析结果
     * sql: 解析后的完整SQL, 可以直接给PreparedStatement执行
     * values: 字段参数值, 可以直接让PreparedStatement来setObject 为空则表示没有条件
     */
    private static final Map<String, Object> result = new HashMap<>();



    /// 主要解析方法
    /**
     * 处理参数是Bean的SQL (插入和更新直接用)
     * @param sql 从注解获取的SQL
     * @param bean 实体对象
     * @return 解析结果map
     */
    public static Map<String, Object> parseBeanSql(String sql, Object bean) {
        // 1. 创建一个字段列表 用于存放从SQL中提取的字段 #{字段}
        List<String> fieldNames = new ArrayList<>();

        // 2. 获取 #{字段} 并加入字段列表, 并在SQL中替换为?
        while (sql.contains("#")) {
            String Field = findField(sql);
            fieldNames.add(Field);
            // System.out.println(Field);
            sql = sql.replace("#{" + Field + "}", "?");
            // System.out.println(sql);
        }

        // 检查点
        // System.out.println(fieldNames);
        // System.out.println(sql);
        result.put("sql", sql);     // 最终SQL语句

        // 利用反射 获取Bean中的字段值(get) 存入参数列表
        List<Object> fieldValues = ReflectUtils.getFieldValue(bean, fieldNames);

        // 检查点
        // System.out.println(fieldValues);
        result.put("values", fieldValues);   // 参数列表

        return result;
    }


    /**
     * 处理参数是简单类型的SQL
     * @param sql 从注解获取的SQL
     * @param params 参数列表
     * @return 解析结果map
     */
    public static Map<String, Object> parseParamsSql(String sql, Object[] params) {
        // 1. 创建一个字段列表 用于存放从SQL中提取的字段 #{字段}
        List<String> fieldNames = new ArrayList<>();

        // 2. 获取 #{字段} 并加入字段列表, 并在SQL中替换为?
        while (sql.contains("#")) {
            String Field = findField(sql);
            fieldNames.add(Field);
            // System.out.println(Field);
            sql = sql.replace("#{" + Field + "}", "?");
            // System.out.println(sql);
            result.put("sql", sql);
        }

        // 3. 构建参数列表
        List<Object> fieldValues = Arrays.asList( params);
        result.put("values", fieldValues);

        return result;
    }

    /**
     * 处理参数是Map的SQL
     * @param sql 从注解获取的SQL
     * @param paramsMap 封装到Map内的参数
     * @return 解析结果map
     */
    public static Map<String, Object> parseMapSql(String sql, Map<String, Object> paramsMap) {
        // 1. 创建一个字段列表 用于存放从SQL中提取的字段 #{字段}
        List<String> fieldNames = new ArrayList<>();

        // 2. 获取 #{字段} 并加入字段列表, 并在SQL中替换为?
        while (sql.contains("#")) {
            String Field = findField(sql);
            fieldNames.add(Field);
            // System.out.println(Field);
            sql = sql.replace("#{" + Field + "}", "?");
        }
        result.put("sql", sql);

        // 3. 从Map中取出对应的值
        List<Object> fieldValues = new ArrayList<>(); // 参数列表
        for (String fieldName : fieldNames) {
            if (paramsMap.get(fieldName) == null)
                throw new RuntimeException("[WARN] 参数Map中缺少字段: " + fieldName);

            fieldValues.add(paramsMap.get(fieldName));
        }
        result.put("values", fieldValues);
        return result;
    }

    public static Map<String, Object> parseUpdateSql(String sql, Object bean, Method m) {
        System.out.println(
                bean.toString()
        );
        // 判断mapper的参数是否为bean
        if (ReflectUtils.isBean(bean)) {
            // 是bean, 沿用处理插入语句解析SQL
            return parseBeanSql(
                    sql,
                    bean
            );
        } else if (bean instanceof Map) {
            // 是Map, 沿用处理Map语句解析SQL
            return parseMapSql(
                    sql,
                    (Map<String, Object>) bean
            );
        } else if (bean instanceof Object[]) {
            return parseMapSql(
                    sql,
                    parseParamsAnno2Map((Object[]) bean, m)
            );
        } else {
            throw new RuntimeException("[ERR] 不支持的参数格式: " + bean);
        }
    }

    /**
     * 处理删除的SQL
     * @param sql 从注解获取的SQL
     * @param bean 实体对象
     * @return 解析结果map
     */
    public static Map<String, Object> parseDeleteSql(String sql, Object bean) {
        // 判断mapper的参数是否为bean
        if (ReflectUtils.isBean(bean)) {
            // 是bean, 沿用处理插入语句解析SQL
            return parseBeanSql(
                    sql,
                    bean
            );
        } else if (bean.getClass().getComponentType() == Map.class) {
            // 是Map, 沿用处理Map语句解析SQL
            return parseMapSql(
                    sql,
                    (Map<String, Object>) bean
            );
        } else {
            // 不是bean, 代表传进来的是int或String, 直接存入参数中
            return parseParamsSql(
                    sql,
                    //
                    bean.getClass().getTypeName().equals(Arrays.class.getTypeName()) ?
                            (Object[]) bean : new Object[]{bean}
            );
        }
    }


    /**
     * 处理查询的SQL
     * @param sql 从注解获取的SQL
     * @param bean 实体对象
     * @return 解析结果map
     */
    public static Map<String, Object> parseSelectSql(String sql, Object bean) {
//        // 判断是查*还是部分查询, 怎么判断? 看看SQL语句有没有*
//        if (! sql.contains("*")) {
//
//        }
        // 判断查询语句中是否有条件, 怎么判断? 看看SQL语句有没有WHERE或#
        if (sql.contains("#"))
            // 有条件, 处理方式同删除SQL
            return parseDeleteSql(
                    sql,
                    bean
            );
        else {
            // 无条件, 即没有参数, 直接返回原SQL
            Map<String, Object> map = new HashMap<>();
            map.put("sql", sql);
            return map;
        }
    }



    /// 解析方法用到的一些SQL字符串操作方法
    /**
     * 获取 #{字段}
     * @param sql 从注解获取的SQL语句
     * @return 字段名称
     */
    private static String findField(String sql) {
        return sql.substring(
                sql.indexOf("#{") + 2,
                sql.indexOf("}")
        );
    }


    /**
     * @没用到
     * 获取查询字段
     */
    private static String getSelectField(String sql) {
        sql = sql.substring(
                sql.indexOf("SELECT") + 6,
                sql.indexOf("FROM")
        );
        return null;
    }


    /**
     * 驼峰转下划线
     * @param sql 字段名
     * @return 转换完成的字段名
     */
    public static String mapUnderscoreToCamelCase(String sql) {
        if (sql == null || sql.isEmpty()) return sql;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            // 如果是大写字母，则在前面加上下划线并转为小写
            if (Character.isUpperCase(c)) {
                // 如果不是第一个字符，则添加下划线
                if (i > 0) {
                    result.append("_");
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        // System.out.println("[INFO] 驼峰转下划线完成: " + result);

        return result.toString();
    }

    /**
     * 基本数据类型识别器
     * @param clazz 字节码
     * @return 是否是基本数据类型
     */
    public static boolean isSingleType(Class<?> clazz) {
        // 先拿到类型并全转小写方便后续处理
        String type = clazz.getTypeName();
        type = type.toLowerCase().substring(type.lastIndexOf(".") + 1);
        // System.out.println("[INFO] 数据类型识别: " + type);

        // 返回对应类型
        if (type.equals("int"))      return true;
        if (type.equals("integer"))  return true;
        if (type.equals("long"))     return true;
        if (type.equals("float"))    return true;
        if (type.equals("double"))   return true;
        if (type.equals("boolean"))  return true;
        if (type.equals("char"))     return true;
        if (type.equals("string"))   return true;
        if (type.equals("date"))     return true;
        if (type.equals("time"))     return true;

        // 这俩不是基本数据类型, 但是还是得识别
        if (type.equals("datetime")) return true;
        if (type.equals("timestamp")) return true;

        try {
            if (ReflectUtils.isBean(clazz.newInstance())) return false;
        } catch (Exception e) {
            throw new RuntimeException("[ERR] 意外情况...");
        }
        throw new RuntimeException("[ERR] 不支持的类型: " + type);
    }

    public static Map<String, Object> parseParamsAnno2Map(Object[] args, Method method) {
        Map<String, Object> fieldValues = new HashMap<>();
        Annotation[][] annotations = method.getParameterAnnotations();
        // 遍历每个参数
        for (int i = 0; i < args.length; i++) {
            // 遍历第i个参数上的所有注解
            if (i < annotations.length) {
                for (Annotation annotation : annotations[i]) {
                    // 检查是否是@Param注解
                    if (annotation instanceof Param) {
                        fieldValues.put( ((Param) annotation).value(), args[i]);
                        break;
                    }
                    else
                        throw new RuntimeException("[ERR] 缺少@Param标记参数 !");
                }
            }
        }
        return fieldValues;
    }
}
