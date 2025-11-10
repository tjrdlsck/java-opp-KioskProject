package mainpage;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane; // [3단계 신규] 팝업창을 위해 import
import javax.swing.UIManager;   // (2단계에서 추가됨)
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * [View - Panel 3 (3단계 수정)]
 * 1. handleProductClick 메소드에 JOptionPane 확인/알림 로직 구현
 */
public class ProductMenuPanel extends JPanel {

    private KioskAppManager manager;
    private JLabel titleLabel;
    private JPanel productButtonPanel;
    private JButton backButton;
    
    private Store currentStore;

    // --- 페이지네이션 상태 변수 ---
    private List<Product> allProducts;
    private int currentPage;
    private int totalPages;
    
    private final int ROWS_PER_PAGE = 3;
    private final int COLS_PER_PAGE = 3;
    private final int PRODUCTS_PER_PAGE = ROWS_PER_PAGE * COLS_PER_PAGE; 

    private JPanel paginationPanel;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageLabel;
    // --- 페이지네이션 끝 ---


    public ProductMenuPanel(KioskAppManager manager) {
        this.manager = manager;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. 상단 타이틀 레이블
        titleLabel = new JLabel("상품 목록", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 중앙: 상품 버튼 패널 (3x3 고정)
        productButtonPanel = new JPanel(new GridLayout(ROWS_PER_PAGE, COLS_PER_PAGE, 10, 10));
        add(productButtonPanel, BorderLayout.CENTER);

        
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
        backButton = new JButton("이전 메뉴로 돌아가기 (카테고리 선택)");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        bottomPanel.add(backButton, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);

        
        // 4. 페이지네이션 버튼 이벤트 리스너
        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateProductView(); 
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                updateProductView(); 
            }
        });

        // 5. '뒤로 가기' 버튼 이벤트 리스너
        backButton.addActionListener(e -> {
            if (this.currentStore != null) {
                manager.showStoreMenu(this.currentStore);
            } else {
                manager.goHome();
            }
        });
    }
    
    /**
     * [setMenu 메소드] (변경 없음)
     */
    public void setMenu(Menu menu, Store store) {
        if (menu == null || store == null) return;

        this.currentStore = store;
        this.allProducts = menu.getProducts();
        
        titleLabel.setText(String.format("[%s] 상품 목록", menu.getCategoryName()));

        this.currentPage = 0; 
        if (this.allProducts.isEmpty()) {
            this.totalPages = 1;
        } else {
            this.totalPages = (int) Math.ceil((double) this.allProducts.size() / PRODUCTS_PER_PAGE);
        }

        updateProductView();
    }
    
    /**
     * [updateProductView 메소드] (변경 없음)
     */
    private void updateProductView() {
        productButtonPanel.removeAll();

        int startIndex = currentPage * PRODUCTS_PER_PAGE;
        int endIndex = Math.min(startIndex + PRODUCTS_PER_PAGE, allProducts.size());

        // 버튼 스타일 (2단계 수정본)
        Border defaultBorder = UIManager.getBorder("Button.border");
        Border padding = new EmptyBorder(10, 10, 10, 10);
        Border compoundBorder = new CompoundBorder(defaultBorder, padding);

        for (int i = startIndex; i < endIndex; i++) {
            Product product = allProducts.get(i);
            
            String buttonText = String.format("<html><center>%s<br/>(%,d원)</center></html>",
                                              product.getName(),
                                              product.getPrice());
                                              
            JButton productButton = new JButton(buttonText);
            productButton.setFont(new Font("SansSerif", Font.BOLD, 16));

            // 스타일 적용 (2단계 수정본)
            productButton.setBackground(Color.WHITE);
            productButton.setOpaque(true);
            productButton.setFocusPainted(false);
            productButton.setBorder(compoundBorder);
            
            productButton.addActionListener(e -> {
                handleProductClick(product); // [수정] 이 메소드 내부 로직 변경
            });
            
            productButtonPanel.add(productButton);
        }

        // 빈 슬롯 채우기 (2단계 수정본)
        int emptySlots = PRODUCTS_PER_PAGE - (endIndex - startIndex);
        for (int i = 0; i < emptySlots; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setOpaque(false);
            productButtonPanel.add(emptyPanel);
        }

        // 페이지네이션 UI 갱신 (2단계 수정본)
        pageLabel.setText(String.format("%d / %d", currentPage + 1, totalPages));
        prevButton.setEnabled(currentPage > 0); 
        nextButton.setEnabled(currentPage < totalPages - 1);
        
        if (totalPages <= 1) {
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
        }

        productButtonPanel.revalidate();
        productButtonPanel.repaint();
    }
    
    /**
     * [3단계 핵심 수정]
     * 사용자가 상품 버튼을 클릭했을 때 호출되는 메소드.
     * @param product 사용자가 클릭한 Product 객체
     */
    private void handleProductClick(Product product) {
        
        // 1. [신규] 확인 팝업창 띄우기
        // (부모 컴포넌트, 메시지, 타이틀, 옵션 타입)
        String confirmMsg = String.format("'%s' (%s) 상품을\n장바구니에 추가하시겠습니까?", 
                                         product.getName(),
                                         String.format("%,d원", product.getPrice()));
        
        int choice = JOptionPane.showConfirmDialog(
                        this,  // 부모 컴포넌트 (이 패널 중앙에 뜸)
                        confirmMsg, 
                        "장바구니 추가 확인", 
                        JOptionPane.OK_CANCEL_OPTION // 확인 / 취소 버튼
                     );

        // 2. [신규] 사용자가 '확인'을 눌렀는지 검사
        if (choice == JOptionPane.OK_OPTION) {
            // 3. Controller를 통해 Cart 모델에 상품 추가
            manager.getCart().addProduct(product);
            
            // 4. [신규] 완료 알림 팝업창 띄우기
            JOptionPane.showMessageDialog(
                    this, 
                    "'" + product.getName() + "' 상품이 장바구니에 추가되었습니다.",
                    "추가 완료", 
                    JOptionPane.INFORMATION_MESSAGE // 정보 아이콘
            );
        } else {
            // (사용자가 '취소'를 누른 경우 - 아무것도 하지 않음)
        }
    }
}