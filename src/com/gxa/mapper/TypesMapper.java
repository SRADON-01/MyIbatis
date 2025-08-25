package com.gxa.mapper;

import com.gxa.myIbatis.anno.*;
import com.gxa.pojo.entity.Types;

import java.util.List;
import java.util.Map;

public interface TypesMapper {
    @Insert("INSERT INTO tb_types(type_num,type_sort_desc,type_name) VALUES(#{typeNum},#{typeSortDesc},#{typeName})")
    int insert(Types types);

    @Insert(value = "INSERT INTO tb_types(type_num,type_sort_desc,type_name) VALUES(#{typeNum},#{typeSortDesc},#{typeName})", returnInsertKey = true)
    int insertReturnId(Types types);

    @Insert(value = "INSERT INTO tb_types(type_num,type_sort_desc,type_name) VALUES(#{typeNum},#{typeSortDesc},#{typeName})", returnInsertKey = true)
    int insertByMap(Map map);

    @Insert(value = "INSERT INTO tb_types(type_num,type_sort_desc,type_name) VALUES(#{typeNum},#{typeSortDesc},#{typeName})", returnInsertKey = true)
    int insertByParamsAnno( String typeNum,  int typeSortDesc,  String typeName);

    @Select("SELECT * FROM tb_types")
    List<Types> selectAll();

    @Delete("DELETE FROM tb_types WHERE id = #{id}")
    int deleteById(Integer id);

    @Delete("DELETE FROM tb_types WHERE id = #{id} AND type_num = #{typeNum}")
    int deleteByIdAndNum(Types types);

    @Update("UPDATE tb_types " +
            "SET type_num = #{typeNum}, type_sort_desc = #{typeSortDesc}, type_name = #{typeName} " +
            "WHERE id = #{id}")
    int updateById(Types types);

    @Select("SELECT * FROM tb_types WHERE id = #{id}")
    Map selectMapById(Integer id);

//    @Select("SELECT * FROM tb_types")
//    List<Map> selectListMap();

    @Select("SELECT * FROM tb_types WHERE id = #{id} AND type_name = #{typeName}")
    Map selectMapByIdAndName(Types types);

    @Select("SELECT type_num FROM tb_types WHERE id = #{id}")
    String selectStringById(Integer id);

    @Select("SELECT type_sort_desc FROM tb_types WHERE id = #{id}")
    Integer selectIntegerById(Integer id);

    @Select("SELECT * FROM tb_types WHERE id = #{id}")
    Types selectTypesById(Integer id);
}
