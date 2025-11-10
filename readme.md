# Kiosk Project (Java Swing M-V-C)

## 1. 프로젝트 개요 (Introduction)

본 프로젝트는 콘솔 기반의 키오스크 애플리케이션을 **Java Swing**을 이용한 완전한 그래픽 사용자 인터페이스(GUI) 애플리케이션으로 마이그레이션하는 것을 목표로 합니다.

이 프로젝트의 가장 중요한 핵심은 **M-V-C (Model-View-Controller) 디자인 패턴**을 엄격하게 준수하는 것입니다. 이 문서는 Swing을 처음 접하는 팀원들이 M-V-C 아키텍처 하에서 어떻게 협업하고 기여할 수 있는지 상세히 안내하는 "A-to-Z" 가이드입니다.

---

## 2. 주요 기능 및 시연 (Features & Demo)

* **동적 UI 렌더링:**
    * `menuData` 텍스트 파일을 기반으로 가게, 카테고리, 상품을 동적으로 로드합니다.
    * 상품(`Product`)에 지정된 이미지(`images/...`)를 로드하여 버튼에 표시합니다.
* **페이지네이션 (Paging):**
    * 카테고리(`Menu`)와 상품(`Product`) 목록이 많아질 경우를 대비한 `<` `>` 버튼 페이지네이션을 구현했습니다.
* **M-V-C 데이터 흐름:**
    * **장바구니 추가:** 상품을 장바구니(`Cart`)에 추가하고 즉시 `JOptionPane`으로 피드백을 제공합니다.
    * **장바구니 조회:** 장바구니 패널(`CartPanel`) 진입 시, 현재 `Cart` 모델의 상태를 실시간으로 렌더링합니다.
* **주문 로직:**
    * '주문하기' 선택 시, `Cart` 모델을 기반으로 불변(Immutable) 객체인 `Order`를 생성합니다.
    * 주문 완료 패널(`OrderCompletePanel`)에 'GUI 영수증'을 동적으로 생성하여 표시합니다.
* **파일 I/O (데이터 관리):**
    * `InputValidator`를 통한 고객 정보(이름, 전화번호) 검증을 수행합니다.
    * **장바구니 저장:** 현재 `Cart`의 상태를 `saved_carts/` 디렉토리에 파일로 저장합니다.
    * **장바구니 불러오기:** 파일 시스템에서 `Cart` 데이터를 읽어와 현재 장바구니 상태를 교체합니다.
    * **저장 내역 삭제:** 기존에 저장된 장바구니 파일을 삭제합니다.

### 최종 UI



---

## 3. 핵심 아키텍처: M-V-C 패턴 (필독)

Swing을 처음 다루는 팀원들이 가장 먼저 이해해야 할 부분입니다. 왜 우리는 이 간단한 프로그램을 `mainpage`라는 하나의 패키지에 두지 않고, `controller`, `model`, `view` 등으로 복잡하게 분리했을까요?

이는 **관심사 분리 (Separation of Concerns, SoC**)라는 소프트웨어 공학의 핵심 원칙 때문입니다. M-V-C는 이 원칙을 구현하는 가장 고전적이고 강력한 디자인 패턴입니다.

> **M-V-C를 레스토랑에 비유해봅시다:**
>
> * **M (Model):** **주방장(Chef)과 요리(Dish)**입니다. 실제 데이터(`Product`, `Cart`)와 그 데이터를 조리하는 로직(`getTotalPrice()`)을 가집니다.
> * **V (View):** **레스토랑의 메뉴판과 테이블**입니다. 손님에게 '보여지는' 껍데기(`StorePagePanel`, `CartPanel`)입니다.
> * **C (Controller):** **웨이터(Waiter)**입니다. 손님(User)의 주문을 받아 주방(Model)에 전달하고, 완성된 요리(Model)를 손님(View)에게 가져다줍니다.

### M - Model (`mainpage.model`)

* **역할:** 애플리케이션의 **'데이터'**와 **'핵심 비즈니스 로직'**을 담당합니다.
* **예시:** `Product.java`, `Cart.java`, `Order.java`
* **⭐ 황금률 (Golden Rule):** Model은 View나 Controller의 존재를 **절대 알아서는 안 됩니다.** Model은 그저 자신의 데이터(예: `List<CartItem>`)를 관리하고, 자신의 로직(예: `addProduct`)을 수행할 뿐입니다.
* **엄격한 규칙:** `mainpage.model` 패키지의 클래스는 **절대로 `javax.swing.*`을 `import` 해서는 안 됩니다.** UI가 어떻게 생겼는지 알 필요가 없습니다. (v4.0 리팩토링에서 모든 `System.out`을 제거한 이유입니다.)

### V - View (`mainpage.view`)

* **역할:** 사용자에게 **'보여지는 껍데기'**입니다.
* **예시:** `StorePagePanel.java`, `CartPanel.java`, `MainFrame.java` (모든 `JPanel`과 `JFrame`이 여기에 속합니다.)
* **⭐ 황금률:** View는 **'멍청해야(Dumb)'** 합니다. View는 "이 버튼이 눌렸습니다!", "텍스트가 입력되었습니다!"라고 Controller에게 보고할 뿐, *그 결과* 무슨 일이 벌어지는지에 대한 비즈니스 로직을 스스로 처리하지 않습니다.

