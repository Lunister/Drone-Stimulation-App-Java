package com.droneapp;

import javax.swing.SwingUtilities;

public class Main {

	public static void main(String[] args) {

        Connection con = new Connection();

        while (!con.isDataRetrievalComplete()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        SwingUtilities.invokeLater(() -> {
            HomePage homePage = new HomePage(con.dronesList);
            homePage.setVisible(true);


        });
    }
}
