package tracker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Student {
    private static int idCounter = 10000;
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final Map<String, Integer> points = new HashMap<>();
    private final Map<String, Integer> submissions = new HashMap<>();
    private final Set<String> notifiedCourses = new HashSet<>();

    public Student(String firstName, String lastName, String email) {
        this.id = idCounter++;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        for (String course : new String[]{"Java", "DSA", "Databases", "Spring"}) {
            points.put(course, 0);
            submissions.put(course, 0);
        }
    }

    public void updatePoints(int java, int dsa, int db, int spring) {
        addPoints("Java", java);
        addPoints("DSA", dsa);
        addPoints("Databases", db);
        addPoints("Spring", spring);
    }

    private void addPoints(String course, int pts) {
        if (pts > 0) {
            points.put(course, points.get(course) + pts);
            submissions.put(course, submissions.get(course) + 1);
        }
    }

    public int getId() { return id; }
    public Map<String, Integer> getPoints() { return points; }
    public int getPoints(String course) { return points.get(course); }
    public int getSubmissions(String course) { return submissions.get(course); }
    public String getProgress() {
        return String.format("%d points: Java=%d; DSA=%d; Databases=%d; Spring=%d",
                id, getPoints("Java"), getPoints("DSA"), getPoints("Databases"), getPoints("Spring"));
    }

    public boolean hasCompleted(String course, int threshold) {
        return getPoints(course) >= threshold;
    }

    public boolean isNotifiedFor(String course) {
        return notifiedCourses.contains(course);
    }

    public void markNotified(String course) {
        notifiedCourses.add(course);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }
}