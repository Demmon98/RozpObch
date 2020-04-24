
import java.util.Random;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class AddRemoveEdge implements Runnable {
    @Override
    public void run() {
        Random random = new Random();
        int src;
        int dest;

        while (!Thread.currentThread().isInterrupted()) {
            Main.rwl.writeLock().lock();
            System.out.println("Начало добавления нового города");
            src = random.nextInt(Main.cities.size());
            do {
                dest = random.nextInt(Main.cities.size());
            } while (dest == src);

            Main.cities.get(src).addEdge(Main.cities.get(dest), random.nextInt(50));
            System.out.println("Конец добавления нового города");
            Main.rwl.writeLock().unlock();
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                currentThread().interrupt();
                ex.printStackTrace();
            }

            Main.rwl.writeLock().lock();
            System.out.println("Начало удаления города");
            src = random.nextInt(Main.cities.size());
            do {
                dest = random.nextInt(Main.cities.size());
            } while (dest == src);

            Main.cities.get(src).deleteEdge(Main.cities.get(dest));
            System.out.println("Конец удаления города");
            Main.rwl.writeLock().unlock();
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                currentThread().interrupt();
                ex.printStackTrace();
            }
        }
    }
}
