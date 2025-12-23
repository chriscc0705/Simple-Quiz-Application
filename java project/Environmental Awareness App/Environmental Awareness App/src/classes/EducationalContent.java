// Contributed by: Ahmad Afiq Adzly bin Rosli (101454)
// Checked by: Mohd Naafiz bin Mohammad Idris (97545)

/*OOP Concepts in this class

1. Abstraction: 
   - Methods like updatePage(), updateTitle(), and showErrorDialog() hide complex logic and expose simple interfaces for content navigation and error handling.

2. Inheritance: 
   - EducationalContent extends JFrame, inheriting window properties and behaviors.

3. Overriding: 
   - The componentResized() method is overridden with a custom implementation such as adjusting the layout or component sizes to provide custom resizing behavior.

4. Exception Handling: 
   - try-catch blocks such as try-catch blocks are used in event handlers and initialization to handle errors gracefully.
*/

package classes;

import interfaces.IEducationalContent;
import java.awt.*;
import javax.swing.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class EducationalContent extends JFrame implements IEducationalContent {

    private JPanel panel;
    private final JLabel titleLabel;
    private JTextPane contentPane;
    private final JButton backButton;
    private final JButton nextButton;
    private int page = 0;
    private MainWindow mainWindow;

    private List<ContentPage> pages = new ArrayList<>();

    private JLabel imageLabel;

    public EducationalContent(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setTitle("Environmental Awareness");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel = new JPanel();
        panel.setBackground(new Color(230, 255, 240));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create a panel for title and image stacked vertically
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.TOP);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(10)); // spacing
        topPanel.add(imageLabel);

        panel.add(topPanel, BorderLayout.NORTH);

        contentPane = new JTextPane();
        contentPane.setContentType("text/html");
        contentPane.setFont(new Font("Arial", Font.PLAIN, 16));
        contentPane.setEditable(false);
        contentPane.setOpaque(false);
        contentPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        panel.add(contentPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 255, 240));

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 16));
        closeButton.addActionListener(e -> {
            try {
                goToMainMenu();
            } catch (Exception ex) {
                showErrorDialog(ex);
            }
        });

        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));
        backButton.addActionListener(e -> {
            try {
                previousPage();
            } catch (Exception ex) {
                showErrorDialog(ex);
            }
        });

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 16));
        nextButton.addActionListener(e -> {
            try {
                nextPage();
            } catch (Exception ex) {
                showErrorDialog(ex);
            }
        });

        buttonPanel.add(closeButton);
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                try {
                    int width = panel.getWidth() - 40;
                    contentPane.setSize(new Dimension(width, Short.MAX_VALUE));
                    contentPane.setPreferredSize(new Dimension(width, contentPane.getPreferredSize().height));
                    updateTitle(width);
                    panel.revalidate();
                } catch (Exception ex) {
                    showErrorDialog(ex);
                }
            }
        });

        loadContentFromJson();
        try {
            updatePage();
        } catch (Exception ex) {
            showErrorDialog(ex);
        }
    }

    @Override
    public void goToMainMenu() {
        if (mainWindow != null) {
            mainWindow.showMainMenu();
        }
    }

    @Override
    public void nextPage() {
        if (page < pages.size() - 1) {
            page++;
            updatePage();
        }
    }

    @Override
    public void previousPage() {
        if (page > 0) {
            page--;
            updatePage();
        }
    }

    @Override
    public void updatePage() {
        int width = panel.getWidth() > 0 ? panel.getWidth() - 40 : 320;
        if (page >= 0 && page < pages.size()) {
            ContentPage cp = pages.get(page);
            titleLabel.setText(String.format(
                "<html><div style='width:%dpx; text-align:left;'><b>%s</b></div></html>", width, cp.title));
            contentPane.setText("<html>" + cp.content + "</html>");
            backButton.setVisible(page > 0);
            nextButton.setVisible(page < pages.size() - 1);

            if (cp.image != null && !cp.image.isEmpty()) {
                ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/" + cp.image));
                imageLabel.setIcon(icon);
            } else {
                imageLabel.setIcon(null);
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    @Override
    public void updateTitle(int width) {
        titleLabel.setText(String.format(
                "<html><div style='width:%dpx; text-align:left;'>1. What is Environmental <br> Awareness?</div></html>",
                width));
    }

    @Override
    public void showErrorDialog(Exception ex) {
        JOptionPane.showMessageDialog(this,
                "An error occurred:\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void loadContentFromJson() {
        try {
            InputStream is = getClass().getResourceAsStream("/resources/educational_content.json");
            System.out.println("is = " + is);
            if (is == null) {
                throw new RuntimeException("Educational content file not found!");
            }
            JSONArray arr = new JSONArray(new JSONTokener(is));
            pages.clear();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String title = obj.getString("title");
                String content = obj.getString("content");
                String image = obj.has("image") ? obj.getString("image") : null;
                pages.add(new ContentPage(title, content, image));
            }
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    // Add this inner class
    private static class ContentPage {
        String title;
        String content;
        String image; // Add this field
        ContentPage(String title, String content, String image) {
            this.title = title;
            this.content = content;
            this.image = image;
        }
    }
}
