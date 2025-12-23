package interfaces;

public interface IEducationalContent {
    void goToMainMenu();
    void nextPage();
    void previousPage();
    void updatePage();
    void updateTitle(int width);
    void showErrorDialog(Exception ex);
}