package com.gxa.test;

import com.gxa.mapper.GoodsMapper;
import com.gxa.mapper.TypesMapper;
import com.gxa.myIbatis.anno.Param;
import com.gxa.myIbatis.sqlSession.DefaultSqlSession;
import com.gxa.myIbatis.sqlSession.SqlSession;
import com.gxa.myIbatis.sqlSession.SqlSessionFactory;
import com.gxa.pojo.entity.Types;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TypesMapperTest {
    DefaultSqlSession sqlSession;
    SqlSessionFactory factory;
    @Before
    public void init(){
        factory = new SqlSessionFactory("jdbc.properties");
        sqlSession = factory.openSession(false);
    }

    @Test
    public void testInsertTypes() {
        // DefaultSqlSession sqlSession = new DefaultSqlSession();
        TypesMapper mapper = sqlSession.getMapper(TypesMapper.class);
        System.out.println(
                mapper.insert(
                        new Types(null, "X003", 57, "SHOUJI")
                )
        );
        Types types = new Types(null, "X004", 58, "键盘");
        System.out.println(
                mapper.insertReturnId(types) + types.toString()
        );
    }

    @Test
    public void testSelectAllTypes() {
        // SqlSession sqlSession = new DefaultSqlSession();
        TypesMapper mapper = sqlSession.getMapper(TypesMapper.class);
        for (Types types : mapper.selectAll()) {
            System.out.println(types);
        }
    }

    @Test
    public void testUpdateById() {
        // SqlSession sqlSession = new DefaultSqlSession();
        TypesMapper mapper = sqlSession.getMapper(TypesMapper.class);
        System.out.println(
                mapper.updateById(new Types(
                        15L, "10000", 100, "1xx"
                ))
        );
    }

    @Test
    public void testDelete() {
        // SqlSession sqlSession = new DefaultSqlSession();
        TypesMapper mapper = sqlSession.getMapper(TypesMapper.class);
        System.out.println(
                mapper.deleteById(36)
        );
        System.out.println(
                mapper.deleteByIdAndNum(new Types(
                        37L, "2", null, null
                ))
        );
        System.out.println(
                mapper.deleteByIds(
                        new ArrayList<Integer>() {{
                            add(60);
                            add(61);
                            add(62);
                            add(63);
                        }}
                )
        );
    }

    @Test
    public void testSelectMap() {
        // SqlSession sqlSession = new DefaultSqlSession();
        TypesMapper mapper = sqlSession.getMapper(TypesMapper.class);
        System.out.println(
                mapper.selectMapById(
                        60
                )
        );
        System.out.println(
                mapper.selectMapByIdAndName(
                        new Types(
                                61L, null, null, "xxx"
                        )
                )
        );
        System.out.println(
                mapper.selectTypesById(62)
        );
//        System.out.println(
//                mapper.selectListMap()
//
//        );
    }

    @Test
    public void testPoolsAndReturnString(){
        TypesMapper m1 = sqlSession.getMapper(TypesMapper.class);
        System.out.println(
                m1.selectStringById(64)
        );
        TypesMapper m3 = sqlSession.getMapper(TypesMapper.class);
        System.out.println(
                m3.selectIntegerById(65)
        );
        TypesMapper m2 = sqlSession.getMapper(TypesMapper.class);
        for (Types types : m2.selectAll()) {
            System.out.println(types);
        }
    }

    @Test
    public void testParamAnnoANDMap(){
        TypesMapper mapper = sqlSession.getMapper(TypesMapper.class);
        System.out.println(
                mapper.insertByMap(
                        new HashMap<String, Object>() {{
                            put("typeNum", "X011");
                            put("typeSortDesc", 167);
                            put("typeName", "Mouse167");
                        }}
                )
        );
        System.out.println(
                mapper.insertByMap(
                        new HashMap<String, Object>() {{
                            put("typeNum", "X012");
                            put("typeSortDesc", 168);
                            put("typeName", "Mouse3168");
                        }}
                )
        );
        System.out.println(
                mapper.insertByParamsAnno(
                        "X013", 79, "Mouse4"
                )
        );

    }

    @Test
    public void testSelectByParamsAnnoAndMap() {
        //DefaultSqlSession sqlSession = new DefaultSqlSession();
        TypesMapper mapper = sqlSession.getMapper(TypesMapper.class);
        System.out.println(
                mapper.selectMapByParamAnnoWithIdAndNum(
                        71, "X001"
                )
        );
        System.out.println(
                mapper.selectMapByMapWithIdAndNum(
                        new HashMap<String, Object>() {{
                            put("id", 72);
                            put("typeNum", "X002");
                        }}
                )
        );
    }

    @Test
    public void testTrans() {
        TypesMapper mapper = sqlSession.getMapper(TypesMapper.class);
        // sqlSession.beginTransaction();
        try {
            System.out.println(
                    mapper.insert(
                            new Types(null, "X004", 58, "Mouse")
                    )
            );
            // int i = 1 / 0;
            System.out.println(
                    mapper.insert(
                            new Types(null, "X005", 59, "Mouse")
                    )
            );
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
        }
        // sqlSession.close();


    }

}
