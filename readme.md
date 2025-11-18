# KioskProject: 객체지향 키오스크 시스템

## 프로젝트 개요

이 프로젝트는 자바 Swing을 기반으로 개발된 객체지향 키오스크 시스템입니다. 사용자가 카페를 선택하고, 메뉴를 주문하며, 픽업 시간을 지정하고, 장바구니를 관리할 수 있는 기능을 제공합니다. 특히, 가게의 혼잡도를 실시간으로 파악하여 사용자에게 효율적인 픽업 시간을 안내하는 기능이 강화되었습니다.

## 개발 환경 설정 (Development Setup)

이 프로젝트는 Windows와 macOS에서 일관된 UI를 제공하기 위해 **FlatLaf** 라이브러리를 사용합니다. 프로젝트를 Git에서 클론한 후, 각 IDE에 맞게 아래 설정을 진행해야 합니다.

1.  **라이브러리 폴더 생성**
    *   프로젝트 루트 디렉토리(`KioskProject/`)에 `libs` 라는 이름의 폴더를 생성합니다.
    *   `flatlaf-3.6.2.jar` 파일을 이 `libs` 폴더 안에 위치시킵니다. (팀원 중 한 명이 파일을 커밋하면 다른 팀원들은 `git pull`만으로 파일을 받을 수 있습니다.)

2.  **IDE별 라이브러리 추가**
    아래 가이드에 따라 `libs` 폴더의 `.jar` 파일을 프로젝트의 빌드 경로(Build Path)에 추가합니다.

### Eclipse 설정

1.  Package Explorer에서 `KioskProject`를 마우스 오른쪽 버튼으로 클릭합니다.
2.  `Build Path` > `Configure Build Path...` 메뉴로 들어갑니다.
3.  `Libraries` 탭을 선택합니다.
4.  `Classpath`를 선택한 후, 우측의 `Add JARs...` 버튼을 클릭합니다.
5.  프로젝트 내부의 `libs` 폴더로 이동하여 `flatlaf-3.6.2.jar` 파일을 선택하고 `OK`를 누릅니다.
6.  `Apply and Close`를 눌러 설정을 완료합니다.


### IntelliJ IDEA 설정

1.  `File` > `Project Structure...` 메뉴로 들어갑니다. (단축키: `⌘;` on Mac, `Ctrl+Alt+Shift+S` on Win/Linux)
2.  왼쪽 메뉴에서 `Modules`를 선택합니다.
3.  중앙 패널에서 `KioskProject` 모듈을 선택한 후, 오른쪽의 `Dependencies` 탭으로 이동합니다.
4.  `+` 아이콘을 클릭하고 `JARs or directories...`를 선택합니다.
5.  프로젝트 내부의 `libs` 폴더로 이동하여 `flatlaf-3.6.2.jar` 파일을 선택하고 `OK`를 누릅니다.
6.  `Scope`가 `Compile`로 설정되었는지 확인하고 `OK`를 눌러 설정을 완료합니다.
   
## UI 구조
## 1\. 카페 선택 화면 (CafeSelectionScreen)

`MainApplication`의 `SCREEN_SELECT`("SELECT\_CAFE")에 해당하는 화면입니다. `BorderLayout`을 기반으로 하며, 중앙에 스크롤 가능한 2열 그리드(Grid)로 가게 목록을 표시합니다.

  * **핵심 파일:** `CafeSelectionScreen.java`
  * **루트 레이아웃:** `BorderLayout`


```
[ (MainApplication JFrame) - CafeSelectionScreen ]
+=========================================================+
| [NORTH] Header Panel (BorderLayout)                     |
|    "KIOSK | 원하시는 카페를 선택해 주세요" (JLabel)        |
+=========================================================+
| [CENTER] JScrollPane                                    |
| +-----------------------------------------------------+ |
| | [Grid Panel (GridLayout 0, 2)]                      | |
| | (2열, 가변 행)                                        | |
| |                                                     | |
| | +-----------------------+ +-----------------------+ | |
| | | [Store Card 1]        | | [Store Card 2]        | | |
| | | (BorderLayout)        | | (BorderLayout)        | | |
| | | +-------------------+ | | +-------------------+ | | |
| | | | [CENTER]          | | | | [CENTER]          | | | |
| | | | (이미지 없음)        | | | | (이미지 없음)        | | | |
| | | +-------------------+ | | +-------------------+ | | |
| | | | [SOUTH]         | | | |    [SOUTH]         | | | |
| | | | 가게이름 1 (JLabel) | | |   가게이름 2 (JLabel) | | | |
| | | | 설명+혼잡도 (JPanel)| | | 설명+혼잡도 (JPanel)| | | |
| | | +-------------------+ | | +-------------------+ | | |
| | +-----------------------+ +-----------------------+ | |
| |                                                     | |
| | +-----------------------+ +-----------------------+ | |
| | | [Store Card 3]        | |   [Store Card 4]      | | |
| | | ( ... 동일 구조 ... ). | |    ( ... 동일 구조 ... ) | | |
| | +---------------------+ +---------------------+ | |
| |                                                     | |
| |            ... (스크롤 가능) ...                  | |
| +-------------------------------------------------[v]+|
+=========================================================+
```

