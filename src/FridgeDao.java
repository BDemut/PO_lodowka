import java.io.*;

public class FridgeDao {
    private final String productFile;

    public FridgeDao(String productFile) {
        this.productFile = productFile;
    }

    public void save(Model f) throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(productFile));
        stream.writeObject(f);
        stream.close();
    }

    public Model get() throws IOException {
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(productFile));
        try {
            return (Model)stream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
