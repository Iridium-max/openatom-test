
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class task6 {
    public static void main(String[] args) {

        OrderAnalyzer analyzer = new OrderAnalyzer();

        // 创建五笔订单
        Order order1 = new Order(
                "O001", "Alice", 150.0, List.of("电子", "数码")
        );
        Order order2 = new Order(
                "O002", "Bob", -50.0, List.of("服装")
        );
        Order order3 = new Order(
                "O003", "Alice", 200.0, List.of("图书", "电子")
        );
        Order order4 = new Order(
                "O004", "Charlie", 200.0, List.of("生鲜")
        );
        Order order5 = new Order(
                "O005", "Bob", 300.0, List.of("服装", "户外")
        );

        // 添加订单
        analyzer.add(order1);
        analyzer.add(order2);
        analyzer.add(order3);
        analyzer.add(order4);
        analyzer.add(order5);

        // 1. 测试商品标签去重
        System.out.println("所有商品标签：");
        System.out.println(analyzer.getAllUniqueTags());

        // 2. 测试客户消费统计
        System.out.println("客户消费统计：");
        System.out.println(analyzer.getCustomerSpendMap());

        // 3. 测试按客户姓名排序
        System.out.println("按客户姓名排序：");
        for (Order order : analyzer.sortOrdersByCustomerName()) {
            System.out.println(
                    order.getOrderNo() + " "
                            + order.getCustomerName() + " "
                            + order.getAmount()
            );
        }

        // 4. 测试默认排序规则
        List<Order> amountSortedOrders = new ArrayList<>(
                List.of(order1, order2, order3, order4, order5)
        );

        Collections.sort(amountSortedOrders);

        System.out.println("按订单金额降序排序：");
        for (Order order : amountSortedOrders) {
            System.out.println(
                    order.getOrderNo() + " " + order.getAmount()
            );
        }

        // 5. 删除无效订单
        analyzer.removeInvalidOrders();

        System.out.println("删除无效订单后的消费统计：");
        System.out.println(analyzer.getCustomerSpendMap());
    }
}
