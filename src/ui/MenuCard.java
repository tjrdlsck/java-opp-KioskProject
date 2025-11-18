package ui;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

public class MenuCard extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public MenuCard(String name, int price, String imagePath) {

        setPreferredSize(new Dimension(160, 190));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        
     // ============================
     // 이미지 영역 (센터 크롭으로 꽉 채움)
     // ============================
     JPanel imgPanel = new JPanel() {
         Image image = (imagePath != null) ? new ImageIcon(imagePath).getImage() : null;

         @Override
         protected void paintComponent(Graphics g) {
             super.paintComponent(g);

             if (image == null) {
                 g.setColor(new Color(230, 230, 230));
                 g.fillRect(0, 0, getWidth(), getHeight());
                 g.setColor(Color.GRAY);
                 g.drawString("사진", getWidth() / 2 - 10, getHeight() / 2);
                 return;
             }

             Graphics2D g2 = (Graphics2D) g.create();
             g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                     RenderingHints.VALUE_INTERPOLATION_BILINEAR);

             int panelW = getWidth();
             int panelH = getHeight();
             int imgW = image.getWidth(null);
             int imgH = image.getHeight(null);

             double panelRatio = (double) panelW / panelH;
             double imageRatio = (double) imgW / imgH;

             int drawW, drawH, drawX, drawY;

             if (imageRatio > panelRatio) {
                 drawH = panelH;
                 drawW = (int) (panelH * imageRatio);
                 drawX = (panelW - drawW) / 2;
                 drawY = 0;
             } else {
                 drawW = panelW;
                 drawH = (int) (panelW / imageRatio);
                 drawX = 0;
                 drawY = (panelH - drawH) / 2;
             }

             g2.drawImage(image, drawX, drawY, drawW, drawH, this);
             g2.dispose();
         }
     };

     imgPanel.setPreferredSize(new Dimension(160, 140));
     imgPanel.setBackground(new Color(230, 230, 230));



        // ============================
        // 이름 + 가격
        // ============================
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        NumberFormat nf = NumberFormat.getInstance();
        JLabel priceLabel = new JLabel(nf.format(price) + "원", SwingConstants.RIGHT);
        priceLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        priceLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        textPanel.add(nameLabel, BorderLayout.CENTER);
        textPanel.add(priceLabel, BorderLayout.SOUTH);

        add(imgPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);
    }
}
