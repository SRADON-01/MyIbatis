package org.sradon.myIbatis.session;

import org.sradon.myIbatis.anno.Delete;
import org.sradon.myIbatis.anno.Insert;
import org.sradon.myIbatis.anno.Select;
import org.sradon.myIbatis.anno.Update;
import org.sradon.myIbatis.model.Config;
import org.sradon.myIbatis.parser.ExecuteParser;
import org.sradon.myIbatis.parser.ResultParser;
import org.sradon.myIbatis.parser.SqlParser;
import org.sradon.myIbatis.utils.dbUtils;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SqlSession实现类
 */
public class DefaultSqlSession implements SqlSession {
    /**
     * 初始化整个SqlSession都共享的连接
     */
    private Connection conn;

    /**
     * 接收配置对象
     */
    private final Config cfg;

    /**
     * 构造方法 - 初始化SqlSession
     */
    protected DefaultSqlSession(Connection conn, Config cfg) {
        this.conn = conn;
        this.cfg = cfg;
        try {
            conn.setAutoCommit(cfg.isAutoCommit());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[ERR] 事务事项设置失败");
        }
        System.out.println("[INIT] SqlSession初始化完成: [" + this + "] / AUTO_COMMIT: " + cfg.isAutoCommit());
    }


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
            /**
             * SqlSession级别的一级缓存
             * key: String 解析后SQL+参数列表
             * value: Object 返回结果(ResultParser返回结果)
             */
            private final ConcurrentHashMap<String, Object> caches = new ConcurrentHashMap<>();
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("[INFO] 代理执行: " + method.getName() + " :: " + Arrays.toString(args));

                ///  参数处理
                // 如果传了多个参数(基本类型), 则将参数封装成数组到数组第一个位置
                if (args != null && args.length > 1) {
                    Object[] array = new Object[1];
                    array[0] = args;
                    args = array;
                }

