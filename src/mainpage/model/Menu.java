package mainpage.model;

import java.util.ArrayList;
import java.util.List;


// Store 내부에 존재하는 개별 메뉴 카테고리(예: "버거", "음료")에 속한 상품 목록을 관리
public class Menu {

    // 메뉴 카테고리의 이름
    private String categoryName;
    
    // 이 메뉴 카테고리에 속한 상품 Product 들의 목록
    private List<Product> products;

    public Menu(String categoryName) {
        this.categoryName = categoryName;
        this.products = new ArrayList<>(); 
    }

    // 메뉴 카테고리의 이름을 반환
    public String getCategoryName() {
        return categoryName;
    }

    // 이 메뉴 카테고리에 속한 모든 상품의 리스트를 반환
    public List<Product> getProducts() {
        return products;
    }

    // 메뉴 카테고리에 상품(Product) 추가
    public void addProduct(Product product) {
        this.products.add(product);
    }
}