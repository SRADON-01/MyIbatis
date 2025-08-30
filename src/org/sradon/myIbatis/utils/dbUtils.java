package org.sradon.myIbatis.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 数据库操作工具类
 */
public class dbUtils {

    /**
     * 提交事务
     */
    public static void commit(Connection conn) {
        try {
            conn.commit();
            System.out.println("[SUCCESS] 提交事务成功...");
            closer(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("[ERR] 事务提交失败");
        }
    }

    /**
     * 回滚事务
     */
    public static void rollback(Connection conn) {
        try {
            conn.rollback();
            System.out.println("[SUCCESS] 回滚事务成功...");
            closer(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("[ERR] 事务回滚失败");
        }
    }

    /**
     * 释放资源
     * @param ps PrepareStatement对象
     */
    public static void closer(PreparedStatement ps) {
        try {
            if (ps != null) ps.close();
        } catch (Exception e) {
            System.out.println("[WARN] 关闭预编译Statement失败");
            e.printStackTrace();
        }
        System.out.println("[SUCCESS] 释放Statement成功...");
    }

    /**
     * 释放资源
     * @param conn Connection对象
     */
    public static void closer(Connection conn) {
        try {
            if (conn != null) conn.close();
        } catch (Exception e) {
            System.out.println("[WARN] 关闭连接失败");
            e.printStackTrace();
        }
        System.out.println("[SUCCESS] 释放连接成功...");
    }

}
