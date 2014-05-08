package ro.apiticas.runner.gui;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame(RunnerUI.class.getName());
        RunnerUI runnerUI = new RunnerUI();

        frame.setContentPane(runnerUI.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        frame.pack();
        frame.setVisible(true);
    }
}
