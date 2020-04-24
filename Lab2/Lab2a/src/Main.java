import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[]argv) throws InterruptedException {
        final int SIZE = 10000;
        int[][]bearField = new int[SIZE][SIZE];

        int bearRow = new Random().nextInt(SIZE);
        int bearColumn = new Random().nextInt(SIZE);

        bearField[bearRow][bearColumn] = 1;
        TasksCollection tasksCollection = new TasksCollection(bearField);;

        ExecutorService threadPool = Executors.newFixedThreadPool(7);
        for(int i = 0; i < 30; i++){
            Runnable finder = new BearFinder(tasksCollection);
            threadPool.execute(finder);
        }
        Thread.sleep(1000);
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
