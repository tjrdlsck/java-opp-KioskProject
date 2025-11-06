package mainpage;

import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter; 
import java.util.ArrayList;
import java.util.List;

// 주문하기가 완료된 시점의 거래 내역을 기록하는 불변(Immutable) 데이터 객체
// Cart 로부터 주문 순간의 데이터를 복사하여 절대로 변경되지 않는 '거래 기록'을 생성
public class Order {
	// 모든 주문 객체가 공유하는 '주문 번호 생성기', Order 객체가 생성될 때마다 1씩 증가하여 고유한 주문 번호를 보장
    private static int orderSequence = 0;

    private final int orderNumber;

    private final List<CartItem> orderedItems;

    // 주문 시점의 최종 결제 금액
    private final long totalPrice;

    // 주문이 생성된(완료된) 정확한 시각
    private final LocalDateTime orderDateTime;

    public Order(Cart cart) {
        // 'static' 변수인 orderSequence를 1 증가시키고 (전위 증가: ++), 그 값을 이 객체의 'final' 필드인 orderNumber에 할당
        this.orderNumber = ++orderSequence;

        // cart.getItems()가 반환하는 '원본 리스트'를 그대로 쓰는 것이 아니라, 'new ArrayList<>()'를 통해 '새로운 리스트'를 생성하고 원본 리스트의 모든 항목을 여기에 복사
        // 이를 통해, 이후에 'cart'가 비워지더라도 이 'order'의 내역은 안전하게 보존됩니다.
        this.orderedItems = new ArrayList<>(cart.getItems());

        // 주문 순간의 'cart' 총액을 계산하여 'final' 필드에 저장(박제)합니다.
        this.totalPrice = cart.getTotalPrice();
        
        // 'LocalDateTime.now()'를 통해 '현재 시각'을 'final' 필드에 저장(박제)합니다.
        this.orderDateTime = LocalDateTime.now();
    }

    // 주문 번호를 반환
    public int getOrderNumber() {
        return orderNumber;
    }

    // 주문에 포함된 상품 항목 리스트(복사본)를 반환
    public List<CartItem> getOrderedItems() {
        return orderedItems;
    }

    // 주문의 최종 결제 금액을 반환 (Long)
    public long getTotalPrice() {
        return totalPrice;
    }

    // 주문이 완료된 시각을 반환 (LocalDateTime)
    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    // 이 주문의 상세 내역(영수증)을 콘솔에 출력
    public void displayOrderDetails() {
        //    'LocalDateTime' 객체는 데이터일 뿐, "yyyy-MM-dd HH:mm:ss" 형식은 'DateTimeFormatter'를 통해 지정해야 함
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = orderDateTime.format(formatter); // "2025-11-06 10:07:25"

        // 영수증 헤더 출력
        System.out.println("\n========= 주문이 완료되었습니다 =========");
        System.out.println("주문 번호: " + orderNumber);
        System.out.println("주문 시각: " + formattedDateTime);
        
        // 주문 내역 본문 출력
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
        System.out.println("======================================");
    }
}