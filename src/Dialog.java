import javax.swing.*;

public class Dialog {
    private JFrame parent;

    public Dialog(JFrame parent) {
        this.parent = parent;
    }

    public void message(String title, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                title,
                JOptionPane.PLAIN_MESSAGE);
    }

    public void error(String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Błąd",
                JOptionPane.ERROR_MESSAGE);
    }

    public String input(String title, String message){
        return (String)JOptionPane.showInputDialog(
                parent,
                message,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
    }

    public String input(String title){
        return (String)JOptionPane.showInputDialog(
                parent,
                "",
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
    }

    public String chooser(String title, String message, Object[] possibilities) {
        return (String)JOptionPane.showInputDialog(
                parent,
                message,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                possibilities[0]);
    }

    public String chooser(String title, Object[] possibilities) {
        return (String)JOptionPane.showInputDialog(
                parent,
                "",
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                possibilities[0]);
    }
}
