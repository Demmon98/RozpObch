package concurrency.barber;

import java.util.concurrent.Semaphore;

public class BarberShop {
    private Semaphore barber_chair;
    private Semaphore chairs; 		
    private Semaphore barber;		

    private int waiting_customers;
    private final int number_of_chairs;

    private static final int HAIRCUT_TIME = 1000;

    public BarberShop(int number_of_chairs) {
        barber_chair = new Semaphore(1, true);
        chairs = new Semaphore(0, true);
        barber = new Semaphore(0, true);

        this.number_of_chairs = number_of_chairs;
        waiting_customers = 0;
        barberReady();
    }

    private void barberReady() {
        System.out.println("Парикмахер готов делать стрижку");
        barber.release();

        chairs.release();
        System.out.println("Доступные места =" +chairs.availablePermits() +",barber_chair=" +barber_chair.availablePermits());
    }

    public void customerReady(Customer c) {
        System.out.println(c + " клиент готов");
        if(barber_chair.availablePermits() <= 0)
            customerSitDown(c);

        if(c.wantsHaircut()) {
            try {
                barber_chair.acquire();
                haircut(c);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void customerSitDown(Customer c) {
        if(waiting_customers < number_of_chairs) {
            try {
                waiting_customers++;
                System.out.println(c + " сидит в зале ожидания. Всего " + waiting_customers + " клиентов ждут");

                chairs.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(c + " уходит, потому что парихмахерская полна");
            c.wantsToLeave();
        }
    }

    public void haircut(Customer c) {
        if(waiting_customers > 0)
            waiting_customers--;

        try {
            barber.acquire();
            System.out.println(c + " стрижеться");

            Thread.sleep(HAIRCUT_TIME);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        System.out.println(c + " подстрижен, платит парикмахеру и уходит");
        barber_chair.release();
        barberReady();
    }

    public static void main(String[] args) throws InterruptedException {
        int customer_number = 100;
        BarberShop sh = new BarberShop(5);
        Thread[] cust = new Thread[customer_number];

        for(int i=0; i<customer_number; i++)
            cust[i] = new Customer(sh, "" + i);

        for(int i=0; i<customer_number ; i++)
            cust[i].start();
    }
}
