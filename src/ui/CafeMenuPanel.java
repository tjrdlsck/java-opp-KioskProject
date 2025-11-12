package ui;

import mainpage.Menu;
import mainpage.Product;
import mainpage.Store;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class CafeMenuPanel extends JPanel {
    // ⛔️ [변경] Map<String, List<MenuItem>>
    // ✅ [수정] Product 리스트를 저장하도록 변경
    private Map<String, List<Product>> menuData = new HashMap<>();
    private CardLayout categoryLayout;
    private JPanel categoryContainer;
    
    // ✅ [신규 추가] 카테고리 스크롤 패널을 제어하기 위한 필드
    private JScrollPane categoryScroll;

    public CafeMenuPanel() {
        setLayout(new BorderLayout());
        JLabel guide = new JLabel("카페를 선택하세요 ☕", SwingConstants.CENTER);
        guide.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        add(guide, BorderLayout.CENTER);
    }

 // ⛔️ [변경] public void loadCafeMenu(String fileName, Consumer<MenuItem> onAddOrder)
    // ✅ [수정] Store 객체와 Product Consumer를 받도록 시그니처 변경
    public void loadCafeMenu(Store store, Consumer<Product> onAddOrder) {
        // ✅ [신규 추가] 전달받은 Store 객체에서 메뉴 데이터를 'Product' 맵으로 변환
        this.menuData.clear(); // 기존 데이터를 비웁니다.
        for (Menu menu : store.getMenus()) {
            this.menuData.put(menu.getCategoryName(), menu.getProducts());
        }

        removeAll(); // (이하 로직은 대부분 동일)

        // ✅ 2. [신규] Store 객체에서 *실제* 카테고리 이름 목록을 동적으로 추출
        // (순서를 보장하기 위해 store.getMenus() 순서를 그대로 사용)
        List<String> realCategories = new ArrayList<>();
        for (Menu menu : store.getMenus()) {
            realCategories.add(menu.getCategoryName());
        }
        
     // ⛔️ [변경] JPanel categoryPanel = new JPanel(new GridLayout(1, categoryList.size(), 10, 0));
        // ✅ 3. [신규] 스크롤 패널 내부에 FlowLayout 패널을 배치 (MenuScreen의 상단과 동일한 구조)
        JPanel categoryButtonContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        categoryButtonContainer.setBackground(new Color(245, 245, 245)); // 배경색

        // ⛔️ [변경] for (String cat : categoryList) {
        // ✅ 4. [신규] *실제* 카테고리 목록으로 버튼 생성
        for (String cat : realCategories) {
            JButton btn = new JButton(cat);
            btn.setFont(new Font("맑은 고딕", Font.BOLD, 18));
            btn.addActionListener(e -> categoryLayout.show(categoryContainer, cat));
            btn.setBackground(Color.WHITE); // 버튼색은 흰색으로
            categoryButtonContainer.add(btn);
        }

        // ✅ 5. [신규] 버튼 컨테이너를 JScrollPane에 추가
        this.categoryScroll = new JScrollPane(
                categoryButtonContainer,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                // ✅ [수정] 스크롤바를 'NEVER'로 설정하여 숨깁니다 (가게 레이아웃처럼)
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        categoryScroll.setBorder(null); // 테두리 제거
        categoryScroll.getHorizontalScrollBar().setUnitIncrement(16); // 스크롤 속도
        categoryScroll.setBackground(new Color(245, 245, 245));
        // ✅ 6. [신규] 가게 레이아웃과 동일하게, 화살표 버튼을 생성합니다.
        // (이 버튼들은 카테고리 패널의 배경색(245)을 따라야 합니다)
        JButton leftArrow = createStyledArrowButton("◀", new Color(245, 245, 245));
        JButton rightArrow = createStyledArrowButton("▶", new Color(245, 245, 245));

        // ✅ 7. [신규] 화살표 버튼에 스크롤 액션을 연결합니다.
        leftArrow.addActionListener(e -> scrollCategoryList(-150));
        rightArrow.addActionListener(e -> scrollCategoryList(150));
        
        // ✅ 8. [신규] 버튼과 스크롤패널을 감싸는 'topPanel'을 생성합니다.
        JPanel categoryTopPanel = new JPanel(new BorderLayout());
        categoryTopPanel.setBackground(new Color(245, 245, 245)); // 배경색 통일
        categoryTopPanel.add(leftArrow, BorderLayout.WEST);
        categoryTopPanel.add(this.categoryScroll, BorderLayout.CENTER);
        categoryTopPanel.add(rightArrow, BorderLayout.EAST);

        // ⛔️ [변경] add(categoryScroll, BorderLayout.NORTH);
        // ✅ [수정] 'categoryTopPanel' (화살표+스크롤)을 NORTH에 추가합니다.
        add(categoryTopPanel, BorderLayout.NORTH);

        // ✅ 7. 카테고리별 메뉴 (CardLayout)
        categoryLayout = new CardLayout();
        categoryContainer = new JPanel(categoryLayout);

        // ⛔️ [변경] for (String cat : categoryList) {
        // ✅ 8. [신규] *실제* 카테고리 목록으로 카드 생성
        for (String cat : realCategories) {
            List<Product> items = menuData.getOrDefault(cat, new ArrayList<>());
            categoryContainer.add(createPagedMenuPanel(items, onAddOrder), cat);
        }

        add(categoryContainer, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
 // (제네릭 타입은 이전 단계에서 Product로 수정되었다고 가정)
    private JPanel createPagedMenuPanel(List<Product> items, Consumer<Product> onAddOrder) {
        int itemsPerPage = 8;
        int totalPages = Math.max(1, (int) Math.ceil(items.size() / (double) itemsPerPage));
        CardLayout pageLayout = new CardLayout();
        JPanel pageContainer = new JPanel(pageLayout);

        for (int p = 0; p < totalPages; p++) {
            int start = p * itemsPerPage;
            int end = Math.min(start + itemsPerPage, items.size());
            List<Product> pageItems = items.subList(start, end);
            pageContainer.add(createMenuGrid(pageItems, onAddOrder), "page" + p);
        }

        // ✅ 1. [신규] 스타일링된 화살표 버튼 생성
        JButton prev = createStyledArrowButton("‹"); // "◀" 대신 "‹"
        JButton next = createStyledArrowButton("›"); // "▶" 대신 "›"
        
        // ✅ 2. [신규] 페이지 "점" 표시기 생성
        List<JLabel> indicators = new ArrayList<>();
        JPanel indicatorPanel = createPageIndicator(totalPages, indicators);

        // ✅ 3. [신규] 페이지 전환 로직 (ActionListener)
        final int[] current = {0};
        
        // 버튼 활성/비활성 상태 관리
        prev.setEnabled(false); // 처음엔 0페이지이므로 '이전' 비활성화
        next.setEnabled(totalPages > 1); // 페이지가 1개뿐이면 '다음' 비활성화

        prev.addActionListener(e -> {
            if (current[0] > 0) {
                current[0]--;
                pageLayout.show(pageContainer, "page" + current[0]);
                updateIndicators(indicators, current[0]); // ✅ "점" 업데이트
                
                // ✅ 버튼 활성/비활성 업데이트
                prev.setEnabled(current[0] > 0);
                next.setEnabled(true);
            }
        });
        next.addActionListener(e -> {
            if (current[0] < totalPages - 1) {
                current[0]++;
                pageLayout.show(pageContainer, "page" + current[0]);
                updateIndicators(indicators, current[0]); // ✅ "점" 업데이트

                // ✅ 버튼 활성/비활성 업데이트
                prev.setEnabled(true);
                next.setEnabled(current[0] < totalPages - 1);
            }
        });

        // ⛔️ [삭제] 낡은 하단 네비게이션 패널
        // JPanel nav = new JPanel(new BorderLayout()); ...

        // ✅ 4. [수정] pagedMenuPanel (wrapper) 레이아웃 재구성
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE); // 배경색 통일
        
        wrapper.add(pageContainer, BorderLayout.CENTER);  // 중앙: 메뉴 그리드 (페이지 전환)
        wrapper.add(indicatorPanel, BorderLayout.SOUTH); // 하단: 페이지 "점"
        wrapper.add(prev, BorderLayout.WEST);            // 왼쪽: "‹" 버튼
        wrapper.add(next, BorderLayout.EAST);            // 오른쪽: "›" 버튼
        
        return wrapper;
    }

    // ⛔️ [변경] List<MenuItem> items, Consumer<MenuItem> onAddOrder
    // ✅ [수정] Product 리스트와 Consumer를 받도록 시그니처 변경
    private JPanel createMenuGrid(List<Product> items, Consumer<Product> onAddOrder) {
        JPanel grid = new JPanel(new GridLayout(4, 2, 10, 10));
        grid.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        grid.setBackground(Color.WHITE);

        // ⛔️ [변경] for (MenuItem item : items)
        // ✅ [수정]
        for (Product item : items) {
            // ⛔️ [변경] JButton btn = new JButton("<html><center>" + item.name + "<br>" + item.price + "원</center></html>");
            // ✅ [수정] Product의 getter를 사용하고, 가격에 쉼표(,) 포맷을 적용합니다.
            String buttonText = String.format("<html><center>%s<br>%,d원</center></html>",
                    item.getName(), item.getPrice());
            JButton btn = new JButton(buttonText);
            
            btn.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
            btn.setBackground(Color.WHITE);
            btn.setFocusPainted(false);
            // ✅ [수정] 이제 item은 Product 객체입니다.
            btn.addActionListener(e -> onAddOrder.accept(item));
            grid.add(btn);
        }

        int remain = 8 - items.size();
        for (int i = 0; i < remain; i++) grid.add(new JPanel());

        return grid;
    }
    /**
     * [신규 추가] 모던 스타일 화살표 버튼 생성기 (MenuScreen에서 복사/수정)
     * "..." 문제 해결을 위해 setBorder(null)와 setContentAreaFilled(false)를 포함합니다.
     *
     * @param unicodeArrow "◀" 또는 "▶"
     * @param backgroundColor 버튼이 위치할 패널의 배경색
     * @return 스타일이 적용된 JButton
     */
    private JButton createStyledArrowButton(String unicodeArrow, Color backgroundColor) {
        JButton btn = new JButton(unicodeArrow);
        btn.setForeground(Color.DARK_GRAY);
        
        // ✅ [수정] MenuScreen(230)과 다른 카테고리 패널(245)의 배경색을 적용
        btn.setBackground(backgroundColor);
        
        // "..." 문제 해결을 위한 L&F 오버라이드 4종 세트
        btn.setFocusPainted(false);
        btn.setBorder(null);
        btn.setContentAreaFilled(false);
        btn.setMargin(new Insets(0, 0, 0, 0)); 
        
        // MenuScreen과 동일한 크기
        Dimension arrowSize = new Dimension(35, 35);
        btn.setPreferredSize(arrowSize);
        
        return btn;
    }
    /**
     * [신규 추가] 카테고리 스크롤 패널을 좌우로 이동시킵니다.
     *
     * @param offset 이동할 픽셀 값 (음수: 왼쪽, 양수: 오른쪽)
     */
    private void scrollCategoryList(int offset) {
        // ✅ 클래스 필드 'categoryScroll'을 참조합니다.
        JScrollBar bar = this.categoryScroll.getHorizontalScrollBar();
        int newValue = bar.getValue() + offset;
        if (newValue < 0) newValue = 0;
        if (newValue > bar.getMaximum()) newValue = bar.getMaximum();
        bar.setValue(newValue);
    }
    /**
     * [신규] 모던 스타일의 화살표 버튼을 생성합니다.
     * 배경과 테두리가 투명하고, 마우스 오버 시에만 보이도록 설정할 수 있습니다.
     *
     * @param unicodeArrow "‹" (왼쪽) 또는 "›" (오른쪽) 문자
     * @return 스타일이 적용된 JButton
     */
    private JButton createStyledArrowButton(String unicodeArrow) {
        JButton btn = new JButton(unicodeArrow);
        btn.setFont(new Font("SansSerif", Font.BOLD, 36)); // 폰트 크기 증가
        btn.setForeground(Color.DARK_GRAY);
        
        // [핵심] 모던 UI를 위한 스타일링
        btn.setBorderPainted(false);       // 테두리 없음
        btn.setContentAreaFilled(false);   // 배경 투명
        btn.setFocusPainted(false);        // 포커스 테두리 없음
        
        // (선택적) 마우스가 올라갔을 때만 보이게 하려면
        // btn.setOpaque(false);
        // btn.setVisible(true); // -> 기본은 보이게 설정

        return btn;
    }
    /**
     * [신규] 페이지 상태를 "점" (● ○)으로 표시하는 패널을 생성합니다.
     *
     * @param totalPages 총 페이지 수
     * @param outIndicatorLabels [out] 생성된 점(JLabel)들을 담을 리스트
     * @return 점들이 배치된 JPanel
     */
    private JPanel createPageIndicator(int totalPages, List<JLabel> outIndicatorLabels) {
        JPanel indicatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        indicatorPanel.setBackground(Color.WHITE); // 메뉴 그리드와 동일한 배경색

        for (int i = 0; i < totalPages; i++) {
            // "●" (채워진 원), "○" (빈 원)
            JLabel dot = new JLabel(i == 0 ? "●" : "○");
            dot.setFont(new Font("SansSerif", Font.BOLD, 16));
            dot.setForeground(Color.GRAY);
            indicatorPanel.add(dot);
            outIndicatorLabels.add(dot); // 외부 리스트에 라벨 추가
        }
        return indicatorPanel;
    }
    /**
     * [신규] 페이지 표시기("점")의 현재 페이지를 업데이트합니다.
     *
     * @param indicators     모든 점(JLabel)이 담긴 리스트
     * @param currentIndex 현재 활성화된 페이지 인덱스
     */
    private void updateIndicators(List<JLabel> indicators, int currentIndex) {
        for (int i = 0; i < indicators.size(); i++) {
            indicators.get(i).setText(i == currentIndex ? "●" : "○");
            indicators.get(i).setForeground(i == currentIndex ? Color.BLACK : Color.GRAY);
        }
    }
}
