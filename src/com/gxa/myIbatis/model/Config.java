package com.gxa.myIbatis.model;

/**
 * 配置对象
 */
public class Config {
    // 默认开启自动提交(关闭事务)
    private boolean autoCommit = true;

    // 默认关闭驼峰转下划线
    private boolean mapUnderscoreToCamelCase = false;

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
}
