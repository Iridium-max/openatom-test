public class task {
    public static void main(String[] args) {
        LogisticsConfig configA = LogisticsConfig.getInstance();
        LogisticsConfig configB = LogisticsConfig.getInstance();

        System.out.println("单例测试：配置类实例A与实例B是否相同？ -> " + (configA == configB));
        TransportVehicle vehicle=VehicleFactory.createVehicle("Drone");
        double cost=vehicle.calculateCost(10);
        System.out.println("工厂调度：计费结果 -> " + cost);
        LogisticsOrder order=new LogisticsOrder.Builder("OD-999").sender("Alice").receiver("Bob").fragile(true).insuredAmount(1000) .build();
        System.out.println("运单生成：" + order);
    }
}