-----

## 2\. 메뉴 선택 및 주문 화면 (MenuScreen)

`MainApplication`의 `SCREEN_MENU`("MENU")에 해당하는 화면입니다. `BorderLayout`을 기반으로 하며, 상단(가게 선택), 중앙(메뉴), 하단(주문)의 3단 구조로 나뉩니다.

  * **핵심 파일:** `MenuScreen.java`, `CafeMenuPanel.java`, `OrderPanel.java`
  * **루트 레이아웃:** `BorderLayout`

```
[ (MainApplication JFrame) - MenuScreen ]
+==================================================================+
| [NORTH] topPanel (가게 선택 스크롤) (BorderLayout)               |
| +---+----------------------------------------------------+---+ |
| | ◀ | [JScrollPane (cafeScroll)]                         | ▶ | |
| |   | +------------------------------------------------+ |   | |
| |   | | [FlowLayout] [가게1] [가게2] [가게3] ...         | |   | |
| |   | +------------------------------------------------+ |   | |
| +---+----------------------------------------------------+---+ |
+==================================================================+
| [CENTER] cafeMenuPanel (BorderLayout)                        |
|                                                                |
|  (자세한 구조는 아래 [2-1. CafeMenuPanel 상세] 참고)           |
|                                                                |
|                                                                |
|                                                                |
+==================================================================+
| [SOUTH] bottomPanel (GridLayout 1, 2)                          |
| +--------------------------------+---------------------------+ |
| | [OrderPanel] (BorderLayout)    | [rightButtons]            | |
| |                                | (GridLayout 2, 2)         | |
| | (아래 [2-2. OrderPanel 상세])    | +-----------+-----------+ | |
| |                                | | 저장      | | 전체삭제  | | |
| |                                | +-----------+-----------+ | |
| |                                | | 불러오기  | | 주문하기  | | |
| |                                | +-----------+-----------+ | |
| +--------------------------------+---------------------------+ |
+==================================================================+
```

### 2-1. CafeMenuPanel 상세 구조 (New UI)

`MenuScreen`의 `[CENTER]` 영역입니다. 새로운 UI는 `MenuCard` 컴포넌트를 사용하여 이미지가 포함된 메뉴를 표시합니다.

1.  **카테고리 전환:** 상단 스크롤 버튼으로 메뉴 카테고리(커피, 디저트 등)를 전환합니다.
2.  **페이지 전환:** 각 카테고리 내에서 메뉴가 많을 경우, 좌우 화살표와 하단의 점(indicator)으로 페이지를 넘길 수 있습니다.

  * **핵심 파일:** `CafeMenuPanel.java`, `MenuCard.java`
  * **루트 레이아웃:** `BorderLayout`

```
[ [2-1. CafeMenuPanel 상세 구조] (MenuScreen의 [CENTER] 영역) ]
+------------------------------------------------------------------+
| [NORTH] categoryTopPanel (카테고리 선택) (BorderLayout)            |
| +---+----------------------------------------------------+---+ |
| | ◀ | [JScrollPane] [카테고리1] [카테고리2] [카테고리3]  | ▶ | |
| +---+----------------------------------------------------+---+ |
+==================================================================+
| [CENTER] categoryContainer (CardLayout - 카테고리별 메뉴판 전환)   |
| +--------------------------------------------------------------+ |
| | [pagedMenuPanel] (BorderLayout - 페이지 전환 UI)             | |
| | +---+--------------------------------------------------+---+ |
| | |   | [CENTER] pageContainer (CardLayout - 페이지 1/N)   |   | |
| | |   | +----------------------------------------------+ |   | |
| | | ‹ | | [menuGrid (GridLayout 2, 3)]                 | | › | |
| | |(이전)| | +-----------+ +-----------+ +-----------+  | |(다음)|
| | |   | | | | +-------+ | | +-------+ | | +-------+ |  | |   | |
| | |   | | | | | Image | | | | Image | | | | Image | |  | |   | |
| | |   | | | | +-------+ | | +-------+ | | +-------+ |  | |   | |
| | |   | | | | Name/Price| | | Name/Price| | | Name/Price|  | |   | |
| | |   | | | +-----------+ | +-----------+ | +-----------+  | |   | |
| | |   | | | (MenuCard)  | | (MenuCard)  | | (MenuCard)   | |   | |
| | |   | | +----------------------------------------------+ |   | |
| | |   | | [SOUTH] indicatorPanel (FlowLayout)              |   | |
| | |   | |                ( ● ○ ○ ... )                     |   | |
| | +---+--------------------------------------------------+---+ |
| +--------------------------------------------------------------+ |
+------------------------------------------------------------------+
```

### 2-2. OrderPanel 상세 구조

`MenuScreen`의 `[SOUTH]` 영역 좌측입니다. `BoxLayout(Y_AXIS)`을 사용하여 주문 항목(`row`)을 세로로 쌓고, 각 `row`는 `GridBagLayout`을 사용해 "상품명"은 늘어나고 "수량 버튼"은 고정 폭을 갖도록 구현했습니다.

  * **핵심 파일:** `OrderPanel.java`
  * **루트 레이아웃:** `BorderLayout` (내부에 `BoxLayout` 사용)


