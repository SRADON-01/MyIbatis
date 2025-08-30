package org.sradon.mapper;

import org.sradon.myIbatis.anno.*;
import org.sradon.myIbatis.anno.*;
import org.sradon.pojo.entity.Goods;

import java.util.List;
import java.util.Map;

public interface GoodsMapper {

    ///  插入
    /**
     * 插入一个Bean
     * @param goods Bean
     * @return 受影响行数
     */
    @Insert(value = "INSERT INTO tb_goods(name, price, stock, description)" +
            "VALUES(#{name}, #{price}, #{stock}, #{description})")
    int insert(Goods goods);

    /**
     * 插入一个Bean并返回主键
     * @param goods Bean
     * @return 受影响行数
     */
    @Insert(value = "INSERT INTO tb_goods(name, price, stock, description)" +
            "VALUES(#{name}, #{price}, #{stock}, #{description})", returnInsertKey = true)
    int insertReturnId(Goods goods);

    /**
     * 插入一个Map
     * @param map
     * @return
     */
    @Update("UPDATE tb_goods " +
            "SET name = #{name}, price = #{price}, stock = #{stock}, description = #{description} " +
            "WHERE id = #{id}")
    int updateByMap(Map map);

    /**
     * 插入多个简单数据类型
     * @return
     */
    @Update("UPDATE tb_goods " +
            "SET name = #{name}, price = #{price}, stock = #{stock}, description = #{description} " +
            "WHERE id = #{id}")
    int updateByParamsAnno(@Param("id") Integer id,
                           @Param("name") String name,
                           @Param("price") Double price,
                           @Param("stock") Integer stock,
                           @Param("description") String description
                           );


    ///  删除
    /**
     * 根据ID删除
     * @param id 单个简单类型的参数
     * @return 受影响行数
     */
    @Delete("DELETE FROM tb_goods WHERE id = #{id}")
    int delete(Integer id);

    /**
     * 根据Bean的属性删除
     * @param goods Bean (SQL语句中有多个条件, 在Bean中读取这些属性)
     * @return 受影响行数
     */
    @Delete("DELETE FROM tb_goods WHERE id = #{id} AND stock = #{stock}")
    int deleteByIdAndStock(Goods goods);

    /**
     * 根据Map中的的属性删除
     * @param map
     * @return
     */
    @Delete("DELETE FROM tb_goods WHERE id = #{id} AND stock = #{stock}")
    int deleteByMapWithIdAndStock(Map map);

    /**
     * 根据多个参数删除
     * @param id
     * @param stock
     * @return
     */
    @Delete("DELETE FROM tb_goods WHERE id = #{id} AND stock = #{stock}")
    int deleteByParamsAnnoWithIdAndStock(@Param("id") Integer id, @Param("stock") Integer stock);


    ///  更新
    /**
     * 根据ID更新
     * @param goods Bean (SQL语句中有多个条件, 在Bean中读取这些属性)
     * @return
     */
    @Update("UPDATE tb_goods " +
            "SET name = #{name}, price = #{price}, stock = #{stock}, description = #{description} " +
            "WHERE id = #{id}")
    int update(Goods goods);


    /**
     * 根据ID查询
     * @param id 单个简单类型的参数
     * @return 单个Bean
     */
    @Select("SELECT * FROM tb_goods WHERE id = #{id}")
    Goods selectById(Integer id);


    /**
     * 根据Bean的属性查询
     * @param goods Bean (SQL语句中有多个条件, 在Bean中读取这些属性)
     * @return 单个Bean
     */
    @Select("SELECT * FROM tb_goods WHERE id = #{id}")
    Goods selectById(Goods goods);


    /**
     * 根据Bean的属性查询
     * @param goods
     * @return 多个Bean
     */
    @Select("SELECT * FROM tb_goods WHERE price = #{price}")
    List<Goods> selectByPrice(Goods goods);

    /**
     * 查询所有
     * @return 多个Bean
     */
    @Select("SELECT * FROM tb_goods")
    List<Goods> selectAll();

    /**
     * 查询部分
     * @return 多个Bean
     */
    @Select("SELECT name, price FROM tb_goods")
    List<Goods> selectPart();

    /**
     * 查询部分
     * @param goods
     * @return 单个Bean
     */
    @Select("SELECT name, price FROM tb_goods WHERE id = #{id} AND stock = #{stock}")
    Goods selectPart(Goods goods);

    /**
     * 查询总数
     */
    @Select("SELECT COUNT(*) FROM tb_goods")
    Long selectCount();

    /**
     * 模糊查询
     */
    @Select("SELECT * FROM tb_goods WHERE name LIKE CONCAT('%',#{value},'%')")
    List<Goods> selectLikeName(String value);


}
