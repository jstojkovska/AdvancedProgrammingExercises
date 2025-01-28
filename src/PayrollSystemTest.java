import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees(System.in);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i = 5; i <= 10; i++) {
            levels.add("level" + i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: " + level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
        });


    }
}

abstract class Employee {
    String id;
    String level;

    public String getLevel() {
        return level;
    }

    public Employee(String id, String level) {
        this.id = id;
        this.level = level;
    }

    public abstract double salary();
}

class HourlyEmployee extends Employee {

    double hours;
    double hourlyRate;

    public HourlyEmployee(String id, String level, double hours, double hourlyRate) {
        super(id, level);
        this.hours = hours;
        this.hourlyRate = hourlyRate;
    }

    @Override
    public double salary() {
        if (hours <= 40) {
            return hours * hourlyRate;
        } else {
            return (40 * hourlyRate) + ((hours - 40) * hourlyRate * 1.5);
        }
    }

    @Override
    public String toString() {
        if (hours > 40) {
            return String.format("Employee ID: %s Level: %s Salary: %.2f Regular hours: %.2f Overtime hours: %.2f", id, level, salary(), 40.00, hours - 40.00);
        } else {
            return String.format("Employee ID: %s Level: %s Salary: %.2f Regular hours: %.2f Overtime hours: %.2f", id, level, salary(), hours, 0.00);
        }
    }
}

class FreelanceEmployee extends Employee {

    List<Integer> points;
    double ticketRate;

    public FreelanceEmployee(String id, String level, List<Integer> points, double ticketRate) {
        super(id, level);
        this.points = points;
        this.ticketRate = ticketRate;
    }

    public int sumOfPoints() {
        int suma = 0;
        for (Integer point : points) {
            suma += point;
        }
        return suma;
    }

    @Override
    public double salary() {
        return sumOfPoints() * ticketRate;
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f Tickets count: %d Tickets points: %d", id, level, salary(), points.size(), sumOfPoints());
    }
}

class PayrollSystem {
    Map<String, Double> hourlyRateByLevel;
    Map<String, Double> ticketRateByLevel;
    Map<String, Set<Employee>> mapa;
    List<Employee> employees;


    PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
        mapa = new LinkedHashMap<>();
        employees = new ArrayList<>();
    }

    void readEmployees(InputStream is) {
        Scanner sc = new Scanner(is);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            if (parts[0].equals("F")) {
                String id = parts[1];
                String level = parts[2];
                double rate = ticketRateByLevel.get(level);
                List<Integer> points = new ArrayList<>();
                for (int i = 3; i < parts.length; i++) {
                    points.add(Integer.parseInt(parts[i]));
                }
                mapa.putIfAbsent(level, new HashSet<>());
                mapa.get(level).add(new FreelanceEmployee(id, level, points, rate));
                employees.add(new FreelanceEmployee(id, level, points, rate));
            } else if (parts[0].equals("H")) {
                String id = parts[1];
                String level = parts[2];
                double rate = hourlyRateByLevel.get(level);
                double hours = Double.parseDouble(parts[3]);
                mapa.putIfAbsent(level, new HashSet<>());
                mapa.get(level).add(new HourlyEmployee(id, level, hours, rate));
                employees.add(new HourlyEmployee(id, level, hours, rate));
            }
        }
    }

    Map<String, Set<Employee>> printEmployeesByLevels(OutputStream os, Set<String> levels) {
        Map<String, Set<Employee>> result = new LinkedHashMap<>();

        levels.stream().sorted().forEach(level -> {
            Set<Employee> levelEmployees = employees.stream()
                    .filter(e -> e.getLevel().equals(level))
                    .sorted(Comparator.comparing(Employee::salary).reversed())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            if (!levelEmployees.isEmpty()) {
                result.put(level, levelEmployees);
            }
        });

        PrintWriter writer = new PrintWriter(os);
        result.forEach((level, levelEmployees) -> {
            writer.println("LEVEL: " + level);
            writer.println("Employees: ");
            levelEmployees.forEach(writer::println);
            writer.println("------------");
        });
        writer.flush();
        writer.close();

        return result;

    }

}