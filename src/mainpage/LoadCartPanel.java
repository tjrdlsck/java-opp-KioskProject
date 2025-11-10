package mainpage;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

/**
 * [View - Panel 6 (Skeleton)]
 * 장바구니 불러오기 화면 (이름, 전화번호 입력).
 * 1단계에서는 '뒤로 가기' 기능만 구현합니다.
 */
public class LoadCartPanel extends JPanel {

    private KioskAppManager manager;

    public LoadCartPanel(KioskAppManager manager) {
        this.manager = manager;

        add(new JLabel("장바구니 불러오기 패널 (4단계에서 구현)"));
        
        // (4단계에서 이름/전화번호 JTextFie_ld 추가)

        JButton loadButton = new JButton("불러오기 (미구현)");
        JButton backButton = new JButton("메인으로 돌아가기");
        
        add(loadButton);
        add(backButton);

        // '뒤로 가기' 버튼 클릭 시
        backButton.addActionListener(e -> manager.navigateTo("MAIN_PAGE"));
    }
}