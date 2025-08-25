package com.gxa.myIbatis.utils;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * SQL语句执行类 (设置Statement参数)
 */
public class ExecuteParser {
    /**
     * SQL类型枚举
     */
    public enum SqlType {
        UPDATE,
        SELECT
    }

    /**
     * 处理对PrepareStatement的参数设置
     * @param ps PrepareStatement对象
     * @param params 参数列表
     * @param type 返回结果类型
     * @return 返回结果
     */
    public static Object handelSetStatement(PreparedStatement ps, List<?> params, SqlType type) {
        System.out.println("[INFO] 参数列表: " + params.toString());
        try {
            // 依次设置参数
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            // 执行对应的SQL
            if (type == SqlType.UPDATE) return ps.executeUpdate();
            else if (type == SqlType.SELECT) return ps.executeQuery();
            else return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[ERR] SQL参数设置失败");
        }
    }

    /**
     * 获取自增主键
     * @param ps PrepareStatement对象
     * @param bean 实体对象
     */
    public static void returnInsertKey(PreparedStatement ps, Object bean) {
        try {
            ResultParser.parseResultSet(
                    ps.executeQuery("SELECT LAST_INSERT_ID()"),
                    bean
            );
        } catch (Exception e) {
            throw new RuntimeException("[ERR] 获取自增主键失败");
        }
    }

}
