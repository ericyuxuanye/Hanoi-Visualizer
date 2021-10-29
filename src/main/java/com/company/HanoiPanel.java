package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class HanoiPanel extends JPanel {

    /**
     * Font for step text
     */
    private static final Font STEP_FONT = new Font("Monospace", Font.PLAIN, 15);

    /**
     * Height of disk
     */
    public static final int D_HEIGHT = 20;

    /**
     * Width of smallest disk
     */
    public static final int INITIAL_WIDTH = 50;

    /**
     * How much the width increases with each successive disk
     */
    private static final int D_WIDTH_INC = 10;

    /**
     * Color of poles
     */
    private static final Color POLE_COLOR = new Color(0x594700);

    /**
     * Color of disks
     */
    private static final Color[] D_COLORS = {
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

    /**
     * Number of disks
     */
    private final int nDisks;

    private ArrayList<Integer> stackA;
    private final ArrayList<Integer> stackB = new ArrayList<>();
    private final ArrayList<Integer> stackC = new ArrayList<>();

    private final TowerOfHanoi solver;

    private int curr;

    public HanoiPanel(int n) {
        super();
        setBackground(Color.white);
        stackA = new ArrayList<>(n);
        nDisks = n;
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
        int curr = switch (step.from) {
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
        int curr = switch (step.to) {
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
        // fill stackA with decreasing numbers
        stackA = new ArrayList<>(IntStream.range(0, nDisks).map(i -> nDisks - i - 1).boxed().toList());
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
            g.fillRoundRect(j, height - D_HEIGHT * 11, 5, D_HEIGHT * 12, 5, 5);
        }
        // draw disks
        for (int i = 0; i < stackA.size(); i++) {
            drawDisk(g, i, 0, width, height, stackA.get(i));
        }
        for (int i = 0; i < stackB.size(); i++) {
            drawDisk(g, i, 1, width, height, stackB.get(i));
        }
        for (int i = 0; i < stackC.size(); i++) {
            drawDisk(g, i, 2, width, height, stackC.get(i));
        }
        // draw step
        g.setColor(Color.black);
        g.setFont(STEP_FONT);
        g.drawString("Step " + (curr + 1), 5, 20);
    }

    /**
     * Draws this disk
     * @param g      graphics object
     * @param num    height from bottom starting from 0
     * @param stack  number from 0 to 2, the stack of this rectangle
     * @param width  width of outer panel
     * @param height height of outer panel
     * @param diskNo the disk number, starting with 0 for the smallest
     */
    public void drawDisk(Graphics g, int num, int stack, int width, int height, int diskNo) {
        int x = width / 4 * (stack + 1) - (diskNo * D_WIDTH_INC) - INITIAL_WIDTH / 2;
        int rectWidth = diskNo * D_WIDTH_INC * 2 + INITIAL_WIDTH;
        g.setColor(D_COLORS[diskNo]);
        //noinspection SuspiciousNameCombination
        g.fillRoundRect(x, height - num * D_HEIGHT - D_HEIGHT, rectWidth, D_HEIGHT, D_HEIGHT, D_HEIGHT);
    }
}
