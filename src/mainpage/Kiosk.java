package mainpage;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

// 전체 프로그램의 실행 흐름(Flow), 사용자 입력(UI), 그리고 핵심 '상태(State)'(현재 장바구니, 로그인 정보)를 관리
public class Kiosk {
	// DataLoader를 통해 로드된 모든 가게 정보를 보관하는 리스트
	private List<Store> stores;
 
    /*
     * '현재' 사용자가 사용 중인 장바구니 객체입니다.
     * - 프로그램 시작 시: new Cart() (빈 장바구니)
     * - 장바구니 불러오기 시: cartFileManager.loadCart() (불러온 장바구니)
     * - 주문 완료 시: new Cart() (새로운 빈 장바구니)
     */
	private Cart cart;
 
	private final Scanner scanner;
 
    // 장바구니를 파일로 저장, 로드, 삭제하는 로직을 담당하는 객체
	private final CartFileManager cartFileManager;
	
    // '장바구니 불러오기'를 통해 로그인한 고객의 이름. null이면 '비로그인(익명)' 상태, null이 아니면 '로그인' 상태
	private String currentCustomerName;
 
	// '로그인'한 고객의 전화번호. '주문 완료' 시 이 정보를 사용하여 저장된 장바구니 파일을 '삭제'할 때 필요
	private String currentCustomerPhone;
	
	public Kiosk() {
		this.cart = new Cart();
		this.scanner = new Scanner(System.in);
        // 파일 관리 객체 생성 (이때 'saved_carts' 디렉토리도 자동 생성됨)
		this.cartFileManager = new CartFileManager();
        
        // '로그인' 상태를 'null' (로그아웃)으로 명시적 초기화
		this.currentCustomerName = null;
		this.currentCustomerPhone = null;
        
        // Kiosk 객체가 생성됨과 동시에 실행에 필요한 데이터를 준비시킵니다.
		initializeData();
	}
	
	// DataLoader를 통해 가게/메뉴/상품 데이터를 로드하여 'stores' 필드에 저장
	private void initializeData() {
		DataLoader dataLoader = new DataLoader();
		this.stores = dataLoader.loadStores();

		if (this.stores.isEmpty()) {
			System.err.println("오류: 로드된 가게 정보가 없습니다. 프로그램을 확인해주세요.");
		} else {
			System.out.println("--- 가게 메뉴 로딩 완료 ---");
			for (Store store : this.stores) {
				System.out.println("-> '" + store.getName() + "' 가게 입점 완료.");
			}
			System.out.println("-------------------------");
		}
	}
	
    // --- 4. 메인 실행 로직 ---
    
	public void run() {
		if (stores.isEmpty()) {
			return;
		}
  
		while (true) {
			// 화면 표시
			displayMainMenu(); 
			int choice = getUserInput();

			if (choice == 0) {
				System.out.println("프로그램을 종료합니다.");
				break;
			
            } else if (choice > 0 && choice <= stores.size()) {
                // 사용자가 '새로' 가게를 선택했다는 것은, '불러온 장바구니'의 소유권(로그인 상태)을 포기하는 것으로 간주합니다.
                // 단, 장바구니에 담긴 내용물은 초기화하지 않습니다.
                // (즉, 불러온 장바구니에 + 익명으로 상품을 더 담을 수 있음)
				resetLoginState(); 
				
                // 사용자가 선택한 (choice - 1) 인덱스의 가게로 진입
				Store selectedStore = stores.get(choice - 1);
				processStoreMenu(selectedStore);
			
            } else if (choice == stores.size() + 1) {
				processCart();
			
            } else if (choice == stores.size() + 2) {
				processLoadCart();
			
            } else if (choice == stores.size() + 3) {
				processDeleteCart();
			
            } else {
				System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
			}
		}
		scanner.close();
	}

    // --- 화면 표시 메소드 ---
	// 'currentCustomerName' 상태에 따라 동적으로 다른 화면을 보여줍니다.
	private void displayMainMenu() {
		System.out.println("\n===== KIOSK MAIN MENU =====");
        
        // '로그인' 상태(currentCustomerName이 null이 아님)일 경우, 환영 메시지를 추가로 출력합니다.
		if (this.currentCustomerName != null) {
			System.out.printf("[%s 님 환영합니다.]\n", this.currentCustomerName);
		}
 
		for (int i = 0; i < stores.size(); i++) {
			System.out.printf("%d. %s | %s\n", i + 1, stores.get(i).getName(), stores.get(i).getDescription());
		}
        
        // 가게 개수에 따라 동적으로 숫자 변경. 에) 가게 목록이 3개면, 이 메뉴들은 4, 5, 6번이 
		System.out.println("\n[ ORDER & DATA MENU ]");
		System.out.printf("%d. 장바구니 확인 및 주문\n", stores.size() + 1);
		System.out.printf("%d. 장바구니 불러오기\n", stores.size() + 2);
		System.out.printf("%d. 저장된 내역 삭제\n", stores.size() + 3);
        
		System.out.println("\n0. 종료");
		System.out.println("===========================");
		System.out.print("번호를 입력하세요: ");
	}

