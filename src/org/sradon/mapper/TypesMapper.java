package org.sradon.mapper;

import org.sradon.myIbatis.anno.*;
import org.sradon.myIbatis.anno.*;
import org.sradon.pojo.entity.Types;

import java.util.List;
import java.util.Map;

public interface TypesMapper {
    ///  插入
    // 插入Bean
    @Insert("INSERT INTO tb_types(type_num,type_sort_desc,type_name)" +
            " VALUES(#{typeNum},#{typeSortDesc},#{typeName})")
    int insert(Types types);

    // 插入Bean返回ID
    @Insert(value = "INSERT INTO tb_types(type_num,type_sort_desc,type_name)" +
            " VALUES(#{typeNum},#{typeSortDesc},#{typeName})", returnInsertKey = true)
    int insertReturnId(Types types);

    // 插入Map
    @Insert(value = "INSERT INTO tb_types(type_num,type_sort_desc,type_name)" +
            " VALUES(#{typeNum},#{typeSortDesc},#{typeName})", returnInsertKey = true)
    int insertByMap(Map map);

    // 插入多个参数
    @Insert(value = "INSERT INTO tb_types(type_num,type_sort_desc,type_name)" +
            " VALUES(#{typeNum},#{typeSortDesc},#{typeName})", returnInsertKey = true)
    int insertByParamsAnno(@Param("typeNum") String typeNum,
                           @Param("typeSortDesc") int typeSortDesc,
                           @Param("typeName") String typeName);


    ///  删除
    // 根据ID删
    @Delete("DELETE FROM tb_types WHERE id = #{id}")
    int deleteById(Integer id);

    // 根据BEAN里面的ID和NUM删
    @Delete("DELETE FROM tb_types WHERE id = #{id} AND type_num = #{typeNum}")
    int deleteByIdAndNum(Types types);

    // 根据ID批量删除
    @Delete("DELETE FROM tb_types WHERE id IN (${ids})")
    int deleteByIds(List<Integer> list);

    ///  更新
    // 根据BEAN更新
    @Update("UPDATE tb_types " +
            "SET type_num = #{typeNum}, type_sort_desc = #{typeSortDesc}, type_name = #{typeName} " +
            "WHERE id = #{id}")
    int updateById(Types types);


    ///  查询
    // 查全部
    @Select("SELECT * FROM tb_types")
    List<Types> selectAll();

    // 根据ID查, 返回MAP
    @Select("SELECT * FROM tb_types WHERE id = #{id}")
    Map selectMapById(Integer id);

//    @Select("SELECT * FROM tb_types")
//    List<Map> selectListMap();

    // 根据bean的ID和NAME查, 返回MAP
    @Select("SELECT * FROM tb_types WHERE id = #{id} AND type_name = #{typeName}")
    Map selectMapByIdAndName(Types types);

    // 根据ID查, 返回String
    @Select("SELECT type_num FROM tb_types WHERE id = #{id}")
    String selectStringById(Integer id);

    // 根据ID查, 返回Integer
    @Select("SELECT type_sort_desc FROM tb_types WHERE id = #{id}")
    Integer selectIntegerById(Integer id);

    // 根据ID查, 返回Bean
    @Select("SELECT * FROM tb_types WHERE id = #{id}")
    Types selectTypesById(Integer id);

    // 根据Map查, 返回Map
    @Select("SELECT * FROM tb_types WHERE id = #{id} AND type_num = #{typeNum}")
    Map selectMapByMapWithIdAndNum(Map map);

    // 根据ParamAnno查, 返回部分字段到Map
    @Select("SELECT type_num, type_sort_desc FROM tb_types WHERE id = #{id} AND type_num = #{typeNum}")
    Map selectMapByParamAnnoWithIdAndNum(@Param("id") Integer id, @Param("typeNum") String typeNum);

}
