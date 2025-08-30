# MyIbatis：一个轻量级的MyBatis框架实现

## 项目简介

`MyIbatis` 是一个基于 Java 的轻量级 ORM（对象关系映射）框架，灵感来源于 MyBatis。该项目旨在通过简化的设计和清晰的架构，帮助开发者理解 MyBatis 的核心工作原理，包括 SQL 解析、参数处理、结果映射、事务管理以及连接池等关键功能。

本项目完全使用 Java 编写，不依赖任何第三方 ORM 框架，仅使用了 `Apache Commons BeanUtils` 来辅助属性拷贝。它支持常见的数据库操作（CRUD），并提供了注解驱动的开发方式，使得数据库操作更加简洁高效。

---

## 核心功能

### 1. 注解驱动的 SQL 映射
- 使用自定义注解 [@Select](file://D:\WorkStation\IDEA\IdeaProject\MyIbatis\src\org\sradon\myIbatis\anno\Select.java#L7-L11), [@Insert](file://D:\WorkStation\IDEA\IdeaProject\MyIbatis\src\org\sradon\myIbatis\anno\Insert.java#L7-L12), [@Update](file://D:\WorkStation\IDEA\IdeaProject\MyIbatis\src\org\sradon\myIbatis\anno\Update.java#L7-L11), [@Delete](file://D:\WorkStation\IDEA\IdeaProject\MyIbatis\src\org\sradon\myIbatis\anno\Delete.java#L7-L11) 直接在接口方法上定义 SQL。
- 支持动态参数绑定（如 `#{name}`）。
- 支持多种参数类型：
    - 单个简单类型（如 `Integer id`）
    - 实体类（如 `Goods goods`）
    - Map 类型（如 `Map<String, Object> map`）
    - 多个参数（需配合 [@Param](file://D:\WorkStation\IDEA\IdeaProject\MyIbatis\src\org\sradon\myIbatis\anno\Param.java#L7-L11) 注解）

```java
@Select("SELECT * FROM tb_goods WHERE id = #{id}")
Goods selectById(Integer id);

@Update("UPDATE tb_goods SET name = #{name} WHERE id = #{id}")
int update(Goods goods);
```


### 2. SQL 解析与参数处理
- **SqlParser** 负责解析 SQL 中的 `#{}` 占位符，并将其替换为 `?`，同时提取参数列表。
- 支持从实体类、Map 或多个参数中提取字段值。
- 自动将驼峰命名转换为下划线命名（如 `userName` → `user_name`），适配数据库字段名。

### 3. 结果集映射
- **ResultParser** 将数据库返回的结果集映射到 Java 对象中。
- 支持多种返回类型：
    - 单个实体对象（`Bean`）
    - 列表（`List<Bean>`）
    - Map（`Map<String, Object>`）
    - 基本数据类型（如 `Long`, `String`）
- 支持自增主键的自动回填。

### 4. 事务管理
- 提供 `beginTransaction()`, `commit()`, `rollback()` 方法控制事务。
- 支持手动提交或自动提交模式。

### 5. 连接池
- 内置简易连接池 `MyDataSource`，支持初始化连接数和动态扩容。
- 避免频繁创建和关闭数据库连接，提升性能。

### 6. 缓存机制
- **一级缓存**：每个 `SqlSession` 内部维护一个 `ConcurrentHashMap`，缓存 SQL + 参数组合的结果。
- **二级缓存**：全局共享的 `SecondCaches`，支持根据表名清除相关缓存，避免脏读。

---

## 架构设计

以下是项目的整体架构流程图：

```plaintext
+-------------------+
|   Config          |
+--------+----------+
         |
         | 读取配置
         v
+--------v----------+
| SqlSessionFactory |
+--------+----------+
         |
         | 加载jdbc.properties
         v
+--------v----------+
|   MyDataSource    |
+--------+----------+
         |
         | 获取连接
         v
+--------v----------+
|   SqlSession      |
+--------+----------+
         |
         | openSession()
         v
+--------v----------+
|   SqlParser       |
+--------+----------+
         |
         | 解析SQL & 参数
         v
+--------v----------+
| ExecuteParser     |
+--------+----------+
         |
         | 执行SQL
         v
+--------v----------+
| ResultParser      |
+--------+----------+
         |
         | 映射结果
         v
+--------v----------+
| 返回结果          |
+-------------------+
```


### 关键组件说明

| 组件 | 功能 |
|------|------|
| `SqlSessionManager` | 工厂类，负责初始化配置、连接池和创建 `SqlSession` 实例。 |
| `DefaultSqlSession` | 核心会话类，提供 CRUD 操作、事务控制和代理对象生成。 |
| `SqlParser` | 解析 SQL 和参数，生成可执行的预编译语句。 |
| `ExecuteParser` | 设置参数并执行 SQL，返回结果集。 |
| `ResultParser` | 将结果集映射为 Java 对象。 |
| `MyDataSource` | 简易连接池，管理数据库连接。 |
| `SecondCaches` | 全局二级缓存，支持按表名清理缓存。 |

---

## 使用示例

### 1. 配置文件 (`jdbc.properties`)
```properties
jdbc.url=jdbc:mysql://localhost:3306/cow_mall?characterEncoding=UTF-8
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.username=root
jdbc.password=123456
jdbc.initPoolSize=5
jdbc.dynamicAddSize=5
ibatis.mapUnderscoreToCamelCase=true
ibatis.cacheEnabled=true
```


### 2. Mapper 接口
```java
public interface GoodsMapper {
    @Insert("INSERT INTO tb_goods(name, price, stock, description) VALUES(#{name}, #{price}, #{stock}, #{description})")
    int insert(Goods goods);

    @Select("SELECT * FROM tb_goods WHERE id = #{id}")
    Goods selectById(Integer id);
}
```


### 3. 测试代码
```java
@Test
public void testInsert() {
    SqlSessionManager factory = new SqlSessionManager("jdbc.properties");
    DefaultSqlSession sqlSession = factory.openSession();
    GoodsMapper mapper = sqlSession.getMapper(GoodsMapper.class);

    Goods goods = new Goods(null, "iPhone", 9999.0, 100, "描述");
    int result = mapper.insert(goods);
    System.out.println("插入成功，影响行数：" + result);

    sqlSession.commit();
    sqlSession.close();
}
```


---

## 特性亮点

- ✅ **轻量级**：无外部依赖，仅需 JDBC 和 Apache Commons BeanUtils。
- ✅ **易扩展**：模块化设计，便于添加新功能（如 XML 配置支持）。
- ✅ **高性能**：内置连接池和两级缓存，减少数据库访问开销。
- ✅ **学习价值高**：适合初学者深入理解 MyBatis 的底层实现机制。

---

## 未来计划

- ✅ 添加 XML 配置支持（类似 MyBatis 的 `Mapper.xml`）
- ✅ 支持批量操作（如批量插入/更新）
- ✅ 引入更完善的日志系统
- ✅ 提供 Spring Boot 集成支持

---

## 总结

`MyIbatis` 不仅仅是一个简单的数据库访问工具，更是一个优秀的学习项目。它展示了如何从零开始构建一个 ORM 框架，涵盖从配置加载、连接管理、SQL 解析到结果映射的完整流程。无论是想了解 MyBatis 内部原理，还是希望快速搭建一个轻量级的数据访问层，这个项目都值得一试！

> 🌐 项目地址：[GitHub](https://github.com/yourusername/MyIbatis)  
> 💬 欢迎 Star & Fork，一起完善这个小而美的框架！