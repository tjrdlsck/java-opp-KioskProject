package mainpage.model;

import java.util.ArrayList;
import java.util.List;

// Cart (장바구니) 클래스
// [3단계 수정] M-VC 원칙에 따라, Model은 View에 의존해서는 안 됨.
// 모든 System.out.println 구문을 제거하여 순수 Model로 리팩토링.
public class Cart {
	// 장바구니에 담긴 항목(CartItem)들을 저장하는 리스트
    private final List<CartItem> cartItems;
  
    public Cart() {
        this.cartItems = new ArrayList<>();
    }

    // 장바구니에 상품을 추가
    // [3단계 수정] System.out.printf 제거
    public void addProduct(Product product) {
        // 장바구니에 이미 같은 상품이 있는지 찾아 담는 변수 
        CartItem existingItem = null;

        for (CartItem item : cartItems) {
            if (item.getProduct().equals(product)) {
                // (상품을 찾았을 경우) 찾은 항목을 existingItem 변수에 저장, 더 이상 찾아볼 필요가 없으므로 'break'를 통해 반복문을 즉시 중단
                existingItem = item;
                break;
            }
        }
        if (existingItem != null) {
            // (상품이 이미 존재했을 경우) existingItem 변수에 찾은 항목이 들어있으므로, 해당 항목의 수량을 1 증가
            existingItem.increaseQuantity();
        } else {
            // (상품이 존재하지 않았을 경우) 수량 1개를 가진 새로운 CartItem 객체를 만들어 리스트(cartItems)에 추가
            cartItems.add(new CartItem(product, 1));
        }
        // [3단계] GUI의 JOptionPane이 이 알림 역할을 대신함.
        // System.out.printf("'%s'가 장바구니에 추가되었습니다.\n", product.getName());
    }

    // 장바구니에 담긴 모든 항목의 총 가격을 계산하여 반환합니다. [return 값 : 장바구니의 총 주문 금액 (long 타입)]
    public long getTotalPrice() {
        long totalPrice = 0;
        
        for (CartItem item : cartItems) {
            totalPrice += item.getTotalPrice();
        }
        return totalPrice;
    }

    public List<CartItem> getItems() {
        return this.cartItems;
    }

    // (장바구니의 모든 항목을 삭제)
    // [3단계 수정] System.out.println 제거
    public void clear() {
        this.cartItems.clear();
        // System.out.println("장바구니를 비웠습니다.");
    }

    // [3단계 수정] System.out.println 제거
    // (이 메소드의 GUI 버전은 CartPanel.java의 updateCartView()가 담당)
    public void displayCartItems() {
        /*
        System.out.println("\n--- 장바구니 현재 상태 ---");
        
        if (cartItems.isEmpty()) {
            System.out.println("장바구니가 비어있습니다.");
        } else { 
            for (CartItem item : cartItems) {
                Product product = item.getProduct();
                System.out.printf("- %s | %d개 | %,d원\n",
                        product.getName(),      // 상품 이름
                        item.getQuantity(),     // 수량
                        item.getTotalPrice());  // 해당 항목의 총 가격
            }
        }
        
        System.out.println("------------------------");
        System.out.printf("총 주문 금액: %,d원\n", getTotalPrice());
        System.out.println("------------------------");
        */
    }
}