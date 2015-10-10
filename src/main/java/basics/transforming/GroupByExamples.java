package basics.transforming;

import common.Helper;
import rx.Observable;

import java.util.Arrays;
import java.util.List;

/**
 * Demonstrations of using Observable.groupBy()
 * Created by Jupittar on 2015/9/19.
 */
public class GroupByExamples {

    public static void main(String[] args) {
        List<String> albums = Arrays.asList(
            "The Piper at the Gates of Dawn", "A Saucerful of Secrets", "More",
            "Ummagumma", "Atom Heart Mother", "Meddle", "Obscured by Clouds",
            "The Dark Side of the Moon", "Wish You Were Here", "Animals",
            "The Wall");

        Observable
            .from(albums)
            .groupBy((album) -> album.split(" ").length)
            .subscribe(obs -> Helper.subscribePrint(obs, obs.getKey() + " word(s)"));

        Observable
            .from(albums)
            .groupBy(album -> album.replaceAll("[^mM]", "").length(),
                album -> album.replaceAll("[mM]", "*"))
            .subscribe(obs -> Helper.subscribePrint(obs, obs.getKey() + " occurences of 'm'"));
    }
}
