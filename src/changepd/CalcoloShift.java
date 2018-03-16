package changepd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Classe che fornisce i metodi per effettuare la change point detection
 * @author Donato Aquilino
 */
public class CalcoloShift {
    
    public List<Time_series> load(String filename) throws FileNotFoundException, IOException {
        return load(new File(filename));
    }

    /**
     * Load time series from file
     *
     * @param file Input file
     * @return HashMap of time series
     * @throws java.io.FileNotFoundException
     */
    public List<Time_series> load(File file) throws FileNotFoundException, IOException {
        
        System.out.println("Carico file in memoria...");
        List<Time_series> list = new ArrayList<>();
        String line;
        
        BufferedReader br = new BufferedReader(new FileReader(file));
        //skip first line
        if (br.ready()) {
            br.readLine();
        }
        while (br.ready()) {
            line = br.readLine();
            // split values
            String[] word = line.split(",");

            // build time series
            List<Double> values = new ArrayList<>();
            
            System.out.println(word[0]+","+word[1]);
            for (int i = 2; i < word.length; i++) {

                values.add(Double.parseDouble(word[i]));

            }
            try {
                Time_series t = new Time_series(Integer.parseInt(word[0]),word[1],values);
                list.add(t);
            } catch (NumberFormatException e){
                
            }

        }
        br.close();
        System.out.println("File caricato.");
        
        return list;        
    }
    
    /**
     * normalizzo la serie
     * @param l serie
     */
    void normalize(List<Time_series> l){
        System.out.println("Normalizzazione in corso...");
        
        int num_termini = l.size();
        int lung_serie = l.get(0).getVal().size();
        double sum = 0;
        List<Double> medie = new ArrayList<>();
        //calcolo medie di tutte colonne
        for(int j = 0; j < lung_serie; j++){
            for(int i = 0; i < num_termini; i++){

               sum = sum + l.get(i).getVal().get(j);
            }
            medie.add(sum/num_termini);
            sum = 0;
        }
        
        List<Double> varianze = new ArrayList<>();
        //calcolo varianze
        for(int j = 0; j < lung_serie; j++){
            for(int i = 0; i < num_termini; i++){

               sum = sum + ((l.get(i).getVal().get(j) - medie.get(j))*(l.get(i).getVal().get(j) - medie.get(j)));
            }
            varianze.add(Math.sqrt(sum/(num_termini-1)));
            sum = 0;
        }
        
        //calcolo la z score
        for(int k = 0; k < num_termini; k++){
            List<Double> val_norm = new ArrayList<>();
            for(int y = 0; y < lung_serie; y++){
                 
                val_norm.add((l.get(k).getVal().get(y) - medie.get(y))/varianze.get(y));
            }
            l.get(k).set_val_norm(val_norm);
            
        }
        
    }
    
    /**
     * Metodo che ricopia i valori senza normalizzarli
     * @param l lista di time series
     */
    void avoid_normalization(List<Time_series> l){
        for(int i = 0; i < l.size(); i++){
            l.get(i).set_val_norm(l.get(i).getVal());
        }
    }
    
    /**
     * metodo che filtra gli hashtag piu' frequenti
     * @param f hashtag
     * @param l lista delle time series
     */
    ArrayList<Time_series> filtra_hashtag(String f, List<Time_series> l) throws FileNotFoundException, IOException{
        System.out.println("Sto filtrando gli hashtag piu frequenti...");
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        ArrayList<String> parole_freq = new ArrayList<>();
        ArrayList<Time_series> lt = new ArrayList<>();
        //skip first line
        if (br.ready()) {
            br.readLine();
        }
        while (br.ready()) {
            line = br.readLine();
            // split values
            String[] word = line.split(",");
            parole_freq.add(word[0]);     
        }
        br.close();
        for(int i = 0; i < l.size(); i++){
            //boolean flag = false;
            for(int j = 0; j < parole_freq.size(); j++){
                if(l.get(i).getWord().equals(parole_freq.get(j))){
                    //flag = true;
                    Time_series t = l.get(i);
                    lt.add(t);
                }
              
            }
            
        }
        return lt;
    }
   
    /**
     * calcolo la mean shift di una serie
     * @param t serie
     */
    public void meanShift(Time_series t) {
        
        double sum_pre_j = 0;
        double sum_post_j = 0;
        
        List<Double> meanShift = new ArrayList<>();
        for (int j = 0; j < t.getVal_norm().size()-1; j++) {
            //after j
            for (int k = j + 1; k < t.getVal_norm().size(); k++) {
                sum_post_j += t.getVal_norm().get(k);
            }
            sum_post_j = sum_post_j / (t.getVal_norm().size() - (j + 1));
            //before j
            for (int k = 0; k <= j; k++) {
                sum_pre_j += t.getVal_norm().get(k);
            }
            sum_pre_j = sum_pre_j / (j + 1);
            meanShift.add(sum_post_j - sum_pre_j);
            
            sum_post_j = 0;
            sum_pre_j = 0;
        }
        t.setMean_shift(meanShift);
        
    }
    
    /**
     * calcolo i bootstrap di una serie
     * @param t serie
     * @param num_bs numero campioni
     * @return lista di campioni
     */
    public List<Time_series> bootstrapping(Time_series t, int num_bs) {
        
        List<Time_series> bs = new ArrayList<>();

        for (int i = 0; i < num_bs; i++) {
            
            List<Double> temp = new ArrayList<>(t.getVal_norm());
            java.util.Collections.shuffle(temp);
            String s = t.getWord()+i;
            //sto inserendo nei campioni i val come attributo val normale, non normalizzato
            Time_series copia = new Time_series(i,s,temp);
            copia.set_val_norm(temp);
            bs.add(copia);
        }
        return bs;
    }

    /**
     * calcolo i pvalue di una serie
     * @param t serie
     * @param samples campioni di bootstrap 
     */
    public void computePValue(Time_series t, List<Time_series> samples) {
       
        int bootstrap = samples.size();
        int cont = 0;
        List<Double> pvalue = new ArrayList<>();
        for(int i = 0; i < t.getMean_shift().size(); i++){
            for(int j = 0; j < bootstrap; j++){
                double v = t.getMean_shift().get(i);
                double b = samples.get(j).getMean_shift().get(i);
                if(b > v){
                    cont++;
                }
            }
           
            double va = (double)cont/bootstrap; 
            pvalue.add(va);
            
            cont = 0;      
        }
        t.set_pvalue(pvalue);
    }
    
}
