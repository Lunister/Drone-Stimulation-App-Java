package com.droneapp;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.json.JSONArray;
import org.json.JSONObject;
import Models.Drone;
import Models.DroneDynamic;
import Models.DroneType;

public class Connection {

    private static Connection instance;

    private CountDownLatch dataFetchedLatch = new CountDownLatch(1);
    
    public List<DroneDynamic> droneDynamicsList = new ArrayList<>();
    public List<Drone> dronesList = new ArrayList<>();
    public List<DroneType> droneTypesList = new ArrayList<>();

    private Timer refreshTimer;
    private JDialog loadingDialog;
    private JProgressBar progressBar;

    private boolean isLoadingScreenShown = false;
    
    private int pageSize = 5;
    private int currentPage = 1;
    private int maxPages = 5;

    private boolean isDataRetrievalComplete = false;

    private static final String USER_AGENT = "Mozilla Firefox Awesome version";
    private static final String BASE_URL = "http://dronesim.facets-labs.com/api/";
    private static final String ENDPOINT_URL_DRONETYPES = BASE_URL + "dronetypes/?format=json";
    private static final String ENDPOINT_URL_DRONES = BASE_URL + "drones/?format=json";
    private static final String ENDPOINT_URL_DRONEDYNAMICS = BASE_URL + "dronedynamics/?format=json";
    private static final String TOKEN = "Token 25d3818e0d0fb9288a1be8158fa58ecd4efc8ef9";

    public Connection() {
        showLoadingScreen();
        fetchDataAndPopulateTable();

        refreshTimer = new Timer(300000, e -> fetchDataAndPopulateTable());
        refreshTimer.start();
    }

    public static synchronized Connection getInstance() {
        if (instance == null) {
            instance = new Connection();
        }
        return instance;
    }

