package com.droneapp;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import Models.Drone;
import Models.DroneDynamic;

public class HomePage extends JFrame {

    private JLabel topLabel;
    private JTextField searchBar;
    private JTable table;
    
    private JButton viewDetails;
    
    private Drone selectedDrone;
    
    private List<Drone> droneList;
    
    private DroneDetails droneDetailsFrame;
    

    public HomePage(List<Drone> droneList) {
        this.droneList = new ArrayList<>(droneList);
        
        setTitle("HomePage");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        getContentPane().setBackground(Color.WHITE);

        topLabel = new JLabel("HomePage");
        topLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        topLabel.setHorizontalAlignment(JTextField.CENTER);
        topLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchBar = new JTextField();
        searchBar.setPreferredSize(new Dimension(200, 30));
        searchBar.setHorizontalAlignment(JTextField.CENTER);
        searchBar.addKeyListener(new SearchKeyListener());
        searchBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchBar.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchBar.getText().equals("Enter the Serial Number")) {
                	searchBar.setText("");
                	searchBar.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchBar.getText().isEmpty()) {
                	searchBar.setForeground(Color.GRAY);
                	searchBar.setText("Enter the Serial Number");
                }
            }
            });
        

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(topLabel);
        topPanel.add(searchBar);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(topPanel, BorderLayout.NORTH);

        table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(getPreferredSize().height + 15);
        
        
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = table.getSelectedRow();

                if (selectedRow != -1) {
                    // A row is selected
                    viewDetails.setEnabled(true);
                    selectedDrone = droneList.get(selectedRow);
                } else {
                    // No row is selected
                    viewDetails.setEnabled(false);
                    selectedDrone = null;
                }
            }
        });
        
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
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane);
        
        viewDetails = new JButton("View Details");
        
        viewDetails.addActionListener(new ViewDetailsActionListener());
        
        viewDetails.setEnabled(false);
        
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(viewDetails, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 50, 50));
        
        add(bottomPanel, BorderLayout.SOUTH);

        displayDroneData(droneList);

        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void displayDroneData(List<Drone> droneList) {
        Object[][] data = new Object[droneList.size()][3];
        String[] columnNames = {"DroneType", "Serial Number", "Created"};

        for (int i = 0; i < droneList.size(); i++) {
            Drone drone = droneList.get(i);
            data[i][0] = drone.getDronetype().getManufacturer() + ", " + drone.getDronetype().getTypename();
            data[i][1] = drone.getSerialNumber();
            data[i][2] = drone.getCreated();
        }

        SwingUtilities.invokeLater(() -> {
            table.setModel(new DefaultTableModel(data, columnNames));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            table.getColumnModel().getColumn(0).setPreferredWidth(20);
            table.getColumnModel().getColumn(1).setPreferredWidth(150);
            table.getColumnModel().getColumn(2).setPreferredWidth(100);

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
                table.getColumnModel().getColumn(columnIndex).setCellRenderer(centerRenderer);
            }
        });
    }
    
    public void hideHomePage() {
        setVisible(false);
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
            List<Drone> filteredList = new ArrayList<>();

            for (Drone drone : droneList) {
                if (drone.getSerialNumber().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(drone);
                }
            }

            displayDroneData(filteredList);
        }
    }
    
    
    private class ViewDetailsActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedDrone != null) {
                
            	droneDetailsFrame = new DroneDetails(selectedDrone, HomePage.this);
                
                hideHomePage();
            }
        }
    }
    
    
}