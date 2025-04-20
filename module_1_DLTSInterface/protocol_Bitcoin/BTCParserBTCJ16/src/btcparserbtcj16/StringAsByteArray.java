/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btcparserbtcj16;

import java.util.Comparator;

/**
 *This implementation saves strings of 34 characters in 72 bytes instead of the 
 * 112 of traditional String class.
 * @author brodo
 */
public class StringAsByteArray implements Comparable<StringAsByteArray>, Comparator<StringAsByteArray>{
    private byte[] str;
    
    public StringAsByteArray(String s){
        str=s.getBytes();
    }
    public int length(){
        return str.length;
    }
    //NOTE: comparison is made lexigraphically, with end of array counting as lower
    //than any other symbol
    @Override
    public int compareTo(StringAsByteArray o) {
        for(int i=0;i<this.str.length;i++){
            if(o.str.length<=i)
                return 1;
            else{
                if(str[i]>o.str[i])
                    return 1;
                else if(str[i]<o.str[i])
                    return -1;
                //else they are equal, so continue loop
            }
        }
        //if we get here all items are equal and o is not shorter than this
        if(o.str.length>str.length)
            return -1;
        else
            return 0;
    }

    @Override
    public int compare(StringAsByteArray o1, StringAsByteArray o2) {
        return o1.compareTo(o2);
    }
    //@Override
  //public int hashCode() { return str.hashCode();}

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof StringAsByteArray)) return false;
    if(((StringAsByteArray)o).str.length!=str.length) return false;
    for(int i=0;i<str.length;i++){
        if(str[i]!=((StringAsByteArray)o).str[i])
            return false;
    }return true;
  }

    @Override
    public String toString(){
        StringBuilder res=new StringBuilder("");
        for(int i=0;i<str.length;i++){
            res.append((char)str[i]);
        }return res.toString();
    }
}
