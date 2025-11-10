# 1. 핵심 실행 흐름

```mermaid
classDiagram
    direction LR
    
    MainPageManager ..> Kiosk : "uses"
    
    class Kiosk {
        -List~Store~ stores
        -Cart cart
        -CartFileManager cartFileManager
        +run()
        +initializeData()
        +processLoadCart()
        +processSaveCart()
    }
    
    class DataLoader {
        +List~Store~ loadStores()
    }
    
    class CartFileManager {
        +saveCart(Cart, String, String)
        +Cart loadCart(String, String)
        +deleteCart(String, String)
    }

    class Store {
        <<Data Object>>
        -String name
    }
    
    class Cart {
         <<Data Object>>
    }

    Kiosk --* "1" CartFileManager : "owns"
    Kiosk ..> DataLoader : "uses"
    Kiosk ..> Store : "holds list of"
    
    DataLoader ..> Store : "creates"
    CartFileManager ..> Cart : "creates / loads"
```

# 2. 상품 데이터 모델

```mermaid
classDiagram
    direction LR

    class Store {
        -String name
        -String description
        -List~Menu~ menus
        +addMenu(Menu)
        +getMenus()
    }
    
    class Menu {
        -String categoryName
        -List~Product~ products
        +addProduct(Product)
        +getProducts()
    }
    
    class Product {
        -String name
        -int price
        -String imagePath
        +toString()
        +equals(Object)
        +hashCode()
    }
    
    Store "1" *-- "0..*" Menu : "contains"
    Menu "1" *-- "0..*" Product : "contains"
```

# 3. 장바구니 및 주문

```mermaid
classDiagram
    direction LR

    class Kiosk {
        -Cart cart
        +run()
        +placeOrder()
    }
    
    class Cart {
        -List~CartItem~ cartItems
        +addProduct(Product)
        +getTotalPrice()
        +clear()
    }
    
    class CartItem {
        -Product product
        -int quantity
        +increaseQuantity()
        +getTotalPrice()
    }
    
    class Product {
        <<Catalog Data>>
        -String name
        -int price
    }
    
    class Order {
        -List~CartItem~ orderedItems
        -long totalPrice
        -LocalDateTime orderDateTime
        +displayOrderDetails()
    }
    
    Kiosk "1" --o "1" Cart : "manages"
    Cart "1" *-- "0..*" CartItem : "holds"
    CartItem "1" o-- "1" Product : "references"
    
    Kiosk ..> Order : "creates"
    Order ..> Cart : "uses (copies data from)"
    Order "1" *-- "1..*" CartItem : "clones"
```
# 4. 읽는 법

### 1. 기본 구성 요소: 클래스 박스 (Class Box)

가장 기본이 되는 사각형 박스는 **클래스(Class)**, 즉 '설계도' 자체를 의미합니다.

* **맨 위 칸:** `Kiosk` - 클래스의 이름입니다.
* **중간 칸:** 속성(Attributes) 또는 필드(Fields). 이 클래스가 가지는 '데이터'입니다.
    * `+` (Public): **공개**. `run()`처럼 클래스 외부에서 자유롭게 접근(호출)할 수 있습니다.
    * `-` (Private): **비공개**. `-cart`처럼 해당 클래스 *내부*에서만 접근할 수 있습니다. (캡슐화)
    * `List<Store> stores`: `stores`라는 이름의 변수가 `Store` 객체 여러 개를 담는 `List` 자료구조임을 의미합니다.
* **아래 칸:** 연산(Operations) 또는 메서드(Methods). 이 클래스가 할 수 있는 '행동'입니다.
    * `+run()`
    * `+initializeData()`



> **스테레오타입(Stereotype) `<<...>>`**
>
> * `<<Data Object>>`나 `<<Catalog Data>>` 같은 표기는 이 클래스의 '역할'을 명시하는 주석입니다. "이 클래스는 복잡한 로직보다는 데이터를 담는 데 집중하는 객체입니다"라고 알려주는 것입니다.

---

### 2. 핵심: 관계와 화살표 (Relationships)

