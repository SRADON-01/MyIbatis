package com.gxa.test;

import com.gxa.mapper.TypesMapper;
import com.gxa.myIbatis.sqlSession.DefaultSqlSession;
import com.gxa.myIbatis.sqlSession.SqlSession;
import com.gxa.pojo.entity.Types;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TypesMapperTest {
    private DefaultSqlSession sqlSession;
    @Before
    public void init() {
        sqlSession = new DefaultSqlSession();
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
                mapper.deleteById(30)
        );
        System.out.println(
                mapper.deleteByIdAndNum(new Types(
                        31L, "2", null, null
                ))
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
        System.out.println(
                m2.selectAll()
        );
    }

    @Test
    public void testParamAnnoANDMap(){
        TypesMapper mapper = sqlSession.getMapper(TypesMapper.class);
//        System.out.println(
//                mapper.insertByMap(
//                        new HashMap<String, Object>() {{
//                            put("typeNum", "X007");
//                            put("typeSortDesc", 67);
//                            put("typeName", "Mouse2");
//                        }}
//                )
//        );
//        System.out.println(
//                mapper.insertByMap(
//                        new HashMap<String, Object>() {{
//                            put("typeNum", "X008");
//                            put("typeSortDesc", 68);
//                            put("typeName", "Mouse3");
//                        }}
//                )
//        );
        System.out.println(
                mapper.insertByParamsAnno(
                        "X010", 79, "Mouse4"
                )
        );
    }

    @Test
    public void test(){
        Map m = new HashMap();
        Object x = m.get("a");
        System.out.println(
                x
        );
        m.put("b", x);
    }


}
