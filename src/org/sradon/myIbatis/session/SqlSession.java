package org.sradon.myIbatis.session;

/**
 * SqlSession接口
 */
public interface SqlSession {

    /**
     * 获取Mapper接口的代理对象
     * @param clazz
     * @return
     * @param <T>
     */
    <T> T getMapper(Class<T> clazz);

    /**
     * 开启事务
     */
    void beginTransaction();

    /**
     * 提交事务
     */
    void commit();

    /**
     * 回滚事务
     */
    void rollback();

    /**
     * 关闭Session
     */
    void close();
}
