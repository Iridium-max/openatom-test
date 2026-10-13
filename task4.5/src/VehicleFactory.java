public class VehicleFactory {

    public static TransportVehicle createVehicle(String type) {
        if (type.equals("Truck")) {
            return new Truck("T-001");
        }

        if (type.equals("Drone")) {
            return new Drone("D-007");
        }

        return null;
    }
}