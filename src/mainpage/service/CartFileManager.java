package mainpage.service;

import java.io.BufferedReader; 
import java.io.File;           
import java.io.FileReader;     
import java.io.FileWriter;     
import java.io.IOException;    
import java.io.PrintWriter;    
import java.util.List;

import mainpage.model.Cart;
import mainpage.model.CartItem;
import mainpage.model.Product;         

// CartFileManager (장바구니 파일 관리자) 클래스
// [5단계 수정] M-VC 원칙에 따라 saveCart, loadCart 메소드의 System.out 제거
// saveCart의 반환 타입을 boolean으로 변경
public class CartFileManager {

    private static final String CART_DATA_DIRECTORY = "saved_carts";

    public CartFileManager() {
        File dir = new File(CART_DATA_DIRECTORY);
        
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("'" + CART_DATA_DIRECTORY + "' 디렉토리를 생성했습니다.");
            } else {
                System.err.println("오류: '" + CART_DATA_DIRECTORY + "' 디렉토리 생성에 실패했습니다.");
            }
        }
    }
    
    /**
     * [5단계 수정]
     * 1. 반환 타입을 void -> boolean으로 변경
     * 2. 모든 System.out/err 제거
     * 3. Controller(KioskAppManager)가 이 메소드의 반환 값으로 View(JOptionPane) 제어
     */
    public boolean saveCart(Cart cart, String customerName, String phoneNumber) {
        if (cart.getItems().isEmpty()) {
            // System.out.println("장바구니가 비어있어 저장할 수 없습니다."); // [5단계 제거]
            return false; // 저장할 내용이 없으므로 실패 반환
        }
        String fileName = String.format("cart_%s_%s.txt", customerName, phoneNumber);
        File cartFile = new File(CART_DATA_DIRECTORY, fileName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(cartFile))) {
            writer.println("NAME|" + customerName);
            writer.println("PHONE|" + phoneNumber);
            writer.println("---"); 
            
            List<CartItem> items = cart.getItems();
            for (CartItem item : items) {
                Product product = item.getProduct();
                
                writer.printf("%s|%d|%s|%d\n",
                        product.getName(),       
                        product.getPrice(),      
                        product.getImagePath(),  
                        item.getQuantity());     
            }
            // System.out.println("장바구니가 '" + cartFile.getPath() + "' 파일로 성공적으로 저장되었습니다."); // [5단계 제거]
            return true; // 저장 성공
        } catch (IOException e) {
            // System.err.println("파일 저장 중 오류가 발생했습니다: " + e.getMessage()); // [5단계 제거]
            return false; // 저장 실패
        }
    }

    /**
     * [4단계 수정] (4단계에서 완료됨)
     * 모든 System.out/System.err 구문을 제거.
     */
    public boolean deleteCart(String customerName, String phoneNumber) {
        String fileName = String.format("cart_%s_%s.txt", customerName, phoneNumber);
        File cartFile = new File(CART_DATA_DIRECTORY, fileName);

        if (cartFile.exists()) {
            if (cartFile.delete()) {
                return true; 
            } else { 
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * [5단계 수정]
     * 1. 모든 System.err 제거
     * 2. Controller(KioskAppManager)가 null 반환 값을 체크하여 View(JOptionPane) 제어
     */
    public Cart loadCart(String customerName, String phoneNumber) {
        String fileName = String.format("cart_%s_%s.txt", customerName, phoneNumber);
        File cartFile = new File(CART_DATA_DIRECTORY, fileName);

        if (!cartFile.exists()) {
            return null;
        }

        Cart loadedCart = new Cart();

        try (BufferedReader reader = new BufferedReader(new FileReader(cartFile))) {
            String line;
            boolean isProductSection = false;

            // [5단계 수정] addProduct는 System.out이 제거된 3단계 버전을 사용
            while ((line = reader.readLine()) != null) {
                if (line.equals("---")) {
                    isProductSection = true;
                    continue;
                }
                if (isProductSection) {
                    String[] parts = line.split("\\|", -1);
                    if (parts.length == 4) {
                        String name = parts[0];
                        int price = Integer.parseInt(parts[1]);
                        String imagePath = parts[2];
                        int quantity = Integer.parseInt(parts[3]);
                        Product product = new Product(name, price, imagePath);
                        
                        // addProduct는 3단계에서 이미 System.out이 제거됨
                        for (int i = 0; i < quantity; i++) {
                            loadedCart.addProduct(product);
                        }
                    }
                }
            }
            return loadedCart;
        } catch (IOException e) {
            // System.err.println("파일을 불러오는 중 I/O 오류가 발생했습니다: " + e.getMessage()); // [5단계 제거]
            return null; 
        } catch (NumberFormatException e) {
            // System.err.println("파일 데이터 형식이 잘못되었습니다 (숫자 변환 오류): " + e.getMessage()); // [5단계 제거]
            return null; 
        }
    }
}