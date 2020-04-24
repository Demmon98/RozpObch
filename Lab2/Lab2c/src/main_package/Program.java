package main_package;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class Program{
    static volatile ArrayList<Fighter> fighters = new ArrayList<>();

    public static void main(String[] args) {
        Random random = new Random();
        final int n = 500;
        for (int i = 0; i < n; i++)
            fighters.add(new Fighter(i, random.nextInt(10000)));
        fighters.add(new Fighter(150,100000));

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Challenge challenge = new Challenge(0,fighters.size()-1);
        int winner_index = forkJoinPool.invoke(challenge);

        System.out.println("Побелитель: " + winner_index);
        System.out.println("Побелитель: " + fighters.get(winner_index).id);
    }
}
