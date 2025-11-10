package mainpage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

/**
 * [View - Main Frame]
 * 이 애플리케이션의 유일한 메인 프레임(창).
 * * 1. CardLayout을 가진 'mainPanelContainer'를 보유합니다.
 * 2. KioskAppManager(Controller)가 이 Frame을 제어합니다.
 * 3. 화면(Panel)을 추가하는 addPanel()
 * 4. 화면을 전환하는 showPanel() 메소드를 제공합니다. (Controller가 호출)
 */
public class MainFrame extends JFrame {

    /**
     * KioskAppManager로부터 이 패널을 받습니다.
     * 이 패널은 CardLayout을 사용하며, 모든 '화면(JPanel)'들을 담게 됩니다.
     */
    private JPanel mainPanelContainer;

    /**
     * MainFrame 생성자
     * @param mainPanelContainer KioskAppManager가 생성/관리하는 CardLayout 패널
     */
    public MainFrame(JPanel mainPanelContainer) {
        this.mainPanelContainer = mainPanelContainer;

        // --- 프레임 기본 설정 ---
        setTitle("키오스크 (v1.0 Skeleton)");
        // 창을 닫을 때 프로그램이 완전히 종료되도록 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setSize(800, 600); // 초기 창 크기
        
        // 창을 화면 중앙에 배치
        setLocationRelativeTo(null); 

        // --- 컴포넌트 추가 ---
        // KioskAppManager로부터 받은 CardLayout 패널을
        // JFrame의 메인 컨텐츠로 설정
        this.add(mainPanelContainer); 
    }

    /**
     * KioskAppManager가 호출하여 '화면(Panel)'을 추가하는 메소드
     * @param panel 추가할 화면 (예: new MainPagePanel(...))
     * @param name  해당 화면을 식별할 고유 이름 (예: "MAIN_PAGE")
     */
    public void addPanel(JPanel panel, String name) {
        this.mainPanelContainer.add(panel, name);
    }
}