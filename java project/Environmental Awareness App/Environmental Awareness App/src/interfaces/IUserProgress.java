// Contributed by: Sylvester Sam Sibar (82976)
// Checked by: Chrisler Clarens Anak Candder (84546)

package interfaces;

public interface IUserProgress {
    String getUsername();
    void setUsername(String username);

    String getPassword();
    void setPassword(String password);

    int getHighScore();
    void setHighScore(int score);
}

