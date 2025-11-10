package mainpage.view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import mainpage.controller.KioskAppManager;
import mainpage.model.CartItem;
import mainpage.model.Order;
import mainpage.model.Product;

import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Color;
import java.time.LocalDateTime; // [4단계 신규]
import java.time.format.DateTimeFormatter; // [4단계 신규]
import java.util.List;

/**
 * [View - Panel 5 (4단계 수정)]
 * 1단계 Skeleton -> 4단계 View 구현
 * 1. KioskAppManager로부터 'Order' 객체를 전달받음
 * 2. setOrder() 메소드를 통해 주문 내역(영수증)을 JTextArea에 표시
 */
public class OrderCompletePanel extends JPanel {

    private KioskAppManager manager;
    private JTextArea receiptTextArea; // [4단계 신규] 영수증 표시 영역

    public OrderCompletePanel(KioskAppManager manager) {
        this.manager = manager;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. 상단 타이틀
        JLabel titleLabel = new JLabel("주문이 완료되었습니다", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 중앙: 영수증 내역 (스크롤 가능)
        receiptTextArea = new JTextArea();
        receiptTextArea.setEditable(false);
        // [4단계] 3단계 CartPanel과 동일한 폰트 적용
        receiptTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
        
        JScrollPane scrollPane = new JScrollPane(receiptTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);

        // 3. 하단: '메인으로 돌아가기' 버튼
        JButton homeButton = new JButton("메인 화면으로 돌아가기");
        homeButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(homeButton, BorderLayout.SOUTH);

        // '메인으로' 버튼 클릭 시 -> "MAIN_PAGE"로 이동
        // [4단계 참고] KioskAppManager의 goHome()은 resetLoginState()를
        // 호출하므로, 이 버튼을 누르면 로그인 상태가 초기화됨 (정상 동작)
        homeButton.addActionListener(e -> manager.goHome());
    }
    
    /**
     * [4단계 신규 헬퍼 메소드]
     * KioskAppManager가 placeOrder() 실행 후 호출합니다.
     * Order.java의 displayOrderDetails() 로직을 GUI 버전으로 구현
     * @param order KioskAppManager가 생성한 Order 객체
     */
    public void setOrder(Order order) {
        if (order == null) {
            receiptTextArea.setText("오류: 주문 정보를 불러올 수 없습니다.");
            return;
        }
        
        // 1. JTextArea 초기화
        receiptTextArea.setText("");

        // 2. 주문 시각 포맷팅 (Order.java 로직)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = order.getOrderDateTime().format(formatter);

        // 3. 영수증 헤더 추가
        receiptTextArea.append("====================================\n");
        receiptTextArea.append("           주 문 완 료 (영수증)\n");
        receiptTextArea.append("====================================\n");
        receiptTextArea.append("주문 번호: " + order.getOrderNumber() + "\n");
        receiptTextArea.append("주문 시각: " + formattedDateTime + "\n");
        receiptTextArea.append("------------------------------------\n");
        receiptTextArea.append("[주문 내역]\n");

        // 4. 주문 내역 본문 추가
        List<CartItem> items = order.getOrderedItems();
        for (CartItem item : items) {
            Product product = item.getProduct();
            
            // CartPanel의 포맷과 동일하게
            String itemText = String.format("- %-20s | %3d개 | %,8d원\n",
                    product.getName(),     
                    item.getQuantity(),    
                    item.getTotalPrice()); 
            receiptTextArea.append(itemText);
        }

        // 5. 영수증 푸터 추가
        receiptTextArea.append("------------------------------------\n");
        String totalText = String.format("결제 금액: %,d원\n", order.getTotalPrice());
        receiptTextArea.append(totalText);
        receiptTextArea.append("====================================\n");
        
        // 6. 스크롤 맨 위로
        receiptTextArea.setCaretPosition(0);
    }
}