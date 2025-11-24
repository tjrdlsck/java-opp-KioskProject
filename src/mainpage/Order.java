package mainpage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private static final String SEQUENCE_FILE = "order_sequence.txt";
    private static int orderSequence;

    static {
        try (BufferedReader reader = new BufferedReader(new FileReader(SEQUENCE_FILE))) {
            String line = reader.readLine();
            if (line != null && line.contains(",")) {
                String[] parts = line.split(",");
                String lastOrderDate = parts[0];
                int lastSequence = Integer.parseInt(parts[1]);

                String todayDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

                if (todayDate.equals(lastOrderDate)) {
                    orderSequence = lastSequence; // 오늘 날짜와 같으면, 마지막 번호부터 계속
                } else {
                    orderSequence = 0; // 날짜가 다르면, 0으로 초기화
                }
            } else {
                orderSequence = 0; // 파일 내용이 비어있거나 형식이 잘못된 경우
            }
        } catch (IOException | NumberFormatException e) {
            orderSequence = 0; // 파일을 찾을 수 없거나 기타 오류 발생 시
        }
    }

    private final int orderNumber;
    private final String storeName;
    private final List<CartItem> orderedItems;
    private final long totalPrice;
    private final LocalDateTime orderDateTime;
    private final LocalTime pickupTime;
    private final String paymentMethod;

    public Order(Cart cart, LocalTime pickupTime, Store store, String paymentMethod) {
        this.orderNumber = ++orderSequence;
        saveSequence(); // 새 주문번호와 날짜를 파일에 저장

        this.storeName = store.getName();
        this.orderedItems = new ArrayList<>(cart.getItems());
        this.totalPrice = cart.getTotalPrice();
        this.orderDateTime = LocalDateTime.now();
        this.pickupTime = pickupTime;
        this.paymentMethod = paymentMethod;
    }

    private static synchronized void saveSequence() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SEQUENCE_FILE))) {
            String todayDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            writer.println(todayDate + "," + orderSequence);
        } catch (IOException e) {
            System.err.println("주문 번호 저장 중 오류 발생: " + e.getMessage());
        }
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getStoreName() {
        return storeName;
    }

    public List<CartItem> getOrderedItems() {
        return orderedItems;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public LocalTime getPickupTime() {
        return pickupTime;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void displayOrderDetails() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = orderDateTime.format(formatter);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedPickupTime = pickupTime.format(timeFormatter);

        System.out.println("\n========= 주문이 완료되었습니다 =========");
        System.out.println("주문 번호: " + orderNumber);
        System.out.println("주문 시각: " + formattedDateTime);
        System.out.println("픽업 시간: " + formattedPickupTime);
        System.out.println("주문 매장: " + storeName);

        System.out.println("\n[주문 내역]");
        for (CartItem item : orderedItems) {
            Product product = item.getProduct();
            System.out.printf("- %s | %d개 | %,d원\n",
                    product.getName(),
                    item.getQuantity(),
                    item.getTotalPrice());
        }

        System.out.println("------------------------------------");
        System.out.printf("결제 금액: %,d원\n", totalPrice);
        System.out.printf("결제 수단: %s\n", paymentMethod);
        System.out.println("======================================");
    }
}
