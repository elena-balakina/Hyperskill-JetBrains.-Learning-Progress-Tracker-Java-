package tracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<Integer, Student> studentsById = new LinkedHashMap<>();
    private static final Set<String> emails = new HashSet<>();

    private static final Map<String, Integer> courseCompletion = Map.of(
            "Java", 600,
            "DSA", 400,
            "Databases", 480,
            "Spring", 550
    );

    public static void main(String[] args) {
        System.out.println("Learning Progress Tracker");

        while (true) {
            String input = scanner.nextLine().trim();

            switch (input.toLowerCase()) {
                case "exit":
                    System.out.println("Bye!");
                    return;
                case "add students":
                    addStudents();
                    break;
                case "list":
                    listStudents();
                    break;
                case "add points":
                    addPoints();
                    break;
                case "find":
                    findStudent();
                    break;
                case "statistics":
                    showStatistics();
                    break;
                case "back":
                    System.out.println("Enter 'exit' to exit the program.");
                    break;
                case "":
                    System.out.println("No input");
                    break;
                case "notify":
                    notifyStudents();
                    break;
                default:
                    System.out.println("Unknown command!");
            }
        }
    }

    private static void addStudents() {
        int added = 0;
        System.out.println("Enter student credentials or 'back' to return:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equals("back")) {
                System.out.printf("Total %d students have been added.%n", added);
                break;
            }
            String[] parts = input.split(" ");
            if (parts.length < 3) {
                System.out.println("Incorrect credentials.");
                continue;
            }

            int firstSpace = input.indexOf(' ');
            int lastSpace = input.lastIndexOf(' ');
            String firstName = input.substring(0, firstSpace);
            String lastName = input.substring(firstSpace + 1, lastSpace);
            String email = input.substring(lastSpace + 1);

            if (!isValidFirstName(firstName)) {
                System.out.println("Incorrect first name");
            } else if (!isValidLastName(lastName)) {
                System.out.println("Incorrect last name");
            } else if (!isValidEmail(email)) {
                System.out.println("Incorrect email");
            } else if (emails.contains(email)) {
                System.out.println("This email is already taken.");
            } else {
                Student student = new Student(firstName, lastName, email);
                studentsById.put(student.getId(), student);
                emails.add(email);
                System.out.println("The student has been added.");
                added++;
            }
        }
    }

    private static void listStudents() {
        if (studentsById.isEmpty()) {
            System.out.println("No students found");
        } else {
            System.out.println("Students:");
            for (int id : studentsById.keySet()) {
                System.out.println(id);
            }
        }
    }

    private static void addPoints() {
        System.out.println("Enter an id and points or 'back' to return:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equals("back")) break;
            String[] parts = input.split(" ");
            if (parts.length != 5) {
                System.out.println("Incorrect points format.");
                continue;
            }

            String idPart = parts[0];
            int java, dsa, db, spring;
            try {
                java = Integer.parseInt(parts[1]);
                dsa = Integer.parseInt(parts[2]);
                db = Integer.parseInt(parts[3]);
                spring = Integer.parseInt(parts[4]);

                if (java < 0 || dsa < 0 || db < 0 || spring < 0) {
                    System.out.println("Incorrect points format.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Incorrect points format.");
                continue;
            }

            try {
                int id = Integer.parseInt(idPart);
                Student student = studentsById.get(id);
                if (student == null) {
                    System.out.printf("No student is found for id=%d.%n", id);
                } else {
                    student.updatePoints(java, dsa, db, spring);
                    System.out.println("Points updated.");
                }
            } catch (NumberFormatException e) {
                System.out.printf("No student is found for id=%s.%n", idPart);
            }
        }
    }

    private static void findStudent() {
        System.out.println("Enter an id or 'back' to return:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equals("back")) break;
            try {
                int id = Integer.parseInt(input);
                Student student = studentsById.get(id);
                if (student == null) {
                    System.out.printf("No student is found for id=%d.%n", id);
                } else {
                    System.out.println(student.getProgress());
                }
            } catch (NumberFormatException e) {
                System.out.printf("No student is found for id=%s.%n", input);
            }
        }
    }

    private static boolean isValidFirstName(String name) {
        return name.matches("^[A-Za-z](?:[A-Za-z]|(?<![-'])[\\'\\-](?![-']))*[A-Za-z]$");
    }

    private static boolean isValidLastName(String name) {
        String[] parts = name.split(" ");
        if (parts.length == 0) return false;
        for (String part : parts) {
            if (!part.matches("^[A-Za-z](?:[A-Za-z]|(?<![-'])[\\'\\-](?![-']))*[A-Za-z]$")) return false;
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+");
    }

    private static void showStatistics() {
        System.out.println("Type the name of a course to see details or 'back' to quit:");
        Map<String, CourseInfo> stats = calculateStats();

        printCategory("Most popular", stats, CourseInfo::getEnrolledCount, true);
        printCategory("Least popular", stats, CourseInfo::getEnrolledCount, false);
        printCategory("Highest activity", stats, CourseInfo::getSubmissions, true);
        printCategory("Lowest activity", stats, CourseInfo::getSubmissions, false);
        printCategory("Easiest course", stats, CourseInfo::getAverageScore, true);
        printCategory("Hardest course", stats, CourseInfo::getAverageScore, false);

        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equals("back")) return;

            String courseName = formatCourseName(input);
            if (!stats.containsKey(courseName)) {
                System.out.println("Unknown course.");
            } else {
                showCourseDetails(courseName);
            }
        }
    }

    private static void showCourseDetails(String course) {
        System.out.println(course);
        System.out.println("id\tpoints\tcompleted");
        List<Student> students = studentsById.values().stream()
                .filter(s -> s.getPoints(course) > 0)
                .sorted(Comparator
                        .comparingInt((Student s) -> s.getPoints(course))
                        .reversed()
                        .thenComparing(Student::getId))
                .collect(Collectors.toList());

        for (Student s : students) {
            int pts = s.getPoints(course);
            double percent = 100.0 * pts / courseCompletion.get(course);
            BigDecimal percentRounded = BigDecimal.valueOf(percent).setScale(1, RoundingMode.HALF_UP);
            System.out.printf("%d\t%d\t%.1f%%%n", s.getId(), pts, percentRounded);
        }
    }

    private static void printCategory(String title, Map<String, CourseInfo> stats,
                                      Function<CourseInfo, ? extends Comparable> keyExtractor, boolean max) {
        List<CourseInfo> courseList = new ArrayList<>(stats.values());
        if (courseList.isEmpty()) {
            System.out.printf("%s: n/a%n", title);
            return;
        }

        Comparator<CourseInfo> comparator = Comparator.comparing(keyExtractor);
        if (max) comparator = comparator.reversed();

        courseList.sort(comparator);
        Comparable bestValue = keyExtractor.apply(courseList.get(0));
        Comparable worstValue = keyExtractor.apply(courseList.get(courseList.size() - 1));

        // 👇 если все значения равны
        if (bestValue.compareTo(worstValue) == 0) {
            if (bestValue instanceof Number && ((Number) bestValue).doubleValue() == 0) {
                System.out.printf("%s: n/a%n", title);
            } else if (max) {
                // все равны и больше 0 — для max выводим все курсы
                List<String> allCourses = courseList.stream()
                        .map(c -> c.name)
                        .collect(Collectors.toList());
                System.out.printf("%s: %s%n", title, String.join(", ", allCourses));
            } else {
                // все равны — для min выводим n/a
                System.out.printf("%s: n/a%n", title);
            }
            return;
        }

        if (bestValue instanceof Number && ((Number) bestValue).doubleValue() == 0) {
            System.out.printf("%s: n/a%n", title);
            return;
        }

        List<String> topCourses = courseList.stream()
                .filter(c -> keyExtractor.apply(c).compareTo(bestValue) == 0)
                .map(c -> c.name)
                .collect(Collectors.toList());

        System.out.printf("%s: %s%n", title, String.join(", ", topCourses));
    }

    private static Map<String, CourseInfo> calculateStats() {
        Map<String, CourseInfo> stats = new HashMap<>();
        for (String course : courseCompletion.keySet()) {
            stats.put(course, new CourseInfo(course));
        }

        for (Student student : studentsById.values()) {
            for (String course : courseCompletion.keySet()) {
                int points = student.getPoints(course);
                int submissions = student.getSubmissions(course);
                if (points > 0) stats.get(course).enrolledCount++;
                stats.get(course).totalScore += points;
                stats.get(course).submissions += submissions;
            }
        }

        return stats;
    }

    private static String formatCourseName(String input) {
        switch (input.toLowerCase()) {
            case "java":
                return "Java";
            case "dsa":
                return "DSA";
            case "databases":
                return "Databases";
            case "spring":
                return "Spring";
            default:
                return "";
        }
    }

    private static void notifyStudents() {
        int notifiedCount = 0;

        for (Student student : studentsById.values()) {
            boolean studentNotified = false;
            for (String course : courseCompletion.keySet()) {
                int threshold = courseCompletion.get(course);
                if (student.hasCompleted(course, threshold) && !student.isNotifiedFor(course)) {
                    System.out.printf("To: %s%n", student.getEmail());
                    System.out.println("Re: Your Learning Progress");
                    System.out.printf("Hello, %s! You have accomplished our %s course!%n",
                            student.getFullName(), course);
                    student.markNotified(course);
                    studentNotified = true;
                }
            }
            if (studentNotified) notifiedCount++;
        }

        System.out.printf("Total %d students have been notified.%n", notifiedCount);
    }

}





