package mainpage;

// 이 클래스는 특정 '상품(Product)'과 해당 상품의 '수량(quantity)'을 하나의 단위로 묶어 관리함
public class CartItem {

    // 상품 객체
    private final Product product;

    // 상품의 수량
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // 상품 객체 반환 (Product)
    public Product getProduct() {
        return product;
    }

    // 현재 수량을 반환
    public int getQuantity() {
        return quantity;
    }

    // 수량 1 증가 
    // Cart/addProduct(Product) 메소드에서 이미 장바구니에 존재하는 상품이 또 추가될 때 호출됩니다.
    public void increaseQuantity() {
        this.quantity++;
    }
    
    // (상품 가격 * 수량)의 총액 (long 타입) 반환
    public long getTotalPrice() {
        // (long) * (int) -> (long) * (long)으로 자동 형 변환되어 연산 결과가 안전하게 long 타입으로 반환됩니다.
        return (long) product.getPrice() * this.quantity;
    }
}