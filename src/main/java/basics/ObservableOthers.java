package basics;

import common.Helper;
import rx.Observable;

import java.util.concurrent.TimeUnit;

/**
 * demonstrates how to create observables from other factory methods
 */
public class ObservableOthers {
    public static void main(String[] args) {
        Helper.subscribePrint(Observable.interval(500L, TimeUnit.MILLISECONDS), "Interval Observable");
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
