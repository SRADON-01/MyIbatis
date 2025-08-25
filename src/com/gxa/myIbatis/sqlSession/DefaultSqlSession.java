package com.gxa.myIbatis.sqlSession;

import com.gxa.myIbatis.anno.Delete;
import com.gxa.myIbatis.anno.Insert;
import com.gxa.myIbatis.anno.Select;
import com.gxa.myIbatis.anno.Update;
import com.gxa.myIbatis.utils.*;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSqlSession implements SqlSession {
    /**
     * 初始化所有SqlSession都共享的数据库连接吃
     */
    private static final MyDataSource ds = new MyDataSource();



    /**
     * 获取Mapper接口的代理对象
     * @param clazz 传入接口的字节码对象 xxMapper.class
     * @return 接口动态创建的代理对象
     * @param <T> Mapper泛型
     */
    @Override
    public <T> T getMapper(Class<T> clazz) {
        // 打印信息
        System.out.println("[INIT] 字节码对象: " + clazz.getSimpleName());

        // 1. 准备所有接口
        Class<?>[] interfaces = new Class[]{clazz};

        // 2. 准备类加载器
        ClassLoader loader = clazz.getClassLoader();

        // 3. 创建代理对象
        @SuppressWarnings("unchecked")
        T proxyMapper = (T) Proxy.newProxyInstance(loader, interfaces, new InvocationHandler() {
            Map<String, Object> caches = new HashMap<>();
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("[INFO] 代理执行: " + method.getName() + " :: " + Arrays.toString(args));

                // 如果传了多个参数(基本类型), 则将参数封装成数组到数组第一个位置
                if (args != null && args.length > 1) {
                    Object[] array = new Object[1];
                    array[0] = args;
                    args = array;
                }


                // 判断方法是否有注解
                if (method.isAnnotationPresent(Insert.class)) {
                    caches.clear(); // 先清空缓存
                    // 获取SQL
                    Insert insert = method.getAnnotation(Insert.class);
                    String sql = insert.value();
                    System.out.println("[INFO] 获取@Insert注解: " + sql);

                    // 解析SQL
                    Map<String, Object> map = SqlParser.parseUpdateSql(sql, args[0], method);

                    // 获取连接
                    Connection conn = dbUtils.getConnection(ds);
                    System.out.println("[INFO] 获取连接: " + conn);

                    // 设置SQL
                    PreparedStatement ps = conn.prepareStatement((String) map.get("sql"));
                    System.out.println("[INFO] 执行SQL: " + map.get("sql"));

                    // 设置参数并执行SQL
                    int row = (int) ExecuteParser.handelSetStatement(
                            ps, (List<Object>) map.get("values"), ExecuteParser.SqlType.UPDATE
                    );

                    // 如果返回自增主键
                    if (insert.returnInsertKey()) ExecuteParser.returnInsertKey(ps, args[0]);

                    // 释放资源
                    dbUtils.closer(ps, conn);
                    return row;


                } else if (method.isAnnotationPresent(Update.class)) {
                    caches.clear(); // 清空缓存
                    // 获取SQL
                    Update update = method.getAnnotation(Update.class);
                    String sql = update.value();
                    System.out.println("[INFO] 获取@Update注解: " + sql);

                    // 解析SQL
                    Map<String, Object> map = SqlParser.parseUpdateSql(sql, args[0], method);

                    // 获取连接
                    Connection conn = dbUtils.getConnection(ds);
                    System.out.println("[INFO] 获取连接: " + conn);

                    // 设置SQL
                    PreparedStatement ps = conn.prepareStatement((String) map.get("sql"));
                    System.out.println("[INFO] 执行SQL: " + map.get("sql"));

                    // 设置参数并执行SQL
                    int rows = (int) ExecuteParser.handelSetStatement(
                            ps, (List<Object>) map.get("values"), ExecuteParser.SqlType.UPDATE
                    );

                    // 释放资源
                    dbUtils.closer(ps, conn);
                    return rows;


                } else if (method.isAnnotationPresent(Delete.class)) {
                    caches.clear(); // 清空缓存
                    // 获取SQL
                    Delete delete = method.getAnnotation(Delete.class);
                    String sql = delete.value();
                    System.out.println("[INFO] 获取@Delete注解: " + sql);

                    // 解析SQL
                    Map<String, Object> map = SqlParser.parseDeleteSql(sql, args[0]);

                    // 获取连接
                    Connection conn = dbUtils.getConnection(ds);
                    System.out.println("[INFO] 获取连接: " + conn);

                    // 设置SQL
                    PreparedStatement ps = conn.prepareStatement((String) map.get("sql"));
                    System.out.println("[INFO] 执行SQL: " + map.get("sql"));

                    /**
                     * TODO List
                     * select、delete全部弄成适配1-n参数 √
                     * 驼峰转换 √
                     * 返回自增主键 √
                     * 连接池 √
                     * 返回类型是Map √
                     * 返回类型是List<Map>
                     * Mapper方传入多个简单类型
                     * xml
                     */
                    // 设置参数并执行
                    int row = (int) ExecuteParser.handelSetStatement(
                            ps, (List<Object>) map.get("values"),
                            ExecuteParser.SqlType.UPDATE
                    );

                    // 释放资源
                    dbUtils.closer(ps, conn);
                    return row;

                } else if (method.isAnnotationPresent(Select.class)) {
                    // 获取SQL
                    Select select = method.getAnnotation(Select.class);
                    String sql = select.value();
                    System.out.println("[INFO] 获取@Select注解: " + sql);

                    // 解析SQL
                    Map<String, Object> map;    // 准备解析结果
                    if (args == null)
                        // 接口方法无参数代表无条件查
                        map = SqlParser.parseSelectSql(sql, null);
                    else
                        // 接口方法有参数表示有条件查
                        map = SqlParser.parseSelectSql(sql, args[0]);

                    // 查询缓存
                    if (caches.get( sql + map.get("values")) != null) {
                        // 命中缓存直接返回
                        System.out.println("[INFO] 命中缓存");
                        return caches.get(sql + map.get("values"));
                    }

                    // 获取连接
                    Connection conn = dbUtils.getConnection(ds);
                    System.out.println("[INFO] 获取连接: " +  conn);

                    // 设置SQL
                    System.out.println("[INFO] 执行SQL: " + map.get("sql"));
                    PreparedStatement ps = conn.prepareStatement(
                            (String) map.get("sql"),
                            // 设置结果集类型方便获取结果集大小
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_READ_ONLY
                    );

                    // 有参数才设置参数
                    if (map.get("values") != null)
                        ExecuteParser.handelSetStatement(
                                ps, (List<?>) map.get("values"), ExecuteParser.SqlType.SELECT
                        );

                    //// 执行SQL并处理结果集
                    Object returnResult = null;    // 给 结果集解析类 返回的数据 准备容器

                    // 1. 获取调用方的返回类型
                    Class<?> returnType = method.getReturnType();
                    System.out.println("[INFO] 处理返回类型: " + returnType);

                    // 2. 分类处理返回类型
                    if (List.class == returnType) {
                        // 拦截 返回类型列表, 需要获取泛型中的类型以便封装结果集到对象
                        Type genericReturnType = method.getGenericReturnType();
                        ParameterizedType pt = (ParameterizedType) genericReturnType;
                        /**
                         * TODO 区分List里面是Map还是Bean还是基本数据类型
                         * 目前只做了List<Bean>
                         */
                        // 执行SQL后解析结果集
                        returnResult = ResultParser.parseResultSet(
                                ps.executeQuery(), // 执行SQL
                                // *获取List<Bean>中Bean的字节码
                                (Class<?>) pt.getActualTypeArguments()[0],
                                ResultParser.returnType.List
                        );
                        // System.out.println(returnResult);

                    } else if (Map.class == returnType) {
                        // 拦截 返回类型Map, 代表需要把结果集封装成Map
                        return ResultParser.parseResultSet(
                                ps.executeQuery(),  // 执行SQL
                                returnType,
                                ResultParser.returnType.Map
                        );
                        // throw new RuntimeException("[ERR] 暂不支持返回Map类型");

                    } else if (SqlParser.isSingleType(returnType)) {
                        // 是基本类型
                        returnResult = ResultParser.parseResultSet(
                                ps.executeQuery(),
                                returnType, // 这个就是字节码
                                ResultParser.returnType.Single
                        );
                        // System.out.println(returnResult);

                    } else {
                        // 返回类型不是列表, 直接将返回类型的字节码传入
                        returnResult = ResultParser.parseResultSet(
                                ps.executeQuery(),
                                returnType, // 这个就是字节码
                                ResultParser.returnType.Bean
                        );
                        // System.out.println(returnResult);
                    }

                    // 释放资源
                    dbUtils.closer(ps, conn);

                    // 将查询SQL、参数、查询结果一并写入缓存后再返回(key: SQL+参数列表, value:返回数据)
                    caches.put(sql + map.get("values"), returnResult);
                    return returnResult;

//                    if (map.get("values") == null) {
//                        // 解析结果中没有values键表示无条件查询 返回List
//                        // *获取List<Bean>中Bean的类型
//                        Type type = method.getGenericReturnType();
//                        ParameterizedType parameterizedType = (ParameterizedType) type;
//                        return SqlParser.parseResultSet(
//                                ps.executeQuery(),
//                                // *获取List<Bean>中Bean的字节码
//                                (Class<?>) parameterizedType.getActualTypeArguments()[0]
//                        );
//                    }
//                    else {
//                        // 解析结果中有values键表示有条件查询 返回List或单个bean
//                        ps.setObject(1, map.get("values"));
//                        if (List.class == method.getReturnType()) {
//                            Type type = method.getGenericReturnType();
//                            ParameterizedType parameterizedType = (ParameterizedType) type;
//                            return SqlParser.parseResultSet(
//                                    ps.executeQuery(),
//                                    // *获取List<Bean>中Bean的字节码
//                                    (Class<?>) parameterizedType.getActualTypeArguments()[0]
//                            );
//                        }
//                        else
//                            return SqlParser.parseResultSet(
//                                ps.executeQuery(),
//                                method.getReturnType()
//                        );
//                    }

                } else if (method.getName().equals("toString")) {
                    return "[INFO] 你没事调用2似准音干什么?" + "Proxy&MapperImpl";
                } else
                    return "[WARN] 没有这个方法!";
            }
        });

        return proxyMapper;
    }
}
