package common;

import rx.Observable;
import rx.Subscription;

import java.util.concurrent.CountDownLatch;

public class Helper {
    public static <T> Subscription subscribePrint(Observable<T> observable, String name) {
        return observable.subscribe((v) -> System.out.println(name + " : " + v),
                (e) -> {
                    System.err.println("Error from " + name + " : ");
                    System.err.println(e.getMessage());
                },
                () -> System.out.println(name + " ended!"));
    }

    /**
     * Subscribes to an observable, printing all its emissions.
     * Blocks until the observable calls onCompleted or onError.
     * @param observable
     * @param name
     * @param <T>
     */
    public static<T> void blockingSubscribePrint(Observable<T> observable, String name) {
        CountDownLatch latch = new CountDownLatch(1);
        subscribePrint(observable.finallyDo(latch::countDown), name);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
