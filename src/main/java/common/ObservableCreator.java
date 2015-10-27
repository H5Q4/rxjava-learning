package common;

import com.google.gson.Gson;
import org.apache.http.nio.client.HttpAsyncClient;
import rx.Observable;
import rx.Subscriber;
import rx.apache.http.ObservableHttp;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Some methods for Helping to create Observables
 * Created by hq on 15-9-17.
 */
public class ObservableCreator {

    public static final Path CACHE_DIR = Paths.get("src", "main", "resources", "cache");

    private static Map<String, Set<Map<String, Object>>> cache = new ConcurrentHashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Observable<Map> requestJSON(HttpAsyncClient client, String url) {
        Observable<String> rawResponse = ObservableHttp
            .createGet(url, client)
            .toObservable()
            .flatMap(resp -> resp
                .getContent()
                .map(bytes -> new String(bytes, StandardCharsets.UTF_8)))
            .retry(5)
            .cast(String.class)
            .map(String::trim)
            .doOnNext(resp -> getCache(url).clear());

        Observable<String> objects = rawResponse
            .filter(data -> data.startsWith("{"))
            .map(data -> "[" + data + "]");

        Observable<String> arrays = rawResponse
            .filter(data -> data.startsWith("["));

        Observable response = arrays
            .ambWith(objects)
            .map(data -> new Gson().fromJson(data, List.class))
            .flatMapIterable(list -> list)
            .cast(Map.class)
            .doOnNext(json -> getCache(url).add((Map<String, Object>) json));

        return Observable.amb(fromCache(url), response);
    }

    public static Observable<Map<String, Object>> fromCache(String url) {
        return Observable.from(getCache(url))
            .defaultIfEmpty(null)
            .flatMap(json -> (json == null) ? Observable.never() : Observable.just(json))
            .doOnNext(json -> json.put("json_cached", true));
    }

    private static Set<Map<String, Object>> getCache(String url) {
        if (!cache.containsKey(url)) {
            cache.put(url, new HashSet<>());
        }
        return cache.get(url);
    }

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
