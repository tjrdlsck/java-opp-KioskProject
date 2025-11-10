package mainpage.view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import mainpage.controller.KioskAppManager;
import mainpage.model.Store;

import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Dimension; // [신규] 버튼 크기 고정을 위해 import
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * [View - Panel 1] (GUI v2.0 수정)
 * 1. 3열 그리드 -> 1열 수직 리스트로 변경
 * 2. 버튼 크기 고정 (setPreferredSize)
 */
public class MainPagePanel extends JPanel {

    private KioskAppManager manager;
    private JPanel storeButtonPanel; 

    public MainPagePanel(KioskAppManager manager) {
        this.manager = manager;
        
        setLayout(new BorderLayout(10, 10)); 
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 

        // 1. 상단 타이틀 레이블
        JLabel titleLabel = new JLabel("가게를 선택해주세요", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 중앙: 동적 가게 버튼 패널
        
        // [수정] 3열(0, 3) -> 1열(0, 1) 수직 스크롤 리스트로 변경
        storeButtonPanel = new JPanel(new GridLayout(0, 1, 10, 10)); // 1열, 간격 10px
        
        List<Store> stores = manager.getStores();
        for (Store store : stores) {
            String buttonText = String.format("<html><center>%s<br/>(%s)</center></html>",
                                              store.getName(), 
                                              store.getDescription());
                                              
            JButton storeButton = new JButton(buttonText);
            storeButton.setFont(new Font("SansSerif", Font.BOLD, 16)); 
            
            // [신규] 버튼 크기 고정 (가로 600, 세로 80)
            // (이 크기는 MainFrame 크기(800)에 맞춰 조절 가능)
            storeButton.setPreferredSize(new Dimension(600, 80));
            
            storeButton.addActionListener(e -> {
                manager.showStoreMenu(store);
            });
            
            storeButtonPanel.add(storeButton);
        }
        
        // JScrollPane은 1열 리스트의 수직 스크롤을 담당 (변경 없음)
        JScrollPane scrollPane = new JScrollPane(storeButtonPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        add(scrollPane, BorderLayout.CENTER);


        // 3. 하단: 고정 메뉴 패널 (변경 없음)
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

        // 4. 이벤트 리스너(ActionListener) 설정 (변경 없음)
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