import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;

public class GuiUtils {

    public static JButton createLargeButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setPreferredSize(new Dimension(200, 100));
        return button;
    }
}