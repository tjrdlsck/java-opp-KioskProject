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
    private Map<String, List<Product>> menuData = new HashMap<>();
    private CardLayout categoryLayout;
    private JPanel categoryContainer;
    
    private JScrollPane categoryScroll;

    public CafeMenuPanel() {
    	
        setLayout(new BorderLayout());
        
        JLabel guide = new JLabel("카페를 선택하세요 ☕", SwingConstants.CENTER);
        guide.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        add(guide, BorderLayout.CENTER);
    }
    
    public void loadCafeMenu(Store store, Consumer<Product> onAddOrder) {
        this.menuData.clear();
        for (Menu menu : store.getMenus()) {
            this.menuData.put(menu.getCategoryName(), menu.getProducts());
        }

        removeAll();

        List<String> realCategories = new ArrayList<>();
        for (Menu menu : store.getMenus()) {
            realCategories.add(menu.getCategoryName());
        }
        
        JPanel categoryButtonContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        categoryButtonContainer.setBackground(new Color(245, 245, 245)); // 배경색

        for (String cat : realCategories) {
            JButton btn = new JButton(cat);
            btn.setFont(new Font("맑은 고딕", Font.BOLD, 18));
            btn.addActionListener(e -> categoryLayout.show(categoryContainer, cat));
            btn.setBackground(Color.WHITE); // 버튼색은 흰색으로
            categoryButtonContainer.add(btn);
        }

        this.categoryScroll = new JScrollPane(
                categoryButtonContainer,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        categoryScroll.setBorder(null); // 테두리 제거
        categoryScroll.getHorizontalScrollBar().setUnitIncrement(16); // 스크롤 속도
        categoryScroll.setBackground(new Color(245, 245, 245));
        JButton leftArrow = createStyledArrowButton("◀", new Color(245, 245, 245));
        JButton rightArrow = createStyledArrowButton("▶", new Color(245, 245, 245));

        leftArrow.addActionListener(e -> scrollCategoryList(-150));
        rightArrow.addActionListener(e -> scrollCategoryList(150));
        
        JPanel categoryTopPanel = new JPanel(new BorderLayout());
        categoryTopPanel.setBackground(new Color(245, 245, 245)); // 배경색 통일
        categoryTopPanel.add(leftArrow, BorderLayout.WEST);
        categoryTopPanel.add(this.categoryScroll, BorderLayout.CENTER);
        categoryTopPanel.add(rightArrow, BorderLayout.EAST);

        add(categoryTopPanel, BorderLayout.NORTH);

        categoryLayout = new CardLayout();
        categoryContainer = new JPanel(categoryLayout);
        // 메뉴판 전체 연한 회색
        categoryContainer.setBackground(new Color(245, 245, 245));
        
        for (String cat : realCategories) {
            List<Product> items = menuData.getOrDefault(cat, new ArrayList<>());
            categoryContainer.add(createPagedMenuPanel(items, onAddOrder), cat);
        }

        add(categoryContainer, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
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

        JButton prev = createStyledArrowButton("‹");
        JButton next = createStyledArrowButton("›");
        
        List<JLabel> indicators = new ArrayList<>();
        JPanel indicatorPanel = createPageIndicator(totalPages, indicators);
    
        final int[] current = {0};
        
        prev.setEnabled(false); // 처음엔 0페이지이므로 '이전' 비활성화
        next.setEnabled(totalPages > 1); // 페이지가 1개뿐이면 '다음' 비활성화

        prev.addActionListener(e -> {
            if (current[0] > 0) {
                current[0]--;
                pageLayout.show(pageContainer, "page" + current[0]);
                updateIndicators(indicators, current[0]);
                
                prev.setEnabled(current[0] > 0);
                next.setEnabled(true);
            }
        });
        next.addActionListener(e -> {
            if (current[0] < totalPages - 1) {
                current[0]++;
                pageLayout.show(pageContainer, "page" + current[0]);
                updateIndicators(indicators, current[0]);

                prev.setEnabled(true);
                next.setEnabled(current[0] < totalPages - 1);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JPanel indicatorWrapper = new JPanel(new BorderLayout());
        indicatorWrapper.setOpaque(false);
        indicatorWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); // 살짝 위로 올리기

        indicatorWrapper.add(indicatorPanel, BorderLayout.CENTER);

        wrapper.add(pageContainer, BorderLayout.CENTER);
        wrapper.add(indicatorWrapper, BorderLayout.SOUTH);
        wrapper.add(prev, BorderLayout.WEST);
        wrapper.add(next, BorderLayout.EAST);

        return wrapper;
    }
    
    private JPanel createMenuGrid(List<Product> items, Consumer<Product> onAddOrder) {

        // 2행 3열 고정 그리드
        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setBackground(Color.WHITE);

        grid.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));
        
        int count = 0;

        for (Product item : items) {
            if (count >= 6) break;
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            cell.setOpaque(false);

            MenuCard card = new MenuCard(item.getName(), item.getPrice(), item.getImagePath());

            // 클릭 시 주문 추가
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    onAddOrder.accept(item);
                }
            });

            cell.add(card);
            grid.add(cell);

            count++;
        }

        while (count < 6) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            grid.add(empty);
            count++;
        }

        return grid;
    }
    //화살표 버튼 생성기
    private JButton createStyledArrowButton(String unicodeArrow, Color backgroundColor) {
        JButton btn = new JButton(unicodeArrow);
        btn.setForeground(Color.DARK_GRAY);
        
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
    // 카테고리 스크롤 패널을 좌우로 이동
    private void scrollCategoryList(int offset) {
        // ✅ 클래스 필드 'categoryScroll'을 참조합니다.
        JScrollBar bar = this.categoryScroll.getHorizontalScrollBar();
        int newValue = bar.getValue() + offset;
        if (newValue < 0) newValue = 0;
        if (newValue > bar.getMaximum()) newValue = bar.getMaximum();
        bar.setValue(newValue);
    }
    // 화살표 버튼 생성
    private JButton createStyledArrowButton(String unicodeArrow) {
        JButton btn = new JButton(unicodeArrow);
        btn.setFont(new Font("SansSerif", Font.BOLD, 36)); // 폰트 크기 증가
        btn.setForeground(Color.DARK_GRAY);
        
        // 스타일링
        btn.setBorderPainted(false);       // 테두리 없음
        btn.setContentAreaFilled(false);   // 배경 투명
        btn.setFocusPainted(false);        // 포커스 테두리 없음
        
        return btn;
    }
    // 페이지 상태를 점으로 표시하는 기능
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
    // 페이지 표시기 업데이트
    private void updateIndicators(List<JLabel> indicators, int currentIndex) {
        for (int i = 0; i < indicators.size(); i++) {
            indicators.get(i).setText(i == currentIndex ? "●" : "○");
            indicators.get(i).setForeground(i == currentIndex ? Color.BLACK : Color.GRAY);
        }
    }
}