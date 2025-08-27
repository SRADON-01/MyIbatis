package com.gxa.myIbatis.model;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

/**
 * OPO连接池
 */
public class MyDataSource implements DataSource {

    // 数据库连接池底层的集合
    private LinkedList<Connection> conns = new LinkedList<>();

    // 配置文件
    private Properties config = new Properties();

    // 数据库配置
    private String driver = null;
    private String url = null;
    private String user = null;
    private String password = null;
    private Boolean autoCommit = true;
    private Integer initPoolSize = 10;
    private Integer dynamicAddSize = 10;

    // 配置文件是否读取
    private Boolean getProperties = false;
    private Integer setterCount = 0;
    private Integer initCount = 0;

    // 初始化连接池
    private void InitPool(){
        // 没有读取到数据库配置 或者 在初始化后仍然使用set
        if (!getProperties || initCount > 0){
            // 等待4个set全部完成
            if (setterCount != 4) return;
            // 如果先前已经创建了连接池, 用set的参数覆盖该连接池
            if (conns.size() != 0) conns.clear();
        }
        // 开始初始化连接池
        try {
            // 4. 注册驱动
            Class.forName(driver);
            // 5. 创建连接
            for (int i = 0; i < initPoolSize; i++) {
                // 5.1 获取连接
                Connection conn = DriverManager.getConnection(url, user, password);
//                // 5.2 是否开启事务
//                if (!autoCommit) conn.setAutoCommit(false);
                // 吧连接放入集合
                conns.add(conn);
            }
            // 连接池初始化计数, 方便后续用set时覆盖连接池
            initCount++;

            System.out.println("[INIT @MyDataSource] INIT POOL SUCCESS, POOL_SIZE: " + conns.size());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[ERR @MyDataSource] 初始化连接池失败");
        }
    }

    // 构造函数
    public MyDataSource() {
        // 先尝试读取配置文件
        try {
            // 1. Properties集合
            // Properties config = new Properties();

            // 2. 通过类加载加载properties集合, 获取输入对象
            InputStream resourceAsStream = MyDataSource.class.getClassLoader()
                    .getResourceAsStream("jdbc.properties");

            // 3. 把内容保存到集合中
            config.load(resourceAsStream);

            // 4. 把集合中的内容保存到数据库配置信息
            driver = config.getProperty("jdbc.driver");
            url = config.getProperty("jdbc.url");
            user = config.getProperty("jdbc.username");
            password = config.getProperty("jdbc.password");
            // 连接池初始化信息: 坑: 这里不能使用isEmpty或==null, 他找不到属性直接给你抛异常了
            try {
                initPoolSize = Integer.parseInt(config.getProperty("jdbc.initPoolSize"));
                dynamicAddSize = Integer.parseInt(config.getProperty("jdbc.dynamicAddSize"));
            } catch (Exception e) {
                System.out.println("[WARN] CONFIG 'INIT_POOL_SIZE' OR 'DYNAMIC_ADD_SIZE' NOT FOUND");
            }
            // 是否开启自动提交
            try {
                if (config.getProperty("jdbc.autoCommit").equals("false"))
                    autoCommit = false;
            } catch (Exception e) {
                System.out.println("[WARN] CONFIG 'AUTO_COMMIT' NOT FOUND, NORMAL AUTO_COMMIT");
            }
            getProperties = true;
            System.out.println("[INFO @MyDataSource] CONFIG LOADED");

            // 5. 直接初始化连接池
            InitPool();
        } catch (Exception e) {
            // 没有找到配置文件, 先完成构造方法, 等待后续set被调用再执行创建连接池
            System.out.println("[WARN]\"jdbc.properties\" NOT FOUND");
            System.out.println("[WARN] LOADING SETTER...");
        }
    }

    // 有参构造
    public MyDataSource(Properties prop) {
        // 3. 把内容保存到集合中
        config = prop;

        // 4. 把集合中的内容保存到数据库配置信息
        driver = config.getProperty("jdbc.driver");
        url = config.getProperty("jdbc.url");
        user = config.getProperty("jdbc.username");
        password = config.getProperty("jdbc.password");
        // 连接池初始化信息: 坑: 这里不能使用isEmpty或==null, 他找不到属性直接给你抛异常了
        try {
            initPoolSize = Integer.parseInt(config.getProperty("jdbc.initPoolSize"));
            dynamicAddSize = Integer.parseInt(config.getProperty("jdbc.dynamicAddSize"));
        } catch (Exception e) {
            System.out.println("[WARN] CONFIG 'INIT_POOL_SIZE' OR 'DYNAMIC_ADD_SIZE' NOT FOUND");
        }
//        // 是否开启自动提交
//        try {
//            if (config.getProperty("jdbc.autoCommit").equals("false"))
//                autoCommit = false;
//        } catch (Exception e) {
//            System.out.println("[WARN] CONFIG 'AUTO_COMMIT' NOT FOUND, NORMAL AUTO_COMMIT");
//        }
        getProperties = true;
        System.out.println("[INFO @MyDataSource] CONFIG LOADED");

        // 5. 直接初始化连接池
        InitPool();
    }

