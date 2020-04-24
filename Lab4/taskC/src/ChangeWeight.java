

import java.util.Random;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class ChangeWeight implements Runnable {
    @Override
    public void run() {
        int src;
        int dest;
        Random random = new Random();
        while (!currentThread().isInterrupted()) {
            Main.rwl.writeLock().lock();
            System.out.println("Начало изменения цены");
            src = random.nextInt(Main.cities.size());
            do {
                dest = random.nextInt(Main.cities.size());
            } while (dest == src);

            Main.cities.get(src).changeWeight(Main.cities.get(dest), random.nextInt(50));
            System.out.println("Конец изменения цены");
            Main.rwl.writeLock().unlock();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}
