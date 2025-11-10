package mainpage;

import java.util.ArrayList;
import java.util.List;

// Cart (장바구니) 클래스
// [3단계 리팩토링]
// GUI와 완전히 분리하기 위해 모든 System.out.println 제거
public class Cart {
	// 장바구니에 담긴 항목(CartItem)들을 저장하는 리스트
    private final List<CartItem> cartItems;
  
    public Cart() {
        this.cartItems = new ArrayList<>();
    }

    /**
     * 장바구니에 상품을 추가 (3단계 앞부분에서 리팩토링됨)
     */
    public void addProduct(Product product) {
        CartItem existingItem = null;

        for (CartItem item : cartItems) {
            if (item.getProduct().equals(product)) {
                existingItem = item;
                break;
            }
        }
        
        if (existingItem != null) {
            existingItem.increaseQuantity();
        } else {
            cartItems.add(new CartItem(product, 1));
        }
        
        // [제거됨] System.out.printf(...)
    }

    /**
     * 장바구니에 담긴 모든 항목의 총 가격을 계산하여 반환합니다.
     * @return 장바구니의 총 주문 금액 (long 타입)
     */
    public long getTotalPrice() {
        long totalPrice = 0;
        
        for (CartItem item : cartItems) {
            totalPrice += item.getTotalPrice();
        }
        return totalPrice;
    }

    /**
     * 장바구니 항목 리스트(원본)를 반환합니다.
     * @return List<CartItem>
     */
    public List<CartItem> getItems() {
        return this.cartItems;
    }

    /**
     * (장바구니의 모든 항목을 삭제)
     * [3단계 수정] 콘솔 출력(System.out) 로직 제거
     */
    public void clear() {
        this.cartItems.clear();
        
        // [제거됨] System.out.println("장바구니를 비웠습니다.");
        // (이 알림은 4단계에서 KioskAppManager가 GUI 팝업으로 처리)
    }

    /**
     * [3단계] 이 메소드는 GUI(CartPanel)로 대체되었으므로 더 이상 사용되지 않음.
     * (콘솔 출력 로직 제거)
     */
    public void displayCartItems() {
        // [제거됨] System.out.println("\n--- 장바구니 현재 상태 ---");
        
        if (cartItems.isEmpty()) {
            // [제거됨] System.out.println("장바구니가 비어있습니다.");
        } else { 
            for (CartItem item : cartItems) {
                // Product product = item.getProduct();
                // [제거됨] System.out.printf(...)
            }
        }
        
        // [제거됨] System.out.println("------------------------");
        // [제거됨] System.out.printf(...)
        // [제거됨] System.out.println("------------------------");
    }
}