package basics.transforming;

import common.Helper;
import common.ObservableCreator;
import rx.Observable;

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
        Helper.subscribePrint(file.scan(0, (n, m) -> n + 1), "wc - 1");
    }
}
