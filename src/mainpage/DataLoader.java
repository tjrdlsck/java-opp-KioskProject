package mainpage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets; // 인코딩 상수를 사용하기 위한 import (권장)
import java.util.ArrayList;
import java.util.List;

// 프로그램 시작 시 필요한 초기 데이터(가게, 메뉴, 상품)를 외부 파일(.txt)로부터 읽어 자바 객체(Store, Menu, Product)로 변환하는 클래스
public class DataLoader {
    
    // 가게/메뉴 데이터 파일이 위치한 디렉토리 이름을 상수로 정의
    private static final String MENU_DATA_DIRECTORY = "menuData";

    // 모든 .txt 파일을 읽어들여 객체의 리스트를 생성하여 반환합니다. (List<Store>)
    public List<Store> loadStores() {
        List<Store> stores = new ArrayList<>();
        
        File dir = new File(MENU_DATA_DIRECTORY);
        
        File[] files = dir.listFiles(); 

        if (files == null) {
            System.err.println("오류: '" + MENU_DATA_DIRECTORY + "' 디렉토리를 찾을 수 없습니다.");
            return stores; 
        }

        for (File file : files) {
            
            // 실제 '파일'이며(디렉토리가 아니고) 이름이 '.txt'로 끝나는지 확인
            if (file.isFile() && file.getName().endsWith(".txt")) {
                
                // 인코딩을 "UTF-8"로 명시하여 한글 깨짐 방지
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                    
                    String firstLine = reader.readLine();
                    
                    // 파일이 비어있거나 첫 줄이 없는 경우 방어 코드 추가
                    if (firstLine == null || !firstLine.startsWith("STORE")) {
                        System.err.println("오류: '" + file.getName() + "' 파일의 형식이 잘못되었습니다. (STORE 정보 누락)");
                        continue;
                    }

                    String[] storeParts = firstLine.split("\\|");
                    
                    // STORE 정보는 최소 3개 파트(STORE, 이름, 설명)가 있어야 함
                    if (storeParts.length < 3) {
                        System.err.println("오류: '" + file.getName() + "' 파일의 STORE 정보가 부족합니다.");
                        continue; 
                    }

                    String storeName = storeParts[1];
                    String storeDescription = storeParts[2];
                    String storeImagePath = ""; // 카페 이미지 경로
                    
                    // 선택적으로 카페 이미지 경로 파싱 (4번째 파트)
                    if (storeParts.length >= 4 && !storeParts[3].isEmpty()) {
                        storeImagePath = storeParts[3];
                    }
                    
                    Store store = new Store(storeName, storeDescription, storeImagePath);

                    String line;
                    Menu currentMenu = null;
                    
                    while ((line = reader.readLine()) != null) {
                        
                        // '|' 기준으로 줄을 파싱합니다.
                        // limit 인자로 -1을 주어 빈 문자열도 보존합니다.
                        String[] parts = line.split("\\|", -1);
                        if (parts.length < 2) continue; // 최소 2개 파트(타입, 이름)가 없으면 무시
                        
                        String type = parts[0]; // 'CATEGORY' 또는 'PRODUCT'

                        if ("CATEGORY".equals(type)) {
                            currentMenu = new Menu(parts[1]);
                            store.addMenu(currentMenu);
                            
                        // 'PRODUCT' 라인을 만났을 때
                        } else if ("PRODUCT".equals(type) && currentMenu != null) {
                            
                            if (parts.length < 3) continue; // 상품은 최소 3개 파트 필요
                            
                            // 상품 정보 파싱
                            String name = parts[1];
                            int price = 0;
                            try {
                                price = Integer.parseInt(parts[2]); // 문자열 -> 정수
                            } catch (NumberFormatException e) {
                                System.err.println("가격 형식 오류 (" + file.getName() + "): " + parts[2]);
                                continue;
                            }
                            
                            String imagePath = ""; // 기본값은 빈 문자열
                            
                            // 이미지 경로(선택적) 파싱
                            if (parts.length >= 4 && !parts[3].isEmpty()) {
                                imagePath = parts[3];
                            }
                            currentMenu.addProduct(new Product(name, price, imagePath, storeName));
                        }
                    }
                    stores.add(store);

                } catch (IOException e) {
                    System.err.println("파일 처리 중 IO 오류 발생: " + file.getName());
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("파일 처리 중 알 수 없는 오류 발생: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
        
        return stores;
    }
}