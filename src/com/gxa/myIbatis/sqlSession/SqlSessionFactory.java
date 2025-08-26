package com.gxa.myIbatis.sqlSession;

import com.gxa.myIbatis.utils.Config;
import com.gxa.myIbatis.utils.MyDataSource;
import com.gxa.myIbatis.utils.dbUtils;

import java.io.InputStream;
import java.util.Properties;

public class SqlSessionFactory {
    // 所有SqlSession都共享的连接吃
    private MyDataSource ds;

    // 配置信息对象
    private final Config cfg = new Config();

    // 构造函数
    public SqlSessionFactory(String configName) {
        System.out.println("[INFO] 初始化SqlSessionFactory");
        init(configName);   // 构造时完成初始化
        System.out.println("[INIT] 初始化SqlSessionFactory完成");
    }

    // 初始化方法
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

        // 把集合拿给连接吃让连接吃读取数据库信息
        ds = new MyDataSource(prop);

        // 自动提交和驼峰转换拿给配置对象
        try {
            if (prop.getProperty("ibatis.mapUnderscoreToCamelCase").equals("true"))
                cfg.setMapUnderscoreToCamelCase(true);
            if (prop.getProperty("jdbc.autoCommit").equals("false"))
                cfg.setAutoCommit(false);
        } catch (Exception e) {
            System.out.println("[WARN] CONFIG 'AUTO_COMMIT' NOT FOUND, NORMAL AUTO_COMMIT");
        }
    }

    public DefaultSqlSession openSession() {
        return new DefaultSqlSession(ds.getConnection(), cfg);
    }

    public DefaultSqlSession openSession(boolean autoCommit) {
        cfg.setAutoCommit(autoCommit);
        DefaultSqlSession sqlSession = new DefaultSqlSession(ds.getConnection(), cfg);
        return sqlSession;
    }
}
