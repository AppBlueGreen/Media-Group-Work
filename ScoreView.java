import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScoreView extends JPanel {
    private Main main;
    private JLabel scoreLabel;

    public ScoreView(Main main) {
        this.main = main;
        setLayout(new BorderLayout());

        //Game Clearの文字
        JLabel titleLabel = new JLabel("Game Clear!!", JLabel.CENTER);
        titleLabel.setFont(new Font("MS Gothic", Font.BOLD, 32));
        add(titleLabel, BorderLayout.NORTH);

        scoreLabel = new JLabel("Your Score: 0", JLabel.CENTER);
        scoreLabel.setFont(new Font("MS Gothic", Font.PLAIN, 24));
        add(scoreLabel, BorderLayout.CENTER);

        //Course Select画面に移動するボタン
        JButton restartButton = new JButton("コース選択画面へ");
        restartButton.setFont(new Font("MS Gothic", Font.PLAIN, 24));
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.showCourseSelectView(); // スタート画面に戻る
            }
        });
        add(restartButton, BorderLayout.SOUTH);
    }

    public void setScore(int score) {
        scoreLabel.setText("Your Score: " + score);
    }
}
