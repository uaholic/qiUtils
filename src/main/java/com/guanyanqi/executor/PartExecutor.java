package com.guanyanqi.executor;

import java.util.Collection;

/**
 * @author 关岩奇
 * @email admin@guanyanqi.com
 * @Description 单组操作的执行器，用于声明每一组数据，如何去执行。可以使用函数式的方式。
 * @createTime 2020年07月19日 13:27:00
 */
@FunctionalInterface
public interface PartExecutor<P, R> {

    Collection<R> executorPart(Collection<P> param, int partId);
}