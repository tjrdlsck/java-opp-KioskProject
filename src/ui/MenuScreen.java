package ui;

import mainpage.Cart;
import mainpage.CartFileManager;
import mainpage.CartItem;
import mainpage.Order;
import mainpage.OrderFileManager;
import mainpage.Product;
import mainpage.Store;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

public class MenuScreen extends JPanel {
    private final CafeMenuPanel cafeMenuPanel;
    private final OrderPanel orderPanel;
    private JScrollPane cafeScroll;
    private JPanel cafeScrollContainer;

    private List<Store> stores;

    private final CartFileManager cartFileManager;
    private final OrderFileManager orderFileManager;
    private String currentCustomerPhone = null;
    private Store currentStore = null; // 현재 선택된 가게를 저장할 필드

    public MenuScreen(List<Store> allStores, Store initialStore) {
        int width = 648;
        setLayout(new BorderLayout());

        this.cartFileManager = new CartFileManager();
        this.orderFileManager = new OrderFileManager();

        this.stores = allStores;

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(230, 230, 230));

        JButton leftArrow = createStyledArrowButton("◀");
        JButton rightArrow = createStyledArrowButton("▶");

        cafeScrollContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cafeScrollContainer.setBackground(new Color(230, 230, 230));

        if (this.stores != null) {
            for (Store store : this.stores) {
                String cafeName = store.getName();

                JButton cafeBtn = new JButton(cafeName);
                cafeBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
                cafeBtn.setPreferredSize(new Dimension(144, 40));

                cafeBtn.addActionListener(e -> loadCafeMenu(store));

                cafeScrollContainer.add(cafeBtn);
            }
        }

        cafeScroll = new JScrollPane(
                cafeScrollContainer,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        cafeScroll.setBorder(null);
        cafeScroll.setWheelScrollingEnabled(false);
        leftArrow.addActionListener(e -> scrollCafeList(-150));
        rightArrow.addActionListener(e -> scrollCafeList(150));

        topPanel.add(leftArrow, BorderLayout.WEST);
        topPanel.add(cafeScroll, BorderLayout.CENTER);
        topPanel.add(rightArrow, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        cafeMenuPanel = new CafeMenuPanel();
        add(cafeMenuPanel, BorderLayout.CENTER);

        orderPanel = new OrderPanel();

        JPanel rightButtons = new JPanel(new GridLayout(2, 2, 10, 10));
        rightButtons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton saveCartBtn = new JButton("장바구니 저장");
        JButton clearBtn = new JButton("전체삭제");
        JButton loadCartBtn = new JButton("장바구니 불러오기");
        JButton orderBtn = new JButton("주문하기");

        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);
        saveCartBtn.setFont(buttonFont);
        clearBtn.setFont(buttonFont);
        loadCartBtn.setFont(buttonFont);
        orderBtn.setFont(buttonFont);

        rightButtons.add(saveCartBtn);
        rightButtons.add(clearBtn);
        rightButtons.add(loadCartBtn);
        rightButtons.add(orderBtn);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        bottomPanel.add(orderPanel);
        bottomPanel.add(rightButtons);
        bottomPanel.setPreferredSize(new Dimension(width, 200));
        add(bottomPanel, BorderLayout.SOUTH);

        saveCartBtn.addActionListener(e -> processSaveCart());
        clearBtn.addActionListener(e -> orderPanel.clearOrders());
        loadCartBtn.addActionListener(e -> processLoadCart());
        orderBtn.addActionListener(e -> processPlaceOrder());
    }

    private void scrollCafeList(int offset) {
        JScrollBar bar = cafeScroll.getHorizontalScrollBar();
        int newValue = bar.getValue() + offset;
        if (newValue < 0) newValue = 0;
        if (newValue > bar.getMaximum()) newValue = bar.getMaximum();
        bar.setValue(newValue);
    }

    public void loadCafeMenu(Store store) {
        this.currentStore = store; // 현재 가게 정보 저장
        cafeMenuPanel.loadCafeMenu(store, orderPanel::addOrder);
    }

    private JButton createStyledArrowButton(String unicodeArrow) {
        JButton btn = new JButton(unicodeArrow);
        btn.setForeground(Color.DARK_GRAY);
        btn.setBackground(new Color(230, 230, 230));
        btn.setFocusPainted(false);
        btn.setBorder(null);
        btn.setContentAreaFilled(false);
        Dimension arrowSize = new Dimension(35, 35);
        btn.setPreferredSize(arrowSize);
        btn.setMargin(new Insets(0, 0, 0, 0));
        return btn;
    }