    // --- 핵심 기능 메소드 ---
    
	// '장바구니 불러오기' 로직을 처리합니다.
	private void processLoadCart() {
		System.out.println("\n[장바구니 불러오기]");
		System.out.print("불러올 고객의 이름을 입력하세요: ");
		String name = scanner.nextLine();
		String phone = getValidPhoneNumber(); // 전화번호 유효성 검사

        // 실제 파일 로딩은 'CartFileManager'에서 진행
		Cart loadedCart = cartFileManager.loadCart(name, phone);

		if (loadedCart != null) {
			this.cart = loadedCart;
			this.currentCustomerName = name;
			this.currentCustomerPhone = phone;

			System.out.println("\n'" + name + "'님의 장바구니를 성공적으로 불러왔습니다.");
			this.cart.displayCartItems();
		} else {
			System.out.println("\n해당 정보로 저장된 장바구니를 찾을 수 없습니다.");
		}
	}

	// '주문하기' 로직을 처리.주문 완료 후 Kiosk의 상태를 초기화(Reset)
	private void placeOrder() {
        // '현재 장바구니(cart)'를 기반으로 'Order' 객체 생성. Order 생성자는 cart의 내용을 복사해갑니다.   
		Order o = new Order(cart);
        // 주문 내역 표시는 'Order' 객체에서 진행
		o.displayOrderDetails();

        // 만약 '로그인' 상태(불러온 장바구니)에서 주문한 것이라면, 해당 주문은 완료되었으므로, 원본 저장 파일을 삭제하여'이미 주문된 장바구니'를 다시 불러오는 것을 방지
		if (this.currentCustomerName != null && !this.currentCustomerName.isEmpty()) {
			System.out.println("\n주문이 완료되었으므로, 저장된 장바구니 내역을 삭제합니다.");
			cartFileManager.deleteCart(this.currentCustomerName, this.currentCustomerPhone);
			resetLoginState();
		}

		// 주문이 완료되었으므로, 현재 장바구니를 '새로운 빈 장바구니'로 교체. (익명 주문이든, 불러온 주문이든 관계없이 항상 초기화)
		this.cart = new Cart();
	}

     // '로그아웃' 상태로 변경. 'cart' 자체는 건드리지 않고, '고객 정보'만 초기화합니다.     
	private void resetLoginState() {
		this.currentCustomerName = null;
		this.currentCustomerPhone = null;
	}

	// '가게(Store)'의 메뉴 로직을 처리
	private void processStoreMenu(Store store) {
		while (true) {
			displayStoreMenu(store);
			int choice = getUserInput();
			if (choice == 0)
				break; // 메인 메뉴로 복귀
			else if (choice > 0 && choice <= store.getMenus().size()) {
				Menu selectedMenu = store.getMenus().get(choice - 1);
				processProductMenu(selectedMenu); // 상품 메뉴에서 진행
			} else {
				System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
			}
		}
	}

	// 가게의 카테고리 메뉴 화면을 출력
	private void displayStoreMenu(Store store) {
		System.out.printf("\n===== [%s] MENU =====\n", store.getName());
		List<Menu> menus = store.getMenus();
		for (int i = 0; i < menus.size(); i++) {
			System.out.printf("%d. %s\n", i + 1, menus.get(i).getCategoryName());
		}
		System.out.println("\n0. 메인 메뉴로 돌아가기");
		System.out.println("===========================");
		System.out.print("번호를 입력하세요: ");
	}

	// 특정 '메뉴(Menu)'의 상품 로직을 처리
	private void processProductMenu(Menu menu) {
		while (true) {
            // 1. 'View'
			displayProductMenu(menu);
            // 2. 'Input'
			int choice = getUserInput();
            // 3. 'Control'
			if (choice == 0)
				break; // 이전 메뉴(가게 메뉴)로 복귀
			else if (choice > 0 && choice <= menu.getProducts().size()) {
				Product selectedProduct = menu.getProducts().get(choice - 1);
				confirmAddToCart(selectedProduct); // 장바구니 추가 확인 로직
			} else {
				System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
			}
		}
	}

	// 카테고리의 상품 목록 화면을 출력
	private void displayProductMenu(Menu menu) {
		System.out.printf("\n----- [%s] ----- \n", menu.getCategoryName());
		List<Product> products = menu.getProducts();
		for (int i = 0; i < products.size(); i++) {
			Product p = products.get(i);
			System.out.printf("%d. %s\n", i + 1, p.toString());
		}
		System.out.println("\n0. 이전 메뉴로 돌아가기");
		System.out.print("번호를 입력하세요: ");
	}

