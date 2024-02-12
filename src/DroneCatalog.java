import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public class DroneCatalog extends JFrame {

    private static final String USER_AGENT = "Mozilla Firefox Awesome version";
    private static final String TOKEN = "Token 25d3818e0d0fb9288a1be8158fa58ecd4efc8ef9";

    private String endpointUrl = "http://dronesim.facets-labs.com/api/dronetypes/?format=json";

    private int currentPage = 1;

    private JTable table;
    private JButton nextButton;
    private JButton prevButton;
    private JLabel pageLabel;

    private JButton refreshButton;
    
    private JLabel topLabel;

    private Timer refreshTimer;

    public DroneCatalog() {

        setTitle("Drone Catalog");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(800, 600);
        getContentPane().setBackground(Color.WHITE);

        topLabel = new JLabel("Overview");
        topLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        topLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel topPanel = new JPanel();
        topPanel.add(topLabel);
        add(topPanel, BorderLayout.NORTH);

     // Set up the table with empty border
        table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 16)); // Increase font size
        table.setRowHeight(getPreferredSize().height + 15); 

        // Set up the table header
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.BLUE);
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD));

        // Set up the header renderer
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(Color.BLUE);
                setForeground(Color.WHITE);
                setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                return component;
            }
        });
        

        // Add the table to the frame
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        nextButton = new JButton("Next");
        prevButton = new JButton("Previous");
        pageLabel = new JLabel("Page: " + currentPage);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(prevButton);
        buttonPanel.add(pageLabel);
        buttonPanel.add(nextButton);
        
        // Create refresh button
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchDataAndPopulateTable();
            }
        });

        // Add refresh button to buttonPanel
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage++;
                fetchDataAndPopulateTable();
            }
        });

        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPage > 1) {
                    currentPage--;
                    fetchDataAndPopulateTable();
                }
            }
        });

        fetchDataAndPopulateTable();

        refreshTimer = new Timer(300000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchDataAndPopulateTable();
            }
        });

        refreshTimer.start();
    }

    private void fetchDataAndPopulateTable() {

        try {

            URL url = new URL(endpointUrl + "&limit=10&offset=" + (currentPage - 1) * 10);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Authorization", TOKEN);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                
                JSONObject wholeFile = new JSONObject(response.toString());
                int totalCount = wholeFile.getInt("count");

                updateTable(response.toString());
                updatePageLabel(totalCount);
                updateButtonStates();
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
    }

    private void updateTable(String jsonString) {

        JSONObject wholeFile = new JSONObject(jsonString);
        JSONArray jsonFile = wholeFile.getJSONArray("results");

        Object[][] data = new Object[jsonFile.length()][8]; // 8 columns for additional parameters
        String[] columnNames = {"ID", "Manufacturer", "Typename", "Weight", "Maximum Speed", "Battery Capacity", "Control Range", "Maximum Carriage"};

        for (int i = 0; i < jsonFile.length(); i++) {

            JSONObject drone = jsonFile.getJSONObject(i);
            
            int droneID = drone.getInt("id");
            String manufacturer = drone.getString("manufacturer");
            String typename = drone.getString("typename");
            int weight = drone.getInt("weight");
            int max_speed = drone.getInt("max_speed");
            int battery_capacity = drone.getInt("battery_capacity");
            int control_range = drone.getInt("control_range");
            int max_carriage = drone.getInt("max_carriage");
            
          

            data[i][0] = droneID;
            data[i][1] = manufacturer;
            data[i][2] = typename;
            data[i][3] = weight;
            data[i][4] = max_speed;
            data[i][5] = battery_capacity;
            data[i][6] = control_range;
            data[i][7] = max_carriage;
        }

        SwingUtilities.invokeLater(() -> {

            table.setModel(new DefaultTableModel(data, columnNames));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            // Adjust column widths as needed

            table.getColumnModel().getColumn(0).setPreferredWidth(10);
            table.getColumnModel().getColumn(1).setPreferredWidth(60);
            table.getColumnModel().getColumn(2).setPreferredWidth(30);
            table.getColumnModel().getColumn(3).setPreferredWidth(30);
            table.getColumnModel().getColumn(4).setPreferredWidth(40);
            table.getColumnModel().getColumn(5).setPreferredWidth(30);
            table.getColumnModel().getColumn(6).setPreferredWidth(30);
            table.getColumnModel().getColumn(7).setPreferredWidth(30);

            // Center-align the values in the table

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();

            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
                table.getColumnModel().getColumn(columnIndex).setCellRenderer(centerRenderer);
            }

        });
    }



    private void updatePageLabel(int totalCount) {
        SwingUtilities.invokeLater(() -> {
            int totalPages = (int) Math.ceil((double) totalCount / 10);

            pageLabel.setText("Page " + currentPage + " of " + totalPages);
        });
    }

    private void updateButtonStates() {

        SwingUtilities.invokeLater(() -> {

            prevButton.setEnabled(currentPage > 1);

            nextButton.setEnabled(table.getRowCount() == 10);
        });

    }
}
