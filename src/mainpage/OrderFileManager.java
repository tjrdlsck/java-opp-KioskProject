package mainpage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 완료된 주문(Order) 내역을 파일로 관리하는 클래스입니다.
 */
public class OrderFileManager {

    private static final String ORDER_DATA_DIRECTORY = "saved_orders";

    /**
     * 혼잡도 계산에 필요한 최소한의 주문 정보를 담는 내부 데이터 클래스(Record)입니다.
     */
    public record OrderInfo(String storeName, LocalTime pickupTime) {}

    public OrderFileManager() {
        File dir = new File(ORDER_DATA_DIRECTORY);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("'" + ORDER_DATA_DIRECTORY + "' 디렉토리를 생성했습니다.");
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
        String fileName = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".txt";
        File orderFile = new File(ORDER_DATA_DIRECTORY, fileName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(orderFile, true))) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

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
            writer.println("--- END ORDER #" + order.getOrderNumber() + " ---\n");

            System.out.println("주문 #" + order.getOrderNumber() + "이(가) '" + orderFile.getPath() + "' 파일에 저장되었습니다.");

        } catch (IOException e) {
            System.err.println("주문 파일 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * [신규] 오늘 날짜의 로그 파일에서 모든 주문 정보를 읽어와 리스트로 반환합니다.
     *
     * @return 오늘 주문 정보(OrderInfo)가 담긴 리스트
     */
    public List<OrderInfo> getTodaysOrderInfo() {
        String fileName = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".txt";
        File orderFile = new File(ORDER_DATA_DIRECTORY, fileName);
        List<OrderInfo> orderInfos = new ArrayList<>();

        if (!orderFile.exists()) {
            return orderInfos; // 오늘 주문 파일이 없으면 빈 리스트 반환
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(orderFile))) {
            String line;
            String currentStoreName = null;
            LocalTime currentPickupTime = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Store: ")) {
                    currentStoreName = line.substring("Store: ".length());
                } else if (line.startsWith("PickupTime: ")) {
                    String timeStr = line.substring("PickupTime: ".length());
                    currentPickupTime = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
                } else if (line.startsWith("--- END ORDER #")) {
                    if (currentStoreName != null && currentPickupTime != null) {
                        orderInfos.add(new OrderInfo(currentStoreName, currentPickupTime));
                    }
                    // 다음 주문을 위해 초기화
                    currentStoreName = null;
                    currentPickupTime = null;
                }
            }
        } catch (IOException e) {
            System.err.println("주문 로그 파일 읽기 중 오류 발생: " + e.getMessage());
        }
        return orderInfos;
    }
}