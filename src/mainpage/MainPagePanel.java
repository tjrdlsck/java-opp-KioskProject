package mainpage;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * [View - Panel 1]
 * 메인 화면 (가게 선택, 주문 옵션)
 * 1단계에서는 실제 데이터를 표시하는 대신,
 * 다른 화면으로 이동하는 '네비게이션 테스트'에 중점을 둡니다.
 */
public class MainPagePanel extends JPanel {

    // View(Panel)가 Controller(Manager)와 통신하기 위한 참조
    private KioskAppManager manager;

    public MainPagePanel(KioskAppManager manager) {
        this.manager = manager;
        
        // 레이아웃 설정 (상단, 중앙)
        setLayout(new BorderLayout());

        // 1. 상단 타이틀 레이블
        JLabel titleLabel = new JLabel("키오스크 메인 메뉴", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 중앙 버튼 패널 (GridLayout)
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // 4행 1열
        
        // [임시] 2단계에서 이 버튼들은 'stores' 리스트에서 동적으로 생성될 것입니다.
        JButton storeButton = new JButton("가게 메뉴 보기 (StoreMenuPanel로 이동)");
        JButton cartButton = new JButton("장바구니 확인 (CartPanel로 이동)");
        JButton loadButton = new JButton("장바구니 불러오기 (LoadCartPanel로 이동)");
        JButton deleteButton = new JButton("저장된 내역 삭제 (DeleteCartPanel로 이동)");

        buttonPanel.add(storeButton);
        buttonPanel.add(cartButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(deleteButton);
        
        add(buttonPanel, BorderLayout.CENTER);

        // 3. 이벤트 리스너(ActionListener) 설정
        // 버튼 클릭(Event) 시 -> Controller(Manager)의 메소드 호출
        
        // '가게 메뉴 보기' 버튼 클릭 시
        storeButton.addActionListener(e -> {
            // Controller에게 "STORE_MENU" 패널을 보여달라고 요청
            manager.navigateTo("STORE_MENU");
        });
        
        // '장바구니 확인' 버튼 클릭 시
        cartButton.addActionListener(e -> {
            manager.navigateTo("CART");
        });

        // '불러오기' 버튼 클릭 시
        loadButton.addActionListener(e -> {
            manager.navigateTo("LOAD_CART");
        });

        // '삭제하기' 버튼 클릭 시
        deleteButton.addActionListener(e -> {
            manager.navigateTo("DELETE_CART");
        });
    }
}