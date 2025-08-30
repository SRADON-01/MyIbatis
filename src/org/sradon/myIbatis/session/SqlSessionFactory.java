package org.sradon.myIbatis.session;

/**
 * SqlSessionFactory接口
 */
public interface SqlSessionFactory {
    /**
     * 初始化工厂
     * @param configPath
     */
    void init(String configPath);

    /**
     * 打开一个SqlSession
     * @return
     */
    DefaultSqlSession openSession();

    /**
     * 打开一个SqlSession
     * @param autoCommit
     * @return
     */
    DefaultSqlSession openSession(boolean autoCommit);
}
