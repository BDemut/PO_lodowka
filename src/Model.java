
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author barte
 */


// "Model" to urządzenie AGD składające się z kilku lodówek (Fridge). Klasycznym
// przykładem użytym w projekcie jest urządzenie składające się z lodówki
// oraz zamrażarki (zaimplementowanej jako podklasa Fridge) ale klasa Model
// jest w pełni wyposażona do opisania bardziej skomplikowanego urządzenia
// (np. dwie zwykle lodówki, zamrażarka i inny rodzaj lodówki będący nową podklasą Fridge)

// Klasa Model zarządza lodówkami i ogólnymi danymi i operacjami
// nie związanymi z konkretną lodówką
// (data, informacje o typach, "inteligentne" funkcje)
public class Model implements Serializable {

    public class FridgeIndexOutOfBoundsException extends IndexOutOfBoundsException {
        public FridgeIndexOutOfBoundsException (String message) {
            //dodaje do wiadomości informacje o dostępnych lodówkach
            super(message + "\n" + fridges.length + " fridges in the model:\n" + Arrays.deepToString(fridges));
        }
    }

    private Fridge[] fridges = {};

    private ArrayList<Type> types = new ArrayList();
    private int day = 0;
    private final Date initTime = new Date();


    public Model(Fridge[] fridges) {
        this.fridges = fridges;
    }

    public final int getDay()
    {
        return day;
    }
    public final Date getInitTime(){ return initTime; }

    //umieszczenie nowego produktu w urządzeniu
    // (domyslnie w lodówce o indeksie 0, mozna je potem przeniesc)
    public void buy(Product p) {
        if (p == null)
            throw new NullPointerException("tried to add a null product to a fridge");

        boolean typeRegistered = false;
        for (Type type : types) {
            if (type.getName().equals(p.getType().getName())) {
                p.setType(type);
                typeRegistered = true;
                break;
            }
        }
        if (!typeRegistered) {
            types.add(p.getType());
        }

        //kupione produkty dodane są zawsze do pierwszej lodówki
        fridges[0].add(p);
    }

    // próba usunięcia danych o typie o wyszczególnionej nazwie
    // zwraca true jezeli sie udalo
    public boolean delType(String name) {
        Type toDelete = null;
        for (int i = 0; i<types.size(); i++) {
            if (types.get(i).getName().equals(name))
               toDelete = types.get(i);
        }

        if (toDelete != null) {
            for (var fridge : fridges)
                if (fridge.checkForType(toDelete))
                    return false;

            types.remove(toDelete);
            return true;
        }
        else {
            return false;
        }
    }

    //użycie produktu o indeksie id z lodówki o indeksie fridgeIndex
    public void use(int fridgeIndex, int id) {
        if (fridgeIndex < 0 || fridgeIndex >= fridges.length)
            throw new FridgeIndexOutOfBoundsException(fridgeIndex + "is an invalid fridge index");
        fridges[fridgeIndex].use(id);
    }

    //funkcja wywoływana podczas przejścia do następnego dnia
    public void nextDay() {
        day++;
        for (Type type : types) {
            type.nextDay();
        }
        for (var fridge : fridges)
            if (fridge instanceof Freezer)
                ((Freezer) fridge).nextDay();
    }

    //przeniesienie produktu o indeksie 'id' z lodówki o indeksie 'from' do lodówki o indeksie 'to'
    public void move(int from, int to, int id) {
        if (from < 0 || from >= fridges.length)
            throw new FridgeIndexOutOfBoundsException("'from' fridge index " + from + " is invalid");
        if (to < 0 || to >= fridges.length)
            throw new FridgeIndexOutOfBoundsException("'to' fridge index " + from + " is invalid");
        Product p = fridges[from].remove(id);
        if (p != null)
            if (!fridges[to].add(p))
                fridges[from].add(p);
    }

    //wyrzucenie przeterminowanych przedmiotów z każdej lodówki
    public ArrayList<String> checkForExpiredProducts() {
        ArrayList<String> out = new ArrayList();
        for (var fridge : fridges)
            out.addAll(fridge.checkForExpiredProducts(day));
        return out;
    }

    //lista zakupów przewidzianych na ilość dni 'days'
    public String makeShoppingList(int days) {
        StringBuilder list = new StringBuilder("Lista zakupow:\n");
        for (Type type : types) {
            int need = 0;
                for (var fridge : fridges)
                    need += type.calculateNeed(fridge.countPortions(type), days);
            if (need > 0) {
                list.append(type.getName()).append(": ").append(need).append(" porcji\n");
            }
        }
        return list.toString();
    }

    public String[][] getProductData(int fridgeIndex) {
        if (fridgeIndex < 0 || fridgeIndex >= fridges.length)
            throw new FridgeIndexOutOfBoundsException(fridgeIndex + "is an invalid fridge index");
        return fridges[fridgeIndex].getProductData(day);
    }

    public String[] getTypeNames() {
        String[] str = new String[types.size()];
        for (int i = 0; i < types.size(); i++) {
            str[i] = types.get(i).getName();
        }
        return str;
    }
}
