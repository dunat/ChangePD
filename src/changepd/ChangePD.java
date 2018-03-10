/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package changepd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rodman
 */
public class ChangePD {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
                    CalcoloShift c = new CalcoloShift();
                    //carico il file
                    List<Time_series> l = c.load("/home/rodman/Scrivania/test_sample.csv");
                    //normalizzo le serie
                    c.normalize(l);
                    
                    List<List<Time_series>> liste_campione = new ArrayList<>();
                    for(int i = 0; i < l.size(); i++){
                        //calcolo la shift mean per ogni serie
                        c.meanShift(l.get(i));
                        List<Time_series> liste = new ArrayList<>();
                        //creo i bootstrap per ogni serie
                        liste = c.bootstrapping(l.get(i), 1000);
                        liste_campione.add(liste);
                    }
                    
                    for(int i = 0; i < liste_campione.size(); i++){
                        for(int j = 0; j < liste_campione.get(0).size(); j++){
                            //creo le mean schift per serie di ogni campione
                             c.meanShift(liste_campione.get(i).get(j));
                        }
                      
                    }
                    //System.out.println("Campioni: ");
                    //System.out.println(liste_campione.get(0).toString());
                    //calcolo i pvalue
                    for(int i = 0; i < l.size(); i++){
                        c.computePValue(l.get(i), liste_campione.get(i));
                    }
                    System.out.println(l.toString());
    }
    
}
