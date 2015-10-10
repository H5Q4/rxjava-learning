package basics;

import common.Helper;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * "Reactive sum" using BehaviorSubjects
 * Created by Jupittar on 2015/9/13.
 */
public class ReactiveSum {

    private BehaviorSubject<Double> a = BehaviorSubject.create(0.0);
    private BehaviorSubject<Double> b = BehaviorSubject.create(0.0);
    private BehaviorSubject<Double> sum = BehaviorSubject.create(0.0);

    public Double getA() {
        return a.getValue();
    }

    public void setA(Double a) {
        this.a.onNext(a);
    }

    public Observable<Double> obsA() {
        return a.asObservable();
    }

    public Double getB() {
        return b.getValue();
    }

    public void setB(Double b) {
        this.b.onNext(b);
    }

    public Observable<Double> obsB() {
        return b.asObservable();
    }

    public Double getSum() {
        return sum.getValue();
    }

    public Observable<Double> obsSum() {
        return sum.asObservable();
    }

    public ReactiveSum() {
        Observable.combineLatest(a, b, (x, y) -> x + y).subscribe(sum);
    }

    public static void main(String[] args) {
        ReactiveSum reactiveSum = new ReactiveSum();
        Helper.subscribePrint(reactiveSum.obsSum(), "sum");
        reactiveSum.setA(3.0);
        reactiveSum.setB(4.0);
    }
}