```
[ [2-2. OrderPanel 상세 구조] (MenuScreen의 [SOUTH] 좌측 영역) ]
+------------------------------------------------------------------+
| 🧾 주문 내역 (TitleBorder)                                       |
+------------------------------------------------------------------+
| [CENTER] JScrollPane                                             |
| +--------------------------------------------------------------+ |
| | [orderListPanel (BoxLayout Y_AXIS)]                          | |
| |                                                              | |
| | +----------------------------------------------------------+ | |
| | | [row 1] (GridBagLayout)                                  | | |
| | | | [상품 이름 1 (weightx=1.0)]      [ ▲ 1 ▼ ❌ (fixed) ] | | |
| | +----------------------------------------------------------+ | |
| | +----------------------------------------------------------+ | |
| | | [row 2] (GridBagLayout)                                  | | |
| | | | [매우 긴 상품 이름 2 (weightx=1.0)] [ ▲ 3 ▼ ❌ (fixed) ] | | |
| | +----------------------------------------------------------+ | |
| | | [row 3] (GridBagLayout)                                  | | |
| | | | [상품 이름 3 (weightx=1.0)]      [ ▲ 1 ▼ ❌ (fixed) ] | | |
| | +----------------------------------------------------------+ | |
| | | ... (주문 항목들) ...                                    | | |
| | +----------------------------------------------------------+ | |
| |                                                              | |
| +------------------------------------------------------------[v]+ |
+------------------------------------------------------------------+
```

## 시작하기

### 1. 프로젝트 불러오기
*   **Eclipse**: `File` > `Open Projects from File System...`을 통해 프로젝트 폴더를 불러옵니다.
*   **IntelliJ**: `File` > `Open...`을 통해 프로젝트 폴더를 엽니다.

### 2. 프로젝트 구조 확인
*   `src/mainpage`: 비즈니스 로직 및 데이터 모델 관련 클래스들이 위치합니다.
*   `src/ui`: 사용자 인터페이스(UI) 관련 클래스들이 위치합니다.
*   `libs`: `flatlaf-3.6.2.jar`와 같은 외부 라이브러리 파일이 위치합니다.
*   `menuData`: 가게 및 메뉴 정보를 담고 있는 텍스트 파일들이 위치합니다.
*   `saved_carts`: 사용자가 저장한 장바구니 파일들이 저장됩니다.
*   `saved_orders`: 완료된 주문 내역 파일들이 날짜별로 저장됩니다.
*   `order_sequence.txt`: 주문 번호가 영속적으로 저장되는 파일입니다.

### 3. 실행
1.  `src/ui/MainApplication.java` 파일을 엽니다.
2.  파일을 마우스 오른쪽 버튼으로 클릭한 후 `Run`을 선택하여 애플리케이션을 실행합니다.

## 핵심 기능

1.  **가게 선택**: 여러 카페 중 원하는 카페를 선택할 수 있습니다.
2.  **메뉴 주문**: 선택한 카페의 메뉴를 확인하고 장바구니에 담을 수 있습니다.
3.  **장바구니 관리**: 장바구니에 담긴 상품의 수량을 조절하거나 삭제할 수 있습니다.
4.  **장바구니 저장/불러오기**: 전화번호를 이용해 장바구니 내역을 저장하고 불러올 수 있습니다.
5.  **주문 및 픽업 시간 선택**: 주문을 확정하고 픽업 희망 시간을 선택합니다.
6.  **혼잡도 표시**:
    *   가게 선택 화면에서 각 가게의 **15분 내 실시간 혼잡도**를 표시합니다.
    *   가게 클릭 시 **15분 단위의 시간대별 예상 혼잡도**를 팝업으로 보여줍니다.
    *   픽업 시간 선택 시, 선택한 **5분 구간의 상세 혼잡도**를 실시간으로 안내합니다.
7.  **주문번호 영속성**: 프로그램 재시작 시에도 주문번호가 유지되며, 매일 자정에 초기화됩니다.
8.  **주문 내역 저장**: 완료된 주문 내역이 날짜별 파일로 자동 저장됩니다.

## 파일별 상세 설명

### `src/mainpage` (비즈니스 로직 및 데이터 모델)

*   **`Cart.java`**
    *   **목적**: 사용자의 장바구니를 나타내는 클래스입니다. 상품(`Product`)들을 `CartItem` 형태로 담고, 추가/삭제/수량 조절 및 총 가격 계산 기능을 제공합니다.
    *   **주요 메소드**:
        *   `addProduct(Product product)`: 상품을 장바구니에 추가하거나 수량을 늘립니다.
        *   `decrementProduct(Product product)`: 상품 수량을 1 감소시킵니다. 수량이 0이 되면 장바구니에서 제거합니다.
        *   `removeProduct(Product product)`: 장바구니에서 상품을 완전히 제거합니다.
        *   `getTotalPrice()`: 장바구니의 총 가격을 반환합니다.
        *   `getItems()`: 장바구니에 담긴 `CartItem` 리스트를 반환합니다.
    *   **다른 파일과의 관계**: `CartItem`, `Product` 객체를 사용하며, `OrderPanel` (UI)에서 장바구니 조작을 위해 호출됩니다. `CartFileManager`가 이 객체를 저장/로드합니다.
    *   **수정 팁**: 장바구니에 할인 기능 등을 추가하려면 `addProduct`, `getTotalPrice` 등의 로직을 수정할 수 있습니다.

