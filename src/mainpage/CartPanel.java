package mainpage;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

/**
 * [View - Panel 4 (Skeleton)]
 * 장바구니 확인 및 주문 화면.
 * 1단계에서는 네비게이션 기능만 구현합니다.
 */
public class CartPanel extends JPanel {

    private KioskAppManager manager;

    public CartPanel(KioskAppManager manager) {
        this.manager = manager;

        add(new JLabel("장바구니 패널 (3단계에서 구현)"));

        JButton orderButton = new JButton("주문하기 (OrderCompletePanel로 이동)");
        JButton saveButton = new JButton("장바구니 저장하기 (미구현)");
        JButton backButton = new JButton("메뉴 더 담기 (메인으로)");
        
        add(orderButton);
        add(saveButton);
        add(backButton);

        // '주문하기' 버튼 클릭 시
        orderButton.addActionListener(e -> manager.navigateTo("ORDER_COMPLETE"));

        // '메뉴 더 담기' 버튼 클릭 시
        backButton.addActionListener(e -> manager.navigateTo("MAIN_PAGE"));
        
        // (저장 버튼 로직은 4단계에서 구현)
    }
    
    // (3단계에서 구현)
    // public void updateCartView() {
    //     // 이 패널이 보일 때마다 호출되어
    //     // manager.getCart()의 최신 정보로 JTextArea와 총액 JLabel을 갱신합니다.
    // }
}