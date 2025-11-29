package ui;

import mainpage.Cart;
import mainpage.CartItem;
import mainpage.Product;
import javax.swing.*;
import java.awt.*;

public class OrderPanel extends JPanel {
    private final Cart cart = new Cart();
    private final JPanel orderListPanel;
    private final JScrollPane scrollPane;

    public OrderPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("🧾 주문 내역"));

        orderListPanel = new JPanel();
        orderListPanel.setLayout(new BoxLayout(orderListPanel, BoxLayout.Y_AXIS));
        orderListPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(orderListPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        orderListPanel.setPreferredSize(null);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);

    }

    public void addOrder(Product product) {
        cart.addProduct(product);
        refreshOrderList();
    }

    public void clearOrders() {
        cart.clear();
        refreshOrderList();
    }

    public boolean isEmpty() {
        return cart.isEmpty();
    }

    // MenuScreen(주문하기 버튼)에서 총액을 계산할 수 있도록 관리 중인 Cart 객체 자체를 반환합니다.
    public Cart getCart() {
        return this.cart;
    }

    private JButton createButton(String text, Dimension size) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(size);
        btn.setMargin(new Insets(0, 0, 0, 0));
        return btn;
    }
    
    private void refreshOrderList() {
    	orderListPanel.removeAll();

    	for (CartItem item : cart.getItems()) {
    		// CartItem에서 Product, 이름, 수량을 가져옵니다.
            final Product product = item.getProduct();
            String name = product.getName();
            int count = item.getQuantity();
            
            JPanel row = new JPanel(new GridBagLayout());
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 5, 0, 5);
            gbc.gridy = 0;

            // 메뉴명
            JLabel nameLabel = new JLabel(name);
            nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
            gbc.gridx = 0;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            row.add(nameLabel, gbc);

            // 버튼 영역
            JPanel right = new JPanel(new GridBagLayout());
            right.setOpaque(false);

            GridBagConstraints rb = new GridBagConstraints();
            rb.insets = new Insets(0, 2, 0, 2);

            JButton up = createButton("▲", new Dimension(24, 28));
            JButton down = createButton("▼", new Dimension(24, 28));
            JButton del = createButton("❌", new Dimension(32, 28));
            JLabel countLabel = new JLabel(String.valueOf(count));
            countLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

            up.setMargin(new Insets(0, 0, 0, 0));
            down.setMargin(new Insets(0, 0, 0, 0));
            del.setMargin(new Insets(0, 0, 0, 0));
            // 수량 증가
            up.addActionListener(e -> { cart.addProduct(product); refreshOrderList(); });
            // 수량 감소
            down.addActionListener(e -> { cart.decrementProduct(product); refreshOrderList(); });
            // 완전 삭제
            del.addActionListener(e -> { cart.removeProduct(product); refreshOrderList(); });

            rb.gridx = 0; right.add(up, rb);
            rb.gridx = 1; right.add(countLabel, rb);
            rb.gridx = 2; right.add(down, rb);
            rb.gridx = 3; right.add(del, rb);

            // 오른쪽 고정, 왼쪽 가변
            gbc.gridx = 1;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            row.add(right, gbc);

            orderListPanel.add(row);
        }

        SwingUtilities.invokeLater(() ->
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum())
        );

        orderListPanel.setPreferredSize(null);
        orderListPanel.revalidate();
        orderListPanel.repaint();
    }

}
