package game;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class FieldPanel extends JPanel {
    private ReentrantReadWriteLock lock;
    private CyclicBarrier barrier;
    private FieldUpdater updated;
    Thread[] workers = null;
    private volatile FieldModel field;
    private int	cellSize;
    private int	cellGap	= 1;

    private final Color[] CivilColors = { Color.WHITE, Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE };

    FieldPanel(int width, int height, int cSize) {
        setBackground(Color.GRAY);
        cellSize = cSize;
        lock = new ReentrantReadWriteLock();
        field = new FieldModel(width, height, lock);
    }

    void startSimulation(int civAmount, float density) {
        workers = null;
        updated = new FieldUpdater(this, field, lock);
        int amountOfWorkers = 4;
        barrier = new CyclicBarrier(amountOfWorkers, updated);
        field.clear();
        field.generate(civAmount, density);

        int quarterSize = field.getHeight() / 4;
        workers = new WorkerThread[amountOfWorkers];
        for(byte i = 0; i < amountOfWorkers; i++) {
            workers[i] = new WorkerThread(field, barrier, lock, quarterSize*i, quarterSize*(i+1), civAmount);
        }
        for (int i = 0; i < amountOfWorkers; i++) workers[i].start();
    }


    void stopSimulation(JButton button) {
        button.setEnabled(false);
        if (workers != null)
            for (int i = 0; i < workers.length; i++){
                workers[i].interrupt();
            }
        workers = null;
        button.setEnabled(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (field != null) {
            lock.readLock().lock();
            super.paintComponent(g);

            Insets b = getInsets();
            for (int y = 0; y < field.getHeight(); y++) {
                for (int x = 0; x < field.getWidth(); x++) {
                    byte cell = field.getCell(x, y).value;
                    g.setColor(CivilColors[cell]);
                    g.fillRect( b.left + cellGap + x * (cellSize + cellGap),
                            b.top + cellGap + y * (cellSize + cellGap), cellSize, cellSize);
                }
            }
            lock.readLock().unlock();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (field != null) {
            Insets b = getInsets();
            return new Dimension((cellSize + cellGap) * field.getWidth() + cellGap + b.left + b.right,
                    (cellSize + cellGap) * field.getHeight() + cellGap + b.top + b.bottom);
        } else
            return new Dimension(500, 500);
    }
}
