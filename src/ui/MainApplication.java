package ui;

import mainpage.DataLoader;
import mainpage.Store;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * [계획 5.1] 단일 JFrame + CardLayout 아키텍처의 메인 프레임입니다.
 * 이 클래스가 애플리케이션의 유일한 '창'이 되며, 새로운 공식 진입점(main)입니다.
 */
public class MainApplication extends JFrame {

    // CardLayout에서 화면을 식별하기 위한 상수
    public static final String SCREEN_SELECT = "SELECT_CAFE";
    public static final String SCREEN_MENU = "MENU";

    private final CardLayout cardLayout;
    private final JPanel mainPanel; // 모든 화면(JPanel)을 담을 컨테이너

    // 관리할 화면(패널)들
    // [중요] 이 클래스들은 5.2, 5.3 단계에서 모두 JPanel을 상속하도록 수정되어야 합니다.
    private final CafeSelectionScreen cafeSelectionScreen;
    private final MenuScreen menuScreen;

    /**
     * 메인 애플리케이션 프레임을 생성합니다.
     * @param allStores DataLoader가 로드한 전체 가게 목록
     */
    public MainApplication(List<Store> allStores) {
        // 1. 메인 프레임(창) 기본 설정
        setTitle("객체지향 키오스크");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int width = 648;
        int height = 1152;
        setSize(width, height);
        setLocationRelativeTo(null); // 화면 중앙에 배치

        // 2. CardLayout 설정
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 3. 화면(패널)들 생성 (의존성 주입)
        // [중요] 5.2, 5.3 단계에서 수정될 새로운 생성자를 호출합니다.
        
        // 3-1. 카페 선택 화면. 'this'(MainApplication)를 전달하여 콜백이 가능하게 함
        this.cafeSelectionScreen = new CafeSelectionScreen(allStores, this);
        
        // 3-2. 메뉴 화면. 'allStores'는 상단 스크롤바용, 'null'은 초기 선택 가게 없음.
        // (이 생성자는 5.3 단계에서 수정될 예정입니다)
        this.menuScreen = new MenuScreen(allStores, null); 

        // 4. 메인 패널에 화면들 추가
        mainPanel.add(cafeSelectionScreen, SCREEN_SELECT);
        mainPanel.add(menuScreen, SCREEN_MENU);

        // 5. 프레임에 메인 패널을 추가
        add(mainPanel);

        // 6. 프레임 표시
        setVisible(true);
    }

    /**
     * [콜백] CafeSelectionScreen에서 호출할 메소드.
     * 메뉴 화면으로 전환하고 선택된 가게 정보를 전달합니다.
     *
     * @param selectedStore 사용자가 선택한 가게
     */
    public void showMenuScreen(Store selectedStore) {
        // 1. MenuScreen이 선택된 가게의 메뉴를 로드하도록 함
        menuScreen.loadCafeMenu(selectedStore); 
        
        // 2. CardLayout을 이용해 메뉴 화면으로 즉시 전환
        cardLayout.show(mainPanel, SCREEN_MENU);
    }

    /**
     * [콜백] MenuScreen에서 '뒤로가기' (혹은 로고) 클릭 시 호출할 메소드.
     * 카페 선택 화면으로 전환합니다.
     */
    public void showSelectScreen() {
        cardLayout.show(mainPanel, SCREEN_SELECT);
    }

    /**
     * [새로운 공식 진입점]
     * 애플리케이션 시작 시, 데이터를 '먼저' 로드하고 '주입'합니다.
     */
    public static void main(String[] args) {
        // [계획 3] 애플리케이션의 메인 진입점에서 데이터를 '단 한 번' 로드합니다.
        List<Store> allStores = new DataLoader().loadStores();

        // [계획 5] 로드한 데이터를 'MainApplication' 생성자에 주입합니다.
        SwingUtilities.invokeLater(() -> new MainApplication(allStores));
    }
}