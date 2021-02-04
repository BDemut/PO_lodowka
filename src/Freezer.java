import java.io.Serializable;
import java.util.ArrayList;

public class Freezer extends Fridge implements Serializable {

    public Freezer() {}

    //każdy dzień spędzony w zamrażarce przedluza przydatnosc o 1 dzien (nie psują się one)
    void nextDay(){
        for (var p : products) {
            p.prolongExpiryDate(1);
        }
    }

    @Override
    //zamrażarka mieści max 4 produkty
    public boolean add(Product p) {
        if (productCount() < 4) {
            super.add(p);
            return true;
        }
        return false;
    }
}