    // 获取连接
    @Override
    public Connection getConnection()  {
        // 移除并拿到这个链接
        Connection conn = conns.removeFirst();

        // 连接吃用尽了, 扩容
        if (conn == null) {
            System.out.println("[INFO @MyDataSource] CONNECTION POOL EMPTY, NOW ADDING...");
            try {
                for (int i = 0; i < dynamicAddSize; i++) {
                    // 获取连接
                    Connection newConn = DriverManager.getConnection(url, user, password);
                    // 吧连接放入集合
                    conns.add(newConn);
                    System.out.println("[INFO @MyDataSource] CONNECTION ADD SUCCESS, POOL_SIZE " + conns.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("[ERR @MyDataSource] CONNECTION ADD FAILED");
            }
        }

        System.out.println("[INFO @MyDataSource] CONNECTION RETRIEVE, POOL_SIZE: " + conns.size());
        return new MyConnection(conn);
    }

    // 包装设计模式(装饰设计模式)
    // 1. 编写一个类，实现与被增强对象相同的接口
    class MyConnection implements Connection {
        // 2. 定义一个变量, 记住被增强对象
        private Connection conn;

        // 3. 定义一个构造方法, 接收被增强对象
        public MyConnection(Connection conn) {
            this.conn = conn;
        }

        // 4. 覆盖想覆盖的方法
        @Override
        public void close() {
            conns.add(conn);
            System.out.println("[INFO @MyDataSource] CONNECTION RETURN, POOL_SIZE: "+ conns.size());

        }

        // 5.对于不想覆盖的方法，直接调用被增强对象的方法来执行
        @Override
        public Statement createStatement() throws SQLException {
            return conn.createStatement();
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return conn.prepareStatement(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            return conn.prepareCall(sql);
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            return conn.nativeSQL(sql);
        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            conn.setAutoCommit(autoCommit);
        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            return conn.getAutoCommit();
        }

        @Override
        public void commit() throws SQLException {
            conn.commit();
        }

        @Override
        public void rollback() throws SQLException {
            conn.rollback();
        }

        @Override
        public boolean isClosed() throws SQLException {
            return conn.isClosed();
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            return conn.getMetaData();
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            conn.setReadOnly(readOnly);
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            return conn.isReadOnly();
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            conn.setCatalog(catalog);
        }

        @Override
        public String getCatalog() throws SQLException {
            return conn.getCatalog();
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            conn.setTransactionIsolation(level);
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            return conn.getTransactionIsolation();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return conn.getWarnings();
        }

        @Override
        public void clearWarnings() throws SQLException {
            conn.clearWarnings();
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return conn.createStatement(resultSetType, resultSetConcurrency);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return conn.getTypeMap();
        }

        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            conn.setTypeMap(map);
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            conn.setHoldability(holdability);
        }

        @Override
        public int getHoldability() throws SQLException {
            return conn.getHoldability();
        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            return conn.setSavepoint();
        }

        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            return conn.setSavepoint(name);
        }

        @Override
        public void rollback(Savepoint savepoint) throws SQLException {
            conn.rollback(savepoint);
        }

        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            conn.releaseSavepoint(savepoint);
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return conn.prepareStatement(sql, autoGeneratedKeys);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return conn.prepareStatement(sql, columnIndexes);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return conn.prepareStatement(sql, columnNames);
        }

        @Override
        public Clob createClob() throws SQLException {
            return conn.createClob();
        }

        @Override
        public Blob createBlob() throws SQLException {
            return conn.createBlob();
        }

        @Override
        public NClob createNClob() throws SQLException {
            return conn.createNClob();
        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            return conn.createSQLXML();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            return conn.isValid(timeout);
        }

        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            conn.setClientInfo(name, value);
        }

        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            conn.setClientInfo(properties);
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            return conn.getClientInfo(name);
        }

        @Override
        public Properties getClientInfo() throws SQLException {
            return conn.getClientInfo();
        }

        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return conn.createArrayOf(typeName, elements);
        }

        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return conn.createStruct(typeName, attributes);
        }

        @Override
        public void setSchema(String schema) throws SQLException {
            conn.setSchema(schema);
        }

        @Override
        public String getSchema() throws SQLException {
            return conn.getSchema();
        }

        @Override
        public void abort(Executor executor) throws SQLException {
            conn.abort(executor);
        }

        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            conn.setNetworkTimeout(executor, milliseconds);
        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            return conn.getNetworkTimeout();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return conn.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return conn.isWrapperFor(iface);
        }

    }


    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public void setDriver(String driver) {
        this.driver = driver;
        setterCount++;
        InitPool();
    }

    public void setUrl(String url) {
        this.url = url;
        setterCount++;
        InitPool();
    }

    public void setUser(String user) {
        this.user = user;
        setterCount++;
        InitPool();
    }

    public void setPassword(String password) {
        this.password = password;
        setterCount++;
        InitPool();
    }

    public void setInitPoolSize(Integer initPoolSize) {
        this.initPoolSize = initPoolSize;
    }
}
