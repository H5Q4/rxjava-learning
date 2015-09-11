package basics.creating;

import common.Helper;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;

/**
 * Demonstrates how to use ConnectableObservable
 * Created by Jupittar on 2015/9/11.
 */
public class Connectable {
    public static void main(String[] args) {
        Observable<Long> interval = Observable.interval(100L, TimeUnit.MILLISECONDS);
        ConnectableObservable<Long> published = interval.publish();
        Subscription sub1 = Helper.subscribePrint(published, "First");
        Subscription sub2 = Helper.subscribePrint(published, "Second");
        published.connect();
        Subscription sub3 = null;
        try {
            Thread.sleep(300L);
            sub3 = Helper.subscribePrint(published, "Third");
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sub1.unsubscribe();
        sub2.unsubscribe();
        if (sub3 != null) {
            sub3.unsubscribe();
        }

        System.out.println("--------------------------------------");

        Observable<Long> refCount = interval.share();
        sub1 = Helper.subscribePrint(refCount, "First");
        sub2 = Helper.subscribePrint(refCount, "Second");
        try {
            Thread.sleep(300L);
            sub3 = Helper.subscribePrint(refCount, "Third");
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sub1.unsubscribe();
        sub2.unsubscribe();
        if (sub3 != null) {
            sub3.unsubscribe();
        }
        Subscription sub4 = Helper.subscribePrint(refCount, "Four");
        try {
            Thread.sleep(300L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sub4.unsubscribe();

    }
}
