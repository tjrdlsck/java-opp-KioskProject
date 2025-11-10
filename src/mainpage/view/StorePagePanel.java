package mainpage.view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane; 
import javax.swing.SwingConstants;

import mainpage.controller.KioskAppManager;
import mainpage.model.Menu;
import mainpage.model.Product;
import mainpage.model.Store;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout; // [v2.1]
import javax.swing.ImageIcon; // [v2.1]
import javax.swing.JOptionPane; // [3단계]

// (디버깅용 보더 import 제거)
// import java.awt.Color;
// import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.awt.FlowLayout; // [v3.3] 메인 레이아웃 매니저로 사용
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image; // [v2.1]
import java.awt.Dimension; // [v2.1]
import java.awt.Component; // [v2.2]
import java.util.List;

/**
 * [View - Panel (GUI v3.3 최종 수정)]
 * v3.2의 레이아웃 문제를 v3.3 계획서에 따라 최종 수정
 * 1. [v3.3] categoryControlPanel의 레이아웃을 BorderLayout -> FlowLayout(FlowLayout.CENTER)로 변경
 * 2. [v3.3] '래퍼 패널' 기법 폐기
 * 3. [v3.3] categoryButtonPanel (중앙 버튼 패널)의 레이아웃도 FlowLayout(FlowLayout.CENTER)로 유지
 */
public class StorePagePanel extends JPanel {

    // (필드 선언은 기존과 동일)
    // --- M-VC ---
    private KioskAppManager manager;
    // --- State (내부 상태) ---
    private Store currentStore; 
    private Menu currentMenu;   
    private int currentPage;    
    private final int PRODUCTS_PER_PAGE = 9; 
    // --- View (Layout) ---
    private JLabel titleLabel; 
    private JPanel productDisplayPanel; 
    // [v2.2] 카테고리 바 관련 컴포넌트
    private JPanel categoryButtonPanel; 
    private JButton prevCategoryButton; 
    private JButton nextCategoryButton; 
    // --- View (상품 페이지네이션) ---
    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageLabel;
    private JButton backToMainButton; 
    // --- State (Category Page) ---
    private int currentCategoryPage; 
    // [v3.2] 5 -> 4로 변경
    private final int CATEGORIES_PER_PAGE = 4; 

