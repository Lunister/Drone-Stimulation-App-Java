package com.droneapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import Models.Drone;
import Models.DroneDynamic;

public class DroneDynamicDetails extends JFrame {
	
	private Connection connection = Connection.getInstance();
	
	private Drone drone;
	private HomePage homePage;
	private DroneDetails droneDetails;
	
	private List<DroneDynamic> droneDynamicList;
	
    private JLabel header;
    
    private JTextField searchBar;
    
    private JTable table;
    
    private JButton backButton;
   
    private JButton homeButton;
    
    public DroneDynamicDetails(Drone drone, HomePage homePage, DroneDetails droneDetails) {
        this.drone = drone;
        this.homePage = homePage;
        this.droneDetails = droneDetails;
        this.droneDynamicList = connection.droneDynamicsList;
    	
        
    	setTitle("Drone Dynamic Details");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        getContentPane().setBackground(Color.WHITE);

        header = new JLabel("Drone Dynamics");
        header.setFont(new Font("Arial", Font.PLAIN, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        
        searchBar = new JTextField();
        searchBar.setPreferredSize(new Dimension(200, 30));
        searchBar.setHorizontalAlignment(JTextField.CENTER);
        searchBar.addKeyListener(new SearchKeyListener());
        searchBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchBar.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchBar.getText().equals("Enter the TimeStamp")) {
                	searchBar.setText("");
                	searchBar.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchBar.getText().isEmpty()) {
                	searchBar.setForeground(Color.GRAY);
                	searchBar.setText("Enter the TimeStamp");
                }
            }
            });
        
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(header);
        topPanel.add(searchBar);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(topPanel, BorderLayout.NORTH);

        table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(getPreferredSize().height + 15);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.BLUE);
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD));

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
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        table.getTableHeader().resizeAndRepaint();
        ((DefaultTableModel) table.getModel()).fireTableStructureChanged();

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 30, 15));
        
        
        backButton = new JButton("Back");
        backButton.addActionListener(new BackButtonActionListener());
        
        homeButton = new JButton("Home");
        homeButton.addActionListener(new HomeButtonActionListener());

        
        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.add(backButton, BorderLayout.WEST);
        bottomPanel.add(homeButton, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 50, 50));
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        displayDroneData(droneDynamicList);
        
        setLocationRelativeTo(null);
        setVisible(true);
    	
    }
    
    
    private void displayDroneData(List<DroneDynamic> droneList) {
        Object[][] data = new Object[droneList.size()][10];
        String[] columnNames = {"Time Stamp", "Speed", "Alignment Roll", "Alignment Pitch", "Alignment Yaw", "Longitude", "Latitude", "Battery Status", "Last Seen", "Status"};

        for (int i = 0; i < droneList.size(); i++) {
            DroneDynamic drone = droneList.get(i);
            data[i][0] = drone.getTimestamp();
            data[i][1] = drone.getSpeed();
            data[i][2] = drone.getAlignRoll();
            data[i][3] = drone.getAlignPitch();
            data[i][4] = drone.getAlignYaw();
            data[i][5] = drone.getLongitude();
            data[i][6] = drone.getLatitude();
            data[i][7] = drone.getBatteryStatus();
            data[i][8] = drone.getLastSeen();
            data[i][9] = drone.getStatus();
        }

        SwingUtilities.invokeLater(() -> {
            table.setModel(new DefaultTableModel(data, columnNames));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            
            table.getColumnModel().getColumn(0).setPreferredWidth(150);
            table.getColumnModel().getColumn(1).setPreferredWidth(150);
            table.getColumnModel().getColumn(2).setPreferredWidth(100);
            table.getColumnModel().getColumn(3).setPreferredWidth(100);
            table.getColumnModel().getColumn(4).setPreferredWidth(100);
            table.getColumnModel().getColumn(5).setPreferredWidth(100);
            table.getColumnModel().getColumn(6).setPreferredWidth(100);
            table.getColumnModel().getColumn(7).setPreferredWidth(100);
            table.getColumnModel().getColumn(8).setPreferredWidth(100);
            table.getColumnModel().getColumn(9).setPreferredWidth(60);



            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
                table.getColumnModel().getColumn(columnIndex).setCellRenderer(centerRenderer);
            }
        });
    }
    
    private class BackButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
            droneDetails.setVisible(true);
        }
    }
    
    private class HomeButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
            homePage.setVisible(true);
        }
    }
    
    private class SearchKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            filterTable(searchBar.getText());
        }

        private void filterTable(String searchText) {
            List<DroneDynamic> filteredList = new ArrayList<>();

            for (DroneDynamic dronedynamic : droneDynamicList) {
                if (dronedynamic.getTimestamp().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(dronedynamic);
                }
            }

            displayDroneData(filteredList);
        }
    }
    
    
    
}
