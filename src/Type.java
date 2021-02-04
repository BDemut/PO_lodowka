
import java.io.Serializable;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author barte
 */
// typ produktu, kiedy program tworzy listę zakupów
// grupuje produkty wg typów (nie tworzy listy konkretnych produktów, tylko tych abstrakcyjnych typów)

// to czym jest typ jest zależne od użytkownika - użytkownik może np
// używać tylko typu "ser" i każdy kupiony ser dodawać jako ten typ
// lub użyc kilku typów (np. "ser topiony", "ser żółty", "ser twaróg").

class Type implements Serializable {
    private float meanExpireTime;
    private int productsBought;
    // liczba użyć produktu danego typu
    private int uses;
    // liczba dni od pojawienia się typu
    private int days;
    private String name;

    public Type(String n, int u, int d, float met, int pb) {
        name = n;
        uses = u;
        days = d;
        meanExpireTime = met;
        productsBought = pb;
    }

    public final void use() {
        uses++;
    }
    public final void nextDay() {
        days++;
    }

    public final String getName() {
        return name;
    }

    //"zużycie" typu
    // jeżeli dodam typ dnia 5 do lodówki, zużyję 4 procje i dnia 7
    // wywołam tą funkcję zwróci ona 2 (2 porcje/dzień)
    public final double usage() {
        if (days == 0)
            return uses;
        return (double)uses / (double)days;
    }

    // zapotrzebowanie na produkt, zakładając stałe dzienne zużycie
    // w przeciągu następnych 'days' dni
    // posiadając 'portions' porcji
    public int calculateNeed(int portions, int days) {
        int p = Math.round(meanExpireTime);
        days = Math.min(p, days);
        double usage = this.usage();

        int need = (int)(usage * days) - portions;
        need = need > 0 ? need + 1 : 0;

        return need;
    }

    //wywoływane kiedy zostaje kupiony nowy produkt tego typu
    public final void registerProduct(int expireTime) {
        float num = meanExpireTime * productsBought;
        num += expireTime;
        productsBought++;
        meanExpireTime = num / productsBought;
    }
}

