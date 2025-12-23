package interfaces;

public interface Gamification {
    int getTotalStars();
    void setTotalStars(int stars);

    int getStarsForCurrentQuestion();
    void setStarsForCurrentQuestion(int stars);

    int getScore();
    void setScore(int score);
}