### C - Controller (`mainpage.controller`)

* **역할:** **'두뇌'**이자 **'중재자(Waiter)'**입니다.
* **예시:** `KioskAppManager.java` (우리 프로젝트의 유일한 Controller)
* **⭐ 황금률:** Controller는 Model과 View를 **모두 알고 있는 유일한 계층**입니다. 모든 데이터의 흐름은 반드시 Controller를 거쳐야 합니다.

### 데이터 흐름 (예: 상품을 장바구니에 담기)

팀원들이 가장 많이 수행할 작업의 흐름입니다.

1.  **[ V ]** 사용자가 `StorePagePanel`에서 '피치 아이스티' 버튼을 클릭합니다.
2.  **[ V ]** `StorePagePanel`은 `handleProductClick` 메소드에서 이 클릭 이벤트를 감지합니다.
3.  **[ V → C ]** `StorePagePanel`은 스스로 `Cart`를 수정하지 않고, `manager.getCart().addProduct(product)` 코드를 통해 `KioskAppManager`에게 **"이 상품을 카트에 추가해달라고 요청"**합니다.
4.  **[ C → M ]** `KioskAppManager`는 요청을 받아, 자신이 관리하는 Model인 `currentCart` 객체의 `addProduct(product)` 메소드를 **호출합니다.**
5.  **[ M ]** `Cart.java`는 `addProduct` 로직을 수행하여 내부의 `List<CartItem>`을 변경합니다. (이때 `Cart.java`는 이 요청이 `KioskAppManager`에게서 왔는지, GUI에서 왔는지 전혀 모릅니다.)
6.  **[ V ]** `StorePagePanel`은 `JOptionPane.showMessageDialog(...)`를 띄워 사용자에게 **시각적 피드백**을 줍니다.

-----

## 4\. 실행 방법 (How to Run)

이 프로젝트는 표준 Java(JavaSE-22)로 빌드되었으며, 외부 라이브러리 없이 Java Swing만 사용합니다.

### 4.1. 필수 요건

1.  **JDK (Java Development Kit):** **Java 17** 이상이 설치되어 있어야 합니다.
2.  **데이터 폴더:** 프로젝트가 올바르게 작동하려면, `src` 폴더가 아닌 **프로젝트 루트 디렉토리**(예: `KioskProject/`)에 다음 3개의 폴더가 존재해야 합니다.
      * `images/`: 상품 이미지를 보관합니다. (예: `icetea.jpeg`)
      * `menuData/`: 가게의 메뉴 정보가 담긴 `.txt` 파일들을 보관합니다.
      * `saved_carts/`: '장바구니 저장' 시 생성되는 `.txt` 파일들이 보관됩니다. (최초 실행 시 `CartFileManager`가 자동으로 생성합니다.)

### 4.2. IDE에서 실행하기

1.  Eclipse, IntelliJ 등 원하는 Java IDE에서 이 프로젝트를 'Existing Project'로 불러옵니다.
2.  `src` 폴더(패키지 탐색기)를 엽니다.
3.  `mainpage` 패키지 안에 있는 **`MainPageManager.java`** 파일을 찾습니다.
4.  `MainPageManager.java` 파일의 `main` 메소드를 \*\*"Run As Java Application"\*\*으로 실행합니다.
5.  정상적으로 실행되면 메인 프레임(`MainFrame`) 창이 열립니다.

-----

## 5\. 🗂️ 패키지 구조 (Project Structure)

**"어떤 파일을 수정해야 할지 모르겠다면, 이 구조를 보고 알맞은 패키지를 먼저 찾으세요."**

```
src/
│
├── mainpage/ (애플리케이션 진입점)
│   └── MainPageManager.java       # [실행] 프로그램의 main() 메소드
│
├── mainpage.controller/ (C: 두뇌 - 웨이터)
│   └── KioskAppManager.java       # [핵심] Model과 View를 연결하는 유일한 중재자
│
├── mainpage.model/ (M: 데이터 - 주방)
│   ├── Product.java               # 상품 데이터 (이름, 가격, 이미지경로)
│   ├── Cart.java                  # 장바구니 데이터 (상품 목록, 총액 계산)
│   ├── Order.java                 # 주문 데이터 (주문 시각, 주문 내역)
│   └── ... (CartItem.java, Menu.java, Store.java)
│
├── mainpage.view/ (V: 껍데기 - 메뉴판/테이블)
│   ├── MainFrame.java             # 애플리케이션의 유일한 '창'
│   ├── MainPagePanel.java         # 1. 메인 (가게 선택) 패널
│   ├── StorePagePanel.java        # 2. 가게 (카테고리/상품) 패널
│   ├── CartPanel.java             # 3. 장바구니 패널
│   ├── OrderCompletePanel.java    # 4. 주문 완료(영수증) 패널
│   ├── LoadCartPanel.java         # 5. 불러오기 패널
│   └── DeleteCartPanel.java       # 6. 삭제 패널
│
├── mainpage.service/ (Model을 돕는 외부 처리)
│   ├── DataLoader.java            # menuData/ 텍스트 파일을 읽어 Model(Store) 객체 생성
│   └── CartFileManager.java       # saved_carts/에 Cart 모델을 저장/로드/삭제
│
└── mainpage.util/ (보조 도구)
    └── InputValidator.java        # 이름, 전화번호 형식을 검증하는 헬퍼 클래스
```

