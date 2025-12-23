// Contributed by: Sylvester Sam Sibar (82976)
// Checked by: Chrisler Clarens Anak Candder (84546)

package classes;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.*;
import java.nio.file.*;

public class UserProgress {
    private static final String FILE_PATH = "src/resources/users.json";

    private String username;
    private String password;
    private int stars;

    public UserProgress(String username, String password, int stars) {
        this.username = username;
        this.password = password;
        this.stars = stars;
    }

    // Load user or create new with 0 stars if not found
    public static UserProgress loadOrCreate(String username, String password) {
        JSONArray users = readAllUsers();
        for (int i = 0; i < users.length(); i++) {
            JSONObject obj = users.getJSONObject(i);
            if (obj.getString("username").equals(username) && obj.getString("password").equals(password)) {
                int stars = obj.optInt("stars", 0);
                return new UserProgress(username, password, stars);
            }
        }
        return new UserProgress(username, password, 0);
    }

    // Save or update user in JSON file
    public void saveOrUpdate() {
        JSONArray users = readAllUsers();
        boolean updated = false;
        for (int i = 0; i < users.length(); i++) {
            JSONObject obj = users.getJSONObject(i);
            if (obj.getString("username").equals(username) && obj.getString("password").equals(password)) {
                obj.put("stars", stars);
                updated = true;
                break;
            }
        }
        if (!updated) {
            JSONObject newUser = new JSONObject();
            newUser.put("username", username);
            newUser.put("password", password);
            newUser.put("stars", stars);
            users.put(newUser);
        }
        writeAllUsers(users);
    }

    private static JSONArray readAllUsers() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (Files.exists(path)) {
                try (InputStream is = Files.newInputStream(path)) {
                    return new JSONArray(new JSONTokener(is));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    private static void writeAllUsers(JSONArray users) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            writer.write(users.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }

    public String getUsername() {
        return username;
    }
}