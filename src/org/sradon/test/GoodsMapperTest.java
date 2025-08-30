package org.sradon.test;

import org.sradon.mapper.GoodsMapper;
import org.sradon.myIbatis.session.DefaultSqlSession;
import org.sradon.myIbatis.session.SqlSessionManager;
import org.sradon.pojo.entity.Goods;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class GoodsMapperTest {
    DefaultSqlSession sqlSession;
    SqlSessionManager factory;
    @Before
    public void init(){
        factory = new SqlSessionManager("jdbc.properties");
        sqlSession = factory.openSession();
    }
    @Test
    public void testInsert() {

        GoodsMapper mapper = sqlSession.getMapper(GoodsMapper.class);
        int i = mapper.insert(new Goods(
                null,
                "OPPO",
                1998.0,
                9999,
                "goodxm"
        ));
        System.out.println(i);
    }

    @Test
    public void testInsertReturnId() {
        //DefaultSqlSession sqlSession = new DefaultSqlSession();
        GoodsMapper mapper = sqlSession.getMapper(GoodsMapper.class);
        Goods g = new Goods(
                null,
                "红米",
                9999.0,
                114514,
                "hm"
        );
        int i = mapper.insertReturnId(g);
        System.out.println(i + g.toString());
    }

//    @Test
//    public void testIs() {
//        int i = 1;
//        Integer j = 3;
//        System.out.println(
//                ReflectUtils.isBean(i)
//        );
//        System.out.println(
//                ReflectUtils.isBean(j)
//        );
//        System.out.println(
//                ReflectUtils.isBean(new Goods())
//        );
//    }

    @Test
    public void testDelete(){
        //DefaultSqlSession sqlSession = new DefaultSqlSession();
        GoodsMapper mapper = sqlSession.getMapper(GoodsMapper.class);
        System.out.println(
                mapper.delete(36)
        );
        System.out.println(
                mapper.deleteByIdAndStock(new Goods(
                        37,
                        null,
                        null,
                        114514,
                        null
                ))
        );
        System.out.println(
                mapper.deleteByIdAndStock(new Goods(
                        37,
                        null,
                        null,
                        114514, //
                        null
                ))
        );
    }
    @Test
    public void testDelByParamsAnnoAndMap() {
        //DefaultSqlSession sqlSession = new DefaultSqlSession();
        GoodsMapper mapper = sqlSession.getMapper(GoodsMapper.class);
        System.out.println(
                mapper.deleteByMapWithIdAndStock(
                        new HashMap<String, Object>() {{
                            put("id", 39);
                            put("stock", 114514);
                        }}
                )
        );
        System.out.println(
                mapper.deleteByParamsAnnoWithIdAndStock(
                        18, 114514
                )
        );
    }

    @Test
    public void testUpdate(){
        //DefaultSqlSession sqlSession = new DefaultSqlSession();
        GoodsMapper mapper = sqlSession.getMapper(GoodsMapper.class);
        System.out.println(
                mapper.update(new Goods(
                        35,
                        "OPO0_PHONE1",
                        2980.0,
                        333,
                        "xxxx"
                ))
        );
    }

    @Test
    public void testUpdateByParamsAnnoAndMap() {
        //DefaultSqlSession sqlSession = new DefaultSqlSession();
        GoodsMapper mapper = sqlSession.getMapper(GoodsMapper.class);
        Map map = new HashMap<String, Object>();
        map.put("id", 3);
        map.put("name", "OPO_PHONEX");
        map.put("price", 2980.0);
        map.put("stock", 333);
        map.put("description", "xxxx");
        System.out.println(
                mapper.updateByMap(map)
        );

        System.out.println(
                mapper.updateByParamsAnno(
                        4,
                        "OPO_PHONEX",
                        2980.0,
                        333,
                        "xxxx"
                )
        );
    }

    @Test
    public void testSelect() {
        //DefaultSqlSession sqlSession = new DefaultSqlSession();
        GoodsMapper mapper = sqlSession.getMapper(GoodsMapper.class);

        // 查全部 LIST
        for (Goods g : mapper.selectAll())
            System.out.println(g);

        // 根据ID查 bean
        System.out.println(
                mapper.selectById(4)
        );

        // 根据ID查
        System.out.println(
                mapper.selectById(
                        new Goods(5, null, null, null, null)
                )
        );

        // 根据价格查
        System.out.println(
                mapper.selectByPrice(
                        new Goods(null, null, 9999.0, null, null)
                )
        );

        // 无条件查部分字段
        System.out.println(
                mapper.selectPart()
        );

        // 有条件查部分字段
        System.out.println(
                mapper.selectPart(
                        new Goods(3, null, null, 9999, "xxx")
                )
        );

        // 命中缓存
        System.out.println(
                mapper.selectPart(
                        new Goods(3, null, null, 9999, "xxx")
                )
        );

        DefaultSqlSession sqlSession1 = factory.openSession();
        mapper = sqlSession1.getMapper(GoodsMapper.class);

        // 查询个数
        System.out.println(
                mapper.selectCount()
        );

        // 模糊查询
        System.out.println(
                mapper.selectLikeName("OPO")
        );
    }

    @Test
    public void testCaches2() {
        // 第一个session
        DefaultSqlSession sqlSession1 = factory.openSession();
        GoodsMapper mapper1 = sqlSession1.getMapper(GoodsMapper.class);
        System.out.println(
                mapper1.selectById(3)
        );
        System.out.println(
                mapper1.selectById(3)
        );
        // 第二个session
        DefaultSqlSession sqlSession2 = factory.openSession();
        GoodsMapper mapper2 = sqlSession1.getMapper(GoodsMapper.class);
        System.out.println(
                mapper2.selectById(3)
        );
        System.out.println(
                mapper2.selectById(3)
        );
        // 在第一个session中触发删缓存的操作
        System.out.println(
                mapper1.deleteByIdAndStock(new Goods(3, null, null, 9999, null))
        );
        // 再次查询
        System.out.println(
                mapper1.selectById(3)
        );
        System.out.println(
                mapper2.selectById(3)
        );

    }


}
