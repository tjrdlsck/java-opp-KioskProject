package mainpage;

// java.util.Objects 클래스:
// 객체의 null 검사, 해시코드 생성, 'equals' 비교 등을
// 안전하고 편리하게 수행하도록 도와주는 유틸리티 클래스입니다.
import java.util.Objects;

// 키오스크에서 판매하는 개별 상품(예: "치즈버거")의 정보를 관리

public class Product {

	// 상품의 이름
    private String name;
    
    // 상품의 가격
    private int price;
    
    // 상품 이미지 파일의 경로 (빈 경우도 존재) 
    private String imagePath;

    private String storeName;

    public Product(String name, int price, String imagePath, String storeName) {
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.storeName = storeName; // [추가]
    }

    // 상품의 이름을 반환
    public String getName() {
        return name;
    }

    // 상품의 가격을 반환
    public int getPrice() {
        return price;
    }

    // 상품의 이미지 경로를 반환
    public String getImagePath() {
        return imagePath;
    }
    
    public String getStoreName() {
        return storeName;
    }
    
    // 이 상품이 유효한 이미지 경로를 가지고 있는지 검사
    public boolean hasImage() {
        return this.imagePath != null && !this.imagePath.trim().isEmpty();
    }

    // 이 객체가 문자열로 표현되어야 할 때(예: System.out.println(product)) 호출되는 메소드
    @Override
    public String toString() {
        // 'hasImage()'를 사용하여 이미지 경로가 있는지 확인
    	if (hasImage()) {
            // 2. (이미지 있음) 'String.format'과 '%,d'(3자리 콤마)를 사용해 포맷팅
    		return String.format("%s (가격: %,d원) | 이미지: %s", this.name, this.price, this.imagePath);
    	}
        // 3. (이미지 없음) 가격 정보만 포맷팅
    	return String.format("%s (가격: %,d원)", this.name, this.price);
    }
    
    // 이름(name)이 같으면 true, 다르면 false
    @Override
    public boolean equals(Object o) {
        // 'o'가 'this'와 동일한 메모리 주소(객체)인지 확인
        if (this == o) return true;
        
        // 'o'가 null이거나, 'Product' 클래스(또는 그 자식)가 아닌지 확인
        if (o == null || getClass() != o.getClass()) return false;
        
        // 'o'가 'Product'임을 확인했으므로, 안전하게 형 변환
        Product product = (Product) o;
        
        // 'this.name'과 'product.name'이 같은지 비교
        return Objects.equals(name, product.name);
    }

    // equals(): 두 객체가 "논리적으로 같은가?"의 기준을 정합니다. 
    // hashCode(): HashMap 등에서 객체를 저장할 "위치(서가 번호)"를 정합니다.
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}