*   **`CartFileManager.java`**
    *   **목적**: 사용자의 장바구니 정보를 파일로 저장하고 불러오는 역할을 담당합니다. 전화번호를 기반으로 파일을 관리합니다.
    *   **주요 메소드**:
        *   `saveCart(Cart cart, String phoneNumber)`: `Cart` 객체를 `saved_carts` 폴더에 `cart_전화번호.txt` 형태로 저장합니다.
        *   `loadCart(String phoneNumber)`: `cart_전화번호.txt` 파일에서 장바구니 정보를 읽어 `Cart` 객체로 반환합니다.
        *   `deleteCart(String phoneNumber)`: 저장된 장바구니 파일을 삭제합니다.
    *   **다른 파일과의 관계**: `Cart` 객체를 사용하며, `MenuScreen` (UI)에서 장바구니 저장/불러오기 버튼 클릭 시 호출됩니다.
    *   **수정 팁**: 장바구니 파일 저장 형식을 변경하거나, 저장 위치를 바꾸려면 이 파일의 로직을 수정해야 합니다.

*   **`CartItem.java`**
    *   **목적**: 장바구니에 담긴 개별 상품(`Product`)과 그 수량을 묶어서 관리하는 클래스입니다.
    *   **주요 메소드**:
        *   `getProduct()`: 해당 `CartItem`이 담고 있는 `Product` 객체를 반환합니다.
        *   `getQuantity()`: 상품의 현재 수량을 반환합니다.
        *   `increaseQuantity()`, `decreaseQuantity()`: 수량을 조절합니다.
        *   `getTotalPrice()`: 해당 `CartItem`의 총 가격(상품 가격 * 수량)을 반환합니다.
    *   **다른 파일과의 관계**: `Product` 객체를 사용하며, `Cart` 클래스에서 관리됩니다.
    *   **수정 팁**: 상품별 추가 옵션(예: 샷 추가, 휘핑 추가)을 `CartItem`에 포함시키려면 이 클래스를 확장할 수 있습니다.

*   **`CongestionManager.java`**
    *   **목적**: 가게의 혼잡도 정보를 관리하고 계산하는 핵심 클래스입니다. `OrderFileManager`를 통해 주문 데이터를 읽어와 메모리에 캐싱하고, 다양한 기준(15분 내, 15분 단위 시간대별, 5분 구간)으로 혼잡도를 제공합니다.
    *   **주요 메소드**:
        *   `refreshCache()`: `OrderFileManager`로부터 최신 주문 정보를 읽어와 내부 캐시를 갱신합니다.
        *   `getCongestionForNext15Min(String storeName)`: 특정 가게의 **현재 시각 기준 15분 내** 픽업 예정 주문 건수를 반환합니다. (가게 선택 화면의 실시간 혼잡도)
        *   `getCongestionByTimeSlots(String storeName)`: 특정 가게의 **현재 시각이 포함된 15분 단위로 4개(총 1시간)의 시간대별** 예상 주문 건수를 반환합니다. (가게 선택 화면의 팝업 혼잡도)
        *   `getCongestionFor5MinSlot(String storeName, LocalTime startTime)`: 특정 가게의 특정 `startTime`부터 시작하는 **5분 구간**의 주문 건수를 반환합니다. (픽업 시간 선택 팝업의 상세 혼잡도)
    *   **다른 파일과의 관계**: `OrderFileManager`를 사용하여 데이터를 가져오고, `CafeSelectionScreen` 및 `MenuScreen` (UI)에서 혼잡도 정보를 요청합니다.
    *   **수정 팁**: 혼잡도 계산 기준(예: 15분 내, 5분 구간)을 변경하거나, 캐시 갱신 주기를 조절하려면 이 클래스를 수정해야 합니다.

*   **`DataLoader.java`**
    *   **목적**: 프로그램 시작 시 `menuData` 폴더에 있는 텍스트 파일들로부터 가게, 메뉴, 상품 정보를 읽어와 자바 객체(`Store`, `Menu`, `Product`)로 변환하는 역할을 합니다.
    *   **주요 메소드**:
        *   `loadStores()`: `menuData` 폴더의 모든 `.txt` 파일을 파싱하여 `Store` 객체 리스트를 반환합니다.
    *   **다른 파일과의 관계**: `Store`, `Menu`, `Product` 객체를 생성하며, `MainApplication`에서 프로그램 초기화 시 호출됩니다.
    *   **수정 팁**: 새로운 가게나 메뉴를 추가하려면 `menuData` 폴더의 `.txt` 파일을 수정해야 합니다. 파일 파싱 로직을 변경하려면 이 클래스를 수정합니다.

