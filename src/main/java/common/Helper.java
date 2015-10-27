package common;

import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class Helper {

    public static <T> Action1<Notification<? super T>> debug(String description, String offset) {
        AtomicReference<String> nextOffset = new AtomicReference<>(">");
        return new Action1<Notification<? super T>>() {
            @Override
            public void call(Notification<? super T> notification) {
                switch (notification.getKind()) {
                    case OnNext:
                        System.out.println(
                            Thread.currentThread().getName() + "|" +
                                description + ": " + offset + nextOffset.get() + notification.getValue()
                        );
                        break;
                    case OnError:
                        System.out.println(
                            Thread.currentThread().getName() + "|" +
                                description + ": " + offset + nextOffset.get() + notification.getThrowable()
                        );
                        break;
                    case OnCompleted:
                        System.out.println(
                            Thread.currentThread().getName() + "|" +
                                description + ": " + offset + nextOffset.get() + "|"
                        );
                        break;
                    default:break;
                }
                nextOffset.getAndUpdate(new UnaryOperator<String>() {
                    @Override
                    public String apply(String s) {
                        return " - " + s;
                    }
                });
            }
        };
    }

    public static <T> Action1<Notification<? super T>> debug(String description) {

        return debug(description, "");
    }

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
     *
     * @param observable
     * @param name
     * @param <T>
     */
    public static <T> void blockingSubscribePrint(Observable<T> observable, String name) {
        CountDownLatch latch = new CountDownLatch(1);
        subscribePrint(observable.finallyDo(latch::countDown), name);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
