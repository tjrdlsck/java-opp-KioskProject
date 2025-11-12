package ui;

import mainpage.DataLoader;
import mainpage.Store;
import mainpage.Cart;
import mainpage.CartItem;
import mainpage.Product;
// ✅ [신규 추가] Kiosk의 기능을 가져옵니다.
import mainpage.CartFileManager;
import mainpage.Order;
import java.util.regex.Pattern; // ✅ [신규 추가] 전화번호 유효성 검사

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MenuScreen extends JFrame {
    private final CafeMenuPanel cafeMenuPanel;
    private final OrderPanel orderPanel;
    private JScrollPane cafeScroll;
    private JPanel cafeScrollContainer;

    private final List<Store> stores;

    // ✅ [신규 추가] CartFileManager와 로그인 상태 필드
    private final CartFileManager cartFileManager;
    private String currentCustomerName = null;
    private String currentCustomerPhone = null;
    
    public MenuScreen() {
        setTitle("키오스크 메뉴");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int width = 648;
        int height = 1152;
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // ✅ [신규 추가] CartFileManager 필드 초기화
        this.cartFileManager = new CartFileManager();
        // ✅ 1. 'mainpage'의 DataLoader를 사용해 모든 가게 정보를 로드합니다.
        // (이 코드는 생성자 상단, 혹은 'topPanel' 로직 이전에 위치해야 합니다.)
        this.stores = new DataLoader().loadStores(); // ✅ [수정] 필드에 로드 결과 저장

        // ✅ 상단: 카페 선택 (좌우 스크롤 가능)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(230, 230, 230));

        // 🔹 화살표 버튼 크기 (35x35) + margin 0으로 설정
        JButton leftArrow = createStyledArrowButton("◀");  // ✅ "‹" -> "◀"
        JButton rightArrow = createStyledArrowButton("▶"); // ✅ "›" -> "▶"
        
        // 카페 버튼을 담을 패널
        cafeScrollContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cafeScrollContainer.setBackground(new Color(230, 230, 230));

        // ⛔️ [삭제] 기존 File 기반 로직
        // File menuFolder = new File(System.getProperty("user.dir") + "/menuData");
        // File[] txtFiles = menuFolder.listFiles((dir, name) -> name.endsWith(".txt"));
        
        // ✅ 2. 'this.stores' 리스트를 기반으로 버튼을 생성합니다.
        if (this.stores != null) {
            // ⛔️ [변경] for (File f : txtFiles) {
            for (Store store : this.stores) {
                // ⛔️ [변경] String cafeName = f.getName().replace(".txt", "");
                String cafeName = store.getName(); // ✅ Store 객체에서 이름 가져오기
                
                JButton cafeBtn = new JButton(cafeName);
                cafeBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
                // 🔹 카페 버튼 크기 유지 (144x40)
                cafeBtn.setPreferredSize(new Dimension(144, 40));
                
                // ⛔️ [변경] f.getName() (String) 대신 store (Store 객체)를 전달
                // cafeBtn.addActionListener(e -> loadCafeMenu(f.getName()));
                cafeBtn.addActionListener(e -> loadCafeMenu(store)); // ✅ [수정]
                
                cafeScrollContainer.add(cafeBtn);
            }
        }

        // 🔹 스크롤바 숨김
        cafeScroll = new JScrollPane(
                cafeScrollContainer,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        cafeScroll.setBorder(null);
        cafeScroll.setWheelScrollingEnabled(false);
        // 🔹 좌우 버튼으로 스크롤 이동
        leftArrow.addActionListener(e -> scrollCafeList(-150));
        rightArrow.addActionListener(e -> scrollCafeList(150));

        topPanel.add(leftArrow, BorderLayout.WEST);
        topPanel.add(cafeScroll, BorderLayout.CENTER);
        topPanel.add(rightArrow, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ✅ 중앙: 메뉴 패널
        cafeMenuPanel = new CafeMenuPanel();
        add(cafeMenuPanel, BorderLayout.CENTER);

        // ✅ 하단: 주문 패널
        orderPanel = new OrderPanel();
// ⛔️ [전면 수정] 하단 패널 로직 시작
        
        // 1. 오른쪽 버튼 패널을 2x2 그리드로 생성
        JPanel rightButtons = new JPanel(new GridLayout(2, 2, 10, 10)); // 2행 2열, 간격 10
        rightButtons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 2. 4개의 버튼 생성
        JButton saveCartBtn = new JButton("장바구니 저장");
        JButton clearBtn = new JButton("전체삭제");
        JButton loadCartBtn = new JButton("장바구니 불러오기");
        JButton orderBtn = new JButton("주문하기");
        
        // (선택적) 폰트 설정
        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);
        saveCartBtn.setFont(buttonFont);
        clearBtn.setFont(buttonFont);
        loadCartBtn.setFont(buttonFont);
        orderBtn.setFont(buttonFont);

        // 3. 2x2 그리드에 버튼 추가
        rightButtons.add(saveCartBtn);
        rightButtons.add(clearBtn);
        rightButtons.add(loadCartBtn);
        rightButtons.add(orderBtn);

        // 4. 하단 패널(bottomPanel)에 orderPanel과 rightButtons를 1:1로 배치
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        bottomPanel.add(orderPanel);
        bottomPanel.add(rightButtons);
        bottomPanel.setPreferredSize(new Dimension(width, 200));
        add(bottomPanel, BorderLayout.SOUTH);

        // 5. 각 버튼에 새 액션 리스너 연결
        saveCartBtn.addActionListener(e -> processSaveCart());
        clearBtn.addActionListener(e -> orderPanel.clearOrders()); // (기존 로직)
        loadCartBtn.addActionListener(e -> processLoadCart());
        orderBtn.addActionListener(e -> processPlaceOrder());
        
        // ⛔️ [전면 수정] 하단 패널 로직 종료

        setVisible(true);
    }

    private void scrollCafeList(int offset) {
        JScrollBar bar = cafeScroll.getHorizontalScrollBar();
        int newValue = bar.getValue() + offset;
        if (newValue < 0) newValue = 0;
        if (newValue > bar.getMaximum()) newValue = bar.getMaximum();
        bar.setValue(newValue);
    }

    // ⛔️ [변경] private void loadCafeMenu(String fileName) {
    // ✅ Store 객체를 매개변수로 받도록 시그니처 변경
    private void loadCafeMenu(Store store) {
        
        // ⛔️ [변경] cafeMenuPanel.loadCafeMenu(fileName, orderPanel::addOrder);
        // ✅ Store 객체와 람다를 그대로 CafeMenuPanel로 전달
        // (이 시점에서 orderPanel::addOrder는 Consumer<Product>를 참조하게 됩니다.)
        cafeMenuPanel.loadCafeMenu(store, orderPanel::addOrder);
    }
    /**
     * [신규] 상단 스크롤용 모던 스타일 화살표 버튼을 생성합니다.
     * 배경과 테두리가 투명하며, 상단 패널의 배경색과 어우러집니다.
     *
     * @param unicodeArrow "◀" (왼쪽) 또는 "▶" (오른쪽) 문자
     * @return 스타일이 적용된 JButton
     */
    private JButton createStyledArrowButton(String unicodeArrow) {
    	// ✅ "◀", "▶" 문자를 사용합니다.
        JButton btn = new JButton(unicodeArrow);
        
        btn.setForeground(Color.DARK_GRAY);
        
        // [핵심] 배경색(230)과 동일하게 설정하여 '배경만 있는' 버튼 효과
        btn.setBackground(new Color(230, 230, 230));
        
        // 테두리 및 포커스 효과 제거
        btn.setFocusPainted(false);
        
        // ⛔️ [변경] btn.setBorderPainted(false);
        // ✅ [수정 1] Border 객체 자체를 'null'로 설정합니다.
        // 이는 테두리가 차지하는 '논리적 공간(Insets)'까지 0으로 만듭니다.
        btn.setBorder(null);
        
        // ✅ [수정 2] [가장 중요] 버튼의 '콘텐츠 영역'을 그리지 않도록 설정합니다.
        // 이로써 룩앤필(L&F)이 강제하는 내부 여백(Padding)을 무시하고
        // setMargin(0)이 100% 적용되도록 보장합니다.
        btn.setContentAreaFilled(false);
        
        // 버튼의 고정 크기 유지 (원본과 동일하게)
        Dimension arrowSize = new Dimension(35, 35);
        btn.setPreferredSize(arrowSize);
        
        // ✅ [필수] "..." 문제 해결을 위한 0 마진 설정 (원본 코드의 해결책)
        btn.setMargin(new Insets(0, 0, 0, 0)); 
        
        return btn;
    }
    /**
     * [신규] Kiosk 클래스의 유효성 검사 로직을 Swing용으로 포팅합니다.
     * 유효한 전화번호(01012345678)를 입력받을 때까지 반복합니다.
     *
     * @param initialMessage
     * @return 사용자가 입력한 유효한 전화번호, 취소 시 null
     */
    private String getValidPhoneNumber(String initialMessage) {
        String phoneRegex = "^010\\d{8}$"; // 010으로 시작하는 11자리 숫자
        String phone;

        while (true) {
            phone = JOptionPane.showInputDialog(this, initialMessage, "전화번호 입력", JOptionPane.QUESTION_MESSAGE);
            
            // 사용자가 '취소' 또는 'X' 버튼을 누른 경우
            if (phone == null) {
                return null; 
            }

            if (Pattern.matches(phoneRegex, phone)) {
                return phone; // 유효한 형식이면 반환
            } else {
                JOptionPane.showMessageDialog(this,
                        "잘못된 형식입니다. '010'으로 시작하는 11자리 숫자를 입력해주세요.\n(예: 01012345678)",
                        "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * [신규] '장바구니 저장' 버튼 로직
     */
    private void processSaveCart() {
        Cart cart = orderPanel.getCart();
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "장바구니가 비어있어 저장할 수 없습니다.", "저장 실패", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = JOptionPane.showInputDialog(this, "저장할 고객의 이름을 입력하세요:", "이름 입력", JOptionPane.QUESTION_MESSAGE);
        
        // 사용자가 '취소'를 누르거나 빈 이름을 입력한 경우
        if (name == null || name.trim().isEmpty()) {
            return; 
        }

        String phone = getValidPhoneNumber("고객의 전화번호를 입력하세요:");
        if (phone == null) return; // '취소'

        // Kiosk와 동일하게 CartFileManager 사용
        cartFileManager.saveCart(cart, name, phone);
        JOptionPane.showMessageDialog(this, name + "님의 장바구니가 저장되었습니다.", "저장 완료", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * [신규] '장바구니 불러오기' 버튼 로직
     */
    private void processLoadCart() {
        String name = JOptionPane.showInputDialog(this, "불러올 고객의 이름을 입력하세요:", "이름 입력", JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.trim().isEmpty()) return;

        String phone = getValidPhoneNumber("고객의 전화번호를 입력하세요:");
        if (phone == null) return;

        Cart loadedCart = cartFileManager.loadCart(name, phone);

        if (loadedCart != null) {
            // [중요] OrderPanel의 Cart 객체를 교체하는 대신,
            // 기존 패널의 API를 활용하여 항목을 '전송'합니다.
            
            // 1. 현재 UI의 장바구니를 비웁니다. (콘솔 출력 유지됨)
            orderPanel.clearOrders(); 
            
            // 2. 불러온 Cart에서 CartItem을 하나씩 꺼내어 UI에 다시 추가합니다.
            //    (콘솔 출력이 있는 addProduct를 수량만큼 호출)
            for (CartItem item : loadedCart.getItems()) {
                Product p = item.getProduct();
                for (int i = 0; i < item.getQuantity(); i++) {
                    orderPanel.addOrder(p); // OrderPanel의 public API 사용
                }
            }
            
            // 3. '로그인' 상태로 전환 (주문 완료 시 파일 삭제를 위함)
            this.currentCustomerName = name;
            this.currentCustomerPhone = phone;

            JOptionPane.showMessageDialog(this, name + "님의 장바구니를 불러왔습니다.", "불러오기 성공", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "해당 정보로 저장된 장바구니를 찾을 수 없습니다.", "불러오기 실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * [수정/분리] '주문하기' 버튼 로직 (Order 객체 생성 및 파일 삭제 로직 추가)
     */
    private void processPlaceOrder() {
        if (orderPanel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "주문 내역이 없습니다.");
            return;
        }
        
        Cart cart = orderPanel.getCart();

        // [1. 주문 내역 확인창 (이전 단계 로직 재활용)]
        StringBuilder sb = new StringBuilder("🧾 주문내역\n\n");
        for (CartItem item : cart.getItems()) {
            Product p = item.getProduct();
            sb.append(String.format("- %s (%,d원) x %d개 = %,d원\n",
                    p.getName(), p.getPrice(), item.getQuantity(), item.getTotalPrice()));
        }
        sb.append("\n--------------------\n");
        sb.append(String.format("총 결제 금액: %,d원\n\n", cart.getTotalPrice()));
        sb.append("이대로 주문하시겠습니까?");

        // [2. 주문 확정 (신규)]
        int choice = JOptionPane.showConfirmDialog(this, sb.toString(), "주문 확인", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            // [3. Order 객체 생성 (신규)]
            Order newOrder = new Order(cart);
            
            // ✅ [요청 사항] 콘솔 출력 로직 유지
            newOrder.displayOrderDetails(); 

            // [4. '로그인' 상태인 경우, 저장된 파일 삭제 (신규)]
            if (this.currentCustomerName != null) {
                cartFileManager.deleteCart(this.currentCustomerName, this.currentCustomerPhone);
                JOptionPane.showMessageDialog(this,
                        "주문이 완료되어 '" + this.currentCustomerName + "'님의 저장된 장바구니도 삭제했습니다.",
                        "저장된 내역 삭제", JOptionPane.INFORMATION_MESSAGE);
                
                // '로그아웃' 처리
                this.currentCustomerName = null;
                this.currentCustomerPhone = null;
            }

            // [5. 주문 완료 알림 및 장바구니 비우기 (신규)]
            JOptionPane.showMessageDialog(this,
                    "주문이 완료되었습니다. (주문번호: " + newOrder.getOrderNumber() + ")",
                    "주문 완료", JOptionPane.INFORMATION_MESSAGE);
            
            orderPanel.clearOrders(); // 장바구니 비우기 (콘솔 출력 유지됨)
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MenuScreen::new);
    }
}