*   **`Menu.java`**
    *   **목적**: 가게 내의 특정 메뉴 카테고리(예: "커피", "디저트")를 나타내는 클래스입니다. 해당 카테고리에 속한 `Product` 리스트를 관리합니다.
    *   **주요 메소드**:
        *   `getCategoryName()`: 메뉴 카테고리 이름을 반환합니다.
        *   `getProducts()`: 해당 카테고리의 `Product` 리스트를 반환합니다.
        *   `addProduct(Product product)`: 카테고리에 상품을 추가합니다.
    *   **다른 파일과의 관계**: `Product` 객체를 담으며, `Store` 객체 내부에 포함됩니다. `DataLoader`가 이 객체를 생성합니다.
    *   **수정 팁**: 메뉴 카테고리별로 특별한 속성을 추가하려면 이 클래스를 수정할 수 있습니다.

*   **`Order.java`**
    *   **목적**: 완료된 주문의 상세 내역(주문번호, 가게 이름, 상품 목록, 총액, 픽업 시간 등)을 기록하는 불변(Immutable) 데이터 객체입니다. 주문번호 영속성 로직을 포함합니다.
    *   **주요 메소드**:
        *   `Order(Cart cart, LocalTime pickupTime, Store store)`: `Cart`와 픽업 시간, `Store` 정보를 받아 `Order` 객체를 생성합니다.
        *   `getOrderNumber()`: 고유한 주문 번호를 반환합니다. (매일 자정에 초기화)
        *   `getStoreName()`: 주문이 발생한 가게 이름을 반환합니다.
        *   `displayOrderDetails()`: 주문 내역을 콘솔에 출력합니다.
    *   **주문번호 영속성**: `order_sequence.txt` 파일을 사용하여 마지막 주문 번호와 날짜를 저장하고, 프로그램 시작 시 이를 불러와 매일 자정에 주문 번호를 1부터 다시 시작하도록 합니다.
    *   **다른 파일과의 관계**: `Cart`, `CartItem`, `Product`, `Store` 객체를 사용하며, `MenuScreen`에서 주문 완료 시 생성됩니다. `OrderFileManager`가 이 객체를 파일로 저장합니다.
    *   **수정 팁**: 주문에 고객 정보(전화번호 등)를 추가하거나, 주문번호 초기화 로직을 변경하려면 이 클래스를 수정해야 합니다.

*   **`OrderFileManager.java`**
    *   **목적**: 완료된 주문(`Order`) 내역을 날짜별 파일로 저장하고, 혼잡도 계산을 위해 주문 정보를 읽어오는 역할을 담당합니다.
    *   **주요 메소드**:
        *   `saveOrder(Order order)`: `Order` 객체를 `saved_orders` 폴더에 `YYYY-MM-DD.txt` 형태로 저장합니다.
        *   `getTodaysOrderInfo()`: 오늘 날짜의 `YYYY-MM-DD.txt` 파일에서 주문 정보를 읽어와 `OrderInfo` (가게 이름, 픽업 시간) 리스트로 반환합니다.
    *   **`OrderInfo` Record**: `record OrderInfo(String storeName, LocalTime pickupTime)`는 혼잡도 계산에 필요한 최소한의 주문 정보를 담는 내부 클래스입니다.
    *   **다른 파일과의 관계**: `Order` 객체를 저장하고, `CongestionManager`가 혼잡도 계산을 위해 `getTodaysOrderInfo()`를 호출합니다.
    *   **수정 팁**: 주문 내역 파일의 저장 형식이나 읽기 로직을 변경하려면 이 클래스를 수정해야 합니다.

### `src/ui` (사용자 인터페이스)

*   **`CafeMenuPanel.java`**
    *   **목적**: 선택된 카페의 메뉴 카테고리 버튼과 해당 카테고리의 상품 목록을 표시하는 패널입니다.
    *   **주요 기능**:
        *   카테고리별로 메뉴를 분류하여 보여줍니다.
        *   상품 버튼 클릭 시 장바구니에 상품을 추가하는 콜백(`onAddOrder`)을 실행합니다.
        *   메뉴 페이지네이션(페이지 넘기기) 기능을 제공합니다.
    *   **다른 파일과의 관계**: `Store`, `Menu`, `Product` 객체를 사용하여 메뉴를 구성하며, `OrderPanel`에 상품 추가를 요청합니다. `MenuScreen`에 포함됩니다.
    *   **수정 팁**: 메뉴 UI 레이아웃을 변경하거나, 상품 표시 방식을 수정하려면 이 클래스를 수정해야 합니다.

*   **`CafeSelectionScreen.java`**
    *   **목적**: 사용자가 여러 카페 중 하나를 선택할 수 있도록 카페 목록을 표시하는 패널입니다. 각 카페의 실시간 혼잡도 정보를 함께 보여줍니다.
    *   **주요 기능**:
        *   `DataLoader`에서 로드한 `Store` 목록을 카드 형태로 표시합니다.
        *   각 카드에 **15분 내 실시간 혼잡도**(`(15분 내: N건)`)를 표시합니다. (색상 코드 적용)
        *   카페 카드 클릭 시, **15분 단위의 시간대별 예상 혼잡도** 팝업을 보여줍니다.
        *   팝업에서 '확인' 시 `MenuScreen`으로 전환합니다.
    *   **다른 파일과의 관계**: `MainApplication`으로부터 `Store` 목록과 `CongestionManager`를 주입받아 사용합니다. `MainApplication`에 화면 전환을 요청합니다.
    *   **수정 팁**: 카페 카드 디자인을 변경하거나, 실시간 혼잡도 표시 기준(색상, 건수)을 변경하려면 `createStoreCard` 메소드를 수정해야 합니다. 시간대별 혼잡도 팝업의 표시 로직은 `showCongestionDetails` 메소드를 수정합니다.

