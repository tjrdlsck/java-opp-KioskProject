package ui;

// [계획 5.3] JFrame 관련 import는 모두 제거됩니다.
import mainpage.DataLoader;
import mainpage.Store;
import mainpage.Cart;
import mainpage.CartItem;
import mainpage.Product;
import mainpage.CartFileManager;
import mainpage.Order;
import java.util.regex.Pattern;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.*;
import java.awt.*;
// [계획 5.3] List import가 누락되어 추가합니다.
import java.util.List; 

/**
 * [계획 5.3] 'JFrame'에서 'JPanel'로 변경됩니다.
 * MainApplication의 CardLayout에 표시될 '메인 메뉴' 패널입니다.
 */
// 1. 'extends JFrame' -> 'extends JPanel'
public class MenuScreen extends JPanel {
    private final CafeMenuPanel cafeMenuPanel;
    private final OrderPanel orderPanel;
    private JScrollPane cafeScroll;
    private JPanel cafeScrollContainer;

    // [수정] final 키워드 제거 (Plan 3에서 주입받는 로직은 동일)
    private List<Store> stores;

    private final CartFileManager cartFileManager;
    private String currentCustomerName = null;
    private String currentCustomerPhone = null;
    
    /**
     * [계획 5.3] JPanel의 생성자입니다.
     * JFrame 관련 설정을 모두 제거하고, 패널의 레이아웃만 설정합니다.
     *
     * @param allStores      MainApplication에서 로드한 '전체' 가게 목록 (상단 스크롤용)
     * @param initialStore   [참고] 이 생성자에서는 이 파라미터를 '무시'합니다.
     * loadCafeMenu()가 외부(MainApplication)에서 호출될 것이기 때문입니다.
     */
    public MenuScreen(List<Store> allStores, Store initialStore) {
        // 2. 'JFrame' 관련 설정(setTitle, setSize, setDefaultCloseOperation 등) 모두 삭제

        // [수정] 패널 자체의 레이아웃 설정
        int width = 648; // 패널 크기 기준을 위해 변수 유지
        setLayout(new BorderLayout());
        
        this.cartFileManager = new CartFileManager();
        
        // [유지] 주입받은 'allStores' 리스트를 필드에 할당 (Plan 3)
        this.stores = allStores; 

        // ✅ 상단: 카페 선택 (좌우 스크롤 가능)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(230, 230, 230));

        JButton leftArrow = createStyledArrowButton("◀");  
        JButton rightArrow = createStyledArrowButton("▶"); 
        
        cafeScrollContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cafeScrollContainer.setBackground(new Color(230, 230, 230));
        
        // [유지] 주입받은 'this.stores' 리스트로 상단 버튼 생성
        if (this.stores != null) {
            for (Store store : this.stores) {
                String cafeName = store.getName(); 
                
                JButton cafeBtn = new JButton(cafeName);
                cafeBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
                cafeBtn.setPreferredSize(new Dimension(144, 40));
                
                // [유지] 클릭 시 해당 가게 로드
                cafeBtn.addActionListener(e -> loadCafeMenu(store));

                cafeScrollContainer.add(cafeBtn);
            }
        }

        cafeScroll = new JScrollPane(
                cafeScrollContainer,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        cafeScroll.setBorder(null);
        cafeScroll.setWheelScrollingEnabled(false);
        leftArrow.addActionListener(e -> scrollCafeList(-150));
        rightArrow.addActionListener(e -> scrollCafeList(150));

        topPanel.add(leftArrow, BorderLayout.WEST);
        topPanel.add(cafeScroll, BorderLayout.CENTER);
        topPanel.add(rightArrow, BorderLayout.EAST);
        
        // 3. 'JFrame'의 add() 대신 'JPanel'의 add() 사용
        add(topPanel, BorderLayout.NORTH);

        // ✅ 중앙: 메뉴 패널
        cafeMenuPanel = new CafeMenuPanel();
        add(cafeMenuPanel, BorderLayout.CENTER);

        // ✅ 하단: 주문 패널
        orderPanel = new OrderPanel();
        
        // (하단 패널 로직은 원본과 동일)
        JPanel rightButtons = new JPanel(new GridLayout(2, 2, 10, 10)); 
        rightButtons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton saveCartBtn = new JButton("장바구니 저장");
        JButton clearBtn = new JButton("전체삭제");
        JButton loadCartBtn = new JButton("장바구니 불러오기");
        JButton orderBtn = new JButton("주문하기");
        
        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);
        saveCartBtn.setFont(buttonFont);
        clearBtn.setFont(buttonFont);
        loadCartBtn.setFont(buttonFont);
        orderBtn.setFont(buttonFont);

        rightButtons.add(saveCartBtn);
        rightButtons.add(clearBtn);
        rightButtons.add(loadCartBtn);
        rightButtons.add(orderBtn);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        bottomPanel.add(orderPanel);
        bottomPanel.add(rightButtons);
        bottomPanel.setPreferredSize(new Dimension(width, 200));
        add(bottomPanel, BorderLayout.SOUTH);

        // (버튼 액션 리스너 로직은 원본과 동일)
        saveCartBtn.addActionListener(e -> processSaveCart());
        clearBtn.addActionListener(e -> orderPanel.clearOrders()); 
        loadCartBtn.addActionListener(e -> processLoadCart());
        orderBtn.addActionListener(e -> processPlaceOrder());
        
        // [계획 5.3] 핵심 변경:
        // 생성자에서는 더 이상 'initialStore'를 로드하지 않습니다.
        // (MainApplication이 showMenuScreen()을 호출할 때 loadCafeMenu()를
        // 대신 호출해 줄 것이기 때문입니다.)
        // (삭제) if (initialStore != null) { ... }
        
        // 4. 'setVisible(true)' 삭제 (패널의 표시는 MainApplication이 결정)
    }