    private void showLoadingScreen() {
    	
    	isLoadingScreenShown = true;
    	
    	SwingUtilities.invokeLater(() -> {
            loadingDialog = new JDialog();
            loadingDialog.setTitle("Loading Data");
            loadingDialog.setSize(300, 100);
            loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);

            loadingDialog.add(progressBar);
            loadingDialog.setLocationRelativeTo(null);
            loadingDialog.setModal(true);
            loadingDialog.setResizable(false);

            // Use a Swing Timer to update the progress
            Timer timer = new Timer(50, e -> {
                int progressValue = progressBar.getValue();
                if (progressValue <= 100) {
                    progressBar.setValue(progressValue + 1);
                } else {
                    loadingDialog.dispose();
                    ((Timer) e.getSource()).stop();
                }
            });

            // Set the timer to repeat until the loading is complete
            timer.setRepeats(true);
            timer.start();

            // Set the default close operation to dispose the loading dialog
            loadingDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            // Show the loading dialog
            loadingDialog.setVisible(true);
        });
    }

    private void showDialog(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void fetchDataAndPopulateTable() {
        startLoadingAnimation();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                fetchDataForDrone(ENDPOINT_URL_DRONES, dronesList);
                fetchDataForDroneType(ENDPOINT_URL_DRONETYPES, droneTypesList);
                return null;
            }

            @Override
            protected void done() {
                if (dronesList.size() > 0 && droneTypesList.size() > 0) {
                    isDataRetrievalComplete = true;
                }
                stopLoadingAnimation();
            }
        };

        worker.execute();
    }

    public void fetchDroneDynamicDataForDrone(Drone drone) {
        if (drone != null) {
            // Show the loading screen only if it hasn't been shown before
            if (!isLoadingScreenShown) {
                showLoadingScreen();
            }

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    fetchNextPageDroneDynamicData(drone.getId());
                    return null;
                }

                @Override
                protected void done() {
                    stopLoadingAnimation();
                }
            };

            worker.execute();
        }
    }
    
    
    
    public void fetchNextPageDroneDynamicData(int droneId) {
        if (currentPage <= maxPages) {
            String nextPageUrl = buildDroneDynamicEndpointUrl(droneId, currentPage + 1, pageSize);
            List<DroneDynamic> nextPageDroneDynamics = fetchNextPageDroneDynamics(nextPageUrl);
            droneDynamicsList.addAll(nextPageDroneDynamics);
            currentPage++;
        }
    }

    private List<DroneDynamic> fetchNextPageDroneDynamics(String nextPageUrl) {
        List<DroneDynamic> droneDynamics = new ArrayList<>();

        try {
            int retrievedPages = 0;  // Track the number of retrieved pages

            while (nextPageUrl != null && !nextPageUrl.isEmpty() && retrievedPages < maxPages) {
                JSONObject nextPageResponse = fetchURLDetails(nextPageUrl);

                if (nextPageResponse != null) {
                    JSONArray dataArray = nextPageResponse.getJSONArray("results");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject droneDynamicJson = dataArray.getJSONObject(i);
                        DroneDynamic droneDynamic = createDroneDynamic(droneDynamicJson);

                        if (droneDynamic != null && !droneDynamics.contains(droneDynamic)) {
                            droneDynamics.add(droneDynamic);
                            System.out.println(droneDynamic.toString());
                        } else {
                            System.out.println("Duplicate DroneDynamic entry found, skipping: " + droneDynamic.toString());
                        }
                    }

                    if (!nextPageResponse.isNull("next")) {
                        nextPageUrl = nextPageResponse.getString("next");
                        retrievedPages++;
                    } else {
                        // Exit the loop when there is no next page
                        nextPageUrl = null;
                    }
                } else {
                    showDialog("Error", "An error occurred while fetching data from the URLs.");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showDialog("Error", "An error occurred while loading drone dynamics from the JSON response.");
        }

        return droneDynamics;
    }

    private String buildDroneDynamicEndpointUrl(int droneId, int page, int pageSize) {
        return String.format("http://dronesim.facets-labs.com/api/dronedynamics/?format=json&drone=%d&page=%d&page_size=%d",
                droneId, page, pageSize);
    }

    private void fetchDataForDrone(String endpointUrl, List<Drone> dataList) {
        try {
            String nextEndpointUrl = endpointUrl;

            while (nextEndpointUrl != null && !nextEndpointUrl.isEmpty()) {
                JSONObject jsonResponse = fetchURLDetails(nextEndpointUrl);

                if (jsonResponse != null) {
                    JSONArray dataArray = jsonResponse.getJSONArray("results");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataJson = dataArray.getJSONObject(i);
                        Drone drone = createDrone(dataJson);

                        if (drone != null && !dataList.contains(drone)) {
                            dataList.add(drone);
                            System.out.println(drone.toString());
                        } else {
                            System.out.println("Duplicate Drone entry found, skipping: " + drone.toString());
                        }
                    }

                    nextEndpointUrl = jsonResponse.optString("next", null);
                } else {
                    showDialog("Error", "An error occurred while fetching data from the URLs.");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showDialog("Error", "An error occurred while fetching data from the URLs.");
        }
    }

    private void fetchDataForDroneType(String endpointUrl, List<DroneType> dataList) {
        try {
            String nextEndpointUrl = endpointUrl;

            while (nextEndpointUrl != null) {
                JSONObject jsonResponse = fetchURLDetails(nextEndpointUrl);

                if (jsonResponse != null) {
                    JSONArray dataArray = jsonResponse.getJSONArray("results");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataJson = dataArray.getJSONObject(i);
                        DroneType droneType = createDroneType(dataJson);

                        if (droneType != null && !dataList.contains(droneType)) {
                            dataList.add(droneType);
                            System.out.println(droneType.toString());
                        } else {
                            System.out.println("Duplicate DroneType entry found, skipping: " + droneType.toString());
                        }
                    }

                    if (!jsonResponse.isNull("next")) {
                        nextEndpointUrl = jsonResponse.getString("next");
                    } else {
                        // Exit the loop when there is no next page
                        nextEndpointUrl = null;
                    }
                } else {
                    showDialog("Error", "An error occurred while fetching data from the URLs.");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showDialog("Error", "An error occurred while fetching data from the URLs.");
        }
    }

    private JSONObject fetchURLDetails(String url) {
        try {
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            connection.setRequestProperty("Authorization", TOKEN);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    return new JSONObject(response.toString());
                }
            } else {
                System.out.println("HTTP request failed with response code: " + responseCode);
            }
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL: " + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("General IO Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return null;
    }
    
    private DroneDynamic createDroneDynamic(JSONObject droneDynamicJson) {
        try {
            String droneUrl = droneDynamicJson.getString("drone");
            String timestamp = droneDynamicJson.getString("timestamp");
            int speed = droneDynamicJson.getInt("speed");
            String alignRoll = droneDynamicJson.getString("align_roll");
            String alignPitch = droneDynamicJson.getString("align_pitch");
            String alignYaw = droneDynamicJson.getString("align_yaw");
            String longitude = droneDynamicJson.getString("longitude");
            String latitude = droneDynamicJson.getString("latitude");
            int batteryStatus = droneDynamicJson.getInt("battery_status");
            String lastSeen = droneDynamicJson.getString("last_seen");
            String status = droneDynamicJson.getString("status");

            JSONObject droneDetails = fetchURLDetails(droneUrl);

            Drone drone = createDrone(droneDetails);

            DroneDynamic newDroneDynamic = new DroneDynamic(drone, timestamp, speed, alignRoll, alignPitch, alignYaw, longitude,
                    latitude, batteryStatus, lastSeen, status);

            // Check for duplicate before adding
            if (!droneDynamicsList.contains(newDroneDynamic)) {
                return newDroneDynamic;
            } else {
                System.out.println("Duplicate DroneDynamic entry found, skipping: " + newDroneDynamic.toString());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Drone createDrone(JSONObject droneJson) {
        try {
            int id = droneJson.getInt("id");
            String droneTypeUrl = droneJson.getString("dronetype");
            String created = droneJson.getString("created");
            String serialNumber = droneJson.getString("serialnumber");
            int carriageWeight = droneJson.getInt("carriage_weight");
            String carriageType = droneJson.getString("carriage_type");

            JSONObject droneTypeDetails = fetchURLDetails(droneTypeUrl);
            DroneType droneType = createDroneType(droneTypeDetails);

            Drone newDrone = new Drone(id, droneType, created, serialNumber, carriageWeight, carriageType);

            // Check for duplicate before adding
            if (!dronesList.contains(newDrone)) {
                return newDrone;
            } else {
                System.out.println("Duplicate Drone entry found, skipping: " + newDrone.toString());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private DroneType createDroneType(JSONObject droneTypeJson) {
        try {
            int id = droneTypeJson.getInt("id");
            String manufacturer = droneTypeJson.getString("manufacturer");
            String typeName = droneTypeJson.getString("typename");
            int weight = droneTypeJson.getInt("weight");
            int maxSpeed = droneTypeJson.getInt("max_speed");
            int batteryCapacity = droneTypeJson.getInt("battery_capacity");
            int controlRange = droneTypeJson.getInt("control_range");
            int maxCarriage = droneTypeJson.getInt("max_carriage");

            DroneType newDroneType = new DroneType(id, manufacturer, typeName, weight, maxSpeed, batteryCapacity, controlRange,
                    maxCarriage);

            // Check for duplicate before adding
            if (!droneTypesList.contains(newDroneType)) {
                return newDroneType;
            } else {
                System.out.println("Duplicate DroneType entry found, skipping: " + newDroneType.toString());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void startLoadingAnimation() {
        SwingUtilities.invokeLater(() -> {
            progressBar.setIndeterminate(true);
            loadingDialog.setVisible(true);
        });
    }

    private void stopLoadingAnimation() {
        SwingUtilities.invokeLater(() -> {
            progressBar.setIndeterminate(false);
            loadingDialog.dispose();
        });
    }

    public boolean isDataRetrievalComplete() {
        return isDataRetrievalComplete;
    }
}
