package mainpage.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

/**
 * [View - Main Frame]
 * 이 애플리케이션의 유일한 메인 프레임(창).
 * [v3.1 수정] 창 세로 크기 (600 -> 700)
 * [v3.2 수정] 창 세로 크기 (700 -> 800)
 */
public class MainFrame extends JFrame {

    private JPanel mainPanelContainer;

    public MainFrame(JPanel mainPanelContainer) {
        this.mainPanelContainer = mainPanelContainer;

        // --- 프레임 기본 설정 ---
        setTitle("키오스크 (v2.0 Data-Bound)"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
        // [v3.2 수정] 이미지 짤림 현상 해결을 위해 세로 800으로 최종 조정
        setSize(800, 800); 
        
        setLocationRelativeTo(null); 

        this.add(mainPanelContainer); 
    }

    /**
     * KioskAppManager가 호출하여 '화면(Panel)'을 추가하는 메소드
     */
    public void addPanel(JPanel panel, String name) {
        this.mainPanelContainer.add(panel, name);
    }
}