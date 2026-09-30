public class Truck extends TransportVehicle implements GPSLocatable {
    public Truck(String vehicleId)
    {
        super(vehicleId);
    }
    @Override
    double calculateCost(double weight){
        return weight*5.0;
    }
    @Override
    public String getLocation() {
        return "仓库A区";
    }
}