package basics.creating;

import common.Helper;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


/**
 * Demonstrates how to create Observables using Observable.create().
 * Created by Jupittar on 2015/9/11.
 */
public class ObservableCreate {
    public static <T> Observable<T> fromIterable(final Iterable<T> iterable) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    for (T t : iterable) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(t);
                        }
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }


    public static void main(String[] args) {
        Helper.subscribePrint(fromIterable(Arrays.asList(1, 2, 3)), "IntegerList");
        Helper.subscribePrint(fromIterable(Arrays.asList("a", "b", "c")), "StringList");

        try {
            Path path = Paths.get("src", "main", "resources", "lorem_big.txt");
            List<String> data = Files.readAllLines(path);
            Observable<String> observable = fromIterable(data).subscribeOn(
                Schedulers.computation());

            Subscription subscription = Helper.subscribePrint(observable, "File");
            System.out.println("Before unsubscribe!");
            System.out.println("-------------------");

            Thread.sleep(1L);
            subscription.unsubscribe();

            System.out.println("-------------------");
            System.out.println("After unsubscribe!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
