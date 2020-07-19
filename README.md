# 自己做的小工具

## 1.ShardingGroupUtil

>  可以很方便的对数据分组，对每一组执行指定操作，并将返回结果合并。

在分库分表的场景下批量查询数据时，非常适用。具体可参考 ShardingGroupUtilTest 中的示例。
##### <span style="color:red;"> 注意：由于代码中大量使用了lambda表达式，所以该工具类需要在java8以上版本使用。 </span> 