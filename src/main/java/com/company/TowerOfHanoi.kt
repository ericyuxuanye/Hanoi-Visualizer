package com.company

/**
 * Class used for solving the steps for the Towers of Hanoi problem
 * @param n number of disks
 */
class TowerOfHanoi(n: Int) {
    /**
     * The array to store the steps to solve the problem
     */
    private val steps: Array<Step?>

    /**
     * Last filled index in the steps array
     */
    private var idx = -1

    /**
     * Number of disks to solve for
     */
    private val n: Int

    /**
     * Creates a TowerOfHanoi object
     */
    init {
        // number of steps required is 2ⁿ - 1
        steps = arrayOfNulls((1 shl n) - 1)
        this.n = n
        recursive_solve(n, 'A', 'C', 'B')
    }

    /**
     * Returns the step given an index
     * @param i index of step
     * @return the Step object
     */
    fun getStep(i: Int): Step? {
        return steps[i]
    }

    /**
     * Returns the number of steps required for the configuration
     * @return the number of steps required
     */
    val size: Int
        get() = (1 shl n) - 1

    /**
     * Uses recursion to solve the Towers of Hanoi problem
     * @param n Disk number, 1 for the smallest one
     * @param begin Initial peg
     * @param destination Target peg
     * @param extra the other peg that is not begin or destination
     */
    private fun recursive_solve(n: Int, begin: Char, destination: Char, extra: Char) {
        if (n == 1) {
            // move from beginning to destination if it is the first disk
            steps[++idx] = Step(1, begin, destination)
            return
        }
        // move the disk above it out of the way
        recursive_solve(n - 1, begin, extra, destination)
        // move this disk to the correct place
        steps[++idx] = Step(n, begin, destination)
        // move the disk above it on top of this disk
        recursive_solve(n - 1, extra, destination, begin)
    }

    override fun toString(): String {
        val sb = StringBuilder("Tower of Hanoi for $n disks:")
        for (step in steps) {
            sb.append('\n')
            sb.append(step)
        }
        return sb.toString()
    }

    /**
     * Represents a single step in solving the tower of hanoi
     */
    class Step
    /**
     * Creates a Step object
     * @param disk Disk number, 0 for the smallest
     * @param from Initial peg
     * @param to Final peg
     */(
            /**
             * Disk number, 0 for the smallest
             */
            var disk: Int,
            /**
             * Initial peg
             */
            var from: Char,
            /**
             * Final peg
             */
            var to: Char) {
        override fun toString(): String {
            return "Move disk $disk from $from to $to."
        }
    }
}