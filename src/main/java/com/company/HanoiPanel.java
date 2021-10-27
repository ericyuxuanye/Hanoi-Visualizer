package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class HanoiPanel extends JPanel {
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
        // draw poles
        g.setColor(Color.black);
        for (int i = 0, j = getWidth() / 4; i < 3; i++, j += getWidth() / 4) {
            g.drawLine(j, 0, j, getHeight());
        }
        // draw disks
        for (int i = 0; i < stackA.size(); i++) {
            stackA.get(i).draw(g, i, 0);
        }
        for (int i = 0; i < stackB.size(); i++) {
            stackB.get(i).draw(g, i, 1);
        }
        for (int i = 0; i < stackC.size(); i++) {
            stackC.get(i).draw(g, i, 2);
        }
    }

    private class Disk {
        public static final int HEIGHT = 20;
        public static final int INITIAL_WIDTH = 50;
        private final int number;
        private static final Random rand = new Random();
        private final Color color = Color.getHSBColor(rand.nextFloat(),
                rand.nextFloat() * 0.5f + 0.5f, rand.nextFloat() * 0.5f + 0.5f);
        private static final int WIDTH_INC = 10;
        public Disk(int number) {
            this.number = number;
        }

        /**
         * Draws this rectangle
         *
         * @param g     graphics object
         * @param num   height from bottom starting from 0
         * @param stack number from 0 to 2, the stack of this rectangle
         */
        public void draw(Graphics g, int num, int stack) {
            int x = HanoiPanel.this.getWidth() / 4 * (stack + 1) - (number * WIDTH_INC) - INITIAL_WIDTH / 2;
            int width = number * WIDTH_INC * 2 + INITIAL_WIDTH;
            g.setColor(color);
            g.fillRect(x, HanoiPanel.this.getHeight() - num * HEIGHT - HEIGHT, width, HEIGHT);
        }
    }
}
