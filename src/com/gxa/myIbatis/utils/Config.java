package com.gxa.myIbatis.utils;

public class Config {
    // 默认开启自动提交(关闭事务)
    private boolean autoCommit = true;

    // 默认关闭驼峰转下划线
    private boolean mapUnderscoreToCamelCase = true;

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