    public StorePagePanel(KioskAppManager manager) {
        this.manager = manager;

        // 1. 메인 레이아웃 (BorderLayout)
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 2. NORTH: 상단 (타이틀 + 카테고리 바) ---
        
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS)); 
        
        // 2-1. 가게 이름 타이틀
        titleLabel = new JLabel("가게 이름", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        northPanel.add(titleLabel); 

        // 2-2. [v3.3 최종 수정] 카테고리 네비게이션 바
        
        // [v3.3] categoryControlPanel: BorderLayout -> FlowLayout(FlowLayout.CENTER)로 변경
        // 이 패널 자체가(<, 버튼들, >를 묶어서) 중앙 정렬되도록 함
        JPanel categoryControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        prevCategoryButton = new JButton("<");
        nextCategoryButton = new JButton(">");
        prevCategoryButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        nextCategoryButton.setFont(new Font("SansSerif", Font.BOLD, 16));

        // [v3.2] 중앙 패널 (FlowLayout.CENTER) - 유지
        categoryButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        
        // [v3.3] '래퍼 패널' 기법 폐기 및 FlowLayout에 순서대로 추가
        categoryControlPanel.add(prevCategoryButton);
        categoryControlPanel.add(categoryButtonPanel);
        categoryControlPanel.add(nextCategoryButton);
        
        northPanel.add(categoryControlPanel); 

        add(northPanel, BorderLayout.NORTH); 

        // --- 3. CENTER: 상품 그리드 (3x3) ---
        productDisplayPanel = new JPanel(new GridLayout(3, 3, 10, 10)); 
        add(productDisplayPanel, BorderLayout.CENTER);

        // --- 4. SOUTH: 하단 제어 (페이지네이션 + 뒤로가기) ---
        JPanel southPanel = new JPanel(new BorderLayout());

        // 4-1. 페이지네이션 (CENTER)
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevButton = new JButton("< 이전");
        nextButton = new JButton("다음 >");
        pageLabel = new JLabel("1 / 1");
        
        prevButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nextButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        paginationPanel.add(prevButton);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextButton);
        southPanel.add(paginationPanel, BorderLayout.CENTER);
        
        // 4-2. 메인으로 돌아가기 (SOUTH)
        backToMainButton = new JButton("메인으로 돌아가기 (가게 선택)");
        backToMainButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        southPanel.add(backToMainButton, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // --- 5. 이벤트 리스너 (기존 코드와 동일) ---
        
        prevCategoryButton.addActionListener(e -> {
            if (currentCategoryPage > 0) {
                currentCategoryPage--;
                updateCategoryBar(); 
            }
        });
        nextCategoryButton.addActionListener(e -> {
            currentCategoryPage++;
            updateCategoryBar(); 
        });
        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateProductGrid(); 
            }
        });
        nextButton.addActionListener(e -> {
            currentPage++; 
            updateProductGrid(); 
        });
        backToMainButton.addActionListener(e -> manager.goHome());
    }

    // --- setStore (기존 코드와 동일) ---
    public void setStore(Store store) {
        if (store == null) return;
        this.currentStore = store;
        titleLabel.setText(String.format("[%s]", store.getName())); 
        this.currentCategoryPage = 0; 
        updateCategoryBar(); 
        
        List<Menu> menus = store.getMenus();
        if (menus.isEmpty()) {
            productDisplayPanel.removeAll(); 
            productDisplayPanel.revalidate();
            productDisplayPanel.repaint();
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            pageLabel.setText("1 / 1");
            return; 
        }
        selectCategory(menus.get(0));
    }

    // --- updateCategoryBar (기존 코드와 동일) ---
    // (v3.2에서 CATEGORIES_PER_PAGE가 4로 변경됨)
    private void updateCategoryBar() {
        if (currentStore == null) {
            categoryButtonPanel.removeAll();
            prevCategoryButton.setEnabled(false);
            nextCategoryButton.setEnabled(false);
            categoryButtonPanel.revalidate();
            categoryButtonPanel.repaint();
            return;
        }

        categoryButtonPanel.removeAll();
        List<Menu> allMenus = currentStore.getMenus();
        int totalMenus = allMenus.size();
        int totalCategoryPages = (int) Math.ceil((double) totalMenus / CATEGORIES_PER_PAGE);
        if (totalCategoryPages == 0) totalCategoryPages = 1;

        if (currentCategoryPage >= totalCategoryPages) {
            currentCategoryPage = totalCategoryPages - 1;
        }
        if (currentCategoryPage < 0) {
            currentCategoryPage = 0;
        }

        int startIndex = currentCategoryPage * CATEGORIES_PER_PAGE;
        int endIndex = Math.min(startIndex + CATEGORIES_PER_PAGE, totalMenus);

        for (int i = startIndex; i < endIndex; i++) {
            Menu menu = allMenus.get(i);
            JButton categoryButton = new JButton(menu.getCategoryName());
            categoryButton.setFont(new Font("SansSerif", Font.BOLD, 16));
            
            categoryButton.addActionListener(e -> {
                selectCategory(menu);
            });
            categoryButtonPanel.add(categoryButton);
        }

        prevCategoryButton.setEnabled(currentCategoryPage > 0);
        nextCategoryButton.setEnabled(currentCategoryPage < totalCategoryPages - 1);

        categoryButtonPanel.revalidate();
        categoryButtonPanel.repaint();
    }

    // --- selectCategory (기존 코드와 동일) ---
    private void selectCategory(Menu menu) {
        this.currentMenu = menu;
        this.currentPage = 0; 
        updateProductGrid();
    }

    // --- updateProductGrid (v3.1 코드와 동일) ---
    // (v3.1에서 상품 이미지 100x100으로 변경됨)
    private void updateProductGrid() {
        if (currentMenu == null) return;

        productDisplayPanel.removeAll();
        List<Product> allProducts = currentMenu.getProducts();
        int totalProducts = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / PRODUCTS_PER_PAGE);
        if (totalPages == 0) totalPages = 1; 

        if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }
        if (currentPage < 0) {
            currentPage = 0;
        }

        int startIndex = currentPage * PRODUCTS_PER_PAGE;
        int endIndex = Math.min(startIndex + PRODUCTS_PER_PAGE, totalProducts);

        for (int i = startIndex; i < endIndex; i++) {
            Product product = allProducts.get(i);
            
            String buttonText = String.format("<html><center>%s<br/>(%,d원)</center></html>",
                                              product.getName(),
                                              product.getPrice());
                                              
            JButton productButton = new JButton(buttonText);
            productButton.setFont(new Font("SansSerif", Font.BOLD, 16));

            if (product.hasImage()) {
                try {
                    ImageIcon originalIcon = new ImageIcon(product.getImagePath());
                    // [v3.1] (100, 100) 크기 유지 (MainFrame 800px에 맞춤)
                    Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaledImage);
                    
                    productButton.setIcon(icon);
                    productButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                    productButton.setHorizontalTextPosition(SwingConstants.CENTER);

                } catch (Exception e) {
                    System.err.println("이미지 로드 오류: " + product.getImagePath());
                }
            }

            productButton.addActionListener(e -> {
                handleProductClick(product);
            });
            
            productDisplayPanel.add(productButton);
        }

        int emptySlots = PRODUCTS_PER_PAGE - (endIndex - startIndex);
        for (int i = 0; i < emptySlots; i++) {
            productDisplayPanel.add(new JPanel()); 
        }

        pageLabel.setText(String.format("%d / %d", currentPage + 1, totalPages));
        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < totalPages - 1);

        productDisplayPanel.revalidate();
        productDisplayPanel.repaint();
    }

    // --- handleProductClick (3단계 코드와 동일) ---
    private void handleProductClick(Product product) {
        
        String confirmMsg = String.format("'%s' 상품을 장바구니에 추가하시겠습니까?\n(%,d원)", 
                                          product.getName(), product.getPrice());
        
        int choice = JOptionPane.showConfirmDialog(
            this, 
            confirmMsg, 
            "장바구니 추가", 
            JOptionPane.YES_NO_OPTION 
        );

        if (choice == JOptionPane.YES_OPTION) {
            manager.getCart().addProduct(product);
            
            JOptionPane.showMessageDialog(
                this, 
                "'" + product.getName() + "'가 장바구니에 추가되었습니다.", 
                "추가 완료", 
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}