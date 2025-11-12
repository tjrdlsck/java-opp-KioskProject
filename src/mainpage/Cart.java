package mainpage;

import java.util.ArrayList;
import java.util.List;

// Cart (장바구니) 클래스
// 상품 추가, 총액 계산, 항목 조회
public class Cart {
	// 장바구니에 담긴 항목(CartItem)들을 저장하는 리스트
    private final List<CartItem> cartItems;
  
    public Cart() {
        this.cartItems = new ArrayList<>();
    }

    // 장바구니에 상품을 추가
    // 이미 같은 상품이 장바구니에 있다면 수량을 1 증가시키고, 새로운 상품이라면 장바구니에 새로 추가합니다.
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
        System.out.printf("'%s'가 장바구니에 추가되었습니다.\n", product.getName());
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

    // (장바구니의 모든 항목을 삭제) 주로 주문이 완료되었을 때 호출
    public void clear() {
        this.cartItems.clear();
        System.out.println("장바구니를 비웠습니다.");
    }
    
    /**
     * [신규] 장바구니에서 상품의 수량을 1 감소시킵니다. (UI의 '▼' 버튼용)
     * <p>
     * 만약 상품의 수량이 1개였으면, 감소 시 0개가 되므로 리스트에서 완전히 제거합니다.
     * 요청에 따라 콘솔 출력 로직을 포함합니다.
     *
     * @param product 수량을 감소시킬 Product 객체
     */
    public void decrementProduct(Product product) {
        CartItem targetItem = null;
        for (CartItem item : cartItems) {
            if (item.getProduct().equals(product)) {
                targetItem = item;
                break;
            }
        }

        if (targetItem != null) {
            // 수량이 1보다 많으면 단순 감소
            if (targetItem.getQuantity() > 1) {
                targetItem.decreaseQuantity(); // CartItem에 이 메서드를 추가해야 합니다.
                System.out.printf("'%s'의 수량을 1 감소시켰습니다.\n", product.getName());
            } else {
                // 수량이 1개였으면 리스트에서 제거
                cartItems.remove(targetItem);
                System.out.printf("'%s'이(가) 장바구니에서 제거되었습니다.\n", product.getName());
            }
        }
    }

    /**
     * [신규] 장바구니에서 상품을 완전히 제거합니다. (UI의 '❌' 버튼용)
     * 요청에 따라 콘솔 출력 로직을 포함합니다.
     *
     * @param product 제거할 Product 객체
     */
    public void removeProduct(Product product) {
        // removeIf(람다) : cartItems 리스트를 순회하며,
        // 람다식(item -> ...)이 true를 반환하는 모든 항목을 제거합니다.
        // 제거 성공 여부를 boolean으로 반환합니다.
        boolean removed = cartItems.removeIf(item -> item.getProduct().equals(product));
        
        if (removed) {
            System.out.printf("'%s'을(를) 장바구니에서 완전히 제거했습니다.\n", product.getName());
        }
    }

    /**
     * [신규] 장바구니가 비어있는지 확인합니다. (UI의 isEmpty() 대체)
     *
     * @return 장바구니가 비어있으면 true
     */
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    // 장바구니의 현재 상태(담긴 항목, 총액)를 콘솔에 출력
    public void displayCartItems() {
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
    }
}