package mainpage;

import javax.swing.JPanel;
import java.awt.CardLayout;
import java.util.List;

/**
 * [Controller] (2단계 수정)
 * 모든 View(Panel) 객체를 필드로 직접 소유하고,
 * 데이터를 포함한 화면 전환(Navigation)을 관리합니다.
 */
public class KioskAppManager {

    // --- View (GUI) ---
    private MainFrame mainFrame; 
    private CardLayout cardLayout; 
    private JPanel mainPanelContainer; 

    // [2단계 수정] 모든 패널을 필드로 직접 참조
    private MainPagePanel mainPagePanel;
    private StoreMenuPanel storeMenuPanel;
    private ProductMenuPanel productMenuPanel;
    private CartPanel cartPanel;
    private LoadCartPanel loadCartPanel;
    private DeleteCartPanel deleteCartPanel;
    private OrderCompletePanel orderCompletePanel;

    // --- Model (Data & State) ---
    private List<Store> stores; 
    private Cart currentCart; 
    private String currentCustomerName; 
    private String currentCustomerPhone; 

    // --- Model (Service) ---
    private final DataLoader dataLoader;
    private final CartFileManager cartFileManager;

    public KioskAppManager() {
        this.dataLoader = new DataLoader();
        this.cartFileManager = new CartFileManager(); 
        this.currentCart = new Cart(); 
        this.stores = dataLoader.loadStores();
        this.cardLayout = new CardLayout();
        this.mainPanelContainer = new JPanel(cardLayout);
    }

    public void start() {
        if (this.stores.isEmpty()) {
            System.err.println("오류: 로드된 가게 정보가 없습니다. 프로그램을 종료합니다.");
            return;
        }

        // 1. 메인 프레임 생성
        this.mainFrame = new MainFrame(this.mainPanelContainer);

        // 2. [2단계 수정] 모든 패널을 '필드'에 생성 및 등록
        // KioskAppManager가 각 패널의 인스턴스를 명확히 알 수 있도록 변경합니다.
        this.mainPagePanel = new MainPagePanel(this);
        this.storeMenuPanel = new StoreMenuPanel(this);
        this.productMenuPanel = new ProductMenuPanel(this);
        this.cartPanel = new CartPanel(this);
        this.loadCartPanel = new LoadCartPanel(this);
        this.deleteCartPanel = new DeleteCartPanel(this);
        this.orderCompletePanel = new OrderCompletePanel(this);
        
        mainFrame.addPanel(mainPagePanel, "MAIN_PAGE");
        mainFrame.addPanel(storeMenuPanel, "STORE_MENU");
        mainFrame.addPanel(productMenuPanel, "PRODUCT_MENU");
        mainFrame.addPanel(cartPanel, "CART");
        mainFrame.addPanel(loadCartPanel, "LOAD_CART");
        mainFrame.addPanel(deleteCartPanel, "DELETE_CART");
        mainFrame.addPanel(orderCompletePanel, "ORDER_COMPLETE");

        // 3. 첫 화면(메인 페이지)을 보여줍니다.
        this.goHome(); // navigateTo("MAIN_PAGE") 대신 전용 메소드 사용

        // 4. 메인 프레임을 화면에 표시합니다.
        this.mainFrame.setVisible(true);
    }

    /**
     * [핵심 네비게이션 메소드] (데이터 없음)
     * @param panelName MainFrame에 등록된 패널의 고유 이름
     */
    public void navigateTo(String panelName) {
        this.cardLayout.show(mainPanelContainer, panelName);
    }
    
    /**
     * [2단계 추가] 메인 화면(가게 선택)으로 돌아갑니다.
     * (향후 로그인 상태 초기화 로직이 추가될 수 있음)
     */
    public void goHome() {
        // (Phase 4) resetLoginState(); 
        this.navigateTo("MAIN_PAGE");
    }

    /**
     * [2단계 추가] (데이터 전달 O)
     * 특정 가게의 '메뉴 카테고리 화면'을 엽니다.
     * @param store 사용자가 선택한 Store 객체
     */
    public void showStoreMenu(Store store) {
        // 1. StoreMenuPanel에 선택된 가게 정보를 전달하여
        //    (표시될 버튼들을 미리 생성하도록 지시)
        this.storeMenuPanel.setStore(store);
        
        // 2. 가게 메뉴 패널로 화면 전환
        this.navigateTo("STORE_MENU");
    }

    /**
     * [2단계 추가] (데이터 전달 O)
     * 특정 메뉴의 '상품 목록 화면'을 엽니다.
     * @param menu 사용자가 선택한 Menu 객체
     * @param currentStore "뒤로 가기"를 눌렀을 때 돌아올 Store 객체
     */
    public void showProductMenu(Menu menu, Store currentStore) {
        // 1. ProductMenuPanel에 선택된 메뉴 정보를 전달
        this.productMenuPanel.setMenu(menu, currentStore);
        
        // 2. 상품 목록 패널로 화면 전환
        this.navigateTo("PRODUCT_MENU");
    }


    // --- Getter (View/Panel들이 Model 데이터에 접근하기 위한 통로) ---

    public List<Store> getStores() {
        return this.stores;
    }

    public Cart getCart() {
        return this.currentCart;
    }
}