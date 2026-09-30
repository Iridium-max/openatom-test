public class Drone extends TransportVehicle implements GPSLocatable {

    public Drone(String vehicleId) {
        super(vehicleId);
    }

    @Override
    double calculateCost(double weight) {
        return weight * 15.0;
    }

    @Override
    public String getLocation() {
        return "高空坐标(39.9, 116.4)";
    }
}