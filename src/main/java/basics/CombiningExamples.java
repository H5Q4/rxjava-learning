package basics;

import common.Helper;
import rx.Observable;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static common.Helper.blockingSubscribePrint;
import static common.Helper.subscribePrint;

/**
 * Demonstrations of combining
 * Created by Jupittar on 2015/10/13.
 */
public class CombiningExamples {
    public static void main(String[] args) {
        Observable<Integer> zip = Observable.zip(Observable.just(1, 3, 4), Observable.just(5, 7, 4), (m, n) -> m + n);
        subscribePrint(zip, "simple zip");

        Observable<String> timedZip = Observable.zip(Observable.from(Arrays.asList("s", "c", "o", "t", "t")),
            Observable.interval(300L, TimeUnit.MILLISECONDS), (ch, i) -> ch);
        blockingSubscribePrint(timedZip, "timed zip");

        Observable<String> zipWith = Observable.just("a", "b", "c")
            .zipWith(Observable.just(2, 3), (m, n) -> n + m);
        subscribePrint(zipWith, "zip with");

        Observable<String> greetingsObservable = Observable.just("Hi", "Hello", "Howdy", "Yo", "Good to see ya")
            .zipWith(Observable.interval(1L, TimeUnit.SECONDS), (m, n) -> m);
        Observable<String> namesObservable = Observable.just("Meddle", "Tanya", "Dali", "Joshua")
            .zipWith(Observable.interval(1500L, TimeUnit.MILLISECONDS), (m, n) -> m);
        Observable<String> punctuationObservable = Observable.just(".", "?", "!", "!!!", "...")
            .zipWith(Observable.interval(1100L, TimeUnit.MILLISECONDS), (m, n) -> m);

        Observable<String> combinedObservable = Observable.combineLatest(greetingsObservable, namesObservable,
            punctuationObservable, (greeting, name, punctuation) -> greeting + " " + name + punctuation);
        blockingSubscribePrint(combinedObservable, "combined");

        Observable<String> mergedObservable = Observable.merge(greetingsObservable, namesObservable,
            punctuationObservable);
        blockingSubscribePrint(mergedObservable, "words");

        Observable<String> concatedObservable = Observable.concat(greetingsObservable, namesObservable,
            punctuationObservable);
        blockingSubscribePrint(concatedObservable, "concat");

        Observable<String> startWith = punctuationObservable.startWith(namesObservable)
            .startWith(greetingsObservable);
        blockingSubscribePrint(startWith, "start with");

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
