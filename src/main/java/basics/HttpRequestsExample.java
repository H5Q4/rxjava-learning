package basics;

import com.google.gson.Gson;
import common.Helper;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import rx.Observable;
import rx.apache.http.ObservableHttp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Example of doing HTTP requests and handling responses with Observables
 * Created by Jupittar on 2015/10/20.
 */
public class HttpRequestsExample {

    private Map<String, Set<Map<String, Object>>> cache = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try(CloseableHttpAsyncClient client = HttpAsyncClients.createDefault()) {
            client.start();
            String username = "Jupittar";
            Observable<Map> userInfo = new HttpRequestsExample().getUserInfo(client, username);
            Helper.blockingSubscribePrint(userInfo.map(json -> json.get("name") + "(" + json.get("language") + ")"),
                "json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Observable<Map> getUserInfo(HttpAsyncClient client, String username) {
        if (username == null) {
            return Observable.error(new NullPointerException("Github user must not be null"));
        }
        String url = "https://api.github.com/users/" + username + "/repos";
        return requestJSON(client, url)
            .filter(json -> json.containsKey("git_url"))
            .filter(json -> json.get("fork").equals(false));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Observable<Map> requestJSON(HttpAsyncClient client, String url) {
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

    private Observable<Map<String, Object>> fromCache(String url) {
        return Observable.from(getCache(url))
            .defaultIfEmpty(null)
            .flatMap(json -> (json == null) ? Observable.never() : Observable.just(json))
            .doOnNext(json -> json.put("json_cached", true));
    }

    private Set<Map<String, Object>> getCache(String url) {
        if (!cache.containsKey(url)) {
            cache.put(url, new HashSet<>());
        }
        return cache.get(url);
    }
}
