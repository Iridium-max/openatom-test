
import java.util.Comparator;
import java.util.List;

public class Order implements Comparable<Order> {

    // 订单号
    private String orderNo;

    // 客户姓名
    private String customerName;

    // 订单金额
    private double amount;

    // 商品标签
    private List<String> tags;

    @Override
    public int compareTo(Order other){
        //比较订单金额,compareTo方法：小于0，this在other前面
        if(this.amount>other.amount){
            return -1;
        }
        if (this.amount < other.amount) {
            return 1;
        }
        return this.orderNo.compareTo(other.orderNo);

    }
    public Order(String orderNo, String customerName, double amount, List<String> tags) {
        this.orderNo = orderNo;
        this.customerName = customerName;
        this.amount = amount;
        this.tags = tags;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
