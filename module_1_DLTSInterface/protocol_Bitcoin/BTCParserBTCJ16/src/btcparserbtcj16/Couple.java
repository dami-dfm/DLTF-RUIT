/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btcparserbtcj16;

import java.util.Comparator;

/**
 * 
 * @author brodo
 */
public class Couple<T1 extends Comparable<T1>, T2 extends Comparable<T2>>  implements Comparable<Couple>, Comparator<Couple> {
    private T1 id1;//represents the txHash id
    private T2 id2;//represents the position
    
    public Couple(T1 i1,T2 i2){
        id1=i1;id2=i2;
    }

    public T1 getFirst(){return id1;}
    public T2 getSecond(){return id2;}
    
    @Override
    public int compareTo(Couple o) {
        int temp=this.id1.compareTo(((Couple<T1,T2>) o).getFirst());
        if(temp==0){
            return this.id2.compareTo(((Couple<T1,T2>) o).getSecond());
        }else return temp;
    }

    @Override
    public int compare(Couple o1, Couple o2) {
        return o1.compareTo(o2);
    }
    @Override
  public int hashCode() { return id1.hashCode() ^ id2.hashCode(); }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Couple)) return false;
    return this.id1.equals(((Couple) o).getFirst()) &&
           this.id2.equals(((Couple) o).getSecond());
  }

    @Override
    public String toString(){
        return id1.toString()+","+id2.toString();
    }
}
