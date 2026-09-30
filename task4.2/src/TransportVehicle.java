public abstract  class TransportVehicle implements GPSLocatable{
    protected String vehicleId;
    public TransportVehicle(String vehicleId)
    {
        this.vehicleId=vehicleId;
    }
    abstract double calculateCost(double weight);
}
