/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btcparserbtcj16;

import java.util.Comparator;

/**
 *Uses ints to save memory. So far there exists far less transactions than MAX_INT
 * 
 * @author brodo
 */
public class TxOutputCouple  implements Comparable<TxOutputCouple>, Comparator<TxOutputCouple> {
    private int id1;//represents the address id
    private long id2;//represents the amount
    
    public TxOutputCouple(int i1,long i2){
        id1=i1;id2=i2;
    }

    public int getAddrId(){return id1;}
    public long getAmount(){return id2;}
    
    //ordino prima su addre poi su amount se addr sono uguali
    @Override
    public int compareTo(TxOutputCouple o) {
        if(this.id1<o.id1) return -1;
        else if(this.id1>o.id1) return +1;
        else if(this.id2<o.id2) return -1;
        else if(this.id2>o.id2) return +1;
        else return 0;
    }

    @Override
    public int compare(TxOutputCouple o1, TxOutputCouple o2) {
        return o1.compareTo(o2);
    }
    
    @Override
    public boolean equals(Object o2){
        return (id1==((TxOutputCouple)o2).id1)&&(id2==((TxOutputCouple)o2).id2);
    }
    @Override
    public String toString(){
        return id1+","+id2;
    }
}