	// '장바구니 확인 및 주문' 메뉴의 로직을 처리
	private void processCart() {
		while (true) {
            // 1. 'View' (현재 장바구니 상태 표시)
			cart.displayCartItems();
            
            // 2. [방어 코드] 장바구니가 비어있으면 메뉴를 띄울 필요 없이 즉시 복귀
			if (cart.getItems().isEmpty()) {
				System.out.println("메인 메뉴로 돌아갑니다.");
				return; // processCart() 메소드 종료
			}
   
            // 3. 'View' (선택 옵션)
			System.out.println("1. 주문하기        2. 메뉴 더 담기        3. 장바구니 저장하기");
			System.out.print("번호를 입력하세요: ");
            
            // 4. 'Input'
			int choice = getUserInput();
            
            // 5. 'Control'
			if (choice == 1) {
                // 5-1. 주문하기
				placeOrder(); // 주문 로직 실행
				break; // 주문 완료 후 메인 메뉴로 복귀
			} else if (choice == 2)
                // 5-2. 메뉴 더 담기
				break; // 메인 메뉴로 복귀 (가게 선택부터 다시 시작)
			else if (choice == 3)
                // 5-3. 장바구니 저장하기
				processSaveCart(); // 저장 로직 실행 (루프는 계속)
			else
				System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
		}
	}

	// '장바구니 저장하기' 로직을 처리
	private void processSaveCart() {
		System.out.println("\n[장바구니 저장]");
		System.out.print("저장할 고객의 이름을 입력하세요: ");
		String name = scanner.nextLine();
		String phone = getValidPhoneNumber(); // 유효성 검사
        
        // 실제 저장은 'CartFileManager'에서 진행
		cartFileManager.saveCart(this.cart, name, phone);
	}

	// '저장된 내역 삭제' 로직을 처리
	private void processDeleteCart() {
		System.out.println("\n[저장된 내역 삭제]");
		System.out.print("삭제할 고객의 이름을 입력하세요: ");
		String name = scanner.nextLine();
		String phone = getValidPhoneNumber(); // 유효성 검사
        // 실제 삭제는 'CartFileManager'에서 진행
		cartFileManager.deleteCart(name, phone);
	}

    // --- 7. 유틸리티 (Utility) 메소드 ---
    
	// 사용자에게 상품 추가를 '확인' 받음
	private void confirmAddToCart(Product p) {
		System.out.printf("\n\"%s\" 를 장바구니에 추가하시겠습니까?\n", p.toString());
		System.out.println("1. 확인        2. 취소");
		System.out.print("번호를 입력하세요: ");
        
		int choice = getUserInput();
		if (choice == 1)
            // 1. '확인' 시, 'Cart' 객체에서 진행
			cart.addProduct(p);
		else
            // 2. '취소' 시
			System.out.println("추가를 취소했습니다.");
	}

	// 사용자로부터 유효한 전화번호를 입력받을 때까지 반복합니다.
	private String getValidPhoneNumber() {
        // [정규 표현식]
        // ^     : 문자열의 시작
        // 010   : "010" 문자 그대로
        // \d{8} : \d(숫자)가 {8}(8번) 반복
        // $     : 문자열의 끝
		String p = "^010\\d{8}$";
  
		while (true) {
			System.out.print("전화번호를 입력하세요 (예: 01012345678): ");
			String s = scanner.nextLine();
            
            // 'Pattern.matches(regex, input)'
            // : 입력 문자열(s)이 정규식(p)과 *완전히* 일치하는지 검사
			if (Pattern.matches(p, s))
				return s; // 유효한 형식이면 문자열 반환
			else
                // 유효하지 않으면 오류 메시지 출력 후 while 루프 계속
				System.out.println("잘못된 형식입니다. '010'으로 시작하는 11자리 숫자를 입력해주세요.");
		}
	}

	/**
     * [유틸리티] 습니다.
     * <p>
     * [중요] {@link java.util.InputMismatchException} 방지:
     * {@code scanner.nextInt()} 대신 
     * </p>
     * <p>
     * 만약 "abc"처럼 숫자가 아닌 값이 들어와 {@link NumberFormatException}이
     * 발생하면, -1 (잘못된 입력)을 반환하여 메인 루프가 예외 처리(재입력)를
     * 할 수 있도록 합니다.
     * </p>
     *
     * @return 사용자가 입력한 숫자. 변환 실패 시 -1.
     */
	// 사용자로부터 '숫자' 입력을 안전하게 받음
	// scanner.nextLine()으로 한 줄 전체를 문자열로 읽어온 뒤, Integer#parseInt(String) 으로 수동 변환
	private int getUserInput() {
		try {
            // 1. 문자열로 한 줄을 읽음
			String input = scanner.nextLine();
            // 2. 숫자로 변환 시도
			return Integer.parseInt(input); 
		} catch (NumberFormatException e) {
            // 3. 변환 실패(예: "abc" 입력) 시, 예외가 발생
            //    프로그램이 중단되는 대신, -1을 반환
			return -1; 
		}
	}
}