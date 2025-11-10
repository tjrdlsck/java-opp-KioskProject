package mainpage;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * [View - Panel 1] (2단계 수정본)
 * KioskAppManager로부터 List<Store>를 받아 동적(dynamic)으로 가게 버튼을 생성합니다.
 * (수정: 3열 그리드 -> 1열 그리드로 변경)
 */
public class MainPagePanel extends JPanel {

    private KioskAppManager manager;
    
    private JPanel storeButtonPanel; 

    public MainPagePanel(KioskAppManager manager) {
        this.manager = manager;
        
        // 레이아웃 설정 (상단, 중앙, 하단)
        setLayout(new BorderLayout(10, 10)); // 컴포넌트 간 간격 10px
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 패널 자체의 여백

        // 1. 상단 타이틀 레이블
        JLabel titleLabel = new JLabel("가게를 선택해주세요", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 중앙: 동적 가게 버튼 패널 (스크롤 가능하도록)
        
        // [수정] GridLayout(0, 3) -> GridLayout(0, 1)
        // 3열 기준(좌->우)에서 1열 기준(위->아래)으로 변경
        storeButtonPanel = new JPanel(new GridLayout(0, 1, 10, 10)); // 1열, 간격 10px
        
        // 가게 목록을 가져와 동적으로 버튼 생성
        List<Store> stores = manager.getStores();
        for (Store store : stores) {
            // [HTML 사용] JButton 텍스트를 2줄로 만들기
            String buttonText = String.format("<html><center>%s<br/>(%s)</center></html>",
                                              store.getName(),
                                              store.getDescription());
                                              
            JButton storeButton = new JButton(buttonText);
            storeButton.setFont(new Font("SansSerif", Font.BOLD, 16));
            
            // [핵심] 각 버튼에 ActionListener 추가
            storeButton.addActionListener(e -> {
                manager.showStoreMenu(store);
            });
            
            storeButtonPanel.add(storeButton);
        }
        
        // JScrollPane으로 감싸기 (수직 스크롤이 활성화됨)
        JScrollPane scrollPane = new JScrollPane(storeButtonPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // 스크롤패인 자체의 테두리 제거
        add(scrollPane, BorderLayout.CENTER);


        // 3. 하단: 고정 메뉴 패널 (1행 3열)
        JPanel orderMenuPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        
        JButton cartButton = new JButton("장바구니 확인");
        JButton loadButton = new JButton("장바구니 불러오기");
        JButton deleteButton = new JButton("저장된 내역 삭제");
        
        Font bottomFont = new Font("SansSerif", Font.PLAIN, 14);
        cartButton.setFont(bottomFont);
        loadButton.setFont(bottomFont);
        deleteButton.setFont(bottomFont);

        orderMenuPanel.add(cartButton);
        orderMenuPanel.add(loadButton);
        orderMenuPanel.add(deleteButton);
        
        add(orderMenuPanel, BorderLayout.SOUTH);

        // 4. 이벤트 리스너(ActionListener) 설정
        
        cartButton.addActionListener(e -> {
            manager.navigateTo("CART");
        });

        loadButton.addActionListener(e -> {
            manager.navigateTo("LOAD_CART");
        });

        deleteButton.addActionListener(e -> {
            manager.navigateTo("DELETE_CART");
        });
    }
}