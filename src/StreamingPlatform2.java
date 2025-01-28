import java.lang.management.MonitorInfo;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

class CosineSimilarityCalculator {

    public static double cosineSimilarity(Map<String, Integer> c1, Map<String, Integer> c2) {
        return cosineSimilarity(c1.values(), c2.values());
    }

    public static double cosineSimilarity(Collection<Integer> c1, Collection<Integer> c2) {
        int[] array1;
        int[] array2;
        array1 = c1.stream().mapToInt(i -> i).toArray();
        array2 = c2.stream().mapToInt(i -> i).toArray();
        double up = 0.0;
        double down1 = 0, down2 = 0;

        for (int i = 0; i < c1.size(); i++) {
            up += (array1[i] * array2[i]);
        }

        for (int i = 0; i < c1.size(); i++) {
            down1 += (array1[i] * array1[i]);
        }

        for (int i = 0; i < c1.size(); i++) {
            down2 += (array2[i] * array2[i]);
        }

        return up / (Math.sqrt(down1) * Math.sqrt(down2));
    }
}

class Movie {
    String id;
    String name;
    List<Integer> ratings;
    Map<User, Map<Movie, Integer>> userRatingMap;
    Map<String, Integer> mapaZaKorisnikRating;

    public Movie(String id, String name) {
        this.id = id;
        this.name = name;
        ratings = new ArrayList<>();
        userRatingMap = new HashMap<>();
        mapaZaKorisnikRating = new HashMap<>();
    }

    public void addUserRating(String id, int rating) {
        mapaZaKorisnikRating.putIfAbsent(id, rating);
    }

    public void addRating(int rating) {
        ratings.add(rating);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double avgRating() {
        int suma = 0;
        for (Integer rating : ratings) {
            suma += rating;
        }
        return (double) suma / ratings.size();
    }

    @Override
    public String toString() {
        return String.format("Movie ID: %s Title: %s Rating: %.2f", id, name, avgRating());
    }
}

class User {
    String id;
    String username;
    Map<User, List<Movie>> movies;

    public User(String id, String username) {
        this.id = id;
        this.username = username;
        movies = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("User ID: %s Name: %s", id, username);
    }
}

class StreamingPlatform {

    Map<String, User> mapUser;
    Map<String, Movie> mapMovie;


    public StreamingPlatform() {
        mapUser = new HashMap<>();
        mapMovie = new HashMap<>();
    }

    void addMovie(String id, String name) {
        mapMovie.putIfAbsent(id, new Movie(id, name));


    }

    void addUser(String id, String username) {
        mapUser.putIfAbsent(id, new User(id, username));

    }

    void addRating(String userId, String movieId, int rating) {

        User user = mapUser.get(userId);
        Movie movie = mapMovie.get(movieId);

        movie.addRating(rating);

        movie.userRatingMap.putIfAbsent(user, new HashMap<>());
        movie.userRatingMap.get(user).putIfAbsent(movie, rating);


        user.movies.putIfAbsent(user, new ArrayList<>());
        user.movies.get(user).add(movie);

        mapMovie.get(movieId).addUserRating(userId, rating);

    }

    void topNMovies(int n) {
        mapMovie.values().stream()
                .sorted(Comparator.comparing(Movie::avgRating).reversed())
                .limit(n)
                .forEach(System.out::println);
    }

    void favouriteMoviesForUsers(List<String> userIds) {
        for (String userId : userIds) {
            User user = mapUser.get(userId);
            if (user != null && user.movies.containsKey(user)) {
                List<Movie> ratedMovies = user.movies.get(user);

                int maxRating=-1;

                for (Movie ratedMovie : ratedMovies) {
                    Collection<Integer> ratings=ratedMovie.userRatingMap.get(user).values();
                    for (Integer rating : ratings) {
                        if(rating>maxRating){
                            maxRating=rating;
                        }
                    }
                }

                final int max=maxRating;

                List<Movie> favMovies=ratedMovies.stream()
                        .filter(x->x.userRatingMap.get(user).containsValue(max))
                        .sorted(Comparator.comparing(Movie::avgRating).reversed())
                        .collect(Collectors.toList());

                System.out.println(user);
                favMovies.forEach(System.out::println);
                System.out.println();

            }
        }
    }


    void similarUsers(String userId) {
        User currentUser=mapUser.get(userId);
        if(currentUser==null){
            System.out.println("User not found");
            return;
        }

        List<String> allMovies=new ArrayList<>(mapMovie.keySet());

        Map<String,Integer> currentUserRatings=new HashMap<>();
        for (String movieId : allMovies) {
            Movie movie=mapMovie.get(movieId);
            currentUserRatings.put(movieId,movie.mapaZaKorisnikRating.getOrDefault(userId,0));
        }

        Map<User,Double> userSimilarities=new HashMap<>();
        for (User otherUser : mapUser.values()) {
            if(!otherUser.id.equals(userId)){
                Map<String,Integer> otherUserRatings=new HashMap<>();
                for (String movieId : allMovies) {
                    Movie movie=mapMovie.get(movieId);
                    otherUserRatings.put(movieId,movie.mapaZaKorisnikRating.getOrDefault(otherUser.id,0));
                }

                double similarity=CosineSimilarityCalculator.cosineSimilarity(currentUserRatings,otherUserRatings);
                userSimilarities.put(otherUser,similarity);
            }
        }

        DecimalFormat df = new DecimalFormat("#.################");

        userSimilarities.entrySet().stream()
                .sorted(Map.Entry.<User,Double>comparingByValue().reversed())
                .forEach(x->{
                    User user=x.getKey();
                    double similarity=x.getValue();
                    System.out.printf("User ID: %s Name: %s %s%n", user.id, user.username, df.format(similarity));
                });

    }

}

public class StreamingPlatform2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StreamingPlatform sp = new StreamingPlatform();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            if (parts[0].equals("addMovie")) {
                String id = parts[1];
                String name = Arrays.stream(parts).skip(2).collect(Collectors.joining(" "));
                sp.addMovie(id, name);
            } else if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                sp.addUser(id, name);
            } else if (parts[0].equals("addRating")) {
                //String userId, String movieId, int rating
                String userId = parts[1];
                String movieId = parts[2];
                int rating = Integer.parseInt(parts[3]);
                sp.addRating(userId, movieId, rating);
            } else if (parts[0].equals("topNMovies")) {
                int n = Integer.parseInt(parts[1]);
                System.out.println("TOP " + n + " MOVIES:");
                sp.topNMovies(n);
            } else if (parts[0].equals("favouriteMoviesForUsers")) {
                List<String> users = Arrays.stream(parts).skip(1).collect(Collectors.toList());
                System.out.println("FAVOURITE MOVIES FOR USERS WITH IDS: " + users.stream().collect(Collectors.joining(", ")));
                sp.favouriteMoviesForUsers(users);
            } else if (parts[0].equals("similarUsers")) {
                String userId = parts[1];
                System.out.println("SIMILAR USERS TO USER WITH ID: " + userId);
                sp.similarUsers(userId);
            }
        }
    }
}