-----

## 6\. 팀 역할 분담 (Roles & Responsibilities)

M-V-C 패턴은 협업을 위한 완벽한 가이드라인을 제공합니다. 자신의 역할에 맞는 패키지만 수정하고, 다른 영역을 침범하지 않는 것이 중요합니다.

### 롤 1: UI/UX 담당 (View 전문가)

  * **주요 작업 영역:** `mainpage.view` 패키지
  * **여러분의 임무:**
      * `JPanel`의 레이아웃(
        `BorderLayout`, `FlowLayout` 등)을 변경하여 더 예쁘게 만듭니다.
      * 버튼, 레이블의 폰트, 크기, 색상을 변경합니다. (v3.0 계획서의 6단계 참고)
      * "이 버튼은 오른쪽보다 왼쪽에 있는 게 좋겠습니다." -\> `JPanel`의 `add()` 순서를 변경합니다.
  * **여러분의 금지 사항:**
      * `mainpage.view` 클래스 안에서 `new CartFileManager()`를 호출하지 마세요. (파일 처리는 Controller의 영역입니다.)
      * `JButton`의 `ActionListener` 안에서 `getTotalPrice()` 같은 계산 로직을 직접 수행하지 마세요.
      * **규칙:** View는 오직 `manager.doSomething()`을 호출하여 Controller에게 \*\*"신호"\*\*만 보내야 합니다.

### 롤 2: 백엔드/로직 담당 (Model & Service 전문가)

  * **주요 작업 영역:** `mainpage.model`, `mainpage.service` 패키지
  * **여러분의 임무:**
      * "장바구니에 10% 할인 기능을 추가해주세요." -\> `Cart.java`의 `getTotalPrice()` 메소드 로직을 수정합니다.
      * "데이터 저장 방식을 .txt에서 JSON으로 바꿔주세요." -\> `CartFileManager.java` 내부의 파일 I/O 로직을 수정합니다. (Controller는 `saveCart`가 `boolean`만 반환하면 되므로 이 변경을 알 필요가 없습니다.)
      * `Product.java`에 '알레르기 정보' 필드를 추가합니다.
  * **여러분의 금지 사항:**
      * `mainpage.model` 패키지 내부에 **`javax.swing.JOptionPane`이나 `System.out.println`을 절대 사용하지 마세요.**
      * **규칙:** Model과 Service는 자신을 GUI에서 쓰는지, 콘솔에서 쓰는지, 혹은 웹에서 쓰는지 **전혀 몰라야 합니다.** 오직 데이터와 계산에만 집중하세요.

### 롤 3: 프로젝트 리더 (Controller/Architect)

  * **주요 작업 영역:** `mainpage.controller`, `mainpage` (전체 총괄)
  * **여러분의 임무:**
      * UI 담당자(View)와 로직 담당자(Model)가 만든 결과물을 `KioskAppManager.java`에서 \*\*'연결(Wiring)'\*\*합니다.
      * View로부터 `manager.placeOrder()` 신호를 받으면, `new Order()`(Model 생성) -\> `cartFileManager.deleteCart()`(Service 호출) -\> `new Cart()`(Model 갱신) -\> `MapsTo("ORDER_COMPLETE")`(View 갱신)처럼 전체 흐름을 지휘합니다.
      * 프로젝트의 전체 아키텍처를 관리하고 이 `README.md`를 최신화합니다.

-----

## 7\. 기여 방법: 기능 추가 가이드 (초심자 튜토리얼)

M-V-C가 복잡해 보일 수 있지만, 실제 작업 흐름은 매우 명확하고 단순합니다. 이 튜토리얼을 따라 "메인 페이지에 '이벤트' 버튼 및 패널 추가하기" 작업을 함께 해봅시다.

### 예시 시나리오: `MainPagePanel`에 '이벤트' 버튼 추가하기

#### 1단계: (V) View 수정 - 버튼 추가

  * **담당:** UI/UX 담당자
  * **파일:** `mainpage.view.MainPagePanel.java`

먼저 '껍데기'인 View에 버튼을 추가합니다. `MainPagePanel` 생성자 하단의 `orderMenuPanel` (하단 버튼 3개 묶음)을 찾습니다.

```java
// (MainPagePanel.java 생성자 내부)

// 3. 하단: 고정 메뉴 패널 (변경 없음)
// [수정] 1행 3열 -> 1행 4열로 변경
JPanel orderMenuPanel = new JPanel(new GridLayout(1, 4, 10, 10)); // 3 -> 4
        
JButton cartButton = new JButton("장바구니 확인");
JButton loadButton = new JButton("장바구니 불러오기");
JButton deleteButton = new JButton("저장된 내역 삭제");
JButton eventButton = new JButton("이벤트"); // [신규] 이벤트 버튼 추가

Font bottomFont = new Font("SansSerif", Font.PLAIN, 14);
cartButton.setFont(bottomFont);
loadButton.setFont(bottomFont);
deleteButton.setFont(bottomFont);
eventButton.setFont(bottomFont); // [신규] 폰트 적용

orderMenuPanel.add(cartButton);
orderMenuPanel.add(loadButton);
orderMenuPanel.add(deleteButton);
orderMenuPanel.add(eventButton); // [신규] 패널에 추가
        
add(orderMenuPanel, BorderLayout.SOUTH);
```

