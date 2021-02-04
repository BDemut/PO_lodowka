import java.io.Serializable;
import java.util.ArrayList;

//Klasa Fridge przechowuje listę produktów (Product) i zajmuje się
//wszystkimi operacjami związanymi z produktami które przechowuje
public class Fridge implements Serializable {

    protected ArrayList<Product> products = new ArrayList();

    public Fridge() {}

    public int productCount() {
        return products.size();
    }

    //dodaje produkt do lodówki
    //zwraca true jeżeli się udało
    public boolean add(Product p) {
        products.add(p);
        return true;
    }

    //usuwa produkt o konkretnym id z lodówki i zwraca go
    public Product remove(int id) {
        Product p = null;
        if (id < products.size() && id >= 0) {
            p = products.get(id);
            products.remove(id);
        }
        return p;
    }

    //sprawdza czy w lodówce istnieje produkt o typie t
    public boolean checkForType(Type t) {
        for (Product p : products) {
            if (p.getType() == t) {
                return true;
            }
        }
        return false;
    }

    //zużywa porcję produktu
    public void use(int id) {
        if (id >= 0 && id < productCount())
            if (products.get(id).use())
                remove(id);
    }

    //wyrzuca z lodówki przeterminowane produkty
    public final ArrayList<String> checkForExpiredProducts(int day) {
        ArrayList<String> out = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).daysLeftToExpire(day) < 1) {
                out.add(products.get(i).getName());
                remove(i);
            }
        }
        return out;
    }

    //liczy porcje produktów danego typu
    public int countPortions(Type type) {
        int currentPortions = 0;
        for (Product p : products) {
            if (p.getType() == type) {
                currentPortions += p.getPortions();
            }
        }
        return currentPortions;
    }

    public String[][] getProductData(int day) {
        var data = new String[productCount()][4];
        for (int i = 0; i < productCount(); i++) {
            Product p = products.get(i);
            data[i] = p.getTableData(day);
        }
        return data;
    }
}
