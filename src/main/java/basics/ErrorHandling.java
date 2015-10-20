package basics;

import static common.Helper.subscribePrint;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Demonstration of handling errors
 * Created by Jupittar on 2015/10/19.
 */
public class ErrorHandling {
    public static void main(String[] args) {
        Observable<String> numbers = Observable.just("1", "2", "three", "4");

        Observable<Integer> errorReturn = numbers
            .map(Integer::parseInt)
            .onErrorReturn(e -> -1);
        subscribePrint(errorReturn, "onErrorReturn");

        Observable<Integer> defaultOnError = Observable.just(6, 7, 8);
        Observable<Integer> exceptionResumeNext = numbers
            .map(Integer::parseInt)
            .onExceptionResumeNext(defaultOnError);
        subscribePrint(exceptionResumeNext, "Exception resume");

        Observable<Integer> errorResumeNext = numbers
            .doOnNext(number -> {
                assert !number.equals("three");
            })
            .map(Integer::parseInt)
            .onErrorResumeNext(defaultOnError);
        subscribePrint(errorResumeNext, "error resume next");

        errorResumeNext = numbers
            .doOnNext(number -> {
                assert !number.equals("three");
            })
            .map(Integer::parseInt)
            .onExceptionResumeNext(defaultOnError);
        subscribePrint(errorResumeNext, "ex resume next");

        subscribePrint(Observable.create(new ErrorEmitter()).retry(), "retry");

        Observable<Integer> when = Observable.create(new ErrorEmitter())
            .retryWhen(observable ->
                    observable.flatMap(error -> {
                        if (error instanceof FooException) {
                            System.err.println("Delaying...");
                            return Observable.timer(1L, TimeUnit.SECONDS, Schedulers.immediate());
                        }
                        return Observable.error(error);
                    })
            )
            .retry((attempts, error) -> (error instanceof BooException) && attempts < 3);
        subscribePrint(when, "retry when");
    }
}

class ErrorEmitter implements Observable.OnSubscribe<Integer> {

    private int throwAnErrorCounter = 5;

    @Override
    public void call(Subscriber<? super Integer> subscriber) {
        subscriber.onNext(1);
        subscriber.onNext(2);
        if (throwAnErrorCounter > 4) {
            throwAnErrorCounter --;
            subscriber.onError(new FooException());
            return;
        }
        if (throwAnErrorCounter > 0) {
            throwAnErrorCounter --;
            subscriber.onError(new BooException());
            return;
        }
        subscriber.onNext(3);
        subscriber.onNext(4);
        subscriber.onCompleted();
    }
}

class FooException extends RuntimeException {
    public FooException() {
        super("Foo");
    }
}

class BooException extends RuntimeException {
    public BooException() {
        super("Boo");
    }
}
