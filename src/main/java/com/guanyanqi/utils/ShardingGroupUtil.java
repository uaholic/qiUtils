package com.guanyanqi.utils;

import com.guanyanqi.ShardingBean;
import com.guanyanqi.executor.PartExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 关岩奇
 * @email admin@guanyanqi.com
 * @Description 用于对分片数据做分组相关操作
 * @createTime 2020年07月19日 13:27:00
 */
public class ShardingGroupUtil {

    /**
     * 将分片数据按分片key分组后依次执行每一组操作并将结果合并返回。
     * @param collection 分片数据列表
     * @param executor 单组操作的执行器
     * @param <P> 入参数据集元素的类型
     * @param <R> 返回数据集元素的类型
     * @return 将每一个分组执行完的结果合并后的数据
     */
    public static <P extends ShardingBean, R> List<R> executeEachGroup(Collection<P> collection,
                                                                       PartExecutor<P, R> executor) {
        List<R> result = new ArrayList<>();

        Map<Integer, ? extends Collection<P>> groupByShardingKey =
                collection.stream().collect(Collectors.groupingBy(ShardingBean::getShardingKey));

        groupByShardingKey.forEach((k, v) -> result.addAll(executor.executorPart(v,k)));

        return result;
    }
}