                /// 判断方法是否有注解
                if (method.isAnnotationPresent(Insert.class)) {
                    // 获取SQL
                    Insert insert = method.getAnnotation(Insert.class);
                    String sql = insert.value();
                    System.out.println("[INFO] 获取@Insert注解: " + sql);

                    // 缓存处理
                    caches.clear(); // 先清空一缓
                    if (cfg.isCacheEnabled()) cfg.getSecondCaches().removeIfUpdate( sql);

                    // 解析SQL
                    Map<String, Object> map = SqlParser.parseUpdateSql(sql, args[0], method);

                    // 设置SQL
                    PreparedStatement ps = conn.prepareStatement((String) map.get("sql"));
                    System.out.println("[INFO] 执行SQL: " + map.get("sql"));

                    // 设置参数并执行SQL
                    int row = (int) ExecuteParser.handelSetStatement(
                            ps,
                            (List<Object>) map.get("values"),
                            ExecuteParser.SqlType.UPDATE
                    );

                    // 如果返回自增主键
                    if (insert.returnInsertKey()) ExecuteParser.returnInsertKey(ps, args[0]);

                    // 释放资源
                    dbUtils.closer(ps);
                    return row;


                } else if (method.isAnnotationPresent(Update.class)) {
                    // 获取SQL
                    String sql = method.getAnnotation(Update.class).value();
                    System.out.println("[INFO] 获取@Update注解: " + sql);

                    // 缓存处理
                    caches.clear(); // 先清空一缓
                    if (cfg.isCacheEnabled()) cfg.getSecondCaches().removeIfUpdate( sql);

                    // 解析SQL
                    Map<String, Object> map = SqlParser.parseUpdateSql(sql, args[0], method);

                    // 设置SQL
                    PreparedStatement ps = conn.prepareStatement((String) map.get("sql"));
                    System.out.println("[INFO] 执行SQL: " + map.get("sql"));

                    // 设置参数并执行SQL
                    int rows = (int) ExecuteParser.handelSetStatement(
                            ps,
                            (List<Object>) map.get("values"),
                            ExecuteParser.SqlType.UPDATE
                    );

                    // 释放资源
                    dbUtils.closer(ps);
                    return rows;


                } else if (method.isAnnotationPresent(Delete.class)) {
                    caches.clear(); // 清空缓存
                    // 获取SQL
                    String sql = method.getAnnotation(Delete.class).value();
                    System.out.println("[INFO] 获取@Delete注解: " + sql);

                    // 缓存处理
                    caches.clear(); // 先清空一缓
                    if (cfg.isCacheEnabled()) cfg.getSecondCaches().removeIfUpdate( sql);

                    // 解析SQL
                    Map<String, Object> map = SqlParser.parseDeleteSql(sql, args[0], method);

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
                     * Mapper方传入多个简单类型(@Param实现) √
                     * Mapper方传入Map √
                     * xml
                     */
                    // 设置参数并执行
                    int row = (int) ExecuteParser.handelSetStatement(
                            ps, (List<Object>) map.get("values"),
                            ExecuteParser.SqlType.UPDATE
                    );

                    // 释放资源
                    dbUtils.closer(ps);
                    return row;

                } else if (method.isAnnotationPresent(Select.class)) {
                    // 获取SQL
                    String sql = method.getAnnotation(Select.class).value();
                    System.out.println("[INFO] 获取@Select注解: " + sql);

                    // 解析SQL
                    Map<String, Object> map;    // 准备解析结果
                    if (args == null)
                        // 接口方法无参数代表无条件查
                        map = SqlParser.parseSelectSql(sql, null, method);
                    else
                        // 接口方法有参数表示有条件查
                        map = SqlParser.parseSelectSql(sql, args[0], method);

                    // 查询缓存
                    if (cfg.isCacheEnabled()) { // 先查二级缓存
                        Object result = cfg.getSecondCaches().get(sql + map.get("values"));
                        if (result != null) {
                            System.out.println("[INFO] 命中二级缓存");
                            // 由于ConcurrentHashMap不能存储null值, 所以这里用一个对象来表示null
                            return result == cfg.getNULL() ? null : result;
                        }
                    }   // 再查一级缓存
                    if (caches.get(sql + map.get("values")) != null) {
                        System.out.println("[INFO] 命中一级缓存");
                        return caches.get(sql + map.get("values"));
                    }

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
                                ps,
                                (List<?>) map.get("values"),
                                ExecuteParser.SqlType.SELECT
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
                                ResultParser.returnType.List,
                                cfg.isMapUnderscoreToCamelCase()
                        );
                        // System.out.println(returnResult);

                    } else if (Map.class == returnType) {
                        // 拦截 返回类型Map, 代表需要把结果集封装成Map
                        return ResultParser.parseResultSet(
                                ps.executeQuery(),  // 执行SQL
                                returnType,
                                ResultParser.returnType.Map,
                                cfg.isMapUnderscoreToCamelCase()
                        );

                    } else if (SqlParser.isSingleType(returnType)) {
                        // 是基本类型
                        returnResult = ResultParser.parseResultSet(
                                ps.executeQuery(),
                                returnType, // 这个就是字节码
                                ResultParser.returnType.Single,
                                cfg.isMapUnderscoreToCamelCase()
                        );
                        // System.out.println(returnResult);

                    } else {
                        // 返回类型不是列表, 直接将返回类型的字节码传入
                        returnResult = ResultParser.parseResultSet(
                                ps.executeQuery(),
                                returnType, // 这个就是字节码
                                ResultParser.returnType.Bean,
                                cfg.isMapUnderscoreToCamelCase()
                        );
                        // System.out.println(returnResult);
                    }

                    // 释放资源
                    dbUtils.closer(ps);

                    // 处理缓存
                    caches.put(     //  先放一级缓存
                            sql + map.get("values"),
                            returnResult == null ? cfg.getNULL() : returnResult
                    );
                    if (cfg.isCacheEnabled())
                        cfg.getSecondCaches().put(
                                sql + map.get("values"),
                                // 由于ConcurrentHashMap不能存储null值, 所以这里用一个对象来表示null
                                returnResult == null ? cfg.getNULL() : returnResult
                        );
                    return returnResult;

                } else if (method.getName().equals("toString")) {
                    return "[INFO] toSting :: Proxy&MapperImpl";
                } else
                    return "[WARN] 没有这个方法!";
            }
        });

        return proxyMapper;
    }

    @Override
    public void beginTransaction() {
        cfg.setAutoCommit( false);
        try {
            conn.setAutoCommit( false);
            System.out.println("[INFO] 已开启事务... AUTO_COMMIT: "+ cfg.isAutoCommit());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("[ERR] 开始事务失败");
        }
    }

    @Override
    public void commit() {
        if (cfg.isAutoCommit()) return;  // 防止没开事务又去提交事务
        dbUtils.commit(conn);
    }

    @Override
    public void rollback() {
        if (cfg.isAutoCommit()) return;  // 防止没开事务又去提交事务
        dbUtils.rollback(conn);
    }

    @Override
    public void close() {
        // 没开启事务才能归还连接
        if (cfg.isAutoCommit()) dbUtils.closer(conn);
    }
}
