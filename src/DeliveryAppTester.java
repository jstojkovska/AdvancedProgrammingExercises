import java.time.LocalDateTime;
import java.util.*;

//edited for exercise

/*
YOUR CODE HERE
DO NOT MODIFY THE interfaces and classes below!!!
*/

interface Location {
    int getX();

    int getY();

    default int distance(Location other) {
        int xDiff = Math.abs(getX() - other.getX());
        int yDiff = Math.abs(getY() - other.getY());
        return xDiff + yDiff;
    }
}

class LocationCreator {
    public static Location create(int x, int y) {

        return new Location() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}

public class DeliveryAppTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String appName = sc.nextLine();
        DeliveryApp app = new DeliveryApp(appName);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                app.addUser(id, name);
            } else if (parts[0].equals("registerDeliveryPerson")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.registerDeliveryPerson(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addRestaurant")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addRestaurant(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addAddress")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addAddress(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("orderFood")) {
                String userId = parts[1];
                String userAddressName = parts[2];
                String restaurantId = parts[3];
                float cost = Float.parseFloat(parts[4]);
                app.orderFood(userId, userAddressName, restaurantId, cost);
            } else if (parts[0].equals("printUsers")) {
                app.printUsers();
            } else if (parts[0].equals("printRestaurants")) {
                app.printRestaurants();
            } else {
                app.printDeliveryPeople();
            }

        }
    }
}

class Address {
    String addressName;
    Location location;

    public Address(String addressName, Location location) {
        this.addressName = addressName;
        this.location = location;
    }

    public String getAddressName() {
        return addressName;
    }

    public Location getLocation() {
        return location;
    }
}

class DeliveryPerson {
    String idDeliveryPerson;
    String nameDeliveryPerson;
    Location currentLocationDeliveryPerson;
    List<Float> deliveryFeesDeliveryPerson;

    public DeliveryPerson(String idDeliveryPerson, String nameDeliveryPerson, Location currentLocationDeliveryPerson) {
        this.idDeliveryPerson = idDeliveryPerson;
        this.nameDeliveryPerson = nameDeliveryPerson;
        this.currentLocationDeliveryPerson = currentLocationDeliveryPerson;
        deliveryFeesDeliveryPerson = new ArrayList<>();
    }

    public void addFeeDP(int distance,Location location) {
        currentLocationDeliveryPerson=location;
        deliveryFeesDeliveryPerson.add((float) (90+(distance/10)*10));
    }

    public String getIdDeliveryPerson() {
        return idDeliveryPerson;
    }

    public void setIdDeliveryPerson(String idDeliveryPerson) {
        this.idDeliveryPerson = idDeliveryPerson;
    }

    public String getNameDeliveryPerson() {
        return nameDeliveryPerson;
    }

    public void setNameDeliveryPerson(String nameDeliveryPerson) {
        this.nameDeliveryPerson = nameDeliveryPerson;
    }

    public Location getCurrentLocationDeliveryPerson() {
        return currentLocationDeliveryPerson;
    }

    public void setCurrentLocationDeliveryPerson(Location currentLocationDeliveryPerson) {
        this.currentLocationDeliveryPerson = currentLocationDeliveryPerson;
    }

    public List<Float> getDeliveryFeesDeliveryPerson() {
        return deliveryFeesDeliveryPerson;
    }

    public void setDeliveryFeesDeliveryPerson(List<Float> deliveryFeesDeliveryPerson) {
        this.deliveryFeesDeliveryPerson = deliveryFeesDeliveryPerson;
    }

    public float totalDeliveryFee() {
        return (float) deliveryFeesDeliveryPerson.stream()
                .mapToDouble(x -> x)
                .sum();
    }

    public float averageDeliveryFee() {
        return (float) deliveryFeesDeliveryPerson
                .stream()
                .mapToDouble(x -> x)
                .average()
                .orElse(0.00);
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total deliveries: %d Total delivery fee: %.2f Average delivery fee: %.2f", idDeliveryPerson, nameDeliveryPerson, deliveryFeesDeliveryPerson.size(), totalDeliveryFee(), averageDeliveryFee());
    }
}

class Restaurant {
    String idRestaurant;
    String nameRestaurant;
    Location locationRestaurant;
    List<Float> amountEarnedRestaurant;

    public Restaurant(String idRestaurant, String nameRestaurant, Location locationRestaurant) {
        this.idRestaurant = idRestaurant;
        this.nameRestaurant = nameRestaurant;
        this.locationRestaurant = locationRestaurant;
        amountEarnedRestaurant = new ArrayList<>();
    }

    public void addCostRestaurant(float cost) {
        amountEarnedRestaurant.add(cost);
    }

