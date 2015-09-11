package basics.creating;

import rx.Observable;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * demonstrates how to create Obsservables using Observable.from()
 */
public class ObservableFrom {

    public static void main(String[] args) {
        //from list
        List<String> list = Arrays.asList("white", "blank", "pink", "yellow", "red", "green", "blue");
        Observable<String> listObservable = Observable.from(list);
        listObservable.subscribe(System.out::println);
        listObservable.subscribe(color -> System.out.print(color + " | "),
                e -> System.err.println(e.getMessage()),
                System.out::println);
        listObservable.subscribe(color -> System.out.print(color + " / "),
                e -> System.err.println(e.getMessage()),
                System.out::println);

        //from Iterable
        Path path = Paths.get("src", "main", "java");
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
            Observable<Path> dirObservable = Observable.from(dirStream);
            dirObservable.subscribe(System.out::println);
            dirObservable.subscribe(System.out::println, System.err::println, System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //from array
        Observable<Integer> arrayObservable = Observable.from(new Integer[]{1, 3, 5, 7, 9});
        arrayObservable.subscribe(System.out::println);

    }
}
