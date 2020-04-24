package task_b;

import javafx.application.Platform;

public class MyThread extends Thread {
    private boolean up;
    private boolean shutdown = false;

    MyThread(boolean up) {
        this.up = up;
    }

    @Override
    public void run() {
        if (Program.semaphore.get() != 0) SetStateText("Critical section is used!");
        while (Program.semaphore.get() != 0) {
        }
        Program.semaphore.set(1);
        SetStateText("Critical section is just taken");
        while (!shutdown) {
            synchronized (Program.main_slider) {
                if (up) {
                    if (Program.main_slider.getValue() < Program.main_slider.getMax())
                        Program.main_slider.setValue(Program.main_slider.getValue() + 5);
                } else {
                    if (Program.main_slider.getValue() > Program.main_slider.getMin())
                        Program.main_slider.setValue(Program.main_slider.getValue() - 5);
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
                }
            }
        }
        Program.semaphore.set(0);
        SetStateText("Critical section is free");
        System.out.println("Thread " + up + " is interrupted");
    }

    private void SetStateText(String text) {
        Platform.runLater(() -> Program.semaphore_state.setText(text));
    }

    public void MyInterrupt() {
        shutdown = true;
    }
}