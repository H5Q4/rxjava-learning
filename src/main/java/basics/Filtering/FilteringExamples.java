package basics.Filtering;

import static common.Helper.subscribePrint;
import rx.Observable;
import rx.functions.Func1;

/**
 * Demonstrations of using filtering
 * Created by Jupittar on 2015/9/21.
 */
public class FilteringExamples {
    public static void main(String[] args) {

        Observable<Integer> numbers = Observable.just(2, 5, 5, 45, 54, 34, 53);
        subscribePrint(numbers.filter(n -> n % 2 == 0), "filter");
        subscribePrint(numbers.takeLast(4), "last 4");
        subscribePrint(numbers.last(), "last one");

        subscribePrint(numbers.takeLastBuffer(4), "last buffer");
        subscribePrint(numbers.lastOrDefault(100, n -> n % 2 == 0), "last or default");
        subscribePrint(Observable.empty().lastOrDefault(100), "last or default");
        subscribePrint(numbers.first(), "first one");
        subscribePrint(numbers.firstOrDefault(100), "first or default");

        subscribePrint(numbers.skip(4), "skip 4");
        subscribePrint(numbers.skipLast(4), "skip last 4");

        subscribePrint(numbers.elementAt(4), "at 4");

        subscribePrint(numbers.distinct(), "distinct");

    }
}
