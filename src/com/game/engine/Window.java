package com.game.engine;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private static final long serialVersionUID = -240840600533728354L;
    public Window(String title, Game game) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.add(game);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
    }
}