*   **`MainApplication.java`**
    *   **목적**: 애플리케이션의 메인 진입점이며, 전체 UI를 관리하는 최상위 `JFrame`입니다. `CardLayout`을 사용하여 `CafeSelectionScreen`과 `MenuScreen` 간의 화면 전환을 담당합니다.
    *   **주요 기능**:
        *   `DataLoader`를 통해 초기 데이터를 로드합니다.
        *   `CongestionManager`를 생성하여 `CafeSelectionScreen`과 `MenuScreen`에 공유합니다.
        *   `showMenuScreen()`, `showSelectScreen()` 메소드를 통해 화면 전환을 제어합니다.
    *   **다른 파일과의 관계**: `DataLoader`, `CafeSelectionScreen`, `MenuScreen`, `CongestionManager` 객체를 관리하고 연결합니다.
    *   **수정 팁**: 새로운 화면을 추가하거나, 화면 전환 로직을 변경하려면 이 클래스를 수정해야 합니다.

*   **`MenuScreen.java`**
    *   **목적**: 선택된 카페의 메뉴를 보여주고, 장바구니 관리, 주문, 픽업 시간 선택 등 핵심적인 사용자 상호작용을 처리하는 패널입니다.
    *   **주요 기능**:
        *   `CafeMenuPanel`을 통해 메뉴를 표시하고, `OrderPanel`을 통해 장바구니 내역을 보여줍니다.
        *   장바구니 저장/불러오기 기능을 제공합니다.
        *   **픽업 시간 선택**: 팝업을 통해 5분 단위로 픽업 시간을 선택할 수 있으며, 선택된 5분 구간의 상세 혼잡도를 실시간으로 보여줍니다. (색상 코드 적용)
        *   주문 완료 시 `Order` 객체를 생성하고 `OrderFileManager`를 통해 저장합니다.
    *   **다른 파일과의 관계**: `CafeMenuPanel`, `OrderPanel`, `CartFileManager`, `OrderFileManager`, `CongestionManager` 객체를 사용합니다. `MainApplication`에 화면 전환을 요청합니다.
    *   **수정 팁**: 픽업 시간 선택 UI의 로직은 `getPickupTime()` 메소드에 구현되어 있습니다. 혼잡도 표시 기준(색상, 건수)을 변경하려면 `updateCongestionLabel` 메소드를 수정합니다.

*   **`OrderPanel.java`**
    *   **목적**: 사용자의 현재 장바구니 내역을 UI로 표시하고, 장바구니 상품의 수량을 조절하거나 제거하는 기능을 제공합니다.
    *   **주요 기능**:
        *   `Cart` 객체의 내용을 기반으로 주문 내역을 리스트 형태로 보여줍니다.
        *   상품별로 수량 증가/감소, 완전 삭제 버튼을 제공합니다.
    *   **다른 파일과의 관계**: `Cart` 객체를 사용하여 장바구니 상태를 반영합니다. `MenuScreen`에 포함됩니다.
    *   **수정 팁**: 장바구니 내역의 UI 디자인을 변경하거나, 상품별 추가 정보(예: 옵션)를 표시하려면 이 클래스를 수정해야 합니다.

## UI 구조 및 디자인 상세 설명

이 키오스크 시스템의 UI는 자바 Swing 컴포넌트와 다양한 레이아웃 매니저를 활용하여 구현되었습니다. 전체적인 구조는 `MainApplication`이라는 하나의 메인 창(`JFrame`)을 중심으로, 여러 개의 `JPanel` 화면들을 `CardLayout`으로 전환하는 방식으로 디자인되었습니다.

### 1. 전체 UI 흐름 (`MainApplication` 중심)

*   **`MainApplication.java`**:
    *   애플리케이션의 최상위 창(`JFrame`) 역할을 합니다.
    *   내부에 `mainPanel`이라는 `JPanel`을 가지고 있으며, 이 `mainPanel`은 `CardLayout`을 사용합니다.
    *   `CardLayout`은 여러 개의 `JPanel`을 카드처럼 겹쳐놓고, 필요에 따라 한 번에 하나의 `JPanel`만 보이도록 전환하는 역할을 합니다.
    *   `CafeSelectionScreen` (카페 선택 화면)과 `MenuScreen` (메뉴 및 주문 화면)이라는 두 개의 주요 `JPanel`을 관리합니다.
    *   `showSelectScreen()` 또는 `showMenuScreen(Store selectedStore)` 메소드를 호출하여 화면을 전환합니다.

### 2. 주요 화면 (`JPanel` 구성)

#### 2.1. `CafeSelectionScreen.java` (카페 선택 화면)