    private String getValidPhoneNumber(String initialMessage) {
        String phoneRegex = "^010\\d{8}$";
        String phone;

        while (true) {
            phone = JOptionPane.showInputDialog(this, initialMessage, "전화번호 입력", JOptionPane.QUESTION_MESSAGE);

            if (phone == null) {
                return null;
            }

            if (Pattern.matches(phoneRegex, phone)) {
                return phone;
            }
            else {
                JOptionPane.showMessageDialog(this,
                        "잘못된 형식입니다. '010'으로 시작하는 11자리 숫자를 입력해주세요.\n(예: 01012345678)",
                        "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void processSaveCart() {
        Cart cart = orderPanel.getCart();
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "장바구니가 비어있어 저장할 수 없습니다.", "저장 실패", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String phone = getValidPhoneNumber("장바구니를 저장할 전화번호를 입력하세요:");
        if (phone == null) return;
        cartFileManager.saveCart(cart, phone);
        JOptionPane.showMessageDialog(this, "전화번호 " + phone + "으로 장바구니가 저장되었습니다.", "저장 완료", JOptionPane.INFORMATION_MESSAGE);
    }

    private void processLoadCart() {
        String phone = getValidPhoneNumber("불러올 장바구니의 전화번호를 입력하세요:");
        if (phone == null) return;
        Cart loadedCart = cartFileManager.loadCart(phone);
        if (loadedCart != null) {
            orderPanel.clearOrders();
            for (CartItem item : loadedCart.getItems()) {
                Product p = item.getProduct();
                for (int i = 0; i < item.getQuantity(); i++) {
                    orderPanel.addOrder(p);
                }
            }
            this.currentCustomerPhone = phone;
            JOptionPane.showMessageDialog(this, "전화번호 " + phone + "의 장바구니를 불러왔습니다.", "불러오기 성공", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "해당 정보로 저장된 장바구니를 찾을 수 없습니다.", "불러오기 실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalTime getPickupTime() {
        LocalTime pickupTime = null;
        while (pickupTime == null) {
            String timeStr = JOptionPane.showInputDialog(this, "픽업 희망 시간을 입력하세요 (HH:mm 형식, 예: 14:30)", "픽업 시간 선택", JOptionPane.QUESTION_MESSAGE);
            if (timeStr == null) {
                return null;
            }
            try {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime parsedTime = LocalTime.parse(timeStr, timeFormatter);

                if (parsedTime.isBefore(LocalTime.now())) {
                    JOptionPane.showMessageDialog(this, "픽업 시간은 현재 시간 이후여야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    pickupTime = parsedTime;
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "시간 형식이 잘못되었습니다. HH:mm 형식으로 입력해주세요. (예: 14:30)", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
        return pickupTime;
    }

    private void processPlaceOrder() {
        if (orderPanel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "주문 내역이 없습니다.");
            return;
        }

        if (currentStore == null) {
            JOptionPane.showMessageDialog(this, "가게가 선택되지 않았습니다. 상단에서 가게를 선택해주세요.", "주문 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalTime pickupTime = getPickupTime();
        if (pickupTime == null) {
            JOptionPane.showMessageDialog(this, "주문이 취소되었습니다.", "주문 취소", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String formattedPickupTime = pickupTime.format(DateTimeFormatter.ofPattern("HH:mm"));


        Cart cart = orderPanel.getCart();
        StringBuilder sb = new StringBuilder("🧾 주문내역\n\n");
        for (CartItem item : cart.getItems()) {
            Product p = item.getProduct();
            sb.append(String.format("- %s (%,d원) x %d개 = %,d원\n",
                    p.getName(), p.getPrice(), item.getQuantity(), item.getTotalPrice()));
        }
        sb.append("\n--------------------\n");
        sb.append(String.format("총 결제 금액: %,d원\n", cart.getTotalPrice()));
        sb.append(String.format("픽업 희망 시간: %s\n\n", formattedPickupTime));
        sb.append("이대로 주문하시겠습니까?");

        int choice = JOptionPane.showConfirmDialog(this, sb.toString(), "주문 확인", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            Order newOrder = new Order(cart, pickupTime, currentStore);
            newOrder.displayOrderDetails();
            orderFileManager.saveOrder(newOrder);
            if (this.currentCustomerPhone != null) {
                cartFileManager.deleteCart(this.currentCustomerPhone);
                JOptionPane.showMessageDialog(this,
                        "주문이 완료되어 전화번호 '" + this.currentCustomerPhone + "'님의 저장된 장바구니도 삭제했습니다.",
                        "저장된 내역 삭제", JOptionPane.INFORMATION_MESSAGE);
                this.currentCustomerPhone = null;
            }
            JOptionPane.showMessageDialog(this,
                    "주문이 완료되었습니다. (주문번호: " + newOrder.getOrderNumber() + ")",
                    "주문 완료", JOptionPane.INFORMATION_MESSAGE);
            orderPanel.clearOrders();
        }
    }
}
