package basics.schedulers;

import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstration of the different Schedulers types
 * Created by Jupittar on 2015/10/23.
 */
public class SchedulerTypes {

    private void schedule(Scheduler scheduler, int numOfSubTasks, boolean onTheSameWorker) {
        ArrayList<Integer> list = new ArrayList<>(0);
        AtomicInteger current = new AtomicInteger(0);

        Random random = new Random();
        Scheduler.Worker worker = scheduler.createWorker();

        Action0 addWork = () -> {
          synchronized (list) {
              System.out.println(" Add: " + Thread.currentThread().getName() + " " + current.get());
              list.add(random.nextInt(current.get()));
              System.out.println(" End Add: " + Thread.currentThread().getName() + " " + current.get());
          }
        };
        Action0 removeWork = () -> {
          synchronized (list) {
              if (!list.isEmpty()) {
                  System.out.println(" Remove: " + Thread.currentThread().getName());
                  list.remove(0);
                  System.out.println(" End Remove: " + Thread.currentThread().getName());
              }
          }
        };
        Action0 work = () -> {
            System.out.println(Thread.currentThread().getName());

            for (int i = 1; i <= numOfSubTasks; i++) {
                current.set(i);

                System.out.println("Begin Add!");
                if (onTheSameWorker) {
                    worker.schedule(addWork);
                } else {
                    scheduler.createWorker().schedule(addWork);
                }
                System.out.println("End Add!");
            }

            while (!list.isEmpty()) {
                System.out.println("Begin Remove!");
                if (onTheSameWorker) {
                    worker.schedule(removeWork);
                } else {
                    scheduler.createWorker().schedule(removeWork);
                }
                System.out.println("End Remove");
            }
        };

        worker.schedule(work);
    }

    public static void main(String[] args) {
        SchedulerTypes schedulerTypes = new SchedulerTypes();

        System.out.println("Immediate");
        schedulerTypes.schedule(Schedulers.immediate(), 2, true);
        System.out.println("Spawn");
        schedulerTypes.schedule(Schedulers.immediate(), 2, false);
        System.out.println("----------");
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Trampoline");
        schedulerTypes.schedule(Schedulers.trampoline(), 2, true);
        System.out.println("Spawn");
        schedulerTypes.schedule(Schedulers.trampoline(), 2, false);
        System.out.println("----------");
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("New Thread");
        schedulerTypes.schedule(Schedulers.newThread(), 2, true);
        System.out.println("Spawn");
        schedulerTypes.schedule(Schedulers.newThread(), 2, true);
        System.out.println("----------");
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Computation thread");
        schedulerTypes.schedule(Schedulers.computation(), 5, true);
        try { Thread.sleep(500L); } catch (InterruptedException e) {}

        System.out.println("------");

        System.out.println("Spawn!");
        schedulerTypes.schedule(Schedulers.computation(), 5, false);
        try { Thread.sleep(500L); } catch (InterruptedException e) {}

        System.out.println("------");

        System.out.println("IO thread");
        schedulerTypes.schedule(Schedulers.io(), 2, true);
        try { Thread.sleep(500L); } catch (InterruptedException e) {}

        System.out.println("------");

        System.out.println("Spawn!");
        schedulerTypes.schedule(Schedulers.io(), 2, false);
        try { Thread.sleep(500L); } catch (InterruptedException e) {}

    }
}