    public String getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(String idRestaurant) {
        this.idRestaurant = idRestaurant;
    }

    public String getNameRestaurant() {
        return nameRestaurant;
    }

    public void setNameRestaurant(String nameRestaurant) {
        this.nameRestaurant = nameRestaurant;
    }

    public Location getLocationRestaurant() {
        return locationRestaurant;
    }

    public void setLocationRestaurant(Location locationRestaurant) {
        this.locationRestaurant = locationRestaurant;
    }

    public float totalAmountEarned() {
        return (float) amountEarnedRestaurant.stream()
                .mapToDouble(x -> x)
                .sum();
    }

    public float averageAmountEarned() {
        return (float) amountEarnedRestaurant
                .stream()
                .mapToDouble(x -> x)
                .average()
                .orElse(0.00);
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f", idRestaurant, nameRestaurant, amountEarnedRestaurant.size(), totalAmountEarned(), averageAmountEarned());
    }
}

class User {
    String idUser;
    String nameUser;
    Map<String, Address> addressMap;
    List<Float> amountSpentUser;

    public User(String idUser, String nameUser) {
        this.idUser = idUser;
        this.nameUser = nameUser;
        amountSpentUser = new ArrayList<>();
        addressMap = new HashMap<>();
    }

    public Map<String, Address> getAddressMap() {
        return addressMap;
    }

    public void addAddress(String name, Location location) {
        addressMap.put(name, new Address(name, location));
    }

    public void addCostUser(float cost) {
        amountSpentUser.add(cost);
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public List<Float> getAmountSpentUser() {
        return amountSpentUser;
    }

    public void setAmountSpentUser(List<Float> amountSpentUser) {
        this.amountSpentUser = amountSpentUser;
    }

    public float totalAmountSpent() {
        return (float) amountSpentUser.stream()
                .mapToDouble(x -> x)
                .sum();
    }

    public float averageAmountSpent() {
        return (float) amountSpentUser
                .stream()
                .mapToDouble(x -> x)
                .average()
                .orElse(0.00);
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f", idUser, nameUser, amountSpentUser.size(), totalAmountSpent(), averageAmountSpent());
    }
}

class DeliveryApp {
    String name;
    Map<String, DeliveryPerson> deliveryPersonMap;
    Map<String, Restaurant> restaurantMap;
    Map<String, User> userMap;

    public DeliveryApp(String name) {
        this.name = name;
        deliveryPersonMap = new HashMap<>();
        restaurantMap = new HashMap<>();
        userMap = new HashMap<>();
    }

    void registerDeliveryPerson(String id, String name, Location currentLocation) {
        deliveryPersonMap.putIfAbsent(id, new DeliveryPerson(id, name, currentLocation));
    }

    void addRestaurant(String id, String name, Location location) {
        restaurantMap.putIfAbsent(id, new Restaurant(id, name, location));

    }

    void addUser(String id, String name) {
        userMap.putIfAbsent(id, new User(id, name));

    }

    void addAddress(String id, String addressName, Location location) {
        userMap.get(id).addAddress(addressName,location);

    }

    void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        User user=userMap.get(userId);
        user.addCostUser(cost);

        Address address=user.addressMap.get(userAddressName);

        Restaurant restaurant=restaurantMap.get(restaurantId);
        restaurant.addCostRestaurant(cost);

        DeliveryPerson deliveryPerson=deliveryPersonMap.values()
                .stream()
                .min(Comparator.comparing((DeliveryPerson x) ->x.currentLocationDeliveryPerson.distance(restaurant.getLocationRestaurant()))
                        .thenComparing(x->x.deliveryFeesDeliveryPerson.size()))
                .orElseThrow();
        int distance=deliveryPerson.currentLocationDeliveryPerson.distance(restaurant.getLocationRestaurant());
        deliveryPerson.addFeeDP(distance, address.getLocation());

    }

    void printUsers() {
        userMap.values().stream()
                .sorted(Comparator.comparing(User::totalAmountSpent)
                        .thenComparing(User::getIdUser).reversed())
                .forEach(System.out::println);

    }

    void printRestaurants() {
        restaurantMap.values().stream()
                .sorted(Comparator.comparing(Restaurant::averageAmountEarned)
                        .thenComparing(Restaurant::getIdRestaurant).reversed())
                .forEach(System.out::println);

    }

    void printDeliveryPeople() {
        deliveryPersonMap.values().stream()
                .sorted(Comparator.comparing(DeliveryPerson::totalDeliveryFee)
                        .thenComparing(DeliveryPerson::getIdDeliveryPerson).reversed())
                .forEach(System.out::println);
    }
}
