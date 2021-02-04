import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

// Implementacja dla modelu z 2 lodówkami - zwykłą i zamrażarką
public class MainForm extends JFrame {

    public interface Callback {
        void function();
    }
    private JPanel panel;
    private JTextArea console;
    private JTextArea dateText;
    private JButton buy;
    private JButton shoplist;
    private JButton printtype;
    private JButton deltype;
    private JButton use;
    private JButton pause;
    private JScrollPane fridgeScroll;
    private JButton resetButton;
    private JScrollPane freezerScroll;
    private JButton moveButton;
    private JTable fridgeTable;
    private JTable freezerTable;
    FridgeDao dao = new FridgeDao("fridge");
    Dialog dialog;

    Model model;
    static final int FRIDGE = 0;
    static final int FREEZER = 1;

    String[][][] productData = new String[][][]{new String[][]{}, new String[][]{}};

    String info = "";

    volatile Boolean timePaused;
    Timer timer;

    private JTable makeTable(int index) {
        TableModel dataModel = new AbstractTableModel() {
            public int getColumnCount() { return 5; }
            public int getRowCount() { return productData[index].length; }
            public Object getValueAt(int row, int col) {
                if (col == 0)
                    return row;
                else
                    return productData[index][row][col - 1];
            }
        };
        var table = new JTable(dataModel);
        table.getColumnModel().getColumn(0).setHeaderValue("ID");
        table.getColumnModel().getColumn(1).setHeaderValue("Nazwa");
        table.getColumnModel().getColumn(2).setHeaderValue("Typ");
        table.getColumnModel().getColumn(3).setHeaderValue("Przydatność");
        table.getColumnModel().getColumn(4).setHeaderValue("Porcje");
        return table;
    }

    private void createUIComponents() {
        fridgeTable = makeTable(FRIDGE);
        fridgeScroll = new JScrollPane(fridgeTable);
        freezerTable = makeTable(FREEZER);
        freezerScroll = new JScrollPane(freezerTable);
    }

    public MainForm(String title) {
        super(title);
        dialog = new Dialog(this);
        try {
            model = dao.get();
        } catch (IOException e) {
            dialog.error("Błąd podczas wczytywania pliku.\nTworzenie nowej lodówki.");
            model = new Model(new Fridge[]{new Fridge(), new Freezer()});
        }
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel);
        this.pack();
        buy.addActionListener(e -> buyProduct());
        use.addActionListener(e -> useProducts());
        deltype.addActionListener(e -> deleteType());
        printtype.addActionListener(e -> printType());
        shoplist.addActionListener(e -> makeShoppingList());
        resetButton.addActionListener(e -> reset());
        pause.addActionListener(e -> pause());

        timePaused = true;
        timer = new Timer(this::nextDay, 2);
        moveButton.addActionListener(e -> moveProducts());
    }

    public static void main(String[] args) {
        MainForm frame = new MainForm("Lodówka");

        frame.refreshUIandsave();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setVisible(true);
    }

    private void refreshUIandsave() {
        var calendar = Calendar.getInstance();
        calendar.setTime(model.getInitTime());
        calendar.add(Calendar.DATE, model.getDay());
        var date = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime());

        if (timePaused)
            dateText.setText(date + '\n' + "PAUSED");
        else
            dateText.setText(date);

        console.setText(info);
        info = "";

        productData[FRIDGE] = model.getProductData(FRIDGE);
        productData[FREEZER] = model.getProductData(FREEZER);

        fridgeTable.updateUI();
        freezerTable.updateUI();
        try {
            dao.save(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextDay() {
        for (var name : model.checkForExpiredProducts()) {
            info += "Produkt " + name + " przeterminował się!\n";
        }
        model.nextDay();

        refreshUIandsave();
    }

    private void buyProduct() {
        AddProductForm.get(model, this::refreshUIandsave);
        refreshUIandsave();
    }

    private void useProducts() {
        var rows = fridgeTable.getSelectedRows();
        for (var row : rows)
            model.use(FRIDGE, (Integer)fridgeTable.getValueAt(row,0));

        rows = freezerTable.getSelectedRows();
        for (var row : rows)
            model.use(FREEZER, (Integer)freezerTable.getValueAt(row,0));

        deselect();
        refreshUIandsave();
    }

    private void moveProducts() {
        var rows = fridgeTable.getSelectedRows();
        for (int i = rows.length - 1 ; i >= 0 ; i--)
            model.move(FRIDGE, FREEZER, (Integer)fridgeTable.getValueAt(rows[i],0));

        rows = freezerTable.getSelectedRows();
        for (int i = rows.length - 1; i >= 0; i--)
            model.move(FREEZER, FRIDGE, (Integer)freezerTable.getValueAt(rows[i],0));

        deselect();
        refreshUIandsave();
    }

    private void deselect() {
        freezerTable.clearSelection();
        fridgeTable.clearSelection();
    }

    private void deleteType() {
        Object[] possibilities = model.getTypeNames();
        String s;
        if (possibilities.length > 0)
            s = dialog.chooser("Wybierz typ do usunięcia",possibilities);
        else
            s = null;

        if (s != null)
            if (!model.delType(s)) {
                dialog.error("Masz produkt tego typu w lodówce!");
            }

        refreshUIandsave();
    }

    private void printType() {
        StringBuilder builder = new StringBuilder();
        for (var str : model.getTypeNames()) {
            builder.append(str).append("\n");
        }
        dialog.message("Śledzone typy produktów",builder.toString());

        refreshUIandsave();
    }

    private void makeShoppingList() {
        try {
            int days = Integer.parseInt(
                    dialog.input(
                            "Podaj liczbę dni",
                            "Na ile dni mają być przewidziane zakupy?" ));
            dialog.message("Lista zakupów", model.makeShoppingList(days));
        } catch (Exception e) {
            dialog.error("Podano nieprawidłowe dane!");
        }

        refreshUIandsave();
    }

    private void reset() {
        model = new Model(new Fridge[]{new Fridge(), new Freezer()});
        refreshUIandsave();
    }

    private void pause() {
        if (timePaused) {
            pause.setText("Pause");
            timePaused = false;
            timer.start();
        } else {
            pause.setText("Resume");
            timePaused = true;
            timer.stop();
        }
        refreshUIandsave();
    }
}

