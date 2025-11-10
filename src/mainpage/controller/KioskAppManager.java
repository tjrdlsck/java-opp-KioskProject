package mainpage.controller;

import javax.swing.JPanel;
import javax.swing.JOptionPane; // [4단계 신규]
import java.awt.CardLayout;
import java.util.List;

import mainpage.model.Cart;
import mainpage.model.Order;
import mainpage.model.Store;
import mainpage.service.CartFileManager;
import mainpage.service.DataLoader;
import mainpage.view.CartPanel;
import mainpage.view.DeleteCartPanel;
import mainpage.view.LoadCartPanel;
import mainpage.view.MainFrame;
import mainpage.view.MainPagePanel;
import mainpage.view.OrderCompletePanel;
import mainpage.view.StorePagePanel;

/**
 * [Controller] (GUI v2.0 수정)
 * [4단계 수정] (4단계에서 완료)
 * [5단계 수정]
 * - saveCart(), loadCart(), deleteCart() 비즈니스 로직 메소드 신규 구현
 * - 이 메소드들은 CartFileManager와 View(JOptionPane) 사이의 중재자 역할을 함.
 */
public class KioskAppManager {

    // --- View (GUI) ---
    private MainFrame mainFrame; 
    private CardLayout cardLayout; 
    private JPanel mainPanelContainer; 

    // [수정] 패널 참조 변경
    private MainPagePanel mainPagePanel;
    private StorePagePanel storePagePanel; 
    
    private CartPanel cartPanel;
    private LoadCartPanel loadCartPanel;
    private DeleteCartPanel deleteCartPanel;
    private OrderCompletePanel orderCompletePanel;

    // --- Model (Data & State) ---
    private List<Store> stores; 
    private Cart currentCart; 

    // [4단계 신규] Kiosk.java로부터 이관된 '로그인 상태'
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

