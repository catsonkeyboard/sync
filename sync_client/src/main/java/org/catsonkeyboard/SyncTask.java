package org.catsonkeyboard;

public class SyncTask extends Thread {

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(1000);
                System.out.println("aaa");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
