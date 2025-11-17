package ui;

import mainpage.CongestionManager;
import mainpage.Store;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CafeSelectionScreen extends JPanel {

    private final List<Store> stores;
    private final MainApplication mainApp;
    private final CongestionManager congestionManager;
    private final Map<String, JLabel> congestionLabels;

    public CafeSelectionScreen(List<Store> stores, MainApplication mainApp, CongestionManager congestionManager) {
        this.stores = stores;
        this.mainApp = mainApp;
        this.congestionManager = congestionManager;
        this.congestionLabels = new HashMap<>();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel title = new JLabel("KIOSK | 원하시는 카페를 선택해 주세요   ");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 34));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 24, 20));
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(20, 20, 20, 20));

        if (this.stores != null && !this.stores.isEmpty()) {
            int frameWidth = 648;
            int columns = 2;
            int hGap = 24;
            int totalWidth = frameWidth - 40 - (columns - 1) * hGap;
            int cardWidth = totalWidth / columns;
            int cardHeight = 220;

            for (Store s : this.stores) {
                JPanel card = createStoreCard(s);
                card.setPreferredSize(new Dimension(cardWidth, cardHeight));
                grid.add(card);
            }
        } else {
            grid.add(new JLabel("불러올 카페 정보가 없습니다."));
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createStoreCard(Store store) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel imageArea = new JPanel(new GridBagLayout());
        imageArea.setPreferredSize(new Dimension(360, 180));
        imageArea.setBackground(new Color(250, 250, 250));
        imageArea.setBorder(BorderFactory.createLineBorder(new Color(235, 235, 235)));
        imageArea.add(new JLabel("(이미지 없음)"));

        JPanel textArea = new JPanel();
        textArea.setLayout(new BoxLayout(textArea, BoxLayout.Y_AXIS));
        textArea.setBackground(Color.WHITE);
        textArea.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel nameLabel = new JLabel(store.getName());
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- UI 수정 부분 ---
        // 설명과 혼잡도를 한 줄에 담을 패널
        JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        descPanel.setBackground(Color.WHITE);
        descPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(store.getDescription());
        descLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        descLabel.setForeground(new Color(110, 110, 110));

        long initialCongestion = congestionManager.getCongestionForNext10Min(store.getName());
        JLabel congestionLabel = new JLabel(" (10분 내: " + initialCongestion + "건)");
        congestionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        
        if (initialCongestion >= 15) {
            congestionLabel.setForeground(Color.RED);
        } else if (initialCongestion >= 10) {
            congestionLabel.setForeground(new Color(255, 165, 0));
        } else {
            congestionLabel.setForeground(new Color(0, 128, 0));
        }

        descPanel.add(descLabel);
        descPanel.add(congestionLabel);
        congestionLabels.put(store.getName(), congestionLabel); // 맵에 혼잡도 라벨 저장

        textArea.add(nameLabel);
        textArea.add(Box.createRigidArea(new Dimension(0, 6)));
        textArea.add(descPanel); // 수정된 패널 추가

        card.add(imageArea, BorderLayout.CENTER);
        card.add(textArea, BorderLayout.SOUTH);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showCongestionDetails(store);
            }
        });

        return card;
    }

    private void showCongestionDetails(Store store) {
        Map<LocalTime, Long> slots = congestionManager.getCongestionByTimeSlots(store.getName());
        
        StringBuilder sb = new StringBuilder("<html><body>");
        sb.append("<h3>[").append(store.getName()).append("] 시간대별 예상 혼잡도</h3>");
        sb.append("<p>15분 단위로 픽업 예약된 주문 건수입니다.</p><hr>");

        if (slots.isEmpty()) {
            sb.append("<p>현재 예약된 주문이 없습니다.</p>");
        } else {
            for (Map.Entry<LocalTime, Long> entry : slots.entrySet()) {
                LocalTime startTime = entry.getKey();
                LocalTime endTime = startTime.plusMinutes(15);
                long count = entry.getValue();
                String congestionLevel;
                String color;

                if (count >= 15) {
                    congestionLevel = "혼잡";
                    color = "red";
                } else if (count >= 10) {
                    congestionLevel = "보통";
                    color = "orange";
                } else {
                    congestionLevel = "여유";
                    color = "green";
                }
                sb.append(String.format("<p><b>%s ~ %s</b>: %d건 <font color='%s'>(%s)</font></p>",
                        startTime.format(DateTimeFormatter.ofPattern("HH:mm")), 
                        endTime.format(DateTimeFormatter.ofPattern("HH:mm")), 
                        count, color, congestionLevel));
            }
        }
        sb.append("</body></html>");

        int choice = JOptionPane.showConfirmDialog(
                this,
                sb.toString(),
                "혼잡도 정보",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (choice == JOptionPane.OK_OPTION) {
            mainApp.showMenuScreen(store);
        }
    }
}
