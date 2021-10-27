package com.company;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.net.URL;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * Class used for laying out the main JFrame.
 */
public class Main implements Runnable {
    private final JPanel controls = new JPanel(new GridBagLayout());
    // GridPanel that shows the interactive grid
    private HanoiPanel hp = new HanoiPanel(5);
    // JButton that plays/stops the simulation
    private final JButton playPause = new JButton(playIcon);
    // Tells us whether the simulation is currently playing
    private boolean play = false;

    private final Timer timer = new Timer(1000, e -> next());
    private final JSlider slider = new JSlider(1, 1 << 5, 1);
    // image icons for the buttons
    private static final ImageIcon playIcon;
    private static final ImageIcon pauseIcon;
    private static final ImageIcon resetIcon;
    private static final ImageIcon nextIcon;
    private static final ImageIcon prevIcon;
    // Initialize stuff the first time this class is run
    static {
        // Set look and feel. I think FlatLightLaf looks more modern than the default java look and feel.
        if (!FlatLightLaf.setup()) System.err.println("Unable to set look and feel");
        // initialize images
        URL playUrl = Main.class.getResource("/play.png");
        URL pauseUrl = Main.class.getResource("/pause.png");
        URL resetUrl = Main.class.getResource("/reset.png");
        URL prevUrl = Main.class.getResource("/prev.png");
        URL nextUrl = Main.class.getResource("/next.png");
        if (playUrl == null) {
            System.err.println("Unable to load play image");
            playIcon = null;
        } else {
            playIcon = new ImageIcon(playUrl);
        }
        if (pauseUrl == null) {
            System.err.println("Unable to load pause image");
            pauseIcon = null;
        } else {
            pauseIcon = new ImageIcon(pauseUrl);
        }
        if (resetUrl == null) {
            System.err.println("Unable to load reset image");
            resetIcon = null;
        } else {
            resetIcon = new ImageIcon(resetUrl);
        }
        if (nextUrl == null) {
            System.err.println("Unable to load next image");
            nextIcon = null;
        } else {
            nextIcon = new ImageIcon(nextUrl);
        }if (prevUrl == null) {
            System.err.println("Unable to load prev image");
            prevIcon = null;
        } else {
            prevIcon = new ImageIcon(prevUrl);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main());
    }

    public void run() {
        // apple stuff
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        JFrame f = new JFrame("Hanoi Solver");
        JMenuBar mb = new JMenuBar();
        mb.setName("Hanoi Solver");
        JMenu optionsMenu = new JMenu("Options");
        JMenuItem changeDisks = new JMenuItem("Set number of Disks");
        changeDisks.setAccelerator(
                KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        f.setPreferredSize(new Dimension(1000, 500));
        f.setMinimumSize(new Dimension(500, 370));
        optionsMenu.add(changeDisks);
        mb.add(optionsMenu);
        f.setJMenuBar(mb);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(hp, BorderLayout.CENTER);
        JButton reset = new JButton(resetIcon);
        reset.addActionListener(e -> hp.reset());
        JButton next = new JButton(nextIcon);
        next.addActionListener(e -> next());
        JButton prev = new JButton(prevIcon);
        prev.addActionListener(e -> prev());
        reset.addActionListener(e -> reset());
        slider.addChangeListener(e -> hp.moveTo(slider.getValue()));
        playPause.addActionListener(e -> {
            if (play) {
                playPause.setIcon(playIcon);
                timer.stop();
            } else {
                playPause.setIcon(pauseIcon);
                timer.start();
            }
            play = !play;
        });
        changeDisks.addActionListener(new ActionListener() {
            // Default random chance
            private static final int DEFAULT_VALUE = 3;
            private final SpinnerModel model = new SpinnerNumberModel(DEFAULT_VALUE,
                    1,
                    10,
                    1);

            // Lets the user type or click the arrows on the right to choose answer.
            private final JSpinner spinner = new JSpinner(model);
            // Holds the panel that the JOptionPane will show
            private final JPanel panel = new JPanel();

            // initialize some stuff when object is created
            {
                panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
                JLabel prompt = new JLabel("Enter a number between 1 and 10.");
                prompt.setAlignmentX(Component.RIGHT_ALIGNMENT);
                panel.add(prompt);
                // 5 pixels between prompt and spinner
                panel.add(Box.createVerticalStrut(5));
                // add spinner to panel
                panel.add(spinner);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showOptionDialog(f,
                        panel,
                        "Enter a Number",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null,
                        null);
                if (result == JOptionPane.OK_OPTION) {
                    int num = (Integer)spinner.getValue();
                    mainPanel.remove(hp);
                    slider.setMaximum(1 << num);
                    slider.setValue(1);
                    hp = new HanoiPanel(num);
                    mainPanel.add(hp, BorderLayout.CENTER);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                }
            }
        });
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 3, 0, 3);
        gbc.weightx = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        controls.add(slider, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        controls.add(prev, gbc);
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        controls.add(playPause, gbc);
        controls.add(reset, gbc);
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        controls.add(next, gbc);
        mainPanel.add(controls, BorderLayout.SOUTH);
        f.setContentPane(mainPanel);
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }
    public void next() {
        if (hp.end()) {
            playPause.setIcon(playIcon);
            timer.stop();
            play = false;
            return;
        }
        slider.setValue(slider.getValue() + 1);
    }
    public void prev() {
        slider.setValue(Math.max(1, slider.getValue() - 1));
    }

    public void reset() {
        slider.setValue(1);
    }
}

