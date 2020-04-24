package main_package;

import java.util.concurrent.RecursiveTask;

public class Challenge extends RecursiveTask<Integer> {
    private final int left;
    private final int right;

    Challenge(final int left, final int right) {
      this.left  = left;
      this.right = right;
    }

    @Override
    protected Integer compute() {
        System.out.println(String.format("Поток[%d,%d]", left, right));
        if (right == left) {
            return left;
        } else {
            int middle = (left + right)/2;
            Challenge ch1 = new Challenge(left, middle);
            Challenge ch2 = new Challenge(middle + 1, right);
            ch1.fork(); ch2.fork();
            int index_1 = ch1.join(), index_2 = ch2.join();
            return letsBattle(index_1, index_2);
        }
    }
    private int letsBattle(int index_1, int index_2) {
        Fighter f1 = Program.fighters.get(index_1);
        Fighter f2 = Program.fighters.get(index_2);
        int winner_index;
        if (f1.energyTsy < f2.energyTsy) {
            f2.goForward();
            winner_index = index_2;
        } else {
            f1.goForward();
            winner_index = index_1;
        }
        return winner_index;
    }
}