#### 2단계: (V → C) Controller 호출 - 신호 보내기

  * **담당:** UI/UX 담당자
  * **파일:** `mainpage.view.MainPagePanel.java`

버튼을 추가했으니, 이 버튼이 눌렸을 때 Controller(`manager`)에게 "신호"를 보내도록 `ActionListener`를 연결합니다.

```java
// (MainPagePanel.java 생성자 하단 '이벤트 리스너' 섹션)

// [신규] '이벤트' 버튼 클릭 시
eventButton.addActionListener(e -> {
    // ⚠️ 중요: View는 로직을 처리하지 않습니다.
    // manager.navigateTo("EVENT_PAGE"); // <- 이렇게 View가 직접 화면 전환을 결정하지 않습니다!
    
    // manager에게 "이벤트 버튼이 눌렸음"을 '보고'합니다.
    manager.showEventPage(); // [신규] Controller에게 위임
});
```

#### 3단계: (C) Controller 확장 - 신호 수신 및 처리

  * **담당:** 프로젝트 리더
  * **파일:** `mainpage.controller.KioskAppManager.java`

View로부터 `showEventPage()`라는 신호를 받도록 `KioskAppManager`에 새로운 메소드를 생성합니다. 이 메소드가 '두뇌'가 되어 모든 일을 처리합니다.

```java
// (KioskAppManager.java 내부에 신규 메소드 추가)

/**
 * [신규] MainPagePanel의 '이벤트' 버튼 클릭 시 호출됩니다.
 */
public void showEventPage() {
    // 1. (Model) 만약 이벤트 데이터 로드가 필요하다면, 여기서 service를 호출합니다.
    // EventData data = eventService.loadEventData();
    
    // 2. (View) 이벤트 패널에 데이터를 전달해야 한다면 여기서 전달합니다.
    // this.eventPanel.setEventData(data);
    
    // 3. (View) 최종적으로 '이벤트' 화면으로 이동시킵니다.
    this.navigateTo("EVENT_PAGE"); // [신규] 화면 전환
}
```

#### 4단계: (C) Controller 확장 - 신규 View 등록

  * **담당:** 프로젝트 리더
  * **파일:** `mainpage.controller.KioskAppManager.java`

`KioskAppManager`가 `EVENT_PAGE`라는 이름의 패널을 알 수 있도록, `start()` 메소드에 **신규 `JPanel`을 등록**해야 합니다.

1.  (신규 View 생성) `mainpage.view.EventPanel.java` 파일을 새로 만듭니다. (`JPanel`을 상속)
2.  `KioskAppManager`에 `EventPanel` 필드를 추가합니다.
    `private EventPanel eventPanel;`
3.  `start()` 메소드 내부에서 패널을 생성하고 `mainFrame`에 `addPanel` 합니다.

<!-- end list -->

```java
// (KioskAppManager.java의 start() 메소드 내부)

this.cartPanel = new CartPanel(this); 
this.loadCartPanel = new LoadCartPanel(this); 
this.deleteCartPanel = new DeleteCartPanel(this); 
this.orderCompletePanel = new OrderCompletePanel(this); 
this.eventPanel = new EventPanel(this); // [신규] EventPanel 생성

mainFrame.addPanel(mainPagePanel, "MAIN_PAGE");
mainFrame.addPanel(storePagePanel, "STORE_PAGE"); 
mainFrame.addPanel(cartPanel, "CART");
mainFrame.addPanel(loadCartPanel, "LOAD_CART");
mainFrame.addPanel(deleteCartPanel, "DELETE_CART");
mainFrame.addPanel(orderCompletePanel, "ORDER_COMPLETE");
mainFrame.addPanel(eventPanel, "EVENT_PAGE"); // [신규] CardLayout에 등록
```

**이것으로 끝입니다\!** 이 4단계를 따르면, 팀원 누구라도 M-V-C 패턴을 깨뜨리지 않고 새로운 기능을 안전하게 추가할 수 있습니다.

-----

## 8\. (부록) Swing 초심자를 위한 핵심 개념

이 프로젝트는 Java Swing을 기반으로 합니다. 다음은 우리가 사용한 핵심 개념입니다.

### 1\. JFrame vs JPanel: "창"과 "캔버스"

  * **`JFrame` (`MainFrame.java`):**
      * '창(Window)' 그 자체입니다.
      * 닫기/최소화/최대화 버튼이 있으며, 화면에 '띄우는' 대상입니다.
      * 우리 프로젝트에서는 `MainFrame` **단 1개**만 사용합니다.
  * **`JPanel` (모든 `view` 패널):**
      * '캔버스(Canvas)' 또는 '도화지'입니다.
      * 버튼, 레이블, 텍스트 상자 등을 올려두는 판입니다.
      * `JPanel`은 그 자체로 띄울 수 없으며, 반드시 `JFrame`이나 다른 `JPanel` 안에 포함되어야 합니다.

### 2\. CardLayout: "페이지 넘기기"

`KioskAppManager`는 `CardLayout`이라는 특별한 `JPanel`을 사용합니다. `CardLayout`은 여러 `JPanel`을 겹쳐놓고, 한 번에 하나씩만 보여주는 '카드 덱'과 같습니다.

