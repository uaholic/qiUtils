package com.guanyanqi;

/**
 * @author 关岩奇
 * @email admin@guanyanqi.com
 * @Description 声明那些被用于保存分片数据的Bean
 * @createTime 2020年07月19日 13:25:00
 */
public interface ShardingBean {
    /**
     * 获取分片key
     * @return
     */
    int getShardingKey();
}
