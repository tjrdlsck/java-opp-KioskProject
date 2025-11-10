package mainpage;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * [View - Panel 2 (Skeleton)]
 * 가게의 메뉴 카테고리를 표시할 화면.
 * 1단계에서는 '뒤로 가기' 기능만 구현합니다.
 */
public class StoreMenuPanel extends JPanel {

    private KioskAppManager manager;

    public StoreMenuPanel(KioskAppManager manager) {
        this.manager = manager;

        add(new JLabel("가게 메뉴 패널 (2단계에서 구현)"));
        
        JButton backButton = new JButton("메인으로 돌아가기");
        add(backButton);

        // '뒤로 가기' 버튼 클릭 시 -> "MAIN_PAGE"로 이동
        backButton.addActionListener(e -> manager.navigateTo("MAIN_PAGE"));
    }
    
    // (2단계에서 구현)
    // public void setStore(Store store) {
    //     // 이 메소드가 호출되면, 전달받은 store의 메뉴(getMenus())를
    //     // 기반으로 카테고리 버튼들을 동적으로 생성합니다.
    // }
}