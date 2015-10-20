package basics;

import common.Helper;
import rx.Observable;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by Jupittar on 2015/10/19.
 */
public class Conditionals {
    public static void main(String[] args) {
        Observable<String> words = Observable.just("some", "other");
        Observable<Long> interval = Observable.interval(500L, TimeUnit.MILLISECONDS);
        Observable<? extends Serializable> amb = Observable.amb(words, interval);
        Helper.blockingSubscribePrint(amb, "amb 1");

        Random random = new Random();
        Observable<String> datasource1 = Observable.just("data from datasource 1")
            .delay(random.nextInt(1000), TimeUnit.MILLISECONDS);
        Observable<String> datasource2 = Observable.just("data from datasource 2")
            .delay(random.nextInt(1000), TimeUnit.MILLISECONDS);
        Helper.blockingSubscribePrint(Observable.amb(datasource1, datasource2), "amb 2");

        Observable<String> wordsObservable = Observable.just("reactive", "programming", "with", "java8")
            .zipWith(Observable.interval(200L, TimeUnit.MILLISECONDS), (x, y) -> x);
        Helper.blockingSubscribePrint(wordsObservable.takeUntil(interval), "takeUtil");
        Helper.blockingSubscribePrint(wordsObservable.takeWhile(word -> word.length() > 4), "takeWhile");
        Helper.blockingSubscribePrint(wordsObservable.skipUntil(interval), "skipUtil");

        Observable<Object> defaultIfEmpty = Observable.empty().defaultIfEmpty(5);
        Helper.subscribePrint(defaultIfEmpty, "default if empty");
    }
}
