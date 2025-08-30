package org.sradon.test;

import org.sradon.mapper.TypesMapper;
import org.sradon.myIbatis.session.DefaultSqlSession;
import org.sradon.myIbatis.session.SqlSessionManager;
import org.sradon.pojo.entity.Types;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class TypesMapperTest {
    DefaultSqlSession sqlSession;
    SqlSessionManager factory;
    @Before
    public void init(){
        factory = new SqlSessionManager("jdbc.properties");
        sqlSession = factory.openSession();
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
        // sqlSession.commit();
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
                        15L, "Y007", 1000, "1xx"
                ))
        );
        // sqlSession.commit();
    }

    @Test
    public void testDelete() {
        // SqlSession sqlSession = new DefaultSqlSession();
        // sqlSession.beginTransaction();
        TypesMapper mapper = sqlSession.getMapper(TypesMapper.class);
        System.out.println(
                mapper.deleteById(177)
        );
        System.out.println(
                mapper.deleteByIdAndNum(new Types(
                        34L, "1", null, null
                ))
        );
        System.out.println(
                mapper.deleteByIds(
                        new ArrayList<Integer>() {{
                            add(28);
                            add(29);
                            add(32);
                            add(33);
                        }}
                )
        );
        // sqlSession.commit();
    }

    @Test
    public void testSelectMap() {
        // SqlSession sqlSession = new DefaultSqlSession();
        TypesMapper mapper = sqlSession.getMapper(TypesMapper.class);
        System.out.println(
                mapper.selectMapById(
                        64
                )
        );
        System.out.println(
                mapper.selectMapByIdAndName(
                        new Types(
                                65L, null, null, "xxx"
                        )
                )
        );
        System.out.println(
                mapper.selectTypesById(66)
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
                            new Types(null, "X0040", 58, "Mouse")
                    )
            );
            // int i = 1 / 0;
            System.out.println(
                    mapper.insert(
                            new Types(null, "X0050", 59, "Mouse")
                    )
            );
            // sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
        }
        // sqlSession.close();


    }

}
