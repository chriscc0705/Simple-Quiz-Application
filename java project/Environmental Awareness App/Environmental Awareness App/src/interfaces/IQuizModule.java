// Contributed by: Mohamad Nazmi Bin Nur (84545)
// Contributed by: Chrisler Clarens Anak Candder (84546)
// Contributed by: Mohd Naafiz bin Mohammad Idris (97545)
// Checked by: Ahmad Afiq Adzly bin Rosli (101454)

package interfaces;

public interface IQuizModule {
    void goToMainMenu();
    void nextQuestion();
    void previousQuestion();
    void displayQuestion();
    void showResult();
    void showErrorDialog(Exception ex);
}