        // [4단계] 로그인 상태 null로 명시적 초기화 (Kiosk.java 로직)
        this.currentCustomerName = null;
        this.currentCustomerPhone = null;
    }

    public void start() {
        if (this.stores.isEmpty()) {
            System.err.println("오류: 로드된 가게 정보가 없습니다. 프로그램을 종료합니다.");
            return;
        }

        this.mainFrame = new MainFrame(this.mainPanelContainer);

        this.mainPagePanel = new MainPagePanel(this);
        this.storePagePanel = new StorePagePanel(this); 
        this.cartPanel = new CartPanel(this); 
        this.loadCartPanel = new LoadCartPanel(this); 
        this.deleteCartPanel = new DeleteCartPanel(this); 
        this.orderCompletePanel = new OrderCompletePanel(this); 
        
        mainFrame.addPanel(mainPagePanel, "MAIN_PAGE");
        mainFrame.addPanel(storePagePanel, "STORE_PAGE"); 
        mainFrame.addPanel(cartPanel, "CART");
        mainFrame.addPanel(loadCartPanel, "LOAD_CART");
        mainFrame.addPanel(deleteCartPanel, "DELETE_CART");
        mainFrame.addPanel(orderCompletePanel, "ORDER_COMPLETE");

        this.goHome(); 
        this.mainFrame.setVisible(true);
    }

    // --- 네비게이션 메소드 ---
    
    public void navigateTo(String panelName) {
        this.cardLayout.show(mainPanelContainer, panelName);
    }
    
    /**
     * [4단계 수정] (4단계에서 완료)
     * 메인 화면으로 돌아갈 때 로그인 상태를 리셋합니다.
     */
    public void goHome() { 
        this.resetLoginState(); // [4단계 추가]
        this.navigateTo("MAIN_PAGE");
    }

    /**
     * [수정] (데이터 전달 O)
     * 특정 가게의 '통합 페이지'를 엽니다.
     */
    public void showStoreMenu(Store store) {
        // [수정] StorePagePanel에 store 전달
        this.storePagePanel.setStore(store);
        // [수정] STORE_PAGE로 이동
        this.navigateTo("STORE_PAGE");
    }

    // --- [4단계 신규] 비즈니스 로직 메소드 ---

    /**
     * [4단계 신규] Kiosk.java의 resetLoginState() 로직 이관
     * (4단계에서 완료)
     */
    private void resetLoginState() {
        this.currentCustomerName = null;
        this.currentCustomerPhone = null;
    }

    /**
     * [4단계 신규] CartPanel의 '주문하기' 버튼 클릭 시 호출됩니다.
     * (4단계에서 완료)
     */
    public void placeOrder() {
        // 1. Order 객체 생성
        Order order = new Order(this.currentCart);

        // 2. View에 데이터 전달
        this.orderCompletePanel.setOrder(order);

        // 3. [로그인 상태 연동]
        if (this.currentCustomerName != null) {
            
            boolean deleteSuccess = cartFileManager.deleteCart(
                this.currentCustomerName, 
                this.currentCustomerPhone
            );
            
            if (deleteSuccess) {
                JOptionPane.showMessageDialog(
                    mainFrame, 
                    "'" + this.currentCustomerName + "'님의 저장된 장바구니 내역이 삭제되었습니다.",
                    "저장 내역 삭제",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                 JOptionPane.showMessageDialog(
                    mainFrame, 
                    "저장된 장바구니 삭제에 실패했습니다.", 
                    "삭제 오류",
                    JOptionPane.ERROR_MESSAGE
                );
            }
            
            this.resetLoginState();
        }

        // 4. 장바구니 비우기
        this.currentCart = new Cart();

        // 5. '주문 완료' 화면으로 이동
        this.navigateTo("ORDER_COMPLETE");
    }

    
    // --- [5단계 신규] 비즈니스 로직 메소드 (파일 I/O) ---
    
    /**
     * [5단계 신규] CartPanel의 '저장하기' 시 호출됩니다.
     * @param name 저장할 고객 이름
     * @param phone 저장할 고객 전화번호
     */
    public void saveCart(String name, String phone) {
        // 1. Service(CartFileManager)에 작업 위임
        boolean success = cartFileManager.saveCart(this.currentCart, name, phone);

        // 2. Controller가 Service의 결과에 따라 View(JOptionPane)를 제어
        if (success) {
            JOptionPane.showMessageDialog(
                mainFrame, 
                "'" + name + "'님의 이름으로 장바구니가 저장되었습니다.",
                "저장 완료",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            // (저장 실패 사유는 다양함: 장바구니가 비었거나, IO 오류 등)
            JOptionPane.showMessageDialog(
                mainFrame, 
                "장바구니 저장에 실패했습니다. (장바구니가 비어있거나 파일 오류)",
                "저장 실패",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * [5단계 신규] LoadCartPanel의 '불러오기' 시 호출됩니다.
     * @param name 불러올 고객 이름
     * @param phone 불러올 고객 전화번호
     * @return View(LoadCartPanel)가 화면 전환 여부를 판단할 boolean 값
     */
    public boolean loadCart(String name, String phone) {
        // 1. Service(CartFileManager)에 작업 위임
        Cart loadedCart = cartFileManager.loadCart(name, phone);

        // 2. Controller가 Service의 결과에 따라 Model과 View를 제어
        if (loadedCart != null) {
            // 2-1. (성공) Model(currentCart) 교체
            this.currentCart = loadedCart;
            
            // 2-2. (성공) Controller의 '로그인 상태' 설정
            this.currentCustomerName = name;
            this.currentCustomerPhone = phone;

            // 2-3. (성공) View(JOptionPane) 알림
             JOptionPane.showMessageDialog(
                mainFrame, 
                "'" + name + "'님의 장바구니를 불러왔습니다.",
                "불러오기 성공",
                JOptionPane.INFORMATION_MESSAGE
            );
            return true;
        } else {
            // 2-4. (실패) View(JOptionPane) 알림
            JOptionPane.showMessageDialog(
                mainFrame, 
                "해당 정보로 저장된 장바구니를 찾을 수 없습니다.",
                "불러오기 실패",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
    }

    /**
     * [5단계 신규] DeleteCartPanel의 '삭제하기' 시 호출됩니다.
     * @param name 삭제할 고객 이름
     * @param phone 삭제할 고객 전화번호
     * @return View(DeleteCartPanel)가 화면 전환 여부를 판단할 boolean 값
     */
    public boolean deleteCart(String name, String phone) {
        // 1. Service(CartFileManager)에 작업 위임
        boolean success = cartFileManager.deleteCart(name, phone);

        // 2. Controller가 Service의 결과에 따라 View(JOptionPane)를 제어
        if (success) {
            JOptionPane.showMessageDialog(
                mainFrame, 
                "'" + name + "'님의 저장된 내역을 삭제했습니다.",
                "삭제 완료",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
             JOptionPane.showMessageDialog(
                mainFrame, 
                "해당 정보로 저장된 장바구니를 찾을 수 없습니다.",
                "삭제 실패",
                JOptionPane.WARNING_MESSAGE
            );
        }
        
        // 3. View에 결과 반환
        return success;
    }


    // --- Getter ---
    public List<Store> getStores() {
        return this.stores;
    }
    public Cart getCart() {
        return this.currentCart;
    }
}