`manager.navigateTo("CART")`라는 명령은 "카드 덱(`mainPanelContainer`)에서 'CART'라는 이름표가 붙은 카드(`CartPanel`)를 맨 위로 올려 보여줘"라는 의미입니다.

### 3\. 레이아웃 매니저 (Layout Managers)

Swing에서 **가장 어렵고 가장 중요한 개념**입니다. `JPanel`에 컴포넌트를 어떻게 배치할지 결정하는 '규칙'입니다.

  * **`BorderLayout` (동서남북):**
      * `NORTH`, `SOUTH`, `WEST`, `EAST`, `CENTER` 5개의 구역으로 나눕니다.
      * `NORTH`/`SOUTH`는 가로로 길게, `WEST`/`EAST`는 세로로 길게 늘어납니다.
      * `CENTER`는 남은 모든 공간을 차지합니다.
  * **`FlowLayout` (폭포수):**
      * 컴포넌트를 왼쪽에서 오른쪽으로, 위에서 아래로 물 흐르듯이 배치합니다.
      * **중요:** 공간이 없으면 자동으로 **줄 바꿈**이 일어납니다. (v3.1에서 "ADE/..." 버튼이 줄 바꿈된 이유입니다.)
      * `new FlowLayout(FlowLayout.CENTER)`로 설정하면, 컴포넌트 그룹 전체를 중앙 정렬합니다. (v3.3에서 사용한 최종 해결책입니다.)
  * **`GridLayout` (격자):**
      * `new GridLayout(3, 3)`처럼 설정하면, 모든 공간을 3x3의 동일한 크기 격자로 나눕니다.
      * `StorePagePanel`의 상품 그리드에 사용되었습니다.
  * **`BoxLayout` (상자):**
      * `Y_AXIS` (수직) 또는 `X_AXIS` (수평)로 컴포넌트를 차곡차곡 쌓습니다.
      * `FlowLayout`과 달리 **절대 줄 바꿈을 하지 않습니다.**

### 4\. 이벤트 리스너와 EDT (Event Listeners & EDT)

  * **`addActionListener(e -> { ... });`:**
      * "이 버튼이 클릭될 때까지 기다렸다가, 클릭되면 `{...}` 안의 코드를 실행하라"는 '함정(Hook)'을 설치하는 것입니다.
      * `e -> { ... }`는 '람다식(Lambda Expression)'이라는 Java 8+ 문법입니다.
  * **EDT (Event Dispatch Thread):**
      * `MainPageManager.java`의 `SwingUtilities.invokeLater`가 이것을 보장합니다.
      * Swing은 '단일 스레드' 모델에서 작동합니다. 즉, UI를 변경하는(예: `revalidate()`, `repaint()`) 모든 작업은 **반드시 EDT라는 특별한 스레드**에서만 수행되어야 합니다.
      * `invokeLater`는 "지금 당장 실행하지 말고, EDT가 준비되면 그때 안전하게 실행해줘"라고 Swing에게 요청하는 명령입니다.
      * **결론:** 우리는 이미 `invokeLater`로 안전하게 시작했으므로, `ActionListener` 내부에서 UI를 변경하는 것은 안전합니다.
      
-----

## 9\. (중요) 추후 기능 추가 방향

### M-V-C 3+3 협업 기능 분배 (v4.1)

| 기능 유닛 | 담당 신규 기능 | 👨‍💻 백엔드 전문가 (Model/Service) | 🎨 View 전문가 (View) |
| :--- | :--- | :--- | :--- |
| **[ 유닛 A ]** | **포인트 적립 시스템** | 1. `Customer.java` (Model) 신규 생성 (이름, 전화번호, 포인트 필드)<br>2. `CustomerService.java` (Service) 신규 생성 (포인트 데이터 파일 I/O)<br>3. `KioskAppManager`에 `attemptLogin()`, `earnPoints()` 등 비즈니스 로직 구현 | 1. `LoginPanel.java` (View) 신규 생성 (전화번호 입력 폼)<br>2. `MainPagePanel` 수정 (로그인 버튼 또는 포인트 조회 버튼 추가)<br>3. `OrderCompletePanel` 수정 (적립 완료 내역 표시) |
| **[ 유닛 B ]** | **재고 관리 (매진 표시)** | 1. `Product.java`에 `int stock` (재고) 필드 추가<br>2. `DataLoader` 수정 (`menuData`에서 재고(`stock`) 값 로드)<br>3. `KioskAppManager`에 `decreaseStock(Order order)` 로직 구현 (주문 완료 시 재고 차감) | 1. (선택) `AdminLoginPanel.java` (View) 신규 생성<br>2. (선택) `StockPanel.java` (View) 신규 생성 (관리자용 재고 현황판)<br>3. **(핵심)** `StorePagePanel` 수정 (`product.getStock() == 0`일 경우, 해당 상품 버튼을 `setEnabled(false)`로 **비활성화**) |
| **[ 유닛 C ]** | **UI/UX 폴리싱**<br>(v3.0 계획 6단계) | 1. `Order.java`의 `displayOrderDetails` 등 콘솔 잔여 코드 최종 제거<br>2. `service` 패키지(예: `DataLoader`)의 `System.err` 구문을 `try-catch` 예외 처리로 변경<br>3. 유닛 A, B의 백엔드 작업 지원 | 1. `Theme.java` (Util/View) 신규 생성 (공용 폰트, 색상 상수 정의)<br>2. **모든 `view` 패널**을 열어 `Theme`의 폰트/색상 적용<br>3. 버튼 아이콘(예: `JButton("<")`)을 이미지 아이콘(`ImageIcon`)으로 교체 |

