package mainpage;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Dimension; // [신규] Dimension 클래스 import
import java.awt.FlowLayout; 
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.BorderFactory; // BorderFactory import
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color; // (이미 있을 수 있음)

/**
 * [View - Panel 2 (버튼 크기 수정)]
 * 1. 페이지네이션 기능
 * 2. 스트레칭 방지 래퍼
 * 3. [수정] setPreferredSize로 버튼 크기 고정
 */
public class StoreMenuPanel extends JPanel {

    private KioskAppManager manager;
    private JLabel titleLabel;
    private JPanel menuButtonPanel;
    private JButton backButton; 

    private Store currentStore; 

    // --- 페이지네이션 상태 변수 ---
    private List<Menu> allMenus; 
    private int currentPage; 
    private int totalPages; 

    private final int ROWS_PER_PAGE = 3;
    private final int COLS_PER_PAGE = 3;
    private final int MENUS_PER_PAGE = ROWS_PER_PAGE * COLS_PER_PAGE;

    private JPanel paginationPanel;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageLabel;
    // --- 페이지네이션 끝 ---


    public StoreMenuPanel(KioskAppManager manager) {
        this.manager = manager;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. 상단 타이틀 레이블
        titleLabel = new JLabel("가게 메뉴", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 중앙: 동적 메뉴 버튼 패널
        menuButtonPanel = new JPanel(new GridLayout(ROWS_PER_PAGE, COLS_PER_PAGE, 1, 1));
        add(menuButtonPanel, BorderLayout.CENTER);

        // 3. 하단: 네비게이션 패널 (페이지네이션 + 뒤로가기)
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        // 3-1. 페이지네이션 컨트롤 패널
        paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevButton = new JButton("< 이전");
        nextButton = new JButton("다음 >");
        pageLabel = new JLabel("1 / 1");

        paginationPanel.add(prevButton);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextButton);

        bottomPanel.add(paginationPanel, BorderLayout.NORTH);

        // 3-2. '뒤로 가기' 버튼
        backButton = new JButton("메인으로 돌아가기 (가게 선택)");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));

        bottomPanel.add(backButton, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // 4. 페이지네이션 버튼 이벤트 리스너
        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateMenuView(); 
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                updateMenuView(); 
            }
        });

        // 5. '뒤로 가기' 버튼 이벤트 리스너
        backButton.addActionListener(e -> manager.goHome());
    }

    /**
     * [setStore 메소드]
     */
    public void setStore(Store store) {
        if (store == null) return;

        this.currentStore = store; 
        this.allMenus = this.currentStore.getMenus();

        titleLabel.setText(String.format("[%s] 메뉴 카테고리", this.currentStore.getName()));

        this.currentPage = 0; 
        if (this.allMenus.isEmpty()) {
            this.totalPages = 1;
        } else {
            this.totalPages = (int) Math.ceil((double) this.allMenus.size() / MENUS_PER_PAGE);
        }

        updateMenuView(); 
    }

    /**
     * [헬퍼 메소드 수정본 - 버튼 효과 복원]
     * 'currentPage'와 'currentStore' 필드를 기반으로 갱신합니다.
     * 1. [수정] LineBorder 대신 UIManager의 기본 테두리(효과 포함)를 사용
     * 2. [신규] 배경색(흰색) 및 setFocusPainted(false) 적용
     */
    private void updateMenuView() {
        // 방어 코드: currentStore가 null이면 아무것도 하지 않음
        if (this.currentStore == null) {
            return;
        }
        
        // 1. [중요] 기존 버튼들 모두 삭제
        menuButtonPanel.removeAll();

        // 2. 현재 페이지에 표시할 메뉴의 시작/끝 인덱스 계산
        int startIndex = currentPage * MENUS_PER_PAGE;
        int endIndex = Math.min(startIndex + MENUS_PER_PAGE, allMenus.size());

        // [신규] 버튼에 적용할 '복합 테두리' 생성
        // 1. 시스템의 '기본 테두리' (눌림/호버 효과 포함)
        Border defaultBorder = UIManager.getBorder("Button.border");
        // 2. 내부 여백 (Padding) 생성 (상하 10px, 좌우 10px)
        Border padding = new EmptyBorder(10, 10, 10, 10);
        // 3. 외곽선(기본 효과)과 내부 여백을 합침
        Border compoundBorder = new CompoundBorder(defaultBorder, padding);

        // 3. 현재 페이지의 메뉴만 '잘라서' 버튼 생성
        for (int i = startIndex; i < endIndex; i++) {
            Menu menu = allMenus.get(i);
            String categoryName = menu.getCategoryName();
            JButton menuButton = new JButton(categoryName);
            menuButton.setFont(new Font("SansSerif", Font.BOLD, 16));
            
            // --- [신규] 스타일 적용 ---
            menuButton.setBackground(Color.WHITE); // 배경색 흰색
            menuButton.setOpaque(true);            // 배경색 보이도록
            menuButton.setFocusPainted(false);     // 클릭 시 점선 테두리 제거
            // ------------------------

            // [수정] LineBorder가 아닌 '효과가 포함된' 복합 테두리 적용
            menuButton.setBorder(compoundBorder);

            // 리스너
            menuButton.addActionListener(e -> {
                manager.showProductMenu(menu, this.currentStore);
            });

            // [수정] 버튼을 GridLayout에 직접 추가
            menuButtonPanel.add(menuButton);
        }

        // 4. [중요] 그리드(3x3)의 빈 슬롯 채우기
        int emptySlots = MENUS_PER_PAGE - (endIndex - startIndex);
        for (int i = 0; i < emptySlots; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setOpaque(false); // 투명하게
            menuButtonPanel.add(emptyPanel);
        }

        // 5. 페이지네이션 UI 갱신
        pageLabel.setText(String.format("%d / %d", currentPage + 1, totalPages));
        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < totalPages - 1);

        if (totalPages <= 1) {
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
        }

        // 6. [중요] UI 갱신
        menuButtonPanel.revalidate();
        menuButtonPanel.repaint();
    }
}