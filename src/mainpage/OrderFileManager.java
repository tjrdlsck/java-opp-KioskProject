package mainpage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 완료된 주문(Order) 내역을 파일로 관리하는 클래스입니다.
 */
public class OrderFileManager {

    private static final String ORDER_DATA_DIRECTORY = "saved_orders";

    public OrderFileManager() {
        File dir = new File(ORDER_DATA_DIRECTORY);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("' " + ORDER_DATA_DIRECTORY + "' 디렉토리를 생성했습니다.");
            } else {
                System.err.println("오류: '" + ORDER_DATA_DIRECTORY + "' 디렉토리 생성에 실패했습니다.");
            }
        }
    }

    /**
     * 완료된 주문 정보를 날짜 기반 파일에 저장(추가)합니다.
     *
     * @param order 저장할 Order 객체
     */
    public void saveOrder(Order order) {
        // 1. 파일 이름 결정 (예: "2025-11-17.txt")
        String fileName = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".txt";
        File orderFile = new File(ORDER_DATA_DIRECTORY, fileName);

        // 2. FileWriter를 'append' 모드(true)로 열어 파일에 내용을 추가합니다.
        try (PrintWriter writer = new PrintWriter(new FileWriter(orderFile, true))) {

            // 3. 주문 내역을 서식에 맞춰 문자열로 변환
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            // --- 주문 구분선 ---
            writer.println("--- ORDER #" + order.getOrderNumber() + " ---");
            writer.println("Store: " + order.getStoreName());
            writer.println("OrderTime: " + order.getOrderDateTime().format(dateTimeFormatter));
            writer.println("PickupTime: " + order.getPickupTime().format(timeFormatter));
            writer.println("TotalPrice: " + order.getTotalPrice());
            writer.println("Items:");

            for (CartItem item : order.getOrderedItems()) {
                Product product = item.getProduct();
                writer.printf("- %s | %d개 | %,d원\n",
                        product.getName(),
                        item.getQuantity(),
                        item.getTotalPrice());
            }
            writer.println("--- END ORDER #" + order.getOrderNumber() + " ---\n"); // 주문 간 구분을 위한 공백 라인

            System.out.println("주문 #" + order.getOrderNumber() + "이(가) '" + orderFile.getPath() + "' 파일에 저장되었습니다.");

        } catch (IOException e) {
            System.err.println("주문 파일 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}