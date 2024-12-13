import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CourseSelectView extends JPanel {
    private Main main;

    public CourseSelectView(Main main) {
        this.main = main;
        setLayout(new BorderLayout());

        //CourseSelect画面の背景
        

        //ステージを選択してくださいの文字
        JLabel courseLabel = new JLabel("ステージを選択してください", JLabel.CENTER);
        courseLabel.setFont(new Font("MS Gothic", Font.BOLD, 32)); // 日本語をサポートするフォントを指定
        add(courseLabel, BorderLayout.NORTH);

        // スタートボタン
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("MS Gothic", Font.PLAIN, 24));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.showGameView(); // GameView画面に移動
            }
        });
        add(startButton, BorderLayout.SOUTH);
    }
}
