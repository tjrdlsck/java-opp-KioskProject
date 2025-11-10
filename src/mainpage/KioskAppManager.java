package mainpage;

import javax.swing.JPanel;
import java.awt.CardLayout;
import java.util.List;

/**
 * [Controller]
 * 애플리케이션의 '두뇌' 역할을 수행하는 중앙 컨트롤러 클래스.
 * Kiosk.java의 역할을 대체하여, 모든 View(Frame/Panel)와 Model(Data/Service)을 총괄합니다.
 * * 1. 데이터(Model) 로드 및 상태 관리 (stores, currentCart)
 * 2. GUI(View) 생성 및 초기화 (MainFrame, All Panels)
 * 3. 화면 전환 로직 제공 (navigateTo)
 */
public class KioskAppManager {

    // --- View (GUI) ---
    private MainFrame mainFrame; // 유일한 메인 프레임
    private CardLayout cardLayout; // 화면 전환을 담당하는 레이아웃
    private JPanel mainPanelContainer; // CardLayout이 적용된 패널 (모든 화면을 담음)

    // --- Model (Data & State) ---
    private List<Store> stores; // DataLoader가 로드한 모든 가게 정보
    private Cart currentCart; // 현재 사용자의 장바구니 (앱 전체에서 공유됨)
    private String currentCustomerName; // (Phase 4) 로그인한 고객 이름
    private String currentCustomerPhone; // (Phase 4) 로그인한 고객 전화번호

    // --- Model (Service) ---
    private final DataLoader dataLoader;
    private final CartFileManager cartFileManager;

    /**
     * KioskAppManager 생성자.
     * 앱 실행에 필요한 서비스(Model)들을 초기화합니다.
     * UI(View) 관련 초기화는 start() 메소드에서 진행합니다.
     */
    public KioskAppManager() {
        // 1. 서비스 로직 초기화
        this.dataLoader = new DataLoader();
        this.cartFileManager = new CartFileManager(); // 'saved_carts' 디렉토리 생성
        
        // 2. 핵심 상태(State) 초기화
        this.currentCart = new Cart(); // 빈 장바구니로 시작
        
        // 3. 데이터 로딩 (UI 생성 전)
        this.stores = dataLoader.loadStores();
        
        // 4. View 관련 필드 초기화
        // (UI 컴포넌트 자체의 생성은 start()에서 진행)
        this.cardLayout = new CardLayout();
        this.mainPanelContainer = new JPanel(cardLayout);
    }

    /**
     * 애플리케이션을 시작합니다. (MainPageManager.main()에서 호출됨)
     * 1. 데이터를 로드합니다.
     * 2. 모든 View(Panel)를 생성하고 MainFrame에 등록합니다.
     * 3. MainFrame을 화면에 표시합니다.
     */
    public void start() {
        // 데이터 로드 확인 (기존 Kiosk.java의 방어 코드)
        if (this.stores.isEmpty()) {
            System.err.println("오류: 로드된 가게 정보가 없습니다. 프로그램을 종료합니다.");
            return;
        }

        // 1. 메인 프레임 생성
        this.mainFrame = new MainFrame(this.mainPanelContainer);

        // 2. 모든 '화면(Panel)'을 생성하여 CardLayout 컨테이너에 추가
        // [중요] 각 Panel은 Controller(manager)의 참조를 주입받아,
        // 버튼 클릭 시 manager의 메소드를 호출할 수 있게 합니다.
        mainFrame.addPanel(new MainPagePanel(this), "MAIN_PAGE");
        mainFrame.addPanel(new StoreMenuPanel(this), "STORE_MENU");
        mainFrame.addPanel(new ProductMenuPanel(this), "PRODUCT_MENU");
        mainFrame.addPanel(new CartPanel(this), "CART");
        mainFrame.addPanel(new LoadCartPanel(this), "LOAD_CART");
        mainFrame.addPanel(new DeleteCartPanel(this), "DELETE_CART");
        mainFrame.addPanel(new OrderCompletePanel(this), "ORDER_COMPLETE");
        // (필요시 '장바구니 저장' 패널도 추가)

        // 3. 첫 화면(메인 페이지)을 보여줍니다.
        this.navigateTo("MAIN_PAGE");

        // 4. 모든 준비가 끝나면 메인 프레임을 화면에 표시합니다.
        this.mainFrame.setVisible(true);
    }

    /**
     * [핵심 네비게이션 메소드]
     * 모든 Panel(View)에서 이 메소드를 호출하여 화면 전환을 요청합니다.
     * @param panelName MainFrame에 등록된 패널의 고유 이름 (예: "MAIN_PAGE")
     */
    public void navigateTo(String panelName) {
        this.cardLayout.show(mainPanelContainer, panelName);
    }

    // --- Getter (View/Panel들이 Model 데이터에 접근하기 위한 통로) ---

    /**
     * 로드된 모든 가게 리스트를 반환합니다. (MainPagePanel에서 사용)
     * @return List<Store>
     */
    public List<Store> getStores() {
        return this.stores;
    }

    /**
     * 앱 전체에서 공유되는 '현재 장바구니' 객체를 반환합니다.
     * (ProductMenuPanel, CartPanel 등에서 사용)
     * @return Cart
     */
    public Cart getCart() {
        return this.currentCart;
    }
}