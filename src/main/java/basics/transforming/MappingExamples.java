package basics.transforming;

import common.Helper;
import common.ObservableCreator;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Demonstration of using map, flatMap
 * Created by hq on 15-9-15.
 */
public class MappingExamples {
    public static void main(String[] args) {
        Observable<String> mapped = Observable
            .just(2, 3, 5)
            .map(new Func1<Integer, Integer>() {
                @Override
                public Integer call(Integer integer) {
                    return integer * 3;
                }
            })
            .map(new Func1<Integer, String>() {
                @Override
                public String call(Integer integer) {
                    return (integer % 2 == 0) ? "even" : "odd";
                }
            });
        Helper.subscribePrint(mapped, "map");

        System.out.println("-------------------------");

        Observable<Object> flatMapped = Observable
            .just(1, 3, 5)
            .flatMap(new Func1<Integer, Observable<?>>() {
                @Override
                public Observable<?> call(Integer integer) {
                    return Observable.range(integer, integer);
                }
            });
        Helper.subscribePrint(flatMapped, "flaMap");

        System.out.println("-------------------------");

        flatMapped = Observable
            .just(-1, 0, 1)
            .map(new Func1<Integer, Integer>() {
                @Override
                public Integer call(Integer integer) {
                    return 2 / integer;
                }
            })
            .flatMap(new Func1<Integer, Observable<Integer>>() {
                @Override
                public Observable<Integer> call(Integer integer) {
                    return Observable.just(integer);
                }
            }, new Func1<Throwable, Observable<Integer>>() {
                @Override
                public Observable<Integer> call(Throwable throwable) {
                    return Observable.just(0);
                }
            }, new Func0<Observable<?>>() {
                @Override
                public Observable<?> call() {
                    return Observable.just(42);
                }
            });

        Helper.subscribePrint(flatMapped, "flatMap");

        System.out.println("-----------------------------");

        flatMapped = Observable
            .just(5, 432)
            .flatMap(v -> Observable.range(v, 2), (x, y) -> x + y);
        Helper.subscribePrint(flatMapped, "flatMap");
        System.out.println("-----------------------------");

        Observable<String> fsObs = ObservableCreator
            .listFolder(Paths.get("src", "main", "resources"), "{lorem.txt,letters.txt}")
            .flatMap(new Func1<Path, Observable<String>>() {
                @Override
                public Observable<String> call(Path path) {
                    return ObservableCreator.from(path);
                }
            }, new Func2<Path, String, String>() {
                @Override
                public String call(Path path, String s) {
                    return path.getFileName() + " : " + s;
                }
            });
        Helper.subscribePrint(fsObs, "FS");
        System.out.println("----------------------------");

        Observable<? extends Serializable> fMapped = Observable
            .just(Arrays.asList(2, 4), Arrays.asList("two", "four"))
            .flatMap(Observable::from);
        Helper.subscribePrint(fMapped, "flatMap");

        Observable<?> fIterableMapped = Observable.just(Arrays.asList(2, 4),
            Arrays.asList("two", "four"), Arrays.asList('t', 'f'),
            Arrays.asList(true, false)).flatMapIterable(l -> l);

        Helper.subscribePrint(fIterableMapped, "flatMapIterable");
        System.out.println("-----------------");

        Observable<Object> obs = Observable
            .interval(40L, TimeUnit.MILLISECONDS).switchMap(
                v -> Observable.interval(0L, 10L, TimeUnit.MILLISECONDS)
                    .map(u -> "Observable <" + (v + 1) + "> : " + (v + u)));

        Subscription sub = Helper.subscribePrint(obs, "switchMap");

        try {
            Thread.sleep(400L);
        } catch (InterruptedException e) {
        }
        sub.unsubscribe();

        System.out.println("-----------------");
    }
}




