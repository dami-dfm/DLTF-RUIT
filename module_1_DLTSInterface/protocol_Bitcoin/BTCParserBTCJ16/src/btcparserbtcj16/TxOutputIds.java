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
public class TxOutputIds  implements Comparable<TxOutputIds>, Comparator<TxOutputIds> {
    private int id1;//represents the txHash id
    private int id2;//represents the position
    
    public TxOutputIds(int i1,int i2){
        id1=i1;id2=i2;
    }

    public int getTXid(){return id1;}
    public long getPos(){return id2;}
    
    //ordino prima su primo long e poi su secondo se primi sono uguali
    @Override
    public int compareTo(TxOutputIds o) {
        if(this.id1<o.id1) return -1;
        else if(this.id1>o.id1) return +1;
        else if(this.id2<o.id2) return -1;
        else if(this.id2>o.id2) return +1;
        else return 0;
    }

    @Override
    public int compare(TxOutputIds o1, TxOutputIds o2) {
        return o1.compareTo(o2);
    }
    
    @Override
    public boolean equals(Object o2){
        return (id1==((TxOutputIds)o2).id1)&&(id2==((TxOutputIds)o2).id2);
    }
    @Override
    public String toString(){
        return id1+","+id2;
    }
}
