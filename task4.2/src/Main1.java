public class Main1 {
    public static void main(String[] args) {
        TransportVehicle truck=new Truck("T-998");
        TransportVehicle drone = new Drone("D-007");
        double weight=10.0;
        System.out.println("卡车[T-998]当前位置："+truck.getLocation()+"|配送10kg货物费用："+truck.calculateCost(weight)+"元");
        System.out.println("无人机 [D-007] 当前位置："
                + drone.getLocation()
                + " | 配送10kg货物费用："
                + drone.calculateCost(weight)
                + "元");
    }
}
