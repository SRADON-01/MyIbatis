package org.sradon.myIbatis.session;

import org.sradon.myIbatis.model.Config;
import org.sradon.myIbatis.model.MyDataSource;
import org.sradon.myIbatis.model.SecondCaches;
import org.sradon.myIbatis.utils.dbUtils;
import java.io.InputStream;
import java.util.Properties;

/**
 * SqlSession工厂Bean
 */
public class SqlSessionManager implements SqlSessionFactory{
    // 所有SqlSession都共享的连接池
    private MyDataSource ds;

    // 所有SqlSession都共享的二级缓存
    private SecondCaches secondCaches = null;

    // 配置信息对象
    private final Config cfg = new Config();

    /**
     * 构造器 创建SqlSessionFactory
     * @param configName 配置文件名称 (classpath: jdbc.properties)
     */
    public SqlSessionManager(String configName) {
        // System.out.println("[INFO] 初始化SqlSessionFactory");
        init(configName);   // 构造时完成初始化
        System.out.println("[INIT] 初始化SqlSessionFactory完成");
    }

    /**
     * 初始化方法
     * @param configPath 配置文件路口
     */
    public void init(String configPath) {
        // 加载配置文件到Properties集合中
        Properties prop = new Properties();
        try {
            // 通过输入流读取配置文件
            InputStream is = dbUtils.class
                    .getClassLoader()
                    .getResourceAsStream(configPath);
            prop.load(is);  // 加载配置文件
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[ERR] 初始化失败, " + configPath + " 配置文件不存在");
        }

        // 把集合拿给连接吃让连接池读取数据库信息
        ds = new MyDataSource(prop);

        // 自动提交和驼峰转换拿给配置对象
        try {
            if (prop.getProperty("ibatis.mapUnderscoreToCamelCase").equals("true"))
                cfg.setMapUnderscoreToCamelCase(true);
        } catch (Exception e) {
            System.out.println("[WARN] CONFIG 'ibatis.mapUnderscoreToCamelCase' NOT FOUND, NORMAL NO_CONVERT");
        } try {
            if (prop.getProperty("jdbc.autoCommit").equals("false"))
                cfg.setAutoCommit(false);
        } catch (Exception e) {
            System.out.println("[WARN] CONFIG 'jdbc.autoCommit' NOT FOUND, NORMAL AUTO_COMMIT");
        } try {
            if (prop.getProperty("ibatis.cacheEnabled").equals("true")) {
                secondCaches = new SecondCaches();
                cfg.setSecondCaches(secondCaches);
                cfg.setCacheEnabled(true);
                System.out.println("[INFO] 启用二级缓存");
            }
        } catch (Exception e) {
            System.out.println("[WARN] CONFIG 'ibatis.cacheEnabled' NOT FOUND, NORMAL CACHE_DISABLED");
        }
    }

    /**
     * 新建一个SqlSession
     * @return SqlSession
     */
    public DefaultSqlSession openSession() {
        return new DefaultSqlSession(ds.getConnection(), cfg);
    }

    /**
     * 新建一个SqlSession并指定是否自动提交
     * @param autoCommit 是否自动提交
     * @return SqlSession
     */
    public DefaultSqlSession openSession(boolean autoCommit) {
        cfg.setAutoCommit(autoCommit);
        return new DefaultSqlSession(ds.getConnection(), cfg);
    }
}