    // [계획 5.3] Overloaded constructor (plan 3) 삭제 (이미 MainApplication 생성자로 대체됨)

    private void scrollCafeList(int offset) {
        JScrollBar bar = cafeScroll.getHorizontalScrollBar();
        int newValue = bar.getValue() + offset;
        if (newValue < 0) newValue = 0;
        if (newValue > bar.getMaximum()) newValue = bar.getMaximum();
        bar.setValue(newValue);
    }

    /**
     * [계획 5.3] 핵심 변경:
     * 이 메소드의 가시성(visibility)을 'private'에서 'public'으로 변경합니다.
     * MainApplication이 이 패널로 전환하기 '직전'에 이 메소드를 호출하여
     * 선택된 가게의 메뉴를 로드해야 하기 때문입니다.
     *
     * @param store 사용자가 선택한 가게
     */
    public void loadCafeMenu(Store store) {
        
        // [유지] Store 객체와 람다를 CafeMenuPanel로 전달
        cafeMenuPanel.loadCafeMenu(store, orderPanel::addOrder);
    }
    
    // (createStyledArrowButton 메소드는 원본과 동일)
    private JButton createStyledArrowButton(String unicodeArrow) {
        JButton btn = new JButton(unicodeArrow);
        btn.setForeground(Color.DARK_GRAY);
        btn.setBackground(new Color(230, 230, 230));
        btn.setFocusPainted(false);
        btn.setBorder(null);
        btn.setContentAreaFilled(false);
        Dimension arrowSize = new Dimension(35, 35);
        btn.setPreferredSize(arrowSize);
        btn.setMargin(new Insets(0, 0, 0, 0)); 
        return btn;
    }
    
