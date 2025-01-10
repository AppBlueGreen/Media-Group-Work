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
        JLabel startbackimage = new JLabel("<html><img src='file:CourseSelectViewBack.jpg' width=805 height=580></html>", JLabel.CENTER);
        add(startbackimage, BorderLayout.CENTER);

        //ステージを選択してくださいの文字
        JLabel courseLabel = new JLabel("ステージを選択して、Start Gameを押してください", JLabel.CENTER);
        courseLabel.setFont(new Font("MS Gothic", Font.BOLD, 26));
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
