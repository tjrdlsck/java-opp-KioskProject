package ui;

import mainpage.Store;
// [계획 5.2] JFrame 관련 import는 모두 제거됩니다.
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * [계획 5.2] 'JFrame'에서 'JPanel'로 변경됩니다.
 * MainApplication의 CardLayout에 표시될 '카페 선택 화면' 패널입니다.
 */
// 1. 'extends JFrame' -> 'extends JPanel'
public class CafeSelectionScreen extends JPanel {

    // [계획 5.2] 주입받은 데이터를 저장할 필드
    private final List<Store> stores;
    
    // [계획 5.2] 화면 전환(콜백)을 위해 MainApplication의 참조를 저장할 필드
    private final MainApplication mainApp;

    /**
     * [계획 5.2] 생성자가 List<Store>와 MainApplication 참조를 '주입' 받도록 변경
     * @param stores      MainApplication에서 로드한 전체 가게 목록
     * @param mainApp     화면 전환(콜백)을 위한 MainApplication의 인스턴스
     */
    public CafeSelectionScreen(List<Store> stores, MainApplication mainApp) {
        this.stores = stores;
        this.mainApp = mainApp; // 콜백을 위해 참조 저장

        // 2. 'JFrame' 관련 설정(setTitle, setSize, setDefaultCloseOperation 등) 모두 삭제
        
        // [수정] 패널 자체의 레이아웃 및 기본 설정
        setLayout(new BorderLayout());
        setBackground(Color.WHITE); // 패널 배경색 설정

        // 상단 타이틀 (구조는 동일)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("KIOSK | 원하시는 카페를 선택해 주세요   ");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 34));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel coffeeIcon = new JLabel("☕");
        coffeeIcon.setFont(new Font("Dialog", Font.PLAIN, 28));
        coffeeIcon.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(title, BorderLayout.CENTER);
        header.add(coffeeIcon, BorderLayout.EAST);
        
        // 3. 'JFrame'의 add(header, BorderLayout.NORTH) 대신 'JPanel'의 add 사용
        add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel();
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(20, 20, 20, 20));

        int columns = 2;
        int hGap = 24;
        int vGap = 20;
        grid.setLayout(new GridLayout(0, columns, hGap, vGap));

        // [수정] 카드 크기 계산 시, JFrame의 크기(width) 대신 MainApplication의 크기를 참조
        // (여기서는 하드코딩된 값 648을 기준으로 계산합니다.)
        int frameWidth = 648; 
        
        if (this.stores != null && !this.stores.isEmpty()) {
            int totalWidth = frameWidth - 40; // 좌우 패딩 고려
            int cardWidth = (totalWidth - (columns - 1) * hGap) / columns;
            int cardHeight = 220; // 적당한 세로 크기

            for (Store s : this.stores) {
                // [수정] createStoreCard 호출 시 allStores 대신 '선택된 가게(s)'만 전달
                // (MainApplication의 콜백 메소드를 호출하도록 변경)
                JPanel card = createStoreCard(s);
                card.setPreferredSize(new Dimension(cardWidth, cardHeight));
                grid.add(card);
            }
        } else {
            JLabel empty = new JLabel("불러올 카페 정보가 없습니다.");
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            grid.add(empty);
        }

        JScrollPane scroll = new JScrollPane(grid, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // 4. 'setVisible(true)' 삭제 (패널의 표시는 상위 컨테이너 MainApplication이 결정)
    }

    /**
     * [계획 5.2] 'allStores' 파라미터 제거.
     * @param store      이 카드에 해당하는 가게
     * @return 스타일이 적용된 카드 JPanel
     */
    private JPanel createStoreCard(Store store) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // ... (이미지 영역, 텍스트 영역 UI 생성 코드는 원본과 동일) ...
        // 이미지 영역 (플레이스홀더)
        JPanel imageArea = new JPanel();
        imageArea.setPreferredSize(new Dimension(360, 180));
        imageArea.setBackground(new Color(250, 250, 250));
        imageArea.setBorder(BorderFactory.createLineBorder(new Color(235, 235, 235)));
        JLabel imgLabel = new JLabel("(이미지 없음)");
        imgLabel.setForeground(new Color(140, 140, 140));
        imageArea.setLayout(new GridBagLayout());
        imageArea.add(imgLabel);

        // 텍스트 영역
        JPanel textArea = new JPanel();
        textArea.setLayout(new BoxLayout(textArea, BoxLayout.Y_AXIS));
        textArea.setBackground(Color.WHITE);
        textArea.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel name = new JLabel(store.getName());
        name.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel(store.getDescription());
        desc.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        desc.setForeground(new Color(110, 110, 110));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        textArea.add(name);
        textArea.add(Box.createRigidArea(new Dimension(0, 6)));
        textArea.add(desc);

        card.add(imageArea, BorderLayout.CENTER);
        card.add(textArea, BorderLayout.SOUTH);


        // 마우스 클릭시 MenuScreen 실행
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // [계획 5.2] 핵심 변경:
                // '새 창'을 띄우고 '현재 창'을 닫는 대신,
                // MainApplication의 콜백 메소드를 호출하여 화면 전환을 '요청'합니다.
                
                // (삭제) SwingUtilities.invokeLater(() -> new MenuScreen(allStores, store));
                // (삭제) SwingUtilities.getWindowAncestor(card).dispose();
                
                // [추가] MainApplication의 메소드를 호출합니다.
                // (Swing EDT에서 실행되는 것이 보장되므로 invokeLater 불필요)
                mainApp.showMenuScreen(store);
            }
        });

        return card;
    }

    // 5. 'main' 메소드 완전 삭제 (MainApplication.java가 유일한 진입점)
}