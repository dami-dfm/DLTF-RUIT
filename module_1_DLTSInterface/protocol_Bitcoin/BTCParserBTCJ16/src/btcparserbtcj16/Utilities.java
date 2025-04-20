/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btcparserbtcj16;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
//import javax.xml.bind.DatatypeConverter;


/**
 *
 * @author brodo
 */
public class Utilities {
    public static byte[] concatenateByteArrays(byte[] b1, byte[] b2){
        byte[] bres=new byte[b1.length+b2.length];
        System.arraycopy(b1, 0, bres, 0, b1.length);
        System.arraycopy(b2, 0, bres, b1.length, b2.length);
        return bres;
        /*ByteArrayOutputStream buf = new ByteArrayOutputStream();
	buf.write(b1);
	buf.write(b2);
	return buf.toByteArray();*/
    }
    public static int readUnsignedByte(byte b){
        return (int)(b & 0xff);
    }
    public static long readUnsignedInt(int b){
        return ((long)b) & 0xffffffffL;
    }
    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }
    public static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            hexString.append(byteToHex(byteArray[i]));
        }
        return hexString.toString();
    }    
//public static String toHexString(byte[] array) {
//    return DatatypeConverter.printHexBinary(array);
//}

//public static byte[] toByteArray(String s) {
//    return DatatypeConverter.parseHexBinary(s);
//}
public static String timeHumanReadable(long millis){
    String res=millis%1000+" ms";
    millis=(long)(millis/1000);
    res=millis%60+" s "+res;
    millis=(long)(millis/60);
    res=millis%60+" m "+res;
    millis=(long)(millis/60);
    res=millis+" h "+res;
    return res;
}

// The method returns a list of files in a directory according to a certain
    // pattern (block files have name blkNNNNN.dat)
    public static List<File> buildList(String PREFIX) {
        List<File> list = new LinkedList<File>();
        for (int i = 0; true; i++) {
            File file = new File(PREFIX + String.format(Locale.US, "blk%05d.dat", i));
            if (!file.exists())
                break;
            list.add(file);
        }
        return list;
    }
    
    // The method returns a list of files in a directory according to a certain
    // pattern (block files have name blkNNNNN.dat)
    public static List<File> buildListFake(String TESTFILE) {
        List<File> list = new LinkedList<File>();
        list.add(new File(TESTFILE));
        return list;
    }

    // check if the string represents a valid directory path (not that exists), i.e.
    //checks only that ends with file separator, if not it adds it to the string
    public static String directory(String path) {
        if(path.endsWith(File.separator))
            return path;
        else
            return path+File.separator;
    }

    public static int readNumebrOfLines(File f) throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(f));
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }
    public static int readNumebrOfSeparatedValuesFirstLine(File f,String separator) throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line; int ret=0;
        if((line=reader.readLine()) != null){
            ret=line.split(separator).length;
        }
        reader.close();
        return ret;
    }
    //to be used in stead of waitFor() for subprocesses spawning processes
    public static int runExternalProcess(String[] args) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(args[0], args[1], args[2]);
        pb.redirectErrorStream(true);
        Process proc = pb.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = null;
        while ( (line = br.readLine()) != null){
            System.out.println("Waiting for external process to complete: "+args[0]+" "+args[1]+" "+args[2]);
            TimeUnit.SECONDS.sleep(10);
        }br.close();
        int exitVal = proc.waitFor();
        proc.getInputStream().close();
        proc.getOutputStream().close();
        proc.getErrorStream().close();
        return exitVal;
    }
    public static String getDirFromPath(String path){
        return getDirFromFile(new File(path));
    }
    public static String getDirFromFile(File f){
        return f.getAbsolutePath().substring(0,f.getAbsolutePath().lastIndexOf(File.separator)+1);
    }
    public static void copyFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }
    public static boolean existsFile(String fileName){
        return (new File(fileName)).exists();
    }
    
    /* s must be an even-length string. */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

}
