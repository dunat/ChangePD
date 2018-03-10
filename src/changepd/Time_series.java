/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package changepd;

import java.util.List;

/**
 *
 * @author rodman
 */
public class Time_series {
    
    private final String word;
    private final int ind;
    private final List<Double> val;
    private List<Double> val_norm;
    private List<Double> mean_shift;
    private List<Double> pvalue;
    
    public Time_series(int i, String s, List<Double> l){
        this.ind = i;
        this.word = s;
        this.val = l;
    }

    public String getWord() {
        return word;
    }

    public int getInd() {
        return ind;
    }

    public List<Double> getVal() {
        return val;
    }

    public List<Double> getVal_norm() {
        return val_norm;
    }
    
    public void set_val_norm(List<Double> l){
        this.val_norm = l;
    }

    public List<Double> getMean_shift() {
        return mean_shift;
    }

    public void setMean_shift(List<Double> mean_shift) {
        this.mean_shift = mean_shift;
    }
    
    public void set_pvalue(List<Double> l){
        this.pvalue = l;
    }

    public String toString_pvalue() {
        return "Time_series{" + "word=" + word + " pvalue=" + pvalue + '}';
    }
    
    @Override
    public String toString() {
        return "Time_series{" + "word=" + word + ", ind=" + ind + ",\n val=" + val + ",\n val_norm=" + val_norm + ",\n mean_shift=" + mean_shift + ",\n pvalue=" + pvalue + '}';
    }

    

    

    
}
