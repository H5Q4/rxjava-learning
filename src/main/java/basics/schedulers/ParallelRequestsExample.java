package basics.schedulers;

import common.ObservableCreator;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import rx.Observable;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Demonstration of parallelism
 *
 * Created by Jupittar on 2015/10/27.
 */
public class ParallelRequestsExample {
    public static void main(String[] args) {
        try(CloseableHttpAsyncClient client = HttpAsyncClients.createDefault()) {
            CountDownLatch latch = new CountDownLatch(1);
            client.start();
            Observable<Map> response = ObservableCreator.requestJSON(client, "https://api.github.com/users/meddle0x53/followers");
            response
                .map(followerJson -> followerJson.get("url"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