클래스 박스들을 연결하는 선은 클래스 간의 '관계'를 나타냅니다. 이것이 다이어그램의 핵심입니다.

#### ① 의존성 (Dependency): `..>` (점선 화살표)

* **기호:** `..>`
* **의미:** "Uses" (일시적으로 사용). 한 클래스가 다른 클래스를 *잠깐* 사용하지만, 멤버 변수로 소유하지는 않습니다.
* **예시:** `MainPageManager ..> Kiosk : "uses"`
* **읽는 법:** "`MainPageManager`가 `Kiosk`를 **사용합니다**."
    * (해석: `MainPageManager`의 `main` 메서드가 `Kiosk` 객체를 생성하고 `run()`을 호출하지만, `MainPageManager`가 `Kiosk`를 멤버 변수로 *소유*하지는 않습니다.)

#### ② 집계 (Aggregation): `o--` (빈 다이아몬드)

* **기호:** `o--`
* **의미:** "Has-a" (포함). '전체-부분' 관계이지만, 생명주기가 독립적입니다. "부분"이 "전체" 없이도 존재할 수 있습니다.
* **예시:** `CartItem "1" o-- "1" Product : "references"`
* **읽는 법:** "`CartItem`이 `Product`를 **참조합니다** (포함합니다)."
    * (해석: `CartItem`은 `Product`의 정보를 *참조*하지만, `CartItem`이 장바구니에서 사라져도 원본 `Product`(메뉴판의 상품)는 그대로 존재해야 합니다.)

#### ③ 합성 (Composition): `*--` (채워진 다이아몬드)

* **기호:** `*--`
* **의미:** "Owns" (소유). '전체-부분' 관계이며, 생명주기가 같습니다. "전체"가 사라지면 "부분"도 함께 사라지는 *강한 소유* 관계입니다.
* **예시:** `Store "1" *-- "0..*" Menu : "contains"`
* **읽는 법:** "`Store`가 `Menu`를 **소유합니다** (포함합니다)."
    * (해석: `Menu`는 `Store`에 전적으로 종속됩니다. `Store`가 존재하지 않으면, 그 가게의 `Menu`도 존재 의미가 없습니다.)

---

### 3. 수량: 다중성 (Multiplicity)

관계(선)의 양쪽 끝에 있는 숫자는 각 클래스의 **인스턴스(객체) 수량**을 의미합니다.

* **`1`**: 정확히 **1개**의 객체와 관계를 맺습니다.
* **`0..*`**: **0개 이상** (0, 1, 2, ... N개)의 객체와 관계를 맺을 수 있습니다.
* **`1..*`**: **1개 이상** (최소 1개)의 객체와 관계를 맺습니다.

---

### 종합 연습: 문장으로 읽기

이제 이 세 가지 요소를 조합하여 다이어그램을 '문장'으로 읽어보겠습니다.

> **예시 1: `Store "1" *-- "0..*" Menu : "contains"`**

1.  **양쪽 클래스:** `Store`, `Menu`
2.  **관계 (선):** `*--` (합성/소유)
3.  **다중성:** `Store` 쪽은 `1`, `Menu` 쪽은 `0..*`
4.  **레이블:** `"contains"`

**종합 해석:** "한(`1`) 개의 `Store` 객체는, 0개 이상(`0..*`)의 `Menu` 객체를 **소유(Composition, `*--`)**하며, 이 관계를 '포함(contains)'이라고 부릅니다."

> **예시 2: `Kiosk "1" --o "1" Cart : "manages"`**
>
> (참고: `o--`나 `--o`는 같은 집계 관계입니다.)

**종합 해석:** "한(`1`) 개의 `Kiosk` 객체는, 정확히 한(`1`) 개의 `Cart` 객체를 **참조(Aggregation, `o--`)**하며, 이 관계를 '관리(manages)'라고 부릅니다."

> **예시 3: `Order "1" *-- "1..*" CartItem : "clones"`**

**종합 해석:** "한(`1`) 개의 `Order` 객체는, (주문 순간 복제된) `CartItem` 객체를 1개 이상(`1..*`) **소유(Composition, `*--`)**합니다."
