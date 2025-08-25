package com.gxa.myIbatis.sqlSession;

public interface SqlSession {
    /**
     * 获取Mapper接口的代理对象
     * @param clazz
     * @return
     * @param <T>
     */
    <T> T getMapper(Class<T> clazz);
}
