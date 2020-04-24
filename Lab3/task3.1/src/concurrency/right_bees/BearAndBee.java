package concurrency.right_bees;

class HoneyPot
{
    int potSize;
    int portions;

    HoneyPot(int potSize) {
        this.potSize = potSize;
    }

    void emptyPot() {
        portions = 0;
        System.out.println(Integer.toString(portions));
    }

    void addPortion() {
        portions++;
        System.out.println(Integer.toString(portions));
    }

    boolean isFull() { return potSize == portions; }

}

class Bear extends Thread {
    HoneyPot pot;

    Bear(HoneyPot pot) { this.pot = pot; }

    public void run() {
        while(true) {
            synchronized(pot) {
                while(!pot.isFull()) {
                    try {
                        pot.wait();
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                System.out.println("Медведь проснулся");
                pot.emptyPot();
                pot.notifyAll();
            }
        }

    }
}

class Bee extends Thread {

    HoneyPot pot;

    Bee(HoneyPot pot) {
        this.pot = pot;
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized(pot) {
                while(pot.isFull()) {
                    try {
                        pot.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                pot.addPortion();
                if (pot.isFull())
                    pot.notifyAll();
            }
        }

    }
}

public class BearAndBee {
    static int H = 20;
    static int N = 3;

    public static void Main(String[] args) throws InterruptedException {
        HoneyPot pot = new HoneyPot(H);
        Bear bear = new Bear(pot);
        bear.start();
        for(int i=0; i<N; i++)
        {
            Bee bee = new Bee(pot);
            bee.start();
        }

        bear.join();
    }
}