package Models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Drone {
	
    private int id;
    private DroneType dronetype;
    private String created;
    private String serialNumber;
    private int carriageWeight;
    private String carriageType;
    
    
    
    public Drone(int id, DroneType dronetype, String created, String serialNumber, int carriageWeight, String carriageType) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");  
        
        try {
        	
            Date date = inputFormat.parse(created);
            created = outputFormat.format(date);
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	
    	this.id = id;
    	this.dronetype = dronetype;
    	this.created = created;
    	this.serialNumber = serialNumber;
    	this.carriageWeight = carriageWeight;
    	this.carriageType = carriageType;
    	
    	
    }
	
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DroneType getDronetype() {
        return dronetype;
    }

    public void setDronetype(DroneType dronetype) {
        this.dronetype = dronetype;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
    	
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");  
        
        try {
        	
            Date date = inputFormat.parse(created);
            created = outputFormat.format(date);
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	
        this.created = created;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getCarriageWeight() {
        return carriageWeight;
    }

    public void setCarriageWeight(int carriageWeight) {
        this.carriageWeight = carriageWeight;
    }

    public String getCarriageType() {
        return carriageType;
    }

    public void setCarriageType(String carriageType) {
        this.carriageType = carriageType;
    }
	
}
