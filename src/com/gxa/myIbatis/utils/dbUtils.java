package com.gxa.myIbatis.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

/**
 * 数据库操作工具类
 */
public class dbUtils {
    // 准备Properties集合
    private static Properties prop = new Properties();

    // 创建数据库连接池
    // private static MyDataSource ds = new MyDataSource();

    // 获取是否转换命名
    public static boolean isConvertNaming() {
        return "true".equals(prop.getProperty("ibatis.mapUnderscoreToCamelCase"));
    }

    // 跟随类初始化的静态代码块
    static {
        try {
            // 通过输入流读取配置文件
            InputStream is = dbUtils.class
                    .getClassLoader()
                    .getResourceAsStream("jdbc.properties");
            prop.load(is);  // 加载配置文件

            // 注册驱动
            // Class.forName(prop.getProperty("jdbc.driver"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[ERR] 找不到 jdbc.properties !");
        }
    }

    // 获取连接
//    public static Connection getConnectionx() {
//        try {
//            // 通过读取配置文件获取连接
//            return java.sql.DriverManager.getConnection(
//                    prop.getProperty("jdbc.url"),
//                    prop.getProperty("jdbc.username"),
//                    prop.getProperty("jdbc.password"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("[ERR] 初始化连接失败, 请检查配置文件内容格式");
//        }
//        /**
//         * TODO 连接池
//         */
//
//    }

    /**
     * 从连接池中获取数据
     * @return Connection
     */
    public static Connection getConnection(MyDataSource ds) {
        try {
            return ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[ERR] 获取连接失败");
        }
    }

    /**
     * 释放资源
     * @param ps PrepareStatement对象
     * @param conn 数据库连接对象
     */
    public static void closer(PreparedStatement ps, Connection conn) {
        try {
            if (ps != null) ps.close();
        } catch (Exception e) {
            System.out.println("[WARN] 关闭预编译Statement失败");
            e.printStackTrace();
        }
        try {
            if (conn != null) conn.close();
        } catch (Exception e) {
            System.out.println("[WARN] 关闭数据库连接失败");
            e.printStackTrace();
        }

        System.out.println("[INFO] 释放资源成功...");
    }

}
