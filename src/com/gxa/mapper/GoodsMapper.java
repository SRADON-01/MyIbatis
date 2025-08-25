package com.gxa.mapper;

import com.gxa.myIbatis.anno.Delete;
import com.gxa.myIbatis.anno.Insert;
import com.gxa.myIbatis.anno.Select;
import com.gxa.myIbatis.anno.Update;
import com.gxa.pojo.entity.Goods;

import java.util.List;

public interface GoodsMapper {

    /**
     * 插入一个Bean
     * @param goods Bean
     * @return 受影响行数
     */
    @Insert(value = "INSERT INTO tb_goods(name, price, stock, description)" +
            "VALUES(#{name}, #{price}, #{stock}, #{description})")
    int insert(Goods goods);

    /**
     * 插入一个Bean
     * @param goods Bean
     * @return 受影响行数
     */
    @Insert(value = "INSERT INTO tb_goods(name, price, stock, description)" +
            "VALUES(#{name}, #{price}, #{stock}, #{description})", returnInsertKey = true)
    int insertReturnId(Goods goods);


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

}
