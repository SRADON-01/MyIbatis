package com.gxa.test;

import com.gxa.mapper.GoodsMapper;
import com.gxa.myIbatis.sqlSession.DefaultSqlSession;
import com.gxa.myIbatis.utils.ReflectUtils;
import com.gxa.pojo.entity.Goods;
import org.junit.Test;

public class GoodsMapperTest {
    @Test
    public void testInsert() {
        DefaultSqlSession sqlSession = new DefaultSqlSession();
        GoodsMapper mapper = sqlSession.getMapper(GoodsMapper.class);
        int i = mapper.insert(new Goods(
                null,
                "小米",
                1998.0,
                9999,
                "goodxm"
        ));
        System.out.println(i);
    }

    @Test
    public void testInsertReturnId() {
        DefaultSqlSession sqlSession = new DefaultSqlSession();
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

    @Test
    public void testIs() {
        int i = 1;
        Integer j = 3;
        System.out.println(
                ReflectUtils.isBean(i)
        );
        System.out.println(
                ReflectUtils.isBean(j)
        );
        System.out.println(
                ReflectUtils.isBean(new Goods())
        );
    }

    @Test
    public void testDelete(){
        DefaultSqlSession sqlSession = new DefaultSqlSession();
        GoodsMapper mapper = sqlSession.getMapper(GoodsMapper.class);
        System.out.println(
                mapper.delete(33)
        );
        System.out.println(
                mapper.deleteByIdAndStock(new Goods(
                        32,
                        null,
                        null,
                        114514,
                        null
                ))
        );
        System.out.println(
                mapper.deleteByIdAndStock(new Goods(
                        32,
                        null,
                        null,
                        114514, //
                        null
                ))
        );
    }

    @Test
    public void testUpdate(){
        DefaultSqlSession sqlSession = new DefaultSqlSession();
        GoodsMapper mapper = sqlSession.getMapper(GoodsMapper.class);
        System.out.println(
                mapper.update(new Goods(
                        35,
                        "OPO_PHONE",
                        2980.0,
                        333,
                        "xxxx"
                ))
        );
    }

    @Test
    public void testSelect() {
        DefaultSqlSession sqlSession = new DefaultSqlSession();
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
    }
}
