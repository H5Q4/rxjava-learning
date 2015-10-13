package basics.transforming;

import common.Helper;
import common.ObservableCreator;
import rx.Observable;

import java.beans.Introspector;
import java.nio.file.Paths;

/**
 * Demonstrations of using scan
 * Created by Jupittar on 2015/10/10.
 */
public class ScanningExamples {
    public static void main(String[] args) {
        Observable<Integer> scan = Observable.range(1, 10).scan((n, m) -> n + m);
        Helper.subscribePrint(scan.last(), "final sum");

        Observable<String> file = ObservableCreator.from(Paths.get("src", "main", "resources", "letters.txt"));
        Helper.subscribePrint(file.scan(0, (n, m) -> n + 1).last(), "wc - 1");  //sum lines

        Observable<String> fileObl = ObservableCreator.from(Paths.get("src", "main", "resources", "operators.txt"));
        Observable<String> multiObservable = fileObl
            .flatMap(line -> Observable.from(line.split("\\.")))
            .map(String::trim)
            .map(sentence -> sentence.split(" "))
            .filter(arr -> arr.length > 0)
            .map(arr -> arr[0])
            .distinct()
            .groupBy(word -> word.contains("'"))
            .flatMap(observable -> observable.getKey() ? observable : observable.map(Introspector::decapitalize))
            .map(String::trim)
            .filter(word -> !word.isEmpty())
            .scan((current, word) -> current + " " + word)
            .last()
            .map(sentence -> sentence + ".");
        Helper.subscribePrint(multiObservable, "multi");


    }
}
