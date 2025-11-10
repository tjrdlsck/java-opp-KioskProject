package mainpage;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

/**
 * [View - Panel 5 (Skeleton)]
 * 주문 완료 화면.
 * 1단계에서는 '메인으로 돌아가기' 기능만 구현합니다.
 */
public class OrderCompletePanel extends JPanel {

    private KioskAppManager manager;

    public OrderCompletePanel(KioskAppManager manager) {
        this.manager = manager;
        
        add(new JLabel("주문 완료 패널 (4단계에서 구현)"));

        JButton homeButton = new JButton("메인 화면으로 돌아가기");
        add(homeButton);

        // '메인으로' 버튼 클릭 시 -> "MAIN_PAGE"로 이동
        homeButton.addActionListener(e -> manager.navigateTo("MAIN_PAGE"));
    }
}