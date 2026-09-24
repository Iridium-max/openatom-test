public class Main {
    public static void main(String[] args) {
        Product p1 = new Product("P001", "机械键盘", 450.0);
        Product p2=new Product();
        p2.setId("P002");
        p2.setName("iphone18promax");
        p2.setPrice(11999.0);
        p1.getInfor();
        p2.getInfor();
        System.out.println("系统当前共创建了 " + Product.getTotalProductCount() + " 个商品实例。");
    }
}
