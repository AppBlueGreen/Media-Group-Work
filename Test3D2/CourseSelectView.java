package Test3D2;

import javax.swing.*;
import java.awt.*;

public class CourseSelectView extends JPanel {
    private Main main;
    private Image backgroundImage; // 背景画像

    public CourseSelectView(Main main) {
        this.main = main;
        this.backgroundImage = new ImageIcon(getClass().getResource("/CourseSelectViewBack.jpg")).getImage();
        setLayout(new BorderLayout());

        // 「ステージを選択して、Start Gameを押してください」のラベル (上部配置)
        JLabel courseLabel = new JLabel("ステージを選択して、Start Gameを押してください", JLabel.CENTER);
        courseLabel.setFont(new Font("MS Gothic", Font.BOLD, 26));
        add(courseLabel, BorderLayout.NORTH);

        // 画像の読み込みとリサイズ
        ImageIcon buttonIcon1 = resizeIcon(new ImageIcon("nishi.png"), 300, 200);
        ImageIcon buttonIcon2 = resizeIcon(new ImageIcon("higashi.png"), 300, 200);

        // ボタンの作成
        JButton nishiButton = createImageButton(buttonIcon1);
        JButton higashiButton = createImageButton(buttonIcon2);

        // ボタンのクリックイベント
        nishiButton.addActionListener(e -> {
            SharedData.currentMap = SharedData.Maps.get(0); // 西地区のマップ
            main.showGameView(new View2D(main), new View3D(main));
        });

        higashiButton.addActionListener(e -> {
            SharedData.currentMap = SharedData.Maps.get(1); // 東地区のマップ
            main.showGameView(new View2D(main), new View3D(main));
        });
        JPanel startPanel = createButtonWithLabel(nishiButton, "西地区");
        JPanel exitPanel = createButtonWithLabel(higashiButton, "東地区");

        // ボタンを横並びにするパネル
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 40)); // 間隔 50px
        buttonPanel.setOpaque(false);
        buttonPanel.add(startPanel);
        buttonPanel.add(exitPanel);

        // ボタンパネルを中央配置
        add(buttonPanel, BorderLayout.CENTER);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 30, getWidth(), getHeight(), this);
    }
    private JPanel createButtonWithLabel(JButton button, String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);

        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("MS Gothic", Font.PLAIN, 18));

        if ("西地区".equals(text)) {
            label.setForeground(Color.YELLOW);
        } else if ("東地区".equals(text)) {
            label.setForeground(Color.GREEN);
        }

        panel.add(button, BorderLayout.CENTER);
        panel.add(label, BorderLayout.SOUTH);
        return panel;
    }
    private JButton createImageButton(ImageIcon icon) {
        JButton button = new JButton(icon);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        return button;
    }
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }
}