package mainpage.view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField; // [5단계 신규]

import mainpage.controller.KioskAppManager;
import mainpage.util.InputValidator;

import javax.swing.JOptionPane; // [5단계 신규]
import javax.swing.BorderFactory; // [5단계 신규]
import java.awt.BorderLayout; // [5단계 신규]
import java.awt.GridLayout; // [5단계 신규]
import java.awt.Font; // [5단계 신규]
import java.awt.event.ComponentAdapter; // [5단계 신규]
import java.awt.event.ComponentEvent; // [5단계 신규]


/**
 * [View - Panel 6 (5단계 구현)]
 * 1단계 Skeleton -> 5단계 실제 입력 폼으로 구현
 * 1. 이름/전화번호 JTextField, '불러오기', '뒤로가기' 버튼 배치
 * 2. '불러오기' 버튼 클릭 시 InputValidator로 검증
 * 3. manager.loadCart() 호출 후, 성공 시 "CART" 패널로 이동
 */
public class LoadCartPanel extends JPanel {

    private KioskAppManager manager;
    private JTextField nameField;
    private JTextField phoneField;

    public LoadCartPanel(KioskAppManager manager) {
        this.manager = manager;

        // 1. 레이아웃 설정 (BorderLayout)
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100)); // 상하좌우 여백

        // 2. 상단 타이틀
        JLabel titleLabel = new JLabel("장바구니 불러오기");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // 3. 중앙: 입력 폼 (GridLayout)
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        nameField = new JTextField(20);
        phoneField = new JTextField(20);

        formPanel.add(new JLabel("고객 이름:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("전화번호 (예: 01012345678):"));
        formPanel.add(phoneField);
        
        add(formPanel, BorderLayout.CENTER);

        // 4. 하단: 버튼 (GridLayout)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton loadButton = new JButton("불러오기");
        JButton backButton = new JButton("메인으로 돌아가기");
        
        loadButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 16));

        buttonPanel.add(loadButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 5. 이벤트 리스너

        // '뒤로 가기' 버튼 클릭 시 (기존 로직 유지)
        backButton.addActionListener(e -> manager.navigateTo("MAIN_PAGE"));

        // '불러오기' 버튼 클릭 시
        loadButton.addActionListener(e -> {
            handleLoadCart();
        });

        // [5단계 선택] 패널이 보일 때마다 입력 필드 초기화
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                nameField.setText("");
                phoneField.setText("");
                nameField.requestFocusInWindow(); // 이름 필드에 포커스
            }
        });
    }

    /**
     * [5단계 신규 헬퍼 메소드]
     * '불러오기' 버튼 클릭 시 호출됩니다.
     */
    private void handleLoadCart() {
        String name = nameField.getText();
        String phone = phoneField.getText();

        // 1. [5단계 계획] InputValidator로 입력값 검증
        if (!InputValidator.isValidName(name)) {
            JOptionPane.showMessageDialog(this, "이름이 유효하지 않습니다. 다시 시도해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!InputValidator.isValidPhoneNumber(phone)) {
            JOptionPane.showMessageDialog(this, "전화번호 형식이 잘못되었습니다 (예: 01012345678).", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. (검증 통과) Controller의 비즈니스 로직 호출
        //    (KioskAppManager가 Model 교체, 로그인 상태 설정, 알림창 표시까지 모두 처리)
        boolean success = manager.loadCart(name, phone); //

        // 3. [5단계 계획] 성공 시 "CART" 패널로 이동
        if (success) {
            manager.navigateTo("CART");
        }
        // (실패 시 KioskAppManager가 알림창을 띄우고, 이 패널에 머무름)
    }
}