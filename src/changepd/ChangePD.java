/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package changepd;

import java.io.BufferedWriter;
import java.io.FileWriter;
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
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
                    CalcoloShift c = new CalcoloShift();
                    //carico il file
                    List<Time_series> li = c.load("/home/rodman/Scrivania/Sperimentazione_twitter/twita_tri_cum.csv");
                    //normalizzo le serie
                    c.normalize(li);
                    ArrayList<Time_series> l = c.filtra_hashtag("/home/rodman/Scrivania/Sperimentazione_twitter/11012018_esecuzione_twitter/hash_piu_frequenti.csv", li);
                    li.clear();
                    
                    System.out.println("Permutazioni delle serie...");
                    List<List<Time_series>> liste_campione = new ArrayList<>();
                    for(int i = 0; i < l.size(); i++){
                        //calcolo la shift mean per ogni serie
                        c.meanShift(l.get(i));
                        List<Time_series> liste = new ArrayList<>();
                        //creo i bootstrap per ogni serie
                        liste = c.bootstrapping(l.get(i), 1000);
                        liste_campione.add(liste);
                    }
                    
                    System.out.println("Calcolo mean shift dei campioni...");
                    for(int i = 0; i < liste_campione.size(); i++){
                        for(int j = 0; j < liste_campione.get(0).size(); j++){
                            //creo le mean schift per serie di ogni campione
                             c.meanShift(liste_campione.get(i).get(j));
                        }
                      
                    }
                    //System.out.println("Campioni: ");
                    //System.out.println(liste_campione.get(0).toString());
                    //calcolo i pvalue
                    System.out.println("Calcolo pvalue...");
                    for(int i = 0; i < l.size(); i++){
                        c.computePValue(l.get(i), liste_campione.get(i));
                    }
                    
                    BufferedWriter w = new BufferedWriter(new FileWriter("/home/rodman/Scrivania/Sperimentazione_twitter/032018_twitter/pvalue_cum.csv"));
                    for(int i = 0; i < l.size(); i++){
                        
                        w.write(String.valueOf(l.get(i).getInd()));
                        w.write(",");
                        w.write(l.get(i).getWord());
                        
                        for(int j = 0; j < l.get(i).get_pvalue().size(); j++){
                            w.write(",");
                            w.write(String.valueOf(l.get(i).get_pvalue().get(j)));
                            
                        }
                        w.write("\n");
                    }
                    w.close();
                    //System.out.println(l.toString());

    }
    
}
