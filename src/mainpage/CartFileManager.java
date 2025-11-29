package mainpage;

import java.io.BufferedReader; 
import java.io.File;           
import java.io.FileReader;     
import java.io.FileWriter;     
import java.io.IOException;    
import java.io.PrintWriter;    
import java.util.List;         

// CartFileManager (장바구니 파일 관리자) 클래스 - 파일 저장 및 삭제 담당
public class CartFileManager {

    // 저장된 장바구니 파일들이 위치할 디렉토리(폴더)의 경로를 지정
    private static final String CART_DATA_DIRECTORY = "saved_carts";

    // 프로그램 실행 시 (CART_DATA_DIRECTORY)에 지정된 디렉토리가 존재하는지 확인하고, 존재하지 않으면 생성
    public CartFileManager() {
        // File 객체는 실제 파일을 생성하는 것이 아니라, 해당 경로를 참조하는 '정보' 객체 생성
        File dir = new File(CART_DATA_DIRECTORY);
        
        // 2. 디렉토리 존재 여부 확인
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("'" + CART_DATA_DIRECTORY + "' 디렉토리를 생성했습니다.");
            } else {
                System.err.println("오류: '" + CART_DATA_DIRECTORY + "' 디렉토리 생성에 실패했습니다.");
            }
        }
    }
    
    
    /*  
     장바구니 내용을 텍스트 파일로 저장
     파일 이름은 고객 전화번호를 이용하여 생성
     (예: cart_01012345678.txt)
     
     파일 저장 형식
     PHONE|고객전화번호
     --- 
     상품이름1|가격1|이미지경로1|수량1
     상품이름2|가격2|이미지경로2|수량2
    */ 
    public void saveCart(Cart cart, String phoneNumber) {
        if (cart.getItems().isEmpty()) {
            System.out.println("장바구니가 비어있어 저장할 수 없습니다.");
            return;
        }
        // 파일 이름 지정
        String fileName = String.format("cart_%s.txt", phoneNumber);
        
        File cartFile = new File(CART_DATA_DIRECTORY, fileName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(cartFile))) {
            
            // 고객 정보 쓰기
            writer.println("PHONE|" + phoneNumber);
            // 고객 정보와 상품 정보를 구분하는 구분자
            writer.println("---"); 
            
            // 상품 목록 쓰기
            List<CartItem> items = cart.getItems();
            for (CartItem item : items) {
                Product product = item.getProduct();
                
                writer.printf("%s|%d|%s|%s|%d\n",
                        product.getName(),        
                        product.getPrice(),       
                        product.getImagePath(),
                        product.getStoreName(),   // [추가됨]
                        item.getQuantity());      
            }
            
            System.out.println("장바구니가 '" + cartFile.getPath() + "' 파일로 성공적으로 저장되었습니다.");

        } catch (IOException e) {
            System.err.println("파일 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 저장된 장바구니 파일을 삭제 (return 값 : 삭제 성공 시 true)
    public boolean deleteCart(String phoneNumber) {
        
        // 'saveCart'와 동일한 규칙으로 파일 이름을 생성
        String fileName = String.format("cart_%s.txt", phoneNumber);
        File cartFile = new File(CART_DATA_DIRECTORY, fileName);

        if (cartFile.exists()) {
            if (cartFile.delete()) {
                System.out.println("'" + cartFile.getPath() + "' 파일을 성공적으로 삭제했습니다.");
                return true; 
            } else { 
                System.err.println("'" + cartFile.getPath() + "' 파일 삭제에 실패했습니다.");
                return false;
            }
        } else {
            System.out.println("해당 고객 정보로 저장된 장바구니 내역이 없습니다.");
            return false;
        }
    }

    // 파일로부터 장바구니 데이터를 읽
    public Cart loadCart(String phoneNumber) {
        String fileName = String.format("cart_%s.txt", phoneNumber);
        File cartFile = new File(CART_DATA_DIRECTORY, fileName);

        if (!cartFile.exists()) {
            return null;
        }

        Cart loadedCart = new Cart();

        try (BufferedReader reader = new BufferedReader(new FileReader(cartFile))) {
        	 
            String line;
            // 상태 플래그 - "---" 구분자를 만나기 전까지는 false, 만난 후에는 true
            boolean isProductSection = false;

            while ((line = reader.readLine()) != null) {
                // 구분자("---") 확인
                if (line.equals("---")) {
                    isProductSection = true;
                    continue;
                }

                if (isProductSection) {
                    
                    // [문자열 파싱]
                    // '|' 문자로 구분된 데이터를 문자열 배열로 분리합니다.
                    //  - "\\|": 정규식(Regex)에서 '|'는 'OR'를 의미하는 특수문자이므로,
                    //    문자 그대로의 '|'를 찾으려면 백슬래시(\)로 이스케이프(escape)해야 합니다.
                    //    Java 문자열에 백슬래시 자체를 쓰려면 \\ 두 번 써야 합니다.
                    //  - '-1': split의 두 번째 인자(limit)입니다. -1을 주면
                    //    라인 끝에 빈 문자열(예: ...|imagePath| 처럼 이미지 경로가 없는 경우)도
                    //    배열에 빈 문자열("")로 포함시켜, 항상 일관된 배열 크기를 갖도록 보장합니다.
                    String[] parts = line.split("\\|", -1);
                    
                    if (parts.length >= 5) {
                        String name = parts[0];
                        int price = Integer.parseInt(parts[1]);
                        String imagePath = parts[2];
                        String storeName = parts[3]; // [추가됨] 가게 이름 파싱
                        int quantity = Integer.parseInt(parts[4]);
                     // [수정] Product 생성 시 storeName 전달
                        Product product = new Product(name, price, imagePath, storeName);
                        
                        // [로직 재사용]
                        // loadedCart의 내부 리스트에 직접 CartItem을 추가하는 대신, 'Cart' 클래스가 이미 가지고 있는 'addProduct' 메소드를 'quantity' 횟수만큼 호출합니다. 
                        //  이렇게 하면, 'addProduct' 내부의 "이미 상품이 있으면 수량 증가" 로직을 그대로 재사용하게 되어, 코드 중복을 피하고 일관성을 유지할 수 있습니다.
                        for (int i = 0; i < quantity; i++) {
                            loadedCart.addProduct(product);
                        }
                    }
                }
            }
            
            return loadedCart;

        } catch (IOException e) {
            System.err.println("파일을 불러오는 중 I/O 오류가 발생했습니다: " + e.getMessage());
            return null; 
        } catch (NumberFormatException e) {
            System.err.println("파일 데이터 형식이 잘못되었습니다 (숫자 변환 오류): " + e.getMessage());
            return null; 
        }
    }
}