*   **목적**: 사용자가 이용할 카페를 선택하는 초기 화면입니다.
*   **레이아웃**: `BorderLayout`을 사용하여 상단(`NORTH`)에 제목(`JLabel`)을, 중앙(`CENTER`)에 카페 목록(`JScrollPane` 안에 `JPanel` 그리드)을 배치합니다.
*   **카페 목록**:
    *   `JPanel`에 `GridLayout(0, 2, 24, 20)`을 적용하여 카페 카드들을 2열로 정렬합니다. `0`은 행의 개수를 자동으로 조절하라는 의미입니다.
    *   각 카페는 `createStoreCard()` 메소드를 통해 생성된 `JPanel` 형태의 "카드"로 표현됩니다.
    *   **카페 카드 (`createStoreCard` 내부)**:
        *   `BorderLayout`을 사용하여 상단(`CENTER`)에 이미지 영역(`JPanel`), 하단(`SOUTH`)에 텍스트 영역(`JPanel`)을 배치합니다.
        *   **텍스트 영역**: `BoxLayout(Y_AXIS)`을 사용하여 가게 이름, 설명, 혼잡도 정보를 수직으로 쌓습니다.
        *   **가게 이름**: `JLabel`로 표시됩니다.
        *   **설명 및 실시간 혼잡도**: `descPanel`이라는 `JPanel`에 `FlowLayout(LEFT, 0, 0)`을 사용하여 가게 설명(`JLabel`)과 실시간 혼잡도(`JLabel`)를 한 줄에 나란히 표시합니다. `FlowLayout`은 컴포넌트의 자연스러운 크기를 존중하며 왼쪽에서 오른쪽으로 배치합니다.
*   **혼잡도 표시**:
    *   가게 이름 옆에 `(15분 내: N건)` 형태로 실시간 혼잡도를 표시하며, 건수에 따라 글자 색상이 변경됩니다.
    *   가게 카드 클릭 시, `showCongestionDetails()` 메소드를 통해 시간대별 예상 혼잡도 팝업(`JOptionPane`)이 표시됩니다. 이 팝업은 HTML을 사용하여 정보를 보기 좋게 정렬합니다.

#### 2.2. `MenuScreen.java` (메뉴 및 주문 화면)

*   **목적**: 선택된 카페의 메뉴를 보여주고, 장바구니 관리, 주문, 픽업 시간 선택 등 핵심적인 사용자 상호작용을 처리하는 패널입니다.
*   **레이아웃**: `BorderLayout`을 사용하여 상단(`NORTH`)에 카테고리 선택 영역, 중앙(`CENTER`)에 메뉴 목록, 하단(`SOUTH`)에 주문 내역 및 버튼 영역을 배치합니다.
*   **상단 카테고리 선택 영역**:
    *   `JPanel`에 `BorderLayout`을 사용하여 좌우 화살표 버튼(`JButton`)과 중앙에 카테고리 버튼들을 담은 `JScrollPane`을 배치합니다.
    *   카테고리 버튼들은 `FlowLayout`을 사용하는 `JPanel`에 담겨 수평으로 나열되며, `JScrollPane`을 통해 좌우 스크롤이 가능합니다.
*   **중앙 메뉴 목록**:
    *   `CafeMenuPanel`이라는 별도의 `JPanel` 컴포넌트가 메뉴 카테고리별 상품 목록을 표시합니다.
    *   `CafeMenuPanel` 내부에서는 `CardLayout`을 사용하여 카테고리별 상품 목록을 전환하고, 각 상품 목록은 `GridLayout`을 사용하여 상품 버튼들을 격자 형태로 배치합니다.
*   **하단 주문 내역 및 버튼 영역**:
    *   `GridLayout(1, 2)`를 사용하여 왼쪽에는 `OrderPanel` (장바구니 내역), 오른쪽에는 주문 관련 버튼들(`JButton`)을 배치합니다.
*   **픽업 시간 선택 팝업 (`getPickupTime()` 메소드)**:
    *   `JOptionPane.showConfirmDialog` 내부에 커스텀 `JPanel`을 사용하여 구현됩니다.
    *   `JPanel`은 `BorderLayout`을 사용하여 상단에 안내 문구(`JLabel`), 중앙에 시간 선택 드롭다운(`JComboBox`), 하단에 혼잡도 안내(`JLabel`)를 배치합니다.
    *   `JComboBox`는 `ListCellRenderer`를 커스터마이징하여 "HH:mm" 형식의 시간만 표시합니다.
    *   `ItemListener`를 통해 드롭다운 선택 시 `congestionManager`를 호출하여 해당 5분 구간의 혼잡도를 계산하고, `JLabel`의 텍스트와 색상을 실시간으로 업데이트합니다.

### 3. 레이아웃 매니저 활용

