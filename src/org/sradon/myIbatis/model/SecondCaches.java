package org.sradon.myIbatis.model;

import org.sradon.myIbatis.parser.SqlParser;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 二级缓存对象
 */
public class SecondCaches {
    /**
     * 二级缓存容器
     */
    private final ConcurrentHashMap<String, Object> Caches2 = new ConcurrentHashMap<>();

    /**
     * 获取缓存项
     * @param key 缓存键
     * @return 缓存项
     */
    public Object get(String key) {
        return Caches2.get(key);
    }

    /**
     * 添加缓存项
     * @param key 缓存键
     * @param value 缓存项
     */
    public void put(String key, Object value) {
        Caches2.put(key, value);
    }

    /**
     * 删除缓存项
     * @param key 缓存键
     */
    public void remove(String key) {
        Caches2.remove(key);
    }

    /**
     * 清空缓存
     */
    public void clear() {
        Caches2.clear();
    }

    /**
     * 获取缓存项数量
     * @return 缓存项数量
     */
    public int size() {
        return Caches2.size();
    }

    /**
     * 简单实现删除所有受更新类操作影响的缓存
     * @param sqlAndValues SQL和参数(Key)
     */
    public void removeIfUpdate(String sqlAndValues) {
        String tableName = SqlParser.getTableName(sqlAndValues);
        System.out.println("[INFO] 提取表名: <" + tableName + "> 用于二级缓存");
        // 遍历所有缓存项, 检查是否是tb_xx表
        for (String key : Caches2.keySet()) {
            // 删除所有包含该表的缓存
            if (key.toUpperCase().contains(tableName)) Caches2.remove(key);
        }
    }
}
