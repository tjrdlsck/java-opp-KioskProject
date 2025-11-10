package mainpage;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

/**
 * [View - Panel 7 (Skeleton)]
 * 저장된 내역 삭제 화면 (이름, 전화번호 입력).
 * 1단계에서는 '뒤로 가기' 기능만 구현합니다.
 */
public class DeleteCartPanel extends JPanel {

    private KioskAppManager manager;

    public DeleteCartPanel(KioskAppManager manager) {
        this.manager = manager;

        add(new JLabel("저장된 내역 삭제 패널 (4단계에서 구현)"));

        // (4단계에서 이름/전화번호 JTextField 추가)
        
        JButton deleteButton = new JButton("삭제하기 (미구현)");
        JButton backButton = new JButton("메인으로 돌아가기");
        
        add(deleteButton);
        add(backButton);

        // '뒤로 가기' 버튼 클릭 시
        backButton.addActionListener(e -> manager.navigateTo("MAIN_PAGE"));
    }
}