package mainpage;

import javax.swing.SwingUtilities;

import mainpage.controller.KioskAppManager;

public class MainPageManager {
	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                KioskAppManager manager = new KioskAppManager();
                manager.start();
            }
        });
    }
}