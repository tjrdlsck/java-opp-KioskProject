package ui;

import java.io.*;
import java.util.*;

public class MenuLoader {
    public static Map<String, java.util.List<MenuItem>> loadMenuFromFile(String filePath) {
        Map<String, java.util.List<MenuItem>> menuData = new LinkedHashMap<>();
        String currentCategory = null;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("CATEGORY|")) {
                    currentCategory = line.split("\\|")[1].trim();
                    menuData.put(currentCategory, new ArrayList<>());
                } else if (line.startsWith("PRODUCT|") && currentCategory != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 3) {
                        String name = parts[1].trim();
                        int price = Integer.parseInt(parts[2].trim());
                        menuData.get(currentCategory).add(new MenuItem(name, price));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("⚠ 메뉴 파일 로드 실패: " + filePath);
        }

        return menuData;
    }

    // 메뉴 아이템 구조체
    public static class MenuItem {
        public String name;
        public int price;
        public MenuItem(String name, int price) {
            this.name = name;
            this.price = price;
        }
    }
}
