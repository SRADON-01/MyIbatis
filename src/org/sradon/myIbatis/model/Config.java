package org.sradon.myIbatis.model;

/**
 * 配置对象
 */
public class Config {
    // 默认开启自动提交(关闭事务)
    private boolean autoCommit = true;

    // 默认关闭驼峰转下划线
    private boolean mapUnderscoreToCamelCase = false;

    // 默认关闭二级缓存
    private boolean cacheEnabled = false;

    // 二级缓存引用
    private SecondCaches secondCaches;

    // 空对象 处理ConcurrentHashMap不能存储null的问题
    private final NullObject NULL = new NullObject();

    // 定义空对象
    static class NullObject {
        public Object none = null;
    }

    // setter/getter...
    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public boolean isMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public SecondCaches getSecondCaches() {
        return secondCaches;
    }

    public void setSecondCaches(SecondCaches secondCaches) {
        this.secondCaches = secondCaches;
    }

    public Object getNULL() {
        return NULL;
    }

    public void setNULL(Object none) {
        this.NULL.none = none;
    }
}
