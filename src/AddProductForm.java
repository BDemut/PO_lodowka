import javax.swing.*;
import java.awt.*;

public class AddProductForm extends JFrame {
    private JComboBox typeBox;
    private JTextField portionsField;
    private JButton okbutton;
    private JPanel panel;
    private JTextField nameField;
    private JButton exitButton;
    private JTextField expireField;

    private Model model;

    public AddProductForm(String title, Model f, MainForm.Callback callback) {
        super(title);
        model = f;
        this.setContentPane(panel);
        this.pack();
        okbutton.addActionListener(e -> {
            buy();
            callback.function();
        });
        exitButton.addActionListener(e -> dispose());
    }

    public static void get(Model f, MainForm.Callback c) {
        AddProductForm frame = new AddProductForm("Podaj dane produktu:", f,c);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setVisible(true);
    }

    private void createUIComponents() {
        String[] types = model.getTypeNames();
        typeBox = new JComboBox(types);
        typeBox.setEditable(true);
    }

    private void buy() {
        String name;
        String typeName;
        int portions;
        int day_expires;
        int day_bought ;
        try {
            name = nameField.getText();
            typeName = (String) typeBox.getSelectedItem();
            portions = Integer.parseInt(portionsField.getText());
            day_expires = Integer.parseInt(expireField.getText()) + model.getDay();
            day_bought = model.getDay();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Podano nieprawidłowe dane!",
                    "Błąd",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        model.buy(new Product(name,typeName,day_bought,day_expires,portions));

        nameField.setText("");
        portionsField.setText("");
        expireField.setText("");
    }
}
