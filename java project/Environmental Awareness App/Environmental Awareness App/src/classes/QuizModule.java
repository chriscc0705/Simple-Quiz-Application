// Quiz modules contributed by: Mohamad Nazmi Bin Nur (84545) 
// Question handler contributed by: Mohd Naafiz bin Mohammad Idris (97545)
// Gamification elements contributed by: Chrisler Clarens Anak Candder (84546)

// Checked by: Ahmad Afiq Adzly bin Rosli (101454)

package classes;

import interfaces.Gamification;
import interfaces.IQuizModule;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.io.InputStream;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;

public final class QuizModule extends JFrame implements IQuizModule, Gamification {
    private final List<Question> questions = new ArrayList<>();
    private int currentQuestion = 0;
    private int score = 0;

    private final JTextArea questionArea;
    private final JPanel optionsPanel;
    private final JButton nextButton;
    private final JButton backButton;
    private final JButton closeButton;
    private ButtonGroup buttonGroup;
    private final JLabel feedbackLabel;
    private final JLabel timerLabel; // Add a timer label for UI
    private final MainWindow mainWindow;
    private final UserProgress userProgress;

    // Timer and star tracking
    private Timer questionTimer;
    private int timeLeft = 15;
    private int starsForCurrentQuestion = 3;
    private int totalStars = 0;

    // --- Gamification interface methods ---
    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int getStarsForCurrentQuestion() {
        return starsForCurrentQuestion;
    }

    @Override
    public void setStarsForCurrentQuestion(int stars) {
        this.starsForCurrentQuestion = stars;
    }

    @Override
    public int getTotalStars() {
        return totalStars;
    }

    @Override
    public void setTotalStars(int stars) {
        this.totalStars = stars;
    }

    public QuizModule(MainWindow mainWindow, UserProgress userProgress) {
        this.mainWindow = mainWindow;
        this.userProgress = userProgress;
        setTitle("Environmental Quiz");
        setSize(600, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        loadQuestionsFromJson();

        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 255, 240));

        // Replace JLabel with JTextArea for dynamic scaling
        questionArea = new JTextArea();
        questionArea.setFont(new Font("Arial", Font.BOLD, 18));
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setEditable(false);
        questionArea.setOpaque(false);
        questionArea.setFocusable(false);
        questionArea.setBorder(null);

        JScrollPane questionScroll = new JScrollPane(
            questionArea,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        questionScroll.setBorder(null);
        questionScroll.setOpaque(false);
        questionScroll.getViewport().setOpaque(false);

        mainPanel.add(questionScroll, BorderLayout.NORTH);

        timerLabel = new JLabel("Time left: 15s | Stars: 3");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.RED);
        mainPanel.add(timerLabel, BorderLayout.WEST);

        optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        optionsPanel.setBackground(new Color(240, 255, 240));
        mainPanel.add(optionsPanel, BorderLayout.CENTER);

        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        feedbackLabel.setForeground(Color.BLUE);
        mainPanel.add(feedbackLabel, BorderLayout.SOUTH);

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 16));
        nextButton.addActionListener(e -> {
            try {
                nextQuestion();
            } catch (Exception ex) {
                showErrorDialog(ex);
            }
        });

        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));
        backButton.addActionListener(e -> {
            try {
                previousQuestion();
            } catch (Exception ex) {
                showErrorDialog(ex);
            }
        });

        closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 16));
        closeButton.addActionListener(e -> {
            try {
                goToMainMenu();
            } catch (Exception ex) {
                showErrorDialog(ex);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 255, 240));
        buttonPanel.add(closeButton);
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        displayQuestion();
    }

    // --- Interface Methods ---

    @Override
    public void goToMainMenu() {
        if (questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
        }
        if (mainWindow != null) {
            mainWindow.showMainMenu();
        }
    }

    @Override
    public void nextQuestion() {
        if (questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
        }
        if (currentQuestion < questions.size()) {
            Question q = questions.get(currentQuestion);
            ButtonModel selected = buttonGroup.getSelection();
            boolean correct = false;
            if (selected != null) {
                if (q instanceof MultipleChoiceQuestion mcq) {
                    int selectedIndex = Integer.parseInt(selected.getActionCommand());
                    correct = mcq.isCorrect(selectedIndex);
                } else if (q instanceof TrueFalseQuestion tfq) {
                    boolean selectedValue = Boolean.parseBoolean(selected.getActionCommand());
                    correct = tfq.isCorrect(selectedValue);
                }
            }
            if (selected == null) {
                feedbackLabel.setText("Please select an answer.");
                startTimer(); // Restart timer if user hasn't answered
                return;
            }
            if (correct) {
                score++;
                feedbackLabel.setText("Correct! +" + starsForCurrentQuestion + " stars");
                totalStars += starsForCurrentQuestion;
            } else {
                feedbackLabel.setText("Incorrect. +0 stars");
                // If incorrect, user gets 0 stars for this question
            }
            currentQuestion++;
            // Delay before showing next question
            Timer timer = new Timer(900, e -> displayQuestion());
            timer.setRepeats(false);
            timer.start();
        }
    }

    @Override
    public void previousQuestion() {
        if (questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
        }
        if (currentQuestion > 0) {
            currentQuestion--;
            displayQuestion();
        }
    }

    @Override
    public void displayQuestion() {
        feedbackLabel.setText(" ");
        optionsPanel.removeAll();
        buttonGroup = new ButtonGroup();
        timeLeft = 15;
        starsForCurrentQuestion = 3;

        if (currentQuestion < questions.size()) {
            Question q = questions.get(currentQuestion);
            questionArea.setText("Q" + (currentQuestion + 1) + ": " + q.getQuestion());
            questionArea.setCaretPosition(0); // Scroll to top
            timerLabel.setText("Time left: " + timeLeft + "s | Stars: " + starsForCurrentQuestion);

            if (q instanceof MultipleChoiceQuestion mcq) {
                String[] options = mcq.getOptions();
                for (int i = 0; i < options.length; i++) {
                    JRadioButton optionButton = new JRadioButton(options[i]);
                    optionButton.setFont(new Font("Arial", Font.PLAIN, 16));
                    optionButton.setActionCommand(String.valueOf(i));
                    buttonGroup.add(optionButton);
                    optionsPanel.add(optionButton);
                }
            } else if (q instanceof TrueFalseQuestion) {
                JRadioButton trueBtn = new JRadioButton("True");
                trueBtn.setFont(new Font("Arial", Font.PLAIN, 16));
                trueBtn.setActionCommand("true");
                JRadioButton falseBtn = new JRadioButton("False");
                falseBtn.setFont(new Font("Arial", Font.PLAIN, 16));
                falseBtn.setActionCommand("false");
                buttonGroup.add(trueBtn);
                buttonGroup.add(falseBtn);
                optionsPanel.add(trueBtn);
                optionsPanel.add(falseBtn);
            }
            optionsPanel.revalidate();
            optionsPanel.repaint();

            startTimer();
        } else {
            showResult();
        }
    }

    private void startTimer() {
        if (questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
        }
        timerLabel.setText("Time left: " + timeLeft + "s | Stars: " + starsForCurrentQuestion);
        questionTimer = new Timer(1000, e -> {
            timeLeft--;
            if (timeLeft % 5 == 0 && starsForCurrentQuestion > 1) {
                starsForCurrentQuestion--;
            }
            timerLabel.setText("Time left: " + timeLeft + "s | Stars: " + starsForCurrentQuestion);
            if (timeLeft <= 0) {
                questionTimer.stop();
                feedbackLabel.setText("Time's up! +0 stars");
                // If time runs out, user gets 0 stars for this question
                currentQuestion++;
                Timer delay = new Timer(900, ev -> displayQuestion());
                delay.setRepeats(false);
                delay.start();
            }
        });
        questionTimer.start();
    }

    @Override
    public void showResult() {
        if (questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
        }
        questionArea.setText("Quiz Complete!");
        optionsPanel.removeAll();
        timerLabel.setText("");
        double percentage = (double) score / questions.size() * 100;
        double starPercentage = (double) totalStars / 60 * 100;
        String message;
        if (percentage >= 80) {
            message = "Outstanding!";
        } else if (percentage >= 60) {
            message = "That’s good!";
        } else if (percentage >= 40) {
            message = "Good try!";
        } else if (percentage >= 20) {
            message = "You can do better!";
        } else {
            message = "Don’t give up!";
        }
        feedbackLabel.setText(
            "<html>Your score: " + score + " out of " + questions.size() +
            " (" + String.format("%.0f", percentage) + "%)<br>" +
            "Total stars: " + totalStars + " / 60 (" + String.format("%.0f", starPercentage) + "%)<br>" +
            message + "</html>"
        );
        nextButton.setEnabled(false);
        optionsPanel.revalidate();
        optionsPanel.repaint();

        // Save stars to file
        userProgress.setStars(totalStars);
        userProgress.saveOrUpdate();
    }

    @Override
    public void showErrorDialog(Exception ex) {
        JOptionPane.showMessageDialog(this,
                "An error occurred:\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void loadQuestionsFromJson() {
        try {
            InputStream is = getClass().getResourceAsStream("/resources/quiz_questions.json");
            if (is == null) {
                throw new RuntimeException("Quiz questions file not found!");
            }
            JSONObject root = new JSONObject(new JSONTokener(is));

            JSONArray mcArray = root.getJSONArray("multipleChoice");
            for (int i = 0; i < mcArray.length(); i++) {
                JSONObject obj = mcArray.getJSONObject(i);
                String question = obj.getString("question");
                JSONArray optionsArr = obj.getJSONArray("options");
                String[] options = new String[optionsArr.length()];
                for (int j = 0; j < optionsArr.length(); j++) {
                    options[j] = optionsArr.getString(j);
                }
                int correctIndex = obj.getInt("correctIndex");
                questions.add(new MultipleChoiceQuestion(question, options, correctIndex));
            }

            JSONArray tfArray = root.getJSONArray("trueFalse");
            for (int i = 0; i < tfArray.length(); i++) {
                JSONObject obj = tfArray.getJSONObject(i);
                String question = obj.getString("question");
                boolean answer = obj.getBoolean("answer");
                questions.add(new TrueFalseQuestion(question, answer));
            }

            // Debug print
            System.out.println("Loaded questions: " + questions.size());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load quiz questions: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Question Classes ---

    private abstract static class Question {
        private final String question;
        public Question(String question) { this.question = question; }
        public String getQuestion() { return question; }
    }

    private static class MultipleChoiceQuestion extends Question {
        private final String[] options;
        private final int correctIndex;
        public MultipleChoiceQuestion(String question, String[] options, int correctIndex) {
            super(question);
            this.options = options;
            this.correctIndex = correctIndex;
        }
        public String[] getOptions() { return options; }
        public boolean isCorrect(int selectedIndex) { return selectedIndex == correctIndex; }
    }

    private static class TrueFalseQuestion extends Question {
        private final boolean answer;
        public TrueFalseQuestion(String question, boolean answer) {
            super(question);
            this.answer = answer;
        }
        public boolean isCorrect(boolean selected) { return selected == answer; }
    }
}
