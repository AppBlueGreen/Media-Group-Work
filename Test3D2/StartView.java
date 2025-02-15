package Test3D2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class StartView extends JPanel {
    private Main main;

    public StartView(Main main) {
        this.main = main;
        setLayout(new BorderLayout());

        //StartView画面の背景
        JLabel startbackimage = new JLabel("<html><img src='file:start5.jpg' width=805 height=580></html>", JLabel.CENTER);
        add(startbackimage, BorderLayout.CENTER);

        setFocusable(true);
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    main.showCourseSelectView(); // ENTERキーを押してCourseSelect画面に移動
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }
}
