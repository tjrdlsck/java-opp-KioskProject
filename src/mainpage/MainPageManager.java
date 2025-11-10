package mainpage;

import javax.swing.SwingUtilities;

public class MainPageManager {
	public static void main(String[] args) {
        // Swing UI는 반드시 Event Dispatch Thread(EDT)에서 생성하고 실행해야 합니다.
        // main 스레드에서 직접 UI를 생성하면 스레드 안전성 문제가 발생할 수 있습니다.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 기존 Kiosk.java 대신, GUI와 비즈니스 로직을 총괄하는
                // 'KioskAppManager'를 생성하고 실행합니다.
                KioskAppManager manager = new KioskAppManager();
                manager.start();
            }
        });
    }
}