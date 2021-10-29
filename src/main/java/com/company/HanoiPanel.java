package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class HanoiPanel extends JPanel {
    private static final Color POLE_COLOR = new Color(0x594700);
    private static final Font STEP_FONT = new Font("Monospace", Font.PLAIN, 15);
    private static final char[] STEP_TEXT = new char[9];
    static {
        // initialize the first few chars
        STEP_TEXT[0] = 'S';
        STEP_TEXT[1] = 't';
        STEP_TEXT[2] = 'e';
        STEP_TEXT[3] = 'p';
        STEP_TEXT[4] = ' ';
    }
    private ArrayList<Disk> stackA;
    private final ArrayList<Disk> stackB = new ArrayList<>();
    private final ArrayList<Disk> stackC = new ArrayList<>();
    private final Disk[] disks;
    private final TowerOfHanoi solver;

    private int curr;

    public HanoiPanel(int n) {
        super();
        setBackground(Color.white);
        stackA = new ArrayList<>(n);
        disks = new Disk[n];
        for (int i = 0; i < n; i++) {
            disks[i] = new Disk(n - i - 1);
        }
        solver = new TowerOfHanoi(n);
        reset();
    }

    public boolean end() {
        return curr == solver.getSize() - 1;
    }

    public void moveTo(int num) {
        if (num == 1) {
            reset();
        } else {
            while (curr + 2 != num) {
                if (curr + 2 > num) {
                    prev();
                } else {
                    next();
                }
            }
        }
        repaint();
    }
    private void next() {
        if (curr == solver.getSize() - 1) return;
        TowerOfHanoi.Step step = solver.getStep(++curr);
        Disk curr = switch (step.from) {
            case 'A' -> stackA.remove(stackA.size() - 1);
            case 'B' -> stackB.remove(stackB.size() - 1);
            case 'C' -> stackC.remove(stackC.size() - 1);
            default -> throw new IllegalStateException("Unexpected value: " + step.from);
        };
        switch (step.to) {
            case 'A' -> stackA.add(curr);
            case 'B' -> stackB.add(curr);
            case 'C' -> stackC.add(curr);
        }
    }
    private void prev() {
        if (curr < 0) return;
        TowerOfHanoi.Step step = solver.getStep(curr--);
        Disk curr = switch (step.to) {
            case 'A' -> stackA.remove(stackA.size() - 1);
            case 'B' -> stackB.remove(stackB.size() - 1);
            case 'C' -> stackC.remove(stackC.size() - 1);
            default -> throw new IllegalStateException("Unexpected value: " + step.from);
        };
        switch (step.from) {
            case 'A' -> stackA.add(curr);
            case 'B' -> stackB.add(curr);
            case 'C' -> stackC.add(curr);
        }
    }
    public void reset() {
        curr = -1;
        stackA = new ArrayList<>(Arrays.asList(disks));
        stackB.clear();
        stackC.clear();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        // draw poles
        g.setColor(POLE_COLOR);
        for (int i = 0, j = width / 4; i < 3; i++, j += width / 4) {
            g.fillRoundRect(j, height - Disk.HEIGHT * 11, 5, Disk.HEIGHT * 12, 5, 5);
        }
        // draw disks
        for (int i = 0; i < stackA.size(); i++) {
            stackA.get(i).draw(g, i, 0, width, height);
        }
        for (int i = 0; i < stackB.size(); i++) {
            stackB.get(i).draw(g, i, 1, width, height);
        }
        for (int i = 0; i < stackC.size(); i++) {
            stackC.get(i).draw(g, i, 2, width, height);
        }
        // draw step
        g.setColor(Color.black);
        String number = Integer.toString(curr + 1);
        for (int i = 0; i < number.length(); i++) {
            STEP_TEXT[i + 5] = number.charAt(i);
        }
        g.setFont(STEP_FONT);
        g.drawChars(STEP_TEXT, 0, number.length() + 5, 5, 20);
    }

    private static class Disk {
        public static final int HEIGHT = 20;
        public static final int INITIAL_WIDTH = 50;
        private final int number;
        private static final Color[] COLORS = {
                new Color(0xff8e8e),
                new Color(0xecb283),
                new Color(0xd8ce77),
                new Color(0xa7c56c),
                new Color(0x72b261),
                new Color(0x569f68),
                new Color(0x4b8c78),
                new Color(0x417179),
                new Color(0x364a66),
                new Color(0x2d2c52)
        };
        private final Color color;
        private static final int WIDTH_INC = 10;
        public Disk(int number) {
            this.number = number;
            color = COLORS[number];
        }

        /**
         * Draws this rectangle
         * @param g      graphics object
         * @param num    height from bottom starting from 0
         * @param stack  number from 0 to 2, the stack of this rectangle
         * @param width  width of outer panel
         * @param height height of outer panel
         */
        public void draw(Graphics g, int num, int stack, int width, int height) {
            int x = width / 4 * (stack + 1) - (number * WIDTH_INC) - INITIAL_WIDTH / 2;
            int rectWidth = number * WIDTH_INC * 2 + INITIAL_WIDTH;
            g.setColor(color);
            //noinspection SuspiciousNameCombination
            g.fillRoundRect(x, height - num * HEIGHT - HEIGHT, rectWidth, HEIGHT, HEIGHT, HEIGHT);
        }
    }
}