-----

## 📋 [유닛 A] 개발 계획서: 포인트 적립 시스템

**목표:** 사용자가 전화번호로 로그인하고, 주문 시 포인트를 적립받으며, 이 과정을 GUI에서 확인할 수 있게 합니다.

### 1\. (설계) Controller "Contract" 정의

`mainpage.controller.KioskAppManager` [cite: KioskAppManager.java]에 다음과 같은 '명령어'(메소드 시그니처)를 추가하기로 합의합니다.

```java
// 고객 식별 및 상태
public boolean loginCustomer(String phone);
public void logoutCustomer(); // (참고: 기존 resetLoginState()가 이 역할을 함)
public boolean isLoggedIn();
public String getCurrentCustomerName();
public int getCurrentCustomerPoints();

// 포인트 처리
// (참고: earnPoints()는 placeOrder() 내부에서만 호출되므로 public일 필요 없음)
```

### 2\. 👨‍💻 백엔드 전문가 (Model/Service) 작업 계획

**작업 패키지:** `mainpage.model`, `mainpage.service`, `mainpage.controller`

1.  **[Model] `Customer.java` 신규 생성:**

      * `mainpage.model` 패키지에 `Customer` 클래스를 생성합니다.
      * 필드: `private String phone` (ID), `private String name`, `private int points`.
      * Getter/Setter 메소드를 구현합니다.

2.  **[Service] `CustomerService.java` 신규 생성:**

      * `mainpage.service` 패키지에 `CustomerService` 클래스를 생성합니다.
      * `customers.txt` (예: `phone|name|points`) 또는 `customerData/` 폴더에 `[phone].txt` (예: `name|points`)를 저장하는 방식을 결정합니다. (후자를 추천)
      * **핵심 메소드 구현:**
          * `public Customer findCustomerByPhone(String phone)`: `customerData/`에서 `[phone].txt` 파일을 찾아 `Customer` 객체로 반환. 없으면 `null` 반환.
          * `public void saveCustomer(Customer customer)`: `Customer` 객체의 정보를 파일에 덮어씁니다. (포인트 업데이트 시 사용)
          * `public Customer createCustomer(String name, String phone)`: 새 `Customer` 객체를 생성하고 `saveCustomer`를 호출합니다.

3.  **[Controller] `KioskAppManager.java` 로직 구현:**

      * `private CustomerService customerService;` 필드를 추가하고 생성자에서 초기화합니다.
      * `private String currentCustomerName/Phone` 필드를 `private Customer currentCustomer;` (객체)로 대체합니다.
      * **`resetLoginState()` (수정):** `this.currentCustomer = null;`로 수정합니다.
      * **`loginCustomer(String phone)` (구현):**
          * `this.currentCustomer = customerService.findCustomerByPhone(phone);`
          * `return (this.currentCustomer != null);`
      * **`isLoggedIn()`, `getCurrentCustomerName()`, `getCurrentCustomerPoints()` (구현):** `this.currentCustomer`가 `null`이 아닐 경우, 객체에서 정보를 반환합니다.
      * **`placeOrder()` (수정):**
          * `Order order = new Order(...)` 이후,
          * `if (isLoggedIn()) { ... }` 블록을 추가합니다.
          * `int earnedPoints = (int) (order.getTotalPrice() * 0.01); // 1% 적립`
          * `currentCustomer.setPoints(currentCustomer.getPoints() + earnedPoints);`
          * `customerService.saveCustomer(currentCustomer);`

### 3\. 🎨 View 전문가 (View) 작업 계획

**작업 패키지:** `mainpage.view`

1.  **[View] `LoginPanel.java` 신규 생성:**

      * `LoadCartPanel.java` [cite: LoadCartPanel.java]를 복사하여 `LoginPanel.java`를 생성합니다.
      * 전화번호 `JTextField`, '로그인'(`JButton`), '신규 등록'(`JButton`), '메인으로'(`JButton`)을 배치합니다.
      * '로그인' 버튼 `ActionListener` [cite: 📋 GitHub `README.md` 작성 계획서, 7. 기여 방법: 기능 추가 가이드 (Contribution Guide) ★★★]:
          * `InputValidator.isValidPhoneNumber` [cite: InputValidator.java]로 검증합니다.
          * `boolean success = manager.loginCustomer(phoneField.getText());`
          * `if (success)`: `JOptionPane`으로 환영 메시지를 띄우고 `manager.navigateTo("MAIN_PAGE");`
          * `else`: "등록되지 않은 회원입니다." `JOptionPane` 오류 표시.

2.  **[View] `MainPagePanel.java` 수정:**

      * `orderMenuPanel` [cite: MainPagePanel.java] (하단 패널)에 '로그인/포인트' 버튼을 추가합니다.
      * `ActionListener` [cite: 📋 GitHub `README.md` 작성 계획서, 7. 기여 방법: 기능 추가 가이드 (Contribution Guide) ★★★]: `manager.navigateTo("LOGIN_PANEL");` (Controller에게 '로그인 화면'을 보여달라고 요청)

