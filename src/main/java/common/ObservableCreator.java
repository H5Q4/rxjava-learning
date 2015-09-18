package common;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Some methods for Helping to create Observables
 * Created by hq on 15-9-17.
 */
public class ObservableCreator {

    public static Observable<String> from(Path path) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    BufferedReader reader = Files.newBufferedReader(path);
                    subscriber.add(Subscriptions.create(new Action0() {
                        @Override
                        public void call() {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }));
                    String line = null;
                    while ((line = reader.readLine()) != null && !subscriber.isUnsubscribed()) {
                        subscriber.onNext(line);
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (IOException e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

    public static Observable<Path> listFolder(Path dir, String glob) {
        return Observable.create(new Observable.OnSubscribe<Path>() {
            @Override
            public void call(Subscriber<? super Path> subscriber) {
                try {
                    DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir, glob);
                    subscriber.add(Subscriptions.create(new Action0() {
                        @Override
                        public void call() {
                            try {
                                directoryStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }));
                    Observable.from(directoryStream).subscribe(subscriber);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }



}
