package mainpage;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

/**
 * [View - Panel 3 (Skeleton)]
 * 카테고리의 상품 목록을 표시할 화면.
 * 1단계에서는 '뒤로 가기' (이전 메뉴) 기능만 구현합니다.
 */
public class ProductMenuPanel extends JPanel {

    private KioskAppManager manager;

    public ProductMenuPanel(KioskAppManager manager) {
        this.manager = manager;
        
        add(new JLabel("상품 목록 패널 (2단계에서 구현)"));

        JButton backButton = new JButton("이전 메뉴(가게 메뉴)로 돌아가기");
        add(backButton);

        // '뒤로 가기' 버튼 클릭 시 -> "STORE_MENU"로 이동
        backButton.addActionListener(e -> manager.navigateTo("STORE_MENU"));
    }
    
    // (2단계에서 구현)
    // public void setMenu(Menu menu, Store currentStore) {
    //     // 이 메소드가 호출되면, 전달받은 menu의 상품(getProducts())을
    //     // 기반으로 상품 버튼들을 동적으로 생성합니다.
    // }
}