3.  **[View] `OrderCompletePanel.java` 수정:**

      * `setOrder(Order order)` [cite: OrderCompletePanel.java] 메소드를 수정합니다.
      * 영수증 하단에 `if (manager.isLoggedIn()) { ... }` 블록을 추가합니다.
      * `int points = manager.getCurrentCustomerPoints();`
      * `receiptTextArea.append(...)` [cite: OrderCompletePanel.java]를 이용해, "적립 후 총 포인트: " + `points` + "점"을 표시합니다.

-----

## 📋 [유닛 B] 개발 계획서: 재고 관리 (매진 표시)

**목표:** `menuData` [cite: image\_18cf04.png]에 재고(`stock`) 정보를 추가하고, 주문 시 재고를 차감하며, 재고가 0인 상품은 `StorePagePanel` [cite: StorePagePanel.java]에서 비활성화(매진) 처리합니다.

### 1\. (설계) Controller "Contract" 정의

  * `Product` [cite: Product.java] 모델 자체가 `stock` 정보를 갖게 되므로, View는 Controller에게 별도 요청 없이 `product.getStock()` [cite: M-V-C 3+3 협업 기능 분배 (v4.1)]을 직접 읽으면 됩니다. (M-V-C의 효율적인 구조)
  * Controller는 주문 완료 시 재고를 차감하는 내부 로직만 필요합니다.

### 2\. 👨‍💻 백엔드 전문가 (Model/Service) 작업 계획

**작업 패키지:** `mainpage.model`, `mainpage.service`, `mainpage.controller`

1.  **[Model] `Product.java` 수정:**

      * `private int stock;` 필드를 추가합니다.
      * 생성자 `Product(name, price, imagePath)`를 `Product(name, price, imagePath, stock)`로 변경하고 `this.stock = stock;`를 추가합니다.
      * `public int getStock()` Getter 메소드를 추가합니다.
      * `public void decreaseStock(int quantity)`: `this.stock -= quantity;` (혹은 `setStock(this.stock - quantity)`) 메소드를 추가합니다.

2.  **[Service] `DataLoader.java` 수정:**

      * `loadStores()` [cite: DataLoader.java] 메소드 내부 `PRODUCT` 섹션을 수정합니다.
      * 데이터 형식이 5개(`PRODUCT|Name|Price|ImagePath|Stock`)가 될 것을 예상합니다.
      * `int stock = Integer.parseInt(parts[4]);` (파싱, `try-catch` 추가 권장)
      * `currentMenu.addProduct(new Product(name, price, imagePath, stock));` (새 생성자 호출)

3.  **[Controller] `KioskAppManager.java` 로직 구현:**

      * **`placeOrder()` (수정):**
          * `Order order = new Order(...)`가 성공한 직후,
          * `this.decreaseStockFromCart(this.currentCart);` [cite: 📋 GitHub `README.md` 작성 계획서, 추천 기능 및 역할 분배 (예시)] (신규 헬퍼 메소드 호출)
      * **`private void decreaseStockFromCart(Cart cart)` (신규 헬퍼):**
          * `for (CartItem item : cart.getItems()) { ... }`
          * `Product cartProduct = item.getProduct();`
          * `int quantity = item.getQuantity();`
          * // 중요: `cartProduct`는 `Cart` [cite: Cart.java]의 *복사본*이므로, *원본*을 찾아야 합니다.
          * `Product originalProduct = findOriginalProduct(cartProduct.getName());`
          * `if (originalProduct != null) { originalProduct.decreaseStock(quantity); }`
      * **`private Product findOriginalProduct(String name)` (신규 헬퍼):**
          * `this.stores` [cite: KioskAppManager.java] (모든 가게)를 이중 `for`문으로 순회하며 `product.getName().equals(name)`인 `Product` [cite: Product.java] 객체를 찾아 반환합니다.

### 3\. 🎨 View 전문가 (View) 작업 계획

**작업 패키지:** `mainpage.view`

1.  **[View] `StorePagePanel.java` 수정 (핵심 임무):**
      * `updateProductGrid()` [cite: StorePagePanel.java] 메소드를 수정합니다.
      * `for` 루프 내부에서 `productButton` [cite: StorePagePanel.java]을 생성한 직후,
      * **'매진 처리' 로직을 추가합니다.**
        ```java
        // [유닛 B 작업] 재고 확인
        if (product.getStock() <= 0) {
            productButton.setEnabled(false); // 버튼 비활성화
            productButton.setIcon(null); // (선택) 이미지를 제거
            productButton.setText("<html><center>" + product.getName() + "<br/>(SOLD OUT)</center></html>");
        } else {
            // (기존 이미지 로드 로직은 else 블록 안으로 이동)
            if (product.hasImage()) {
                // ... (ImageIcon 생성) ...
            }
        }

        // [유닛 B 작업] ActionListener 수정
        productButton.addActionListener(e -> {
            // (기존 3단계 코드...)
            // [유닛 B 수정] 재고가 0이면 handleProductClick 자체를 막음 (방어 코드)
            if (product.getStock() > 0) {
                 handleProductClick(product);
            }
        });
        ```

