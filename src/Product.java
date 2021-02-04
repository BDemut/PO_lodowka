
import java.awt.*;
import java.io.Serializable;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author barte
 */
// jeden produkt o n porcjach (karton mleka, kostka masła)
// to czym jest 1 porcja jest zależne od użytkownika
class Product implements Serializable {
    private String name;        public String getName() { return name; }
    private Type type;          public Type getType() { return type; }
    private int day_bought;
    private int day_expires;
    private int portions;       public int getPortions() {
        return portions;
    }

    public void setType(Type t) {
        type = t;
        type.registerProduct(day_expires - day_bought);
    }

    public Product(String n, String t, int db, int de, int p)
    {
        name = n;
        day_bought = db;
        day_expires = de;
        portions = p;

        type = new Type(t, 0, 0, day_expires - day_bought, 1);
    }

    //zużycie 1 porcji produktu
    //true jeżeli produkt się skończył
    public boolean use()
    {
        portions--;
        type.use();
        return portions < 1;
    }

    public int daysLeftToExpire(int day)
    {
        return day_expires - day;
    }
    public void prolongExpiryDate(int days) { day_expires += days; }

    public String[] getTableData(int day) {
        var data = new String[4];
        data[0] = name;
        data[1] = type.getName();
        data[2] = String.valueOf(daysLeftToExpire(day));
        data[3] = String.valueOf(portions);
        return data;
    }
}