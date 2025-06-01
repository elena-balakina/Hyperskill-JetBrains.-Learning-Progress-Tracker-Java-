package tracker;

public class CourseInfo {
    public final String name;
    public int enrolledCount = 0;
    public int submissions = 0;
    public int totalScore = 0;

    public CourseInfo(String name) {
        this.name = name;
    }

    public int getEnrolledCount() { return enrolledCount; }
    public int getSubmissions() { return submissions; }
    public double getAverageScore() {
        return submissions == 0 ? 0.0 : (double) totalScore / submissions;
    }
}
