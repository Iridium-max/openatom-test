public class Product {
    private String id;
    private String name;
    private double price;
    private static int totalProductCount=0;

    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        totalProductCount++;
    }
    public Product(){
        totalProductCount++;
    }
    public static int getTotalProductCount()
    {
        return totalProductCount;
    }
    public void getInfor()
    {
        System.out.println("商品信息：["+id+"] "+name+", 价格: "+price+"元");
    }

    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id=id;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name=name;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
