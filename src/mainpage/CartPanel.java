package mainpage;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane; // [3단계] '저장' 버튼 알림용
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter; // [3단계 신규]
import java.awt.event.ComponentEvent;  // [3단계 신규]
import java.util.List;

/**
 * [View - Panel 4 (3단계 수정)]
 * 장바구니 확인 및 주문 화면.
 * 1. JTextArea와 JScrollPane을 이용해 장바구니 내역 표시
 * 2. componentShown 이벤트를 감지하여 내역 실시간 갱신
 * 3. 장바구니 상태에 따라 버튼 활성화/비활성화
 */
public class CartPanel extends JPanel {

    private KioskAppManager manager;
    
    // [3단계 신규] View 컴포넌트
    private JTextArea cartTextArea;
    private JLabel totalPriceLabel;
    
    // [3단계 신규] 버튼 상태 관리를 위해 필드로 변경
    private JButton orderButton;
    private JButton saveButton;
    private JButton backButton;

    public CartPanel(KioskAppManager manager) {
        this.manager = manager;

        // 레이아웃 설정 (상단, 중앙, 하단)
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. 상단 타이틀
        JLabel titleLabel = new JLabel("장바구니 확인 및 주문", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 중앙: 장바구니 내역 (스크롤 가능)
        cartTextArea = new JTextArea();
        cartTextArea.setEditable(false); // 사용자가 직접 수정 불가
        cartTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // 고정폭 글꼴
        
        JScrollPane scrollPane = new JScrollPane(cartTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);

        // 3. 하단: 총액 및 버튼
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        // 3-1. 총액 레이블
        totalPriceLabel = new JLabel("총 주문 금액: 0원", SwingConstants.RIGHT);
        totalPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        bottomPanel.add(totalPriceLabel, BorderLayout.NORTH);

        // 3-2. 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        orderButton = new JButton("주문하기");
        saveButton = new JButton("장바구니 저장하기");
        backButton = new JButton("메뉴 더 담기 (메인으로)");
        
        Font buttonFont = new Font("SansSerif", Font.BOLD, 14);
        orderButton.setFont(buttonFont);
        saveButton.setFont(buttonFont);
        backButton.setFont(buttonFont);

        buttonPanel.add(orderButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // 4. [3단계 핵심] 이벤트 리스너 설정
        
        // '주문하기' 버튼 (4단계에서 KioskAppManager.placeOrder() 호출로 변경 예정)
        orderButton.addActionListener(e -> {
            // (4단계 구현)
            manager.navigateTo("ORDER_COMPLETE");
        });
        
        // '장바구니 저장' 버튼 (4단계에서 구현 예정)
        saveButton.addActionListener(e -> {
            // (4단계 구현)
            JOptionPane.showMessageDialog(this, 
                    "장바구니 저장 기능은 4단계에서 구현됩니다.", 
                    "준비 중", 
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // '메뉴 더 담기' 버튼
        backButton.addActionListener(e -> manager.goHome());

        // [3단계 핵심] 이 패널(CartPanel)이 화면에 '보여질 때마다'
        // updateCartView() 메소드를 호출하도록 리스너 추가
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // 패널이 보일 때마다 장바구니 뷰를 갱신
                updateCartView();
            }
        });
    }
    
    /**
     * [3단계 신규 헬퍼 메소드]
     * KioskAppManager로부터 현재 Cart [cite: KioskAppManager.java, Cart.java] 상태를 가져와
     * JTextArea와 JLabel을 갱신합니다.
     */
    public void updateCartView() {
        // 1. Controller로부터 현재 Cart 모델을 가져옴
        Cart cart = manager.getCart();
        List<CartItem> items = cart.getItems();

        // 2. JTextArea 초기화
        cartTextArea.setText(""); 

        if (items.isEmpty()) {
            // 3-1. 장바구니가 비어있을 경우
            cartTextArea.setForeground(Color.GRAY);
            cartTextArea.setText("\n    장바구니가 비어있습니다.\n    상품을 추가해주세요.");
            
            // [신규] 비어있으면 주문/저장 버튼 비활성화
            orderButton.setEnabled(false);
            saveButton.setEnabled(false);
            
        } else {
            // 3-2. 장바구니에 상품이 있을 경우
            cartTextArea.setForeground(Color.BLACK);
            // JTextArea에 항목 추가
            for (CartItem item : items) {
                Product product = item.getProduct();
                
                // 콘솔 출력 형식과 유사하게 문자열 포맷팅
                String itemText = String.format("- %-20s | %3d개 | %,8d원\n",
                        product.getName(),      // 상품 이름 [cite: Product.java]
                        item.getQuantity(),     // 수량 [cite: CartItem.java]
                        item.getTotalPrice());  // 해당 항목의 총 가격 [cite: CartItem.java]
                
                cartTextArea.append(itemText);
            }
            
            // [신규] 상품이 있으므로 주문/저장 버튼 활성화
            orderButton.setEnabled(true);
            saveButton.setEnabled(true);
        }

        // 4. 총액 JLabel 갱신
        totalPriceLabel.setText(String.format("총 주문 금액: %,d원", cart.getTotalPrice()));
        
        // (선택) JTextArea가 항상 맨 위에서 시작하도록
        cartTextArea.setCaretPosition(0); 
    }
}