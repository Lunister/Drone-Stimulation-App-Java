package Models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DroneDynamic {
	
    private Drone drone;
    private String timestamp;
    private int speed;
    private String alignRoll;
    private String alignPitch;
    private String alignYaw;
    private String longitude;
    private String latitude;
    private int batteryStatus;
    private String lastSeen;
    private String status;

    public DroneDynamic(Drone drone, String timestamp, int speed, String alignRoll, String alignPitch,
                       String alignYaw, String longitude, String latitude, int batteryStatus,
                       String lastSeen, String status) {
    	
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");  
        
        try {
        	
            Date date = inputFormat.parse(timestamp);
            timestamp = outputFormat.format(date);
            date = inputFormat.parse(lastSeen);
            lastSeen = outputFormat.format(date);
            
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	
    	
        this.drone = drone;
        this.timestamp = timestamp;
        this.speed = speed;
        this.alignRoll = alignRoll;
        this.alignPitch = alignPitch;
        this.alignYaw = alignYaw;
        this.longitude = longitude;
        this.latitude = latitude;
        this.batteryStatus = batteryStatus;
        this.lastSeen = lastSeen;
        this.status = status;
    }

    // Add getters and setters here

    public Drone getDrone() {
        return drone;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");  
        
        try {
        	
            Date date = inputFormat.parse(timestamp);
            timestamp = outputFormat.format(date);
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	
        this.timestamp = timestamp;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getAlignRoll() {
        return alignRoll;
    }

    public void setAlignRoll(String alignRoll) {
        this.alignRoll = alignRoll;
    }

    public String getAlignPitch() {
        return alignPitch;
    }

    public void setAlignPitch(String alignPitch) {
        this.alignPitch = alignPitch;
    }

    public String getAlignYaw() {
        return alignYaw;
    }

    public void setAlignYaw(String alignYaw) {
        this.alignYaw = alignYaw;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(int batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
    	
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");  
        
        try {
            Date date = inputFormat.parse(lastSeen);
            lastSeen = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	
        this.lastSeen = lastSeen;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}