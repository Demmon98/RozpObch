package concurrency.barber;

import java.util.Random;

public class Customer extends Thread {
    private String name;
    private BarberShop shop;
    private Random r;
    private boolean wants_haircut;

    public Customer(BarberShop shop, String name) {
        this.name = name;
        this.shop = shop;
        wants_haircut = true;
        r = new Random();
    }


    public void run() {
        wasteTime();
        shop.customerReady(this);
    }

    public boolean wantsHaircut() {
        return wants_haircut;
    }

    public void wantsToLeave() {
        wants_haircut = false;
    }

    public void wasteTime() {
        try {
            this.sleep(Math.abs(r.nextInt(100000)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return name;
    }
}
