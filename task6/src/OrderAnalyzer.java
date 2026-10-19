import java.util.*;

public class OrderAnalyzer {
    private List<Order> orders=new ArrayList<>();

    public void add(Order order) {
        orders.add(order);
    }
    public void removeInvalidOrders() {
        Iterator<Order> iterator = orders.iterator();

        while (iterator.hasNext()) {
            Order order = iterator.next();

            if (order.getAmount() <= 0) {
                iterator.remove();
            }
        }
    }

    public Set<String> getAllUniqueTags() {
        Set<String> uniqueTags = new HashSet<>();
        //增强for循环，依次取出orders的每一个订单，将订单赋值给变量order
        for (Order order : orders) {
            uniqueTags.addAll(order.getTags());
        }

        return uniqueTags;
    }
    public Map<String, Double> getCustomerSpendMap() {
        Map<String, Double> customerSpendMap = new HashMap<>();

        for (Order order : orders) {
            String name = order.getCustomerName();
            double amount = order.getAmount();

            customerSpendMap.put(
                    name,
                    customerSpendMap.getOrDefault(name, 0.0) + amount
            );
        }

        return customerSpendMap;
    }

    public List<Order> sortOrdersByCustomerName() {
        List<Order> sortedOrders = new ArrayList<>(orders);
        //->表示把左边的参数交给右边的表达式处理
        sortedOrders.sort((o1, o2) ->
                o1.getCustomerName().compareTo(o2.getCustomerName())
        );
        return sortedOrders;
    }


}
