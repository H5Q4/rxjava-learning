package common;

import rx.Observable;
import rx.Subscription;

public class Helper {
    public static <T> Subscription subscribePrint(Observable<T> observable, String name) {
        return observable.subscribe((v) -> System.out.println(name + " : " + v),
                (e) -> {
                    System.err.println("Error from " + name + " : ");
                    System.err.println(e.getMessage());
                },
                () -> System.out.println(name + " ended!"));
    }
}
