package Models;

public class DroneType {
    private int id;
    private String manufacturer;
    private String typename;
    private int weight;
    private int maxSpeed;
    private int batteryCapacity;
    private int controlRange;
    private int maxCarriage;

    public DroneType(int id, String manufacturer, String typename, int weight, int maxSpeed, int batteryCapacity, int controlRange, int maxCarriage) {
    	this.id = id;
    	this.manufacturer = manufacturer;
    	this.typename = typename;
    	this.weight = weight;
    	this.maxSpeed = maxSpeed;
    	this.batteryCapacity = batteryCapacity;
    	this.controlRange = controlRange;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(int batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public int getControlRange() {
        return controlRange;
    }

    public void setControlRange(int controlRange) {
        this.controlRange = controlRange;
    }

    public int getMaxCarriage() {
        return maxCarriage;
    }

    public void setMaxCarriage(int maxCarriage) {
        this.maxCarriage = maxCarriage;
    }
}