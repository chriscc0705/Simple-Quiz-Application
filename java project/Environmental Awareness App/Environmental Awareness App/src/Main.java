import classes.MainWindow;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}