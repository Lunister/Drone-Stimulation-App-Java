package com.droneapp;

import Models.Drone;
import Models.DroneDynamic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DroneDetails extends JFrame {
	
//	private Connection connection = Connection.getInstance();

    private JLabel serialNumber;
    private JLabel droneType;

    private JButton backButton;
    private JButton droneDynamicsButton;

    private JButton droneTypeDetailsBtn;
    
    private Drone drone;
    private HomePage homePage;
    private DroneDynamicDetails droneDynamicFrame;

    private JTable droneTable;
    private JScrollPane tableScrollPane;

    private boolean isTableVisible = false;

    public DroneDetails(Drone drone, HomePage homePage) {
        this.drone = drone;
        this.homePage = homePage;
        

        setTitle("Drone Details");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);

        serialNumber = new JLabel(drone.getSerialNumber());
        droneType = new JLabel(drone.getDronetype().getTypename());

        // Buttons
        backButton = new JButton("Back");
        backButton.addActionListener(new BackButtonActionListener());

        droneDynamicsButton = new JButton("Drone Dynamics");
        droneDynamicsButton.addActionListener(new DroneDynamicsButtonListener());

        droneTypeDetailsBtn = new JButton("Drone Type Details");
        droneTypeDetailsBtn.setBounds(10, 10, 10, 10);
        
        droneTypeDetailsBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String droneTypeString = String.format("ID: %d\nManufacturer: %s\nTypename: %s\nWeight: %d\nMax Speed: %d\nBattery Capacity: %d\nControl Range: %d\nMax Carriage: %d\n",
                        drone.getDronetype().getId(),
                        drone.getDronetype().getManufacturer(),
                        drone.getDronetype().getTypename(),
                        drone.getDronetype().getWeight(),
                        drone.getDronetype().getMaxSpeed(),
                        drone.getDronetype().getBatteryCapacity(),
                        drone.getDronetype().getControlRange(),
                        drone.getDronetype().getMaxCarriage());

                JOptionPane.showMessageDialog(DroneDetails.this, droneTypeString, "Drone Type Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });

      

        droneTable = new JTable();
        droneTable.setAlignmentX(CENTER_ALIGNMENT);
        
        tableScrollPane = new JScrollPane(droneTable);
        
        setTableData(); 


        tableScrollPane.setVisible(isTableVisible);

        
        JButton toggleTableButton = new JButton("Drone Details");
        toggleTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isTableVisible = !isTableVisible;
                tableScrollPane.setVisible(isTableVisible);
                DroneDetails.this.revalidate();
                DroneDetails.this.repaint();
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel labelPanel = new JPanel(new GridLayout(1, 3));

        labelPanel.add(createLabelPanel("Serial Number", serialNumber));
        labelPanel.add(createLabelPanel("Drone Type", droneType));
        labelPanel.add(droneTypeDetailsBtn);

        labelPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        topPanel.add(labelPanel, BorderLayout.NORTH);
        topPanel.add(toggleTableButton, BorderLayout.SOUTH);

        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 30, 15));
        
        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.add(backButton, BorderLayout.WEST);
        bottomPanel.add(droneDynamicsButton, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 50, 50));

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class BackButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
            homePage.setVisible(true);
        }
    }
    
    private class DroneDynamicsButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Connection connection = Connection.getInstance();

            // Fetch drone dynamic data and wait for the loading screen to be closed
            connection.fetchDroneDynamicDataForDrone(drone);

            // If the data fetching is complete, proceed to create the DroneDynamicDetails frame
            if (connection.isDataRetrievalComplete()) {
                // Assuming DroneDynamicDetails constructor takes the drone and other necessary parameters
                DroneDynamicDetails droneDynamicFrame = new DroneDynamicDetails(drone, homePage, DroneDetails.this);

                // Hide the drone details
                hideDroneDetails();
            } else {
                // Handle the case where data retrieval is not complete
                // This can be showing an error message or taking appropriate action
                System.out.println("Data retrieval not complete. Handle accordingly.");
            }
        }
    }

    private JPanel createLabelPanel(String label, JLabel value) {
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.add(new JLabel(label, SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel innerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        innerPanel.add(value, BorderLayout.CENTER);

        labelPanel.add(innerPanel, BorderLayout.SOUTH);

        return labelPanel;
    }

    private void setTableData() {
        Object[][] data = {
                {drone.getCarriageWeight(), drone.getCarriageType(), drone.getCreated()},
        };

        Object[] columnNames = {"Carriage Weight", "Carriage Type" , "Created"};

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        droneTable.setModel(model);
    }
    
    public void hideDroneDetails() {
        setVisible(false);
    }
}
