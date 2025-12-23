// Contributed and checked by: Mohamad Nazmi Bin Nur (84545)
// Contributed and checked by: Chrisler Clarens Anak Candder (84546)
// Contributed and checked by: Ahmad Afiq Adzly bin Rosli (101454)
// Contributed and checked by: Mohd Naafiz bin Mohammad Idris (97545)
// Contributed and checked by: Sylvester Sam Sibar (82976)

package classes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.*;

public class MainWindow extends JFrame {
    private JPanel loginPanel;
    private JPanel mainMenuPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    public UserProgress userProgress;

    public MainWindow() {
        setTitle("Environmental Awareness App");
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        showLoginPanel();
    }

    private void showLoginPanel() {
        loginPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        JLabel userLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());

        loginPanel.add(userLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);

        setContentPane(loginPanel);
        revalidate();
        repaint();
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        if (!username.isEmpty() && !password.isEmpty()) {
            // Check if username exists with a different password
            boolean usernameExists = false;
            boolean passwordMatches = false;
            try (InputStream is = new FileInputStream("src/resources/users.json")) {
                org.json.JSONArray arr = new org.json.JSONArray(new org.json.JSONTokener(is));
                for (int i = 0; i < arr.length(); i++) {
                    org.json.JSONObject obj = arr.getJSONObject(i);
                    if (obj.getString("username").equals(username)) {
                        usernameExists = true;
                        if (obj.getString("password").equals(password)) {
                            passwordMatches = true;
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (usernameExists && !passwordMatches) {
                JOptionPane.showMessageDialog(this, "Incorrect password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            userProgress = UserProgress.loadOrCreate(username, password);
            userProgress.setStars(userProgress.getStars());
            userProgress.saveOrUpdate();
            showMainMenu();
        } else {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    void showMainMenu() {
        mainMenuPanel = new JPanel(new GridLayout(2, 2));
        JButton profileButton = createButton("Profile");
        JButton quizButton = createButton("Quiz");
        JButton leaderboardsButton = createButton("Leaderboards");
        JButton learnButton = createButton("Learn");

        profileButton.addActionListener(new ButtonClickListener());
        quizButton.addActionListener(new ButtonClickListener());
        leaderboardsButton.addActionListener(new ButtonClickListener());
        learnButton.addActionListener(new ButtonClickListener());

        mainMenuPanel.add(profileButton);
        mainMenuPanel.add(quizButton);
        mainMenuPanel.add(leaderboardsButton);
        mainMenuPanel.add(learnButton);

        setContentPane(mainMenuPanel);
        revalidate();
        repaint();
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.PLAIN, 24));
        button.setPreferredSize(new Dimension(100, 50));
        return button;
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            String buttonText = source.getText();
            if ("Learn".equals(buttonText)) {
                EducationalContent eduPanel = new EducationalContent(MainWindow.this);
                setContentPane(eduPanel.getContentPane());
                revalidate();
                repaint();
            } else if ("Quiz".equals(buttonText)) {
                QuizModule quizPanel = new QuizModule(MainWindow.this, userProgress);
                setContentPane(quizPanel.getContentPane());
                revalidate();
                repaint();
            } else if ("Leaderboards".equals(buttonText)) {
                showLeaderboard();
            } else if ("Profile".equals(buttonText)) {
                showProfile();
            }
        }
    }

    private void showLeaderboard() {
        JPanel leaderboardPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Leaderboard (Top 10)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        leaderboardPanel.add(titleLabel, BorderLayout.NORTH);

        // Read users from JSON
        java.util.List<UserProgress> users = new java.util.ArrayList<>();
        try (InputStream is = new FileInputStream("src/resources/users.json")) {
            org.json.JSONArray arr = new org.json.JSONArray(new org.json.JSONTokener(is));
            for (int i = 0; i < arr.length(); i++) {
                org.json.JSONObject obj = arr.getJSONObject(i);
                String username = obj.getString("username");
                int stars = obj.optInt("stars", 0);
                users.add(new UserProgress(username, obj.getString("password"), stars));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load leaderboard: " + ex.getMessage());
        }

        // Sort by stars descending
        users.sort((a, b) -> Integer.compare(b.getStars(), a.getStars()));

        // Prepare leaderboard text
        StringBuilder sb = new StringBuilder("<html><table width='300'>");
        sb.append("<tr><th>Rank</th><th>Username</th><th>Stars</th></tr>");
        int max = Math.min(10, users.size());
        for (int i = 0; i < max; i++) {
            UserProgress u = users.get(i);
            String medal = "";
            if (i == 0) medal = " 🥇";
            else if (i == 1) medal = " 🥈";
            else if (i == 2) medal = " 🥉";
            sb.append("<tr><td>").append(i + 1).append("</td><td>")
              .append(u.getUsername()).append(medal)
              .append("</td><td>").append(u.getStars()).append("</td></tr>");
        }
        sb.append("</table></html>");

        JLabel leaderboardLabel = new JLabel(sb.toString(), SwingConstants.CENTER);
        leaderboardLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        leaderboardPanel.add(leaderboardLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showMainMenu());
        leaderboardPanel.add(backButton, BorderLayout.SOUTH);

        setContentPane(leaderboardPanel);
        revalidate();
        repaint();
    }

    private void showProfile() {
        JPanel profilePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0)); 
        profilePanel.add(titleLabel, BorderLayout.NORTH);

        String username = userProgress.getUsername();
        int stars = userProgress.getStars();

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel usernameLabel = new JLabel("Username: " + username, SwingConstants.CENTER);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 24)); 

        JLabel starsLabel = new JLabel("Highscore: " + stars, SwingConstants.CENTER);
        starsLabel.setFont(new Font("Arial", Font.BOLD, 24)); 

        infoPanel.add(usernameLabel);
        infoPanel.add(starsLabel);
        profilePanel.add(infoPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showMainMenu());
        profilePanel.add(backButton, BorderLayout.SOUTH);

        setContentPane(profilePanel);
        revalidate();
        repaint();
    }
}


