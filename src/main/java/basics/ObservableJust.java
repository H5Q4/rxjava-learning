package basics;

import rx.Observable;

/**
 * demonstrates how to create Observables using Observable.just()
 */
public class ObservableJust {
    public static void main(String[] args) {
        //a sequence of letters
        Observable
            .just("A", "B", "C")
            .subscribe(System.out::println, System.err::println, System.out::println);

        Observable
            .just(new User("Tom", "Hanks"))
            .map(user -> user.getForename() + " " + user.getLastname())
            .subscribe(System.out::println);
    }

    public static class User {
        private String forename;
        private String lastname;

        public User(String forename, String lastname) {
            this.forename = forename;
            this.lastname = lastname;
        }

        public String getForename() {
            return forename;
        }

        public void setForename(String forename) {
            this.forename = forename;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }
    }

}
