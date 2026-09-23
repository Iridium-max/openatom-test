public class ProductCalculator {
    static void main(String[] args) {
        String name="机械键盘";
        double price=150.0;
        int stock=20;
        boolean promotion=false;

        int quantity=2;
        double totalPrice=price * quantity;
        System.out.println(totalPrice);
        boolean freeShipping = totalPrice >= 100 || promotion;
        System.out.println("是否享受包邮：" + freeShipping);
        System.out.println(
                "商品：" + name
                        + "，单价：" + price + "元"
                        + "，促销中：" + promotion
                        + "，购买" + quantity + "件"
                        + "，总价为：" + totalPrice + "元"
        );
        int a=10;int b=20;
        System.out.println("交换前：a="+a+", b="+b);
        //加减法交换法
        a=a+b;
        b=a-b;
        a=a-b;
        System.out.println("交换后：a="+a+", b="+b);



    }
}
