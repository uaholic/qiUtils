import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.guanyanqi.ShardingBean;
import com.guanyanqi.utils.ShardingGroupUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ShardingGroupUtil 单测
 *
 * @author 关岩奇
 * @email admin@guanyanqi.com
 * @Description 该单测展示了 ShardingGroupUtil 的一种使用场景，即订单数据被存储于4个分片中，分片的计算逻辑是 用户id % 4
 *              ，现在有9个订单需要查询，他们的用户id 从1-9 所以这9个订单需要从4个分片中查询。以下两种方案都可以解决这个问题：
 *                  方案一：可以遍历订单列表中的每一笔订单，计算存储位置，查询订单金额最后合并结果。
 *                  方案二：可以将9个订单分为四组，每组查询并返回该组订单的查询结果，最后合并4组结果。
 *              如果使用方案二，那使用 ShardingGroupUtil 可以达到很好的辅助效果，只需要实现分组key计算逻辑，以及同一组数据的
 *              批量查询方法即可，无需关注集合分组、循环、合并等操作。
 *              需要注意的是，按照该单测设计的使用场景，使用方案一效率会更高，因为从map中获取多条数据也是遍历每一条获取并返回结果
 *              ，方案二非但没有减少循环次数反而增加了许多分组合并的操作。但是设想一下，如果数据存储在远程服务例如 mysql、es 中
 *              ，方案二可以大大减少网络传输成本从而提高效率。该单测仅仅为了说明一种解决问题的思路，以及 ShardingGroupUtil 工具
 *              的使用方法。ShardingGroupUtil 是否真正适用，需要根据具体的场景具体分析。
 * @createTime 2020年07月19日 13:28:00
 */
public class ShardingGroupUtilTest {
    private List<Map<Long, Long>> orderAmountMaps = Lists.newArrayList();

    /**
     * 初始化订单存储数据
     */
    @Before
    public void initData() {
        Map<Long, Long> orderAmountMap0 = ImmutableMap.<Long, Long>builder()
                .put(4L, 400L)
                .put(8L, 800L)
                .build();

        Map<Long, Long> orderAmountMap1 = ImmutableMap.<Long, Long>builder()
                .put(1L, 100L)
                .put(5L, 500L)
                .put(9L, 900L)
                .build();

        Map<Long, Long> orderAmountMap2 = ImmutableMap.<Long, Long>builder()
                .put(2L, 200L)
                .put(6L, 600L)
                .build();

        Map<Long, Long> orderAmountMap3 = ImmutableMap.<Long, Long>builder()
                .put(3L, 300L)
                .put(7L, 700L)
                .build();

        orderAmountMaps.add(orderAmountMap0);
        orderAmountMaps.add(orderAmountMap1);
        orderAmountMaps.add(orderAmountMap2);
        orderAmountMaps.add(orderAmountMap3);
    }

    @Test
    public void execute() {
        List<Req> reqs = Lists.newArrayList();

        // 初始化查询请求参数
        for (long i = 1; i < 10; i++) {
            Req req = new Req();
            req.setUserId(i);
            req.setOrderId(i);
            reqs.add(req);
        }

        // 使用ShardingGroupUtil辅助分组查询
        List<Resp> respList = ShardingGroupUtil
                .executeEachGroup(reqs, (param, partId) -> getAmountByReq(Lists.newArrayList(param), partId));

        // 校验查询结果是否符合预期
        respList.forEach(r -> Assert.assertEquals(r.getAmount(), r.getOrderId() * 100));

        // 将返回的查询结果排序
        List<Resp> sortedRespList =
                respList.stream().sorted((a, b) -> (int) (a.getOrderId() - b.getOrderId())).collect(Collectors.toList());

        // 输出排序后的查询结果
        sortedRespList.forEach(System.out::println);

    }

    /**
     * 单个分片批量查询订单信息
     * @param reqs 订单列表
     * @param partId 分片号
     * @return
     */
    private List<Resp> getAmountByReq(List<Req> reqs, int partId) {
        List<Resp> result = Lists.newArrayList();
        for (Req req : reqs) {
            Resp resp = new Resp();
            resp.setAmount(orderAmountMaps.get(partId).get(req.getUserId()));
            resp.setOrderId(req.getOrderId());
            result.add(resp);
        }
        return result;
    }

    /**
     * 封装请求入参
     */
    private static class Req implements ShardingBean {

        private long userId;

        private long orderId;

        public long getOrderId() {
            return orderId;
        }

        public void setOrderId(long orderId) {
            this.orderId = orderId;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        @Override
        public int getShardingKey() {
            return (int) (userId % 4);
        }
    }

    /**
     * 封装返回的查询结果
     */
    private static class Resp {
        private long orderId;
        private long amount;

        public long getOrderId() {
            return orderId;
        }

        public void setOrderId(long orderId) {
            this.orderId = orderId;
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "Resp{" +
                    "orderId=" + orderId +
                    ", amount=" + amount +
                    '}';
        }
    }

}
