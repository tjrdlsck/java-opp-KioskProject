package mainpage.service;

import java.io.BufferedReader; 
import java.io.File;           
import java.io.FileReader;     
import java.io.IOException;    
import java.util.ArrayList;    
import java.util.List;

import mainpage.model.Menu;
import mainpage.model.Product;
import mainpage.model.Store;       

public class DataLoader {
    
    private static final String MENU_DATA_DIRECTORY = "menuData";

    public List<Store> loadStores() {
        List<Store> stores = new ArrayList<>();
        File dir = new File(MENU_DATA_DIRECTORY);
        File[] files = dir.listFiles(); 

        if (files == null) {
            System.err.println("오류: '" + MENU_DATA_DIRECTORY + "' 디렉토리를 찾을 수 없습니다.");
            return stores; 
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String firstLine = reader.readLine();
                    if (firstLine == null || !firstLine.startsWith("STORE")) {
                        System.err.println("오류: '" + file.getName() + "' 파일의 형식이 잘못되었습니다. (STORE 정보 누락)");
                        continue;
                    }

                    String[] storeParts = firstLine.split("\\|");
                    if (storeParts.length < 3) {
                        System.err.println("오류: '" + file.getName() + "' 파일의 STORE 정보가 부족합니다.");
                        continue; 
                    }

                    String storeName = storeParts[1];
                    String storeDescription = storeParts[2];
                    Store store = new Store(storeName, storeDescription);

                    String line;
                    Menu currentMenu = null;
                    
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split("\\|", -1);
                        if (parts.length < 2) continue;
                        
                        String type = parts[0]; 

                        if ("CATEGORY".equals(type)) {
                            currentMenu = new Menu(parts[1]);
                            store.addMenu(currentMenu);
                            
                        } else if ("PRODUCT".equals(type) && currentMenu != null) {
                            if (parts.length < 3) continue; 
                            String name = parts[1];
                            int price = Integer.parseInt(parts[2]); 
                            String imagePath = ""; 
                            
                            if (parts.length == 4 && !parts[3].isEmpty()) {
                                imagePath = parts[3];
                            }
                            currentMenu.addProduct(new Product(name, price, imagePath));
                        }
                    }
                    stores.add(store);

                } catch (IOException | NumberFormatException e) {
                    System.err.println("파일 처리 중 오류 발생: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
        return stores;
    }
}