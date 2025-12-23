# Environmental Awareness App

This project is a Java GUI application designed to promote environmental awareness through interactive learning and gamification. The main window features four large buttons: **Profile**, **Quiz**, **Leaderboards**, and **Learn**. Users can log in, take quizzes, track their progress, and view their ranking among other users. The application is structured for easy expansion and further development.

- **Profile:** View your username and high score for the current session.
- **Quiz:** Test your environmental knowledge with interactive quizzes and earn stars.
- **Leaderboards:** See the top scorers, including medal icons for the top 3 users.
- **Learn:** Access educational content about environmental topics.

User progress, including usernames and stars, is stored in a JSON file for persistence. The app uses Java Swing for the GUI and is organized into clear packages for classes, interfaces, and resources.

## Project Structure

```
Environmental Awareness App
├── src
│   ├── Main.java
│   ├── classes
│   │   ├── MainWindow.java
│   │   ├── UserProgress.java
│   │   ├── QuizModule.java
│   │   └── (other class files)
│   ├── interfaces
│   │   ├── IQuizModule.java
│   │   ├── IUserProgress.java
│   │   └── (other interface files)
│   └── resources
│       ├── users.json
│       ├── quiz_questions.json
├── README.md
```

## Requirements

- Java Development Kit (JDK) 8 or higher
- An IDE or text editor for Java development

## How to Run the Application

1. Clone the repository or download the project files.
2. Open a terminal and navigate to the project directory.
3. Compile the Java files using the following command:
   ```
   javac src/Main.java src/classes/MainWindow.java src/classes/UserProgress.java src/classes/QuizModule.java
   ```
4. Run the application with the following command:
   ```
   java -cp src Main
   ```

## Features

- A user-friendly interface with four large buttons for easy navigation.
- Each button can be programmed to perform specific actions when clicked.
- The project is structured to allow for easy expansion and modification.

## Future Enhancements

- Implement functionality for each button to navigate to different sections of the application.
- Add more GUI components and features as needed.
- Improve the styling and layout of the GUI for better user experience.