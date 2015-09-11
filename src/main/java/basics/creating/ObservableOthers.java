package basics.creating;

import common.Helper;
import rx.Observable;

import java.util.concurrent.TimeUnit;

/**
 * demonstrates how to create observables from other factory methods
 */
public class ObservableOthers {
    public static void main(String[] args) {
        Helper.subscribePrint(Observable.interval(500L, TimeUnit.MILLISECONDS), "Interval Observable");
        Helper.subscribePrint(Observable.interval(500L, 250L, TimeUnit.MILLISECONDS), "Delayed interval Observable");
        Helper.subscribePrint(Observable.timer(1L, TimeUnit.SECONDS), "timer Observable");
        Helper.subscribePrint(Observable.empty(), "empty observable");
        Helper.subscribePrint(Observable.never(), "never observable");
        Helper.subscribePrint(Observable.error(new Exception("test error!")), "error observable");
        Helper.subscribePrint(Observable.range(3, 7), "range observable");

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
