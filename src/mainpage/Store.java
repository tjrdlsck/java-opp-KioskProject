package mainpage;

import java.util.ArrayList;
import java.util.List;

// 가게의 기본 정보(이름, 설명)와, 해당 가게가 보유한 '메뉴 카테고리({@link Menu}) 목록'을 캡슐화합니다.
public class Store {

    // 가게의 이름
    private String name;
    
    // 가게에 대한 간단한 설명
    private String description;
    
    
    // 이 가게가 보유한 메뉴 카테고리(Menu)의 목록
    private List<Menu> menus;

    public Store(String name, String description) {
        this.name = name;
        this.description = description;
        this.menus = new ArrayList<>();
    }
    
     
    // 가게의 이름을 반환
    public String getName() {
        return name;
    }

    // 가게의 설명을 반환
    public String getDescription() {
        return description;
    }

    // 이 가게가 보유한 모든 메뉴 카테고리(Menu)의 리스트를 반환
    public List<Menu> getMenus() {
        return menus;
    }

    // 이 가게에 새로운 메뉴 카테고리(Menu)를 추가
    public void addMenu(Menu menu) {
        this.menus.add(menu);
    }
}