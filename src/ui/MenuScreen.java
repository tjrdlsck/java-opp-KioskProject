package ui;

import mainpage.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class MenuScreen extends JPanel {
    private final CafeMenuPanel cafeMenuPanel;
    private final OrderPanel orderPanel;
    private JScrollPane cafeScroll;
    private final MainApplication mainApp;
    private final CongestionManager congestionManager;

    private List<Store> stores;
    private final CartFileManager cartFileManager;
    private final OrderFileManager orderFileManager;
    private String currentCustomerPhone = null;
    private Store currentStore = null;

    public MenuScreen(List<Store> allStores, Store initialStore, MainApplication mainApp, CongestionManager congestionManager) {
        this.stores = allStores;
        this.mainApp = mainApp;
        this.congestionManager = congestionManager;
        this.cartFileManager = new CartFileManager();
        this.orderFileManager = new OrderFileManager();

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(230, 230, 230));

        JButton leftArrow = createStyledArrowButton("◀");
        JButton rightArrow = createStyledArrowButton("▶");

        JPanel cafeScrollContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cafeScrollContainer.setBackground(new Color(230, 230, 230));

        if (this.stores != null) {
            for (Store store : this.stores) {
                JButton cafeBtn = new JButton(store.getName());
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
        JPanel rightButtons = new JPanel(new GridLayout(2, 2, 10, 10)); // 레이아웃을 2x2로 다시 변경
        rightButtons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton saveCartBtn = new JButton("장바구니 저장");
        JButton clearBtn = new JButton("전체삭제");
        JButton loadCartBtn = new JButton("장바구니 불러오기");
        JButton orderBtn = new JButton("결제하기"); // 다시 "결제하기" 버튼으로 변경

        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);
        saveCartBtn.setFont(buttonFont);
        clearBtn.setFont(buttonFont);
        loadCartBtn.setFont(buttonFont);
        orderBtn.setFont(buttonFont);

        rightButtons.add(saveCartBtn);
        rightButtons.add(clearBtn);
        rightButtons.add(loadCartBtn);
        rightButtons.add(orderBtn); // "결제하기" 버튼 추가

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        bottomPanel.add(orderPanel);
        bottomPanel.add(rightButtons);
        bottomPanel.setPreferredSize(new Dimension(648, 200));
        add(bottomPanel, BorderLayout.SOUTH);

        saveCartBtn.addActionListener(e -> processSaveCart());
        clearBtn.addActionListener(e -> orderPanel.clearOrders());
        loadCartBtn.addActionListener(e -> processLoadCart());
        orderBtn.addActionListener(e -> processBankTransferOrder()); // "결제하기" 버튼 클릭 시 무통장입금 프로세스 실행
    }

    private void scrollCafeList(int offset) {
        JScrollBar bar = cafeScroll.getHorizontalScrollBar();
        int newValue = bar.getValue() + offset;
        if (newValue < 0) newValue = 0;
        if (newValue > bar.getMaximum()) newValue = bar.getMaximum();
        bar.setValue(newValue);
    }

    public void loadCafeMenu(Store store) {
        this.currentStore = store;
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
            if (phone == null) return null;
            if (Pattern.matches(phoneRegex, phone)) return phone;
            else JOptionPane.showMessageDialog(this, "잘못된 형식입니다. '010'으로 시작하는 11자리 숫자를 입력해주세요.\n(예: 01012345678)", "입력 오류", JOptionPane.ERROR_MESSAGE);
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

    private void updateCongestionLabel(LocalTime selectedTime, JLabel congestionLabel) {
        if (selectedTime == null) return;

        long count = congestionManager.getCongestionFor5MinSlot(currentStore.getName(), selectedTime);
        congestionLabel.setText(String.format("해당 5분간 주문: %d건", count));

        if (count >= 5) { // 5건 이상 '혼잡'
            congestionLabel.setForeground(Color.RED);
        } else if (count >= 3) { // 3-4건 '보통'
            congestionLabel.setForeground(new Color(255, 165, 0));
        } else { // 2건 이하 '여유'
            congestionLabel.setForeground(new Color(0, 128, 0));
        }
    }

    private LocalTime getPickupTime() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<LocalTime> timeSelector = new JComboBox<>();

        // [v7 수정] 최소 5분 후 픽업 및 시간 정규화 로직
        LocalTime now = LocalTime.now();
        int minute = now.getMinute();
        int remainder = minute % 5;
        int minutesToNextBoundary = (remainder == 0) ? 5 : (5 - remainder);
        LocalTime firstSlot = now.plusMinutes(minutesToNextBoundary).withSecond(0).withNano(0);

        if (ChronoUnit.MINUTES.between(now, firstSlot) < 5) {
            firstSlot = firstSlot.plusMinutes(5);
        }

        List<LocalTime> timeSlots = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            timeSlots.add(firstSlot.plusMinutes(i * 5L));
        }
        timeSelector.setModel(new DefaultComboBoxModel<>(timeSlots.toArray(new LocalTime[0])));

        JLabel congestionLabel = new JLabel("시간을 선택하세요.");
        congestionLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        // [v7 수정] 렌더러에서 상대 시간 제거, 시간만 표시
        timeSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof LocalTime) {
                    LocalTime time = (LocalTime) value;
                    setText(time.format(DateTimeFormatter.ofPattern("HH:mm")));
                }
                return this;
            }
        });

        timeSelector.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                updateCongestionLabel((LocalTime) e.getItem(), congestionLabel);
            }
        });

        if (!timeSlots.isEmpty()) {
            updateCongestionLabel(timeSlots.get(0), congestionLabel);
        }

        panel.add(new JLabel("픽업 희망 시간을 선택하세요:"), BorderLayout.NORTH);
        panel.add(timeSelector, BorderLayout.CENTER);
        panel.add(congestionLabel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(this, panel, "픽업 시간 선택", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return (LocalTime) timeSelector.getSelectedItem();
        } else {
            return null;
        }
    }



    private void processBankTransferOrder() {
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

        Cart cart = orderPanel.getCart();
        StringBuilder sb = new StringBuilder("🧾 주문내역\n\n");
        for (CartItem item : cart.getItems()) {
            Product p = item.getProduct();
            sb.append(String.format("- %s (%,d원) x %d개 = %,d원\n", p.getName(), p.getPrice(), item.getQuantity(), item.getTotalPrice()));
        }
        sb.append("\n--------------------\n");
        sb.append(String.format("총 결제 금액: %,d원\n", cart.getTotalPrice()));
        sb.append(String.format("픽업 희망 시간: %s\n\n", pickupTime.format(DateTimeFormatter.ofPattern("HH:mm"))));
        sb.append("이대로 주문하시겠습니까?");
        int choice = JOptionPane.showConfirmDialog(this, sb.toString(), "주문 확인", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            // 무통장입금 정보 다이얼로그 표시
            Random rand = new Random();
            String accountNumber = String.format("%03d-%06d-%02d-%03d",
                rand.nextInt(1000),
                rand.nextInt(1000000),
                rand.nextInt(100),
                rand.nextInt(1000)
            );

            String bankInfo = String.format(
                "<html><body style='width: 300px;'>"
                + "<h3>무통장입금 안내</h3>"
                + "<p>아래 계좌로 입금하신 후 '결제 완료' 버튼을 눌러주세요.</p>"
                + "<hr>"
                + "<p><b>은행:</b> 경기은행</p>"
                + "<p><b>계좌번호:</b> %s</p>"
                + "<p><b>예금주:</b> %s</p>"
                + "<p><b>금액:</b> %,d원</p>"
                + "</body></html>",
                accountNumber, "객체지향", cart.getTotalPrice()
            );

            Object[] options = {"결제완료", "취소"};
            int bankChoice = JOptionPane.showOptionDialog(this, bankInfo, "무통장입금",
                                                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                                            null, options, options[0]); // options[0] is default

            if (bankChoice == 0) { // "입금완료" 버튼을 누른 경우 (인덱스 0)
                // "결제 완료"를 누른 경우
                JOptionPane.showMessageDialog(this, "결제가 확인되었습니다.", "결제 확인", JOptionPane.INFORMATION_MESSAGE);

                Order newOrder = new Order(cart, pickupTime, currentStore, "무통장입금");
                newOrder.displayOrderDetails();
                orderFileManager.saveOrder(newOrder);
                congestionManager.refreshCache();

                if (this.currentCustomerPhone != null) {
                    cartFileManager.deleteCart(this.currentCustomerPhone);
                    JOptionPane.showMessageDialog(this, "주문이 완료되어 전화번호 '" + this.currentCustomerPhone + "'님의 저장된 장바구니도 삭제했습니다.", "저장된 내역 삭제", JOptionPane.INFORMATION_MESSAGE);
                    this.currentCustomerPhone = null;
                }
                JOptionPane.showMessageDialog(this, "주문이 완료되었습니다. (주문번호: " + newOrder.getOrderNumber() + ")", "주문 완료", JOptionPane.INFORMATION_MESSAGE);
                orderPanel.clearOrders();
            } else {
                JOptionPane.showMessageDialog(this, "결제가 취소되었습니다.", "결제 취소", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
