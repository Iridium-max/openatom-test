public class Main2 {
    public static void main(String[] args) {
        StorageBox<Product> productBox=new StorageBox<>();
        Product product = new Product("P001", "机械键盘", 299.0);
        productBox.storeItem(product);
        Product takenProduct = productBox.retrieveItem();
        System.out.println("📦商品储物箱存取测试：成功取出商品 -> " + takenProduct.getName());
        StorageBox<TransportVehicle> vehicleBox=new StorageBox<>();
        Drone drone=new Drone("D-007");
        vehicleBox.storeItem(drone);
        TransportVehicle takenVehicle = vehicleBox.retrieveItem();
        System.out.println("📦设备储物箱存取测试：成功取出设备 -> 无人机 [D-007]");
    }
}
