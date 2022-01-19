package com.company

import javax.swing.JPanel
import java.awt.*

/**
 * Underlying representation of hanoi configuration using 3 stacks
 * @param n number of disks
 */
class HanoiPanel(n: Int) : JPanel() {
    /**
     * Number of disks
     */
    private val nDisks: Int
    private var stackA: ArrayList<Int>
    private val stackB = ArrayList<Int>()
    private val stackC = ArrayList<Int>()
    private val solver: TowerOfHanoi
    private var curr = 0

    init {
        background = Color.white
        stackA = ArrayList(n)
        nDisks = n
        solver = TowerOfHanoi(n)
        reset()
    }

    /**
     * Whether there are no more steps
     * @return true if there are no more steps
     */
    fun end(): Boolean {
        return curr == solver.size - 1
    }

    /**
     * Change the panel to show the nth step
     * @param num the step to show, 1 being the initial configuration
     */
    fun moveTo(num: Int) {
        if (num == 1) {
            reset()
        } else {
            while (curr + 2 != num) {
                if (curr + 2 > num) {
                    prev()
                } else {
                    next()
                }
            }
        }
        repaint()
    }

    /**
     * Go to next step
     */
    private operator fun next() {
        if (curr == solver.size - 1) return
        val step = solver.getStep(++curr)!!
        val curr = when (step.from) {
            'A' -> stackA.removeAt(stackA.size - 1)
            'B' -> stackB.removeAt(stackB.size - 1)
            'C' -> stackC.removeAt(stackC.size - 1)
            else -> throw IllegalStateException("Unexpected value: " + step.from)
        }
        when (step.to) {
            'A' -> stackA.add(curr)
            'B' -> stackB.add(curr)
            'C' -> stackC.add(curr)
            else -> throw IllegalStateException("Unexpected value: " + step.to)
        }
    }

    /**
     * Go to previous step
     */
    private fun prev() {
        if (curr < 0) return
        val step = solver.getStep(curr--)!!
        val curr = when (step.to) {
            'A' -> stackA.removeAt(stackA.size - 1)
            'B' -> stackB.removeAt(stackB.size - 1)
            'C' -> stackC.removeAt(stackC.size - 1)
            else -> throw IllegalStateException("Unexpected value: " + step.to)
        }
        when (step.from) {
            'A' -> stackA.add(curr)
            'B' -> stackB.add(curr)
            'C' -> stackC.add(curr)
            else -> throw IllegalStateException("Unexpected value: " + step.from)
        }
    }

    /**
     * Resets the disks to the starting position
     */
    fun reset() {
        curr = -1
        // fill stackA with decreasing numbers
        stackA = ArrayList(generateSequence(nDisks - 1){it - 1}.take(nDisks).toList())
        stackB.clear()
        stackC.clear()
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        // turn on antialiasing
        (g as Graphics2D).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        val width = width
        val height = height
        // draw poles
        g.setColor(POLE_COLOR)
        run {
            var i = 0
            var j = width / 4 - 2
            while (i < 3) {
                g.fillRoundRect(j, height - D_HEIGHT * (MAX + 1), 5, D_HEIGHT * (MAX + 2), 5, 5)
                i++
                j += width / 4
            }
        }
        // draw disks
        for (i in stackA.indices) {
            drawDisk(g, i, 0, width, height, stackA[i])
        }
        for (i in stackB.indices) {
            drawDisk(g, i, 1, width, height, stackB[i])
        }
        for (i in stackC.indices) {
            drawDisk(g, i, 2, width, height, stackC[i])
        }
        // draw step
        g.setColor(Color.black)
        g.setFont(STEP_FONT)
        g.drawString("Step " + (curr + 1), 5, 20)
    }

    /**
     * Draws a disk
     * @param g      graphics object
     * @param num    height from bottom starting from 0
     * @param stack  number from 0 to 2, the stack this disk is on
     * @param width  width of outer panel
     * @param height height of outer panel
     * @param diskNo the disk number, starting with 0 for the smallest
     */
    private fun drawDisk(g: Graphics, num: Int, stack: Int, width: Int, height: Int, diskNo: Int) {
        val x = width / 4 * (stack + 1) - diskNo * D_WIDTH_INC - INITIAL_WIDTH / 2
        val rectWidth = diskNo * D_WIDTH_INC * 2 + INITIAL_WIDTH
        g.color = D_COLORS[diskNo]
        g.fillRoundRect(x, height - num * D_HEIGHT - D_HEIGHT, rectWidth, D_HEIGHT, D_HEIGHT, D_HEIGHT)
    }

    companion object {
        /**
         * Max number of disks, so poles are tall enough
         */
        private const val MAX = 10

        /**
         * Font for step text
         */
        private val STEP_FONT = Font("Monospace", Font.PLAIN, 15)

        /**
         * Height of disk
         */
        const val D_HEIGHT = 20

        /**
         * Width of smallest disk
         */
        const val INITIAL_WIDTH = 50

        /**
         * How much the width increases with each successive disk
         */
        private const val D_WIDTH_INC = 10

        /**
         * Color of poles
         */
        private val POLE_COLOR = Color(0x594700)

        /**
         * Color of disks
         */
        private val D_COLORS = arrayOf(
                Color(0xff8e8e),
                Color(0xecb283),
                Color(0xd8ce77),
                Color(0xa7c56c),
                Color(0x72b261),
                Color(0x569f68),
                Color(0x4b8c78),
                Color(0x417179),
                Color(0x364a66),
                Color(0x2d2c52)
        )
    }
}