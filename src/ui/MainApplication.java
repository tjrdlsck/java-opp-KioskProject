package ui;

import mainpage.CongestionManager;
import mainpage.DataLoader;
import mainpage.OrderFileManager;
import mainpage.Store;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainApplication extends JFrame {

    public static final String SCREEN_SELECT = "SELECT_CAFE";
    public static final String SCREEN_MENU = "MENU";

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final CafeSelectionScreen cafeSelectionScreen;
    private final MenuScreen menuScreen;
    private final CongestionManager congestionManager;

    public MainApplication(List<Store> allStores) {
        setTitle("객체지향 키오스크");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(648, 1152);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        OrderFileManager orderFileManager = new OrderFileManager();
        this.congestionManager = new CongestionManager(orderFileManager);

        this.cafeSelectionScreen = new CafeSelectionScreen(allStores, this, congestionManager);
        this.menuScreen = new MenuScreen(allStores, null, this, congestionManager);

        mainPanel.add(cafeSelectionScreen, SCREEN_SELECT);
        mainPanel.add(menuScreen, SCREEN_MENU);

        add(mainPanel);
        setVisible(true);
    }

    public void showMenuScreen(Store selectedStore) {
        congestionManager.refreshCache();
        menuScreen.loadCafeMenu(selectedStore);
        cardLayout.show(mainPanel, SCREEN_MENU);
    }

    public void showSelectScreen() {
        congestionManager.refreshCache();
        cardLayout.show(mainPanel, SCREEN_SELECT);
    }

    public static void main(String[] args) {
        List<Store> allStores = new DataLoader().loadStores();
        SwingUtilities.invokeLater(() -> new MainApplication(allStores));
    }
}
