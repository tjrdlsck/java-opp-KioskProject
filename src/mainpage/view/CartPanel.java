package mainpage.view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane; 
import javax.swing.JTextArea;
import javax.swing.JTextField; // [5단계 신규]
import javax.swing.SwingConstants;

import mainpage.controller.KioskAppManager;
import mainpage.model.Cart;
import mainpage.model.CartItem;
import mainpage.model.Product;
import mainpage.util.InputValidator;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane; 
import javax.swing.BoxLayout; // [5단계 신규]
import java.awt.BorderLayout;    
import java.awt.Font;            
import java.awt.GridLayout;
import java.awt.Component; // [5단계 신규]
import java.awt.event.ComponentAdapter; 
import java.awt.event.ComponentEvent;   
import java.util.List; 

/**
 * [View - Panel 4 (3, 4단계 구현)]
 * [5단계 수정]
 * - '장바구니 저장하기' 버튼에 ActionListener 추가
 * - processSaveCart() 헬퍼 메소드 구현 (JOptionPane + JTextFields)
 * - InputValidator를 사용한 입력값 검증 로직 추가
 */
public class CartPanel extends JPanel {

    private KioskAppManager manager;

    // --- [3단계 신규] View 컴포넌트 ---
    private JTextArea cartTextArea;
    private JLabel totalPriceLabel;
    private JButton orderButton;
    private JButton saveButton;
    private JButton backButton;

    public CartPanel(KioskAppManager manager) {
        this.manager = manager;

        // 1. 레이아웃 설정 (BorderLayout)
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 2. 상단: 타이틀
        JLabel titleLabel = new JLabel("장바구니 확인", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 3. 중앙: 장바구니 내역 (스크롤 가능)
        cartTextArea = new JTextArea();
        cartTextArea.setEditable(false);
        cartTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
        JScrollPane scrollPane = new JScrollPane(cartTextArea);
        add(scrollPane, BorderLayout.CENTER);

        // 4. 하단: 총액 및 버튼
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        
        // 4-1. 총액 표시 레이블
        totalPriceLabel = new JLabel("총 주문 금액: 0원");
        totalPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalPriceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        southPanel.add(totalPriceLabel, BorderLayout.NORTH);

        // 4-2. 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        orderButton = new JButton("주문하기"); 
        saveButton = new JButton("장바구니 저장하기"); // [5단계] 문구 수정
        backButton = new JButton("메뉴 더 담기 (메인으로)");
        
        buttonPanel.add(orderButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(southPanel, BorderLayout.SOUTH);

        // 5. 이벤트 리스너 설정

        // [4단계] '주문하기' 버튼 클릭 시
        orderButton.addActionListener(e -> {
            handlePlaceOrder(); 
        });

        // [5단계 핵심] '장바구니 저장하기' 버튼 클릭 시
        saveButton.addActionListener(e -> {
            processSaveCart(); // [5단계]
        });

        // '메뉴 더 담기' 버튼 클릭 시 (기존 로직 유지)
        backButton.addActionListener(e -> manager.navigateTo("MAIN_PAGE"));

        // [3단계 핵심]
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateCartView();
            }
        });
    }

    /**
     * [4단계 신규 헬퍼 메소드]
     * '주문하기' 버튼 클릭 시 호출됩니다.
     */
    private void handlePlaceOrder() {
        // (4단계에서 완료된 로직)
        Cart cart = manager.getCart();
        long totalPrice = cart.getTotalPrice();
        
        String confirmMsg = String.format("총 %,d원을 결제하시겠습니까?", totalPrice);
        
        int choice = JOptionPane.showConfirmDialog(
            this, 
            confirmMsg,
            "결제 확인",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            manager.placeOrder();
        }
    }

    /**
     * [5단계 신규 헬퍼 메소드]
     * '장바구니 저장하기' 버튼 클릭 시 호출됩니다.
     * Kiosk.java의 processSaveCart() 로직을 GUI 버전으로 구현합니다.
     */
    private void processSaveCart() {
        // 1. [5단계 계획] 이름과 전화번호 입력을 위한 동적 JPanel 생성
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        
        JTextField nameField = new JTextField(15);
        JTextField phoneField = new JTextField(15);
        
        inputPanel.add(new JLabel("저장할 고객의 이름을 입력하세요:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("전화번호를 입력하세요 (예: 01012345678):"));
        inputPanel.add(phoneField);

        // 2. [5단계 계획] JOptionPane에 JPanel 삽입
        int result = JOptionPane.showConfirmDialog(
            this, // 부모 컴포넌트
            inputPanel, 
            "장바구니 저장", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String phone = phoneField.getText();

            // 3. [5단계 계획] InputValidator로 입력값 검증
            if (!InputValidator.isValidName(name)) {
                JOptionPane.showMessageDialog(this, "이름이 유효하지 않습니다. 다시 시도해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!InputValidator.isValidPhoneNumber(phone)) {
                JOptionPane.showMessageDialog(this, "전화번호 형식이 잘못되었습니다 (예: 01012345678).", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. (검증 통과) Controller의 비즈니스 로직 호출
            manager.saveCart(name, phone); //
        }
    }
    
    /**
     * [3단계 신규 헬퍼 메소드]
     * (3, 4단계에서 완료된 로직)
     */
    private void updateCartView() {
        Cart cart = manager.getCart();
        List<CartItem> items = cart.getItems();
        long total = cart.getTotalPrice();

        cartTextArea.setText("");

        if (items.isEmpty()) {
            cartTextArea.setText("\n   장바구니가 비어있습니다.\n   상품을 추가해주세요.");
            cartTextArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
            
            orderButton.setEnabled(false);
            saveButton.setEnabled(false); // [3단계 계획] 비활성화
            
        } else {
            cartTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
            
            for (CartItem item : items) {
                Product product = item.getProduct();
                String itemText = String.format("- %-20s | %3d개 | %,8d원\n",
                        product.getName(),     
                        item.getQuantity(),    
                        item.getTotalPrice()); 
                cartTextArea.append(itemText);
            }
            
            orderButton.setEnabled(true);
            saveButton.setEnabled(true); // [3단계 계획] 활성화
        }

        totalPriceLabel.setText(String.format("총 주문 금액: %,d원", total));
        cartTextArea.setCaretPosition(0);
    }
}