-----

## 📋 [유닛 C] 개발 계획서: UI/UX 폴리싱

**목표:** `Theme.java` [cite: 6단계: (선택) 스타일링 (Polishing)]를 정의하여 앱 전반의 스타일(폰트, 색상)을 통일하고, 아이콘을 적용하며, 콘솔 잔여 코드를 제거합니다.[cite: 6단계: (선택) 스타일링 (Polishing)]

### 1\. (설계) Controller "Contract" 정의

  * 이 유닛은 Controller를 수정할 필요가 없습니다.
  * 대신, 다른 View 전문가들이 사용할 \*\*`Theme.java`\*\*라는 '디자인 가이드라인'을 제공합니다.

### 2\. 👨‍💻 백엔드 전문가 (Model/Service) 작업 계획

**작업 패키지:** `mainpage.model`, `mainpage.service`

1.  **[Model] `Order.java` 수정:**
      * `displayOrderDetails()` [cite: Order.java] 메소드를 찾아 **삭제합니다.** (GUI `OrderCompletePanel` [cite: OrderCompletePanel.java]이 역할을 완전히 대체함) [cite: 6단계: (선택) 스타일링 (Polishing)]
2.  **[Service] `DataLoader.java` / `CartFileManager.java` 수정:**
      * 프로젝트 전체에서 `System.err.println(...)` 또는 `System.out.println(...)`을 검색합니다.
      * `DataLoader` [cite: DataLoader.java]의 `catch` 블록: `e.printStackTrace();` [cite: DataLoader.java] 대신 `throw new RuntimeException("메뉴 데이터 로드 실패: " + file.getName(), e);`로 변경합니다. (앱 실행을 막는 것이 올바른 처리임)
      * `CartFileManager` [cite: CartFileManager.java]의 `catch` 블록: 모든 `System.err` [cite: CartFileManager.java]을 삭제합니다. (`boolean` 반환으로 이미 처리가 되고 있음) [cite: 6단계: (선택) 스타일링 (Polishing)]

### 3\. 🎨 View 전문가 (View) 작업 계획

**작업 패키지:** `mainpage.util`, `mainpage.view`

1.  **[Util] `Theme.java` 신규 생성:**

      * `mainpage.util` 패키지에 `Theme.java` 클래스를 생성합니다.[cite: 6단계: (선택) 스타일링 (Polishing)]
      * 공용 폰트와 색상 상수를 정의합니다.
        ```java
        package mainpage.util;
        import java.awt.Font;
        import java.awt.Color;

        public class Theme {
            // 폰트 정의
            public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 24);
            public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 18);
            public static final Font FONT_BUTTON_LARGE = new Font("SansSerif", Font.BOLD, 16);
            public static final Font FONT_BUTTON_SMALL = new Font("SansSerif", Font.PLAIN, 14);
            public static final Font FONT_RECEIPT = new Font("Monospaced", Font.PLAIN, 14);
            
            // 색상 정의 (예시)
            public static final Color COLOR_BACKGROUND = Color.WHITE;
            public static final Color COLOR_PRIMARY = new Color(0, 102, 204); // (파란색 계열)
            public static final Color COLOR_TEXT_BRIGHT = Color.WHITE;
            public static final Color COLOR_TEXT_DARK = new Color(51, 51, 51);
        }
        ```

2.  **[View] "The Big Job" - 테마 적용:**

      * `mainpage.view` 패키지의 **모든 7개 `.java` 파일**을 엽니다.[cite: 6단계: (선택) 스타일링 (Polishing)]
      * `import mainpage.util.Theme;`를 추가합니다.
      * 모든 `new Font(...)` [cite: StorePagePanel.java, MainPagePanel.java, CartPanel.java, LoadCartPanel.java, DeleteCartPanel.java, OrderCompletePanel.java] 구문을 `Theme.FONT_TITLE` [cite: 6단계: (선택) 스타일링 (Polishing)] 등으로 교체합니다.
      * 모든 `JPanel` [cite: StorePagePanel.java, MainPagePanel.java, CartPanel.java, LoadCartPanel.java, DeleteCartPanel.java, OrderCompletePanel.java]의 생성자에 `setBackground(Theme.COLOR_BACKGROUND);` [cite: 6단계: (선택) 스타일링 (Polishing)]를 추가합니다.
      * (선택) `JButton`에 `setBackground(Theme.COLOR_PRIMARY); setForeground(Theme.COLOR_TEXT_BRIGHT);` 등을 적용합니다.

3.  **[View] 아이콘 적용:**

      * `images/` [cite: image\_18cf04.png] 폴더에 `arrow_left.png`, `arrow_right.png` (작은 아이콘)을 준비합니다.
      * `StorePagePanel` [cite: StorePagePanel.java]의 `prevCategoryButton = new JButton("<")` [cite: 💡 v3.3 최종 수정 계획서 (제안)]를 `new JButton(new ImageIcon("images/arrow_left.png"))` [cite: 6단계: (선택) 스타일링 (Polishing)]로 교체합니다.
      * 버튼 배경과 테두리를 투명하게 만듭니다: `prevCategoryButton.setBorderPainted(false); prevCategoryButton.setContentAreaFilled(false);`