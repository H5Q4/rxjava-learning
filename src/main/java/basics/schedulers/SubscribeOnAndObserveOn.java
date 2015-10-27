package basics.schedulers;

import common.Helper;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;

/**
 * Demonstrates using SubscribeOn and ObserveOn
 *
 * Created by Jupittar on 2015/10/23.
 */
public class SubscribeOnAndObserveOn {
    public static void main(String[] args) throws InterruptedException {
        Observable
            .range(0, 4)
            .doOnEach(Helper.debug("blocking source"))
            .subscribe();

        System.out.println("blocking");

        System.out.println("----------");

        CountDownLatch latch1 = new CountDownLatch(1);

        Observable
            .range(66, 4)
            .flatMap(n -> Observable
                .range(n, 4)
                .subscribeOn(Schedulers.computation())
                .doOnEach(Helper.debug("parallelism"))
            )
            .subscribe();


        Observable
            .range(5, 4)
            .subscribeOn(Schedulers.computation())
            .doOnEach(Helper.debug("non-blocking source"))
            .finallyDo(new Action0() {
                @Override
                public void call() {
                    latch1.countDown();
                    System.out.println("----------");
                }
            })
            .subscribe();

        System.out.println("non-blocking");

        latch1.await();

        CountDownLatch latch2 = new CountDownLatch(1);
        Observable
            .range(10, 4)
            .doOnEach(Helper.debug("source"))
            .subscribeOn(Schedulers.computation())
            .map(new Func1<Integer, Integer>() {
                @Override
                public Integer call(Integer num) {
                    return num + 48;
                }
            })
            .map(new Func1<Integer, char[]>() {
                @Override
                public char[] call(Integer integer) {
                    return Character.toChars(integer);
                }
            })
            .subscribeOn(Schedulers.io())
            .map(new Func1<char[], Character>() {
                @Override
                public Character call(char[] chars) {
                    return chars[0];
                }
            })
            .subscribeOn(Schedulers.newThread())
            .doOnEach(Helper.debug("char ", " "))
            .finallyDo(new Action0() {
                @Override
                public void call() {
                    latch2.countDown();
                }
            })
            .subscribe();

        latch2.await();

        CountDownLatch latch3 = new CountDownLatch(1);
        Observable
            .range(20, 4)
            .observeOn(Schedulers.newThread())
            .map(n -> n + 48)
            .doOnEach(Helper.debug("+48", "   "))
            .observeOn(Schedulers.computation())
            .map(m -> Character.toChars(m))
            .map(c -> c[0])
            .doOnEach(Helper.debug("chars: ", "   "))
            .finallyDo(() -> latch3.countDown())
            .subscribe();
        System.out.println("observeOn");

        latch3.await();



    }
}
