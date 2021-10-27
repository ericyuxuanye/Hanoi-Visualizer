package com.company;

public class TowerOfHanoi {
    private final Step[] steps;
    private int idx;
    private final int n;

    public TowerOfHanoi(int n) {
        steps = new Step[(1 << n) - 1];
        idx = -1;
        this.n = n;
        recursive_solve(n, 'A', 'C', 'B');
    }

    public Step getStep(int i) {
        return steps[i];
    }

    public int getSize() {
        return (1 << n) - 1;
    }

    private void recursive_solve(int n, char begin, char destination, char extra) {
        if (n == 1) {
            // move from beginning to destination if it is the first disk
            steps[++idx] = new Step(1, begin, destination);
            return;
        }
        // move the thing above it out of the way
        recursive_solve(n - 1, begin, extra, destination);
        // move this thing to the correct place
        steps[++idx] = new Step(n, begin, destination);
        // move the thing above it on top of this thing
        recursive_solve(n - 1, extra, destination, begin);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Tower of Hanoi for " + n + " disks:");
        for (Step step : steps) {
            sb.append('\n');
            sb.append(step);
        }
        return sb.toString();
    }

    public static class Step {
        int disk;
        char from;
        char to;
        public Step(int disk, char from, char to) {
            this.disk = disk;
            this.from = from;
            this.to = to;
        }
        @Override
        public String toString() {
            return "Move disk " + disk + " from " + from + " to " + to + ".";
        }
    }
}
