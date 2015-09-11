package basics;

import common.Helper;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.util.concurrent.TimeUnit;

/**
 * Demonstration of using Subjects
 * Created by hq on 15-9-11.
 */
public class SubjectsDemonstration {
    public static void main(String[] args) {
        Observable<Long> interval = Observable.interval(100L, TimeUnit.MILLISECONDS);
        Subject<Long, Long> publishSubject = PublishSubject.create();
        interval.subscribe(publishSubject);
        Subscription sub1 = Helper.subscribePrint(publishSubject, "First");
        Subscription sub2 = Helper.subscribePrint(publishSubject, "Second");
        Subscription sub3 = null;

        try {
            Thread.sleep(300L);
            publishSubject.onNext(555L);
            sub3 = Helper.subscribePrint(publishSubject, "Third");
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sub1.unsubscribe();
        sub2.unsubscribe();
        if (sub3 != null) {
            sub3.unsubscribe();
        }

        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Subscription sub4 = Helper.subscribePrint(publishSubject, "Fourth");

        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sub4.unsubscribe();
    }
}
