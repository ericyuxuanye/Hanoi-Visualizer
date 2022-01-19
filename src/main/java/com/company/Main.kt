package com.company

import java.lang.Runnable
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.extras.FlatSVGIcon
import java.awt.*
import javax.swing.*

fun main() {
    SwingUtilities.invokeLater(Main())
}

/**
 * Class used for laying out the main JFrame.
 */
class Main : Runnable {
    private val controls = JPanel(GridBagLayout())

    // HanoiPanel that renders the disks
    private var hp = HanoiPanel(5)

    // JButton that plays/stops the simulation
    private val playPause = JButton(playIcon)

    // Tells us whether the simulation is currently playing
    private var play = false

    // timer to go to next step every second. Activated by play button.
    private val timer = Timer(1000) { next() }

    // slider to quickly 'slide' to the requested step
    private val slider = JSlider(1, 1 shl 5, 1)

    override fun run() {
        val f = JFrame("Hanoi Solver")
        val mb = JMenuBar()
        mb.name = "Hanoi Solver"
        val optionsMenu = JMenu("Options")
        val changeDisks = JMenuItem("Set number of Disks")
        changeDisks.accelerator = KeyStroke.getKeyStroke('N'.code, Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx)
        f.preferredSize = Dimension(1000, 500)
        f.minimumSize = Dimension(500, 400)
        optionsMenu.add(changeDisks)
        mb.add(optionsMenu)
        f.jMenuBar = mb
        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(hp, BorderLayout.CENTER)
        val reset = JButton(resetIcon)
        reset.addActionListener { hp.reset() }
        val next = JButton(nextIcon)
        next.addActionListener { next() }
        val prev = JButton(prevIcon)
        prev.addActionListener { prev() }
        reset.addActionListener { reset() }
        slider.addChangeListener { hp.moveTo(slider.value) }
        // play/pause button
        playPause.addActionListener {
            if (play) {
                playPause.icon = playIcon
                timer.stop()
            } else {
                playPause.icon = pauseIcon
                timer.start()
            }
            play = !play
        }
        // show prompt asking number of disks when menu item is invoked
        changeDisks.addActionListener(object : ActionListener {
            val DEFAULT_VALUE = 5
            private val model = SpinnerNumberModel(DEFAULT_VALUE,
                    1,
                    10,
                    1)

            // Lets the user type or click the arrows on the right to choose answer.
            private val spinner = JSpinner(model)

            // Holds the panel that the JOptionPane will show
            private val panel = JPanel()

            // initialize some stuff when object is created
            init {
                panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)
                val prompt = JLabel("Enter a number between 1 and 10.")
                prompt.alignmentX = Component.RIGHT_ALIGNMENT
                panel.add(prompt)
                // 5 pixels between prompt and spinner
                panel.add(Box.createVerticalStrut(5))
                // add spinner to panel
                panel.add(spinner)
            }

            override fun actionPerformed(e: ActionEvent) {
                val result = JOptionPane.showOptionDialog(f,
                        panel,
                        "Enter a Number",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null,
                        null)
                if (result == JOptionPane.OK_OPTION) {
                    val num = spinner.value as Int
                    mainPanel.remove(hp)
                    slider.maximum = 1 shl num
                    slider.value = 1
                    hp = HanoiPanel(num)
                    mainPanel.add(hp, BorderLayout.CENTER)
                    mainPanel.revalidate()
                    mainPanel.repaint()
                }
            }
        })
        // gridbaglayout does make things a little hard to read...
        val gbc = GridBagConstraints()
        gbc.insets = Insets(2, 3, 2, 3)
        gbc.weightx = 1.0
        gbc.gridwidth = 4
        gbc.fill = GridBagConstraints.HORIZONTAL
        controls.add(slider, gbc)
        gbc.fill = GridBagConstraints.NONE
        gbc.gridwidth = 1
        gbc.gridy = 1
        gbc.weightx = 1.0
        gbc.anchor = GridBagConstraints.LINE_END
        controls.add(prev, gbc)
        gbc.weightx = 0.0
        gbc.anchor = GridBagConstraints.CENTER
        controls.add(playPause, gbc)
        controls.add(reset, gbc)
        gbc.weightx = 1.0
        gbc.anchor = GridBagConstraints.LINE_START
        controls.add(next, gbc)
        gbc.gridy = 2
        controls.add(Box.createVerticalStrut(5), gbc)
        mainPanel.add(controls, BorderLayout.SOUTH)
        f.contentPane = mainPanel
        f.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        f.pack()
        f.isVisible = true
    }

    /**
     * Go to the next step
     */
    operator fun next() {
        if (hp.end()) {
            playPause.icon = playIcon
            timer.stop()
            play = false
            return
        }
        slider.value = slider.value + 1
    }

    /**
     * Go to the previous step
     */
    private fun prev() {
        slider.value = (slider.value - 1).coerceAtLeast(1)
    }

    /**
     * Return to initial configuration
     */
    private fun reset() {
        slider.value = 1
    }

    companion object {
        // image icons for the buttons
        private val playIcon = FlatSVGIcon("play.svg", 50, 50)
        private val pauseIcon = FlatSVGIcon("pause.svg", 50, 50)
        private val resetIcon = FlatSVGIcon("reset.svg", 50, 50)
        private val nextIcon = FlatSVGIcon("next.svg", 50, 50)
        private val prevIcon = FlatSVGIcon("prev.svg", 50, 50)

        // Initialize stuff the first time this class is run
        init {
            // Set look and feel. I think FlatLightLaf looks more modern than the default java look and feel.
            if (!FlatLightLaf.setup()) System.err.println("Unable to set look and feel")
            // apple stuff
            System.setProperty("apple.laf.useScreenMenuBar", "true")
            // round buttons
            UIManager.put("Button.arc", 25)
        }
    }
}