*   **`BorderLayout`**: `MainApplication`, `CafeSelectionScreen`, `MenuScreen` 등 주요 화면의 큰 틀을 잡는 데 사용됩니다. NORTH, SOUTH, EAST, WEST, CENTER 영역에 컴포넌트를 배치하여 유연한 구조를 만듭니다.
*   **`GridLayout`**: `CafeSelectionScreen`의 카페 카드 목록, `CafeMenuPanel`의 상품 목록, `MenuScreen`의 하단 버튼 영역 등 컴포넌트들을 격자 형태로 균등하게 배치할 때 사용됩니다.
*   **`FlowLayout`**: `CafeSelectionScreen`의 설명 및 혼잡도 라벨, `MenuScreen`의 카테고리 버튼 목록 등 컴포넌트들을 자연스럽게 한 줄에 배치할 때 사용됩니다.
*   **`BoxLayout`**: `CafeSelectionScreen`의 카페 카드 텍스트 영역처럼 컴포넌트들을 수직 또는 수평으로 쌓을 때 사용됩니다.
*   **`CardLayout`**: `MainApplication`에서 화면 전환, `CafeMenuPanel`에서 카테고리별 메뉴 전환 등 여러 패널 중 하나만 보이도록 할 때 사용됩니다.

### 4. UI 컴포넌트 및 스타일링

*   **`JLabel`**: 텍스트, 이미지, 아이콘 등을 표시하는 데 사용됩니다. 폰트, 색상, 정렬 등을 설정하여 시각적인 정보를 제공합니다.
*   **`JButton`**: 사용자 클릭 이벤트를 처리하는 버튼입니다. `ActionListener`를 통해 클릭 시 특정 동작을 수행합니다.
*   **`JScrollPane`**: 내용이 많아 화면을 벗어날 경우 스크롤 기능을 제공합니다. `CafeSelectionScreen`의 카페 목록, `MenuScreen`의 카테고리 목록 등에 사용됩니다.
*   **`JOptionPane`**: 사용자에게 메시지를 표시하거나 간단한 입력을 받는 팝업창을 띄울 때 사용됩니다. 커스텀 `JPanel`을 내부에 넣어 복잡한 UI를 팝업으로 구현할 수 있습니다.
*   **스타일링**: `setFont()`, `setForeground()`, `setBackground()`, `setBorder()`, `setPreferredSize()` 등을 사용하여 컴포넌트의 외형을 조절합니다. 특히 `JButton`의 `setFocusPainted(false)`, `setBorder(null)`, `setContentAreaFilled(false)` 등은 버튼의 기본 스타일을 제거하고 커스텀 디자인을 적용할 때 유용합니다.

## 일반 코딩 가이드라인 (자바 초보자를 위한)

*   **객체지향 원칙**: 각 클래스는 명확한 하나의 역할(책임)을 가지도록 설계되었습니다. (예: `Cart`는 장바구니 데이터 관리, `CartFileManager`는 장바구니 파일 관리)
*   **Swing UI**: 자바의 GUI 라이브러리인 Swing을 사용합니다. `JFrame`은 최상위 창, `JPanel`은 그 안에 들어가는 화면 단위입니다. `CardLayout`은 여러 `JPanel`을 겹쳐놓고 필요에 따라 전환하는 데 사용됩니다.
*   **레이아웃 매니저**: `BorderLayout`, `FlowLayout`, `GridLayout`, `BoxLayout` 등 다양한 레이아웃 매니저를 사용하여 컴포넌트들을 배치합니다.
    *   `BorderLayout`: NORTH, SOUTH, EAST, WEST, CENTER 영역에 컴포넌트를 배치합니다.
    *   `FlowLayout`: 컴포넌트들을 왼쪽에서 오른쪽으로, 공간이 부족하면 다음 줄로 배치합니다.
    *   `GridLayout`: 컴포넌트들을 격자(그리드) 형태로 배치합니다.
    *   `BoxLayout`: 컴포넌트들을 수직 또는 수평으로 한 줄로 배치합니다.
*   **이벤트 처리**: `ActionListener`, `MouseAdapter`, `ItemListener` 등을 사용하여 버튼 클릭, 마우스 이벤트, 드롭다운 선택 등의 사용자 상호작용을 처리합니다. 람다 표현식(`e -> ...`)이 많이 사용됩니다.
*   **파일 I/O**: `BufferedReader`, `FileReader`, `PrintWriter`, `FileWriter` 등을 사용하여 텍스트 파일을 읽고 씁니다. `try-with-resources` 구문을 사용하여 파일 자원을 자동으로 닫도록 합니다.
*   **날짜/시간 API**: `java.time` 패키지의 `LocalTime`, `LocalDate`, `LocalDateTime`, `DateTimeFormatter`, `ChronoUnit` 등을 사용하여 날짜와 시간을 다룹니다.
*   **컬렉션**: `List`, `Map`, `ArrayList`, `HashMap`, `LinkedHashMap` 등 다양한 컬렉션 프레임워크를 사용하여 데이터를 효율적으로 관리합니다.
*   **스트림 API**: `stream().filter().collect()` 등 자바 8부터 도입된 스트림 API를 사용하여 컬렉션 데이터를 간결하게 처리합니다.
*   **디버깅**: `System.out.println()`을 사용하여 변수 값이나 코드 흐름을 확인하는 것은 초보자에게 유용한 디버깅 방법입니다. Eclipse의 디버거 기능을 활용하면 코드 실행을 단계별로 추적할 수 있습니다.