    // (getValidPhoneNumber 메소드는 원본과 동일)
    private String getValidPhoneNumber(String initialMessage) {
        String phoneRegex = "^010\\d{8}$";
        String phone;

        while (true) {
            phone = JOptionPane.showInputDialog(this, initialMessage, "전화번호 입력", JOptionPane.QUESTION_MESSAGE);
            
            if (phone == null) {
                return null; 
            }

            if (Pattern.matches(phoneRegex, phone)) {
                return phone; 
            } else {
                JOptionPane.showMessageDialog(this,
                        "잘못된 형식입니다. '010'으로 시작하는 11자리 숫자를 입력해주세요.\n(예: 01012345678)",
                        "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // (processSaveCart 메소드는 원본과 동일)
    private void processSaveCart() {
        Cart cart = orderPanel.getCart();
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "장바구니가 비어있어 저장할 수 없습니다.", "저장 실패", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String name = JOptionPane.showInputDialog(this, "저장할 고객의 이름을 입력하세요:", "이름 입력", JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.trim().isEmpty()) {
            return; 
        }
        String phone = getValidPhoneNumber("고객의 전화번호를 입력하세요:");
        if (phone == null) return;
        cartFileManager.saveCart(cart, name, phone);
        JOptionPane.showMessageDialog(this, name + "님의 장바구니가 저장되었습니다.", "저장 완료", JOptionPane.INFORMATION_MESSAGE);
    }

    // (processLoadCart 메소드는 원본과 동일)
    private void processLoadCart() {
        String name = JOptionPane.showInputDialog(this, "불러올 고객의 이름을 입력하세요:", "이름 입력", JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.trim().isEmpty()) return;
        String phone = getValidPhoneNumber("고객의 전화번호를 입력하세요:");
        if (phone == null) return;
        Cart loadedCart = cartFileManager.loadCart(name, phone);
        if (loadedCart != null) {
            orderPanel.clearOrders(); 
            for (CartItem item : loadedCart.getItems()) {
                Product p = item.getProduct();
                for (int i = 0; i < item.getQuantity(); i++) {
                    orderPanel.addOrder(p);
                }
            }
            this.currentCustomerName = name;
            this.currentCustomerPhone = phone;
            JOptionPane.showMessageDialog(this, name + "님의 장바구니를 불러왔습니다.", "불러오기 성공", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "해당 정보로 저장된 장바구니를 찾을 수 없습니다.", "불러오기 실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalTime getPickupTime() {
        LocalTime pickupTime = null;
        while (pickupTime == null) {
            String timeStr = JOptionPane.showInputDialog(this, "픽업 희망 시간을 입력하세요 (HH:mm 형식, 예: 14:30)", "픽업 시간 선택", JOptionPane.QUESTION_MESSAGE);
            if (timeStr == null) { // 사용자가 '취소'를 누른 경우
                return null;
            }
            try {
                // DateTimeFormatter를 사용하여 HH:mm 형식으로만 파싱되도록 엄격하게 설정
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime parsedTime = LocalTime.parse(timeStr, timeFormatter);

                if (parsedTime.isBefore(LocalTime.now())) {
                    JOptionPane.showMessageDialog(this, "픽업 시간은 현재 시간 이후여야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    pickupTime = parsedTime;
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "시간 형식이 잘못되었습니다. HH:mm 형식으로 입력해주세요. (예: 14:30)", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
        return pickupTime;
    }

    // (processPlaceOrder 메소드는 원본과 동일)
    private void processPlaceOrder() {
        if (orderPanel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "주문 내역이 없습니다.");
            return;
        }

        // 1. 픽업 시간 입력받기
        LocalTime pickupTime = getPickupTime();
        if (pickupTime == null) { // 사용자가 픽업 시간 입력을 취소한 경우
            JOptionPane.showMessageDialog(this, "주문이 취소되었습니다.", "주문 취소", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String formattedPickupTime = pickupTime.format(DateTimeFormatter.ofPattern("HH:mm"));


        Cart cart = orderPanel.getCart();
        StringBuilder sb = new StringBuilder("🧾 주문내역\n\n");
        for (CartItem item : cart.getItems()) {
            Product p = item.getProduct();
            sb.append(String.format("- %s (%,d원) x %d개 = %,d원\n",
                    p.getName(), p.getPrice(), item.getQuantity(), item.getTotalPrice()));
        }
        sb.append("\n--------------------\n");
        sb.append(String.format("총 결제 금액: %,d원\n", cart.getTotalPrice()));
        sb.append(String.format("픽업 희망 시간: %s\n\n", formattedPickupTime));
        sb.append("이대로 주문하시겠습니까?");

        int choice = JOptionPane.showConfirmDialog(this, sb.toString(), "주문 확인", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            Order newOrder = new Order(cart, pickupTime);
            newOrder.displayOrderDetails();
            if (this.currentCustomerName != null) {
                cartFileManager.deleteCart(this.currentCustomerName, this.currentCustomerPhone);
                JOptionPane.showMessageDialog(this,
                        "주문이 완료되어 '" + this.currentCustomerName + "'님의 저장된 장바구니도 삭제했습니다.",
                        "저장된 내역 삭제", JOptionPane.INFORMATION_MESSAGE);
                this.currentCustomerName = null;
                this.currentCustomerPhone = null;
            }
            JOptionPane.showMessageDialog(this,
                    "주문이 완료되었습니다. (주문번호: " + newOrder.getOrderNumber() + ")",
                    "주문 완료", JOptionPane.INFORMATION_MESSAGE);
            orderPanel.clearOrders();
        }
    }
    
    // 5. 'main' 메소드 완전 삭제 (MainApplication.java가 유일한 진입점)
}