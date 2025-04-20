/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pandasbtcdatasetcreator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author brodo
 */
public class PandasBTCDatasetCreator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        //args[0] input file with txs list
        //args[1] temp file where to save filtered txs list
        //args[2] max timestamp over wich to ignore txs
        //args[3] directory where to save the resulting csv files
        ///////////////////////////filterTxList(args[0], Integer.parseInt(args[2]), args[1]);
        //args[0] input file with txs list
        //args[1] max blockheight (included) over wich to ignore txs
        //args[2] directory where to save the resulting csv files
        fromTxListToCsvsMaxBlockHeight(args[0],Integer.parseInt(args[1]),args[2]);
        ////////////////checkTemporalConsistency(args[0],args[1]);
        /*
        NEW time incosistency, transaction in block 10565423 spending from block 10565349
NEW time incosistency, transaction in block 10565429 spending from block 10565344
NEW time incosistency, transaction in block 10565431 spending from block 10565340
NEW time incosistency, transaction in block 10565435 spending from block 10565336
NEW time incosistency, transaction in block 10565442 spending from block 10565337
NEW time incosistency, transaction in block 10565458 spending from block 10565347
NEW time incosistency, transaction in block 10565468 spending from block 10565348
NEW time incosistency, transaction in block 10565477 spending from block 10565358
NEW time incosistency, transaction in block 10565477 spending from block 10565347
NEW time incosistency, transaction in block 10565477 spending from block 10565356
NEW time incosistency, transaction in block 10565477 spending from block 10565355
NEW time incosistency, transaction in block 10565480 spending from block 10565355
NEW time incosistency, transaction in block 10565497 spending from block 10565347
NEW time incosistency, transaction in block 10565507 spending from block 10565348
FIRST SCAN END - all 21378770 transaction/lines processed and added to the map.
FOUND: 66679 time incosistencies and 0 block height incosistencies.
FOUND: 7125 maximum time incosistency.

        */
    }
    
    /**
     * Given a list of transactions in the format generalInfo:inputs:outputs, one tx per line
     * builds a set of three csv files (generalInfos, inputs, and outputs) in the specified
     * directory. All files can be joined through the txId field.
     * @param inTxList
     * @param outCsvsFolder 
     */
    public static void fromTxListToCsvs(String inTxList, String outCsvsFolder) throws IOException{
        long time=System.currentTimeMillis();
        System.out.println("START - writing all transactions from file "+ inTxList +" on files transactions.csv inputs.csv outputs.csv in the folder "+ outCsvsFolder +" .");
        outCsvsFolder = directory(outCsvsFolder);
        String txOutputFile = outCsvsFolder+"transactions.csv";
        String inOutputFile = outCsvsFolder+"inputs.csv";
        String outOutputFile = outCsvsFolder+"outputs.csv";
        BufferedWriter bwTx=new BufferedWriter(new FileWriter(txOutputFile));
        BufferedWriter bwIn=new BufferedWriter(new FileWriter(inOutputFile));
        BufferedWriter bwOut=new BufferedWriter(new FileWriter(outOutputFile));
        
        BufferedReader br= new BufferedReader(new FileReader(inTxList));
        int txs = 0;
        String line, txId;
        String[] lineArr, lineArrInner;
        int posCounter;
        while((line = br.readLine()) != null){
            lineArr=line.split(":");
            txId = lineArr[0].split(",")[2];
            
            //save general info
            bwTx.write(lineArr[0]);
            bwTx.newLine();
            
            //save all inputs (if any)
            if(!lineArr[1].equals("")){
                lineArrInner = lineArr[1].split(";");
                for(String input:lineArrInner){
                    bwIn.write(txId+","+input);
                    bwIn.newLine();
            }}
            
            //save all outputs
            lineArrInner = lineArr[2].split(";");
            posCounter = 0;
            for(String output:lineArrInner){
                bwOut.write(txId+","+posCounter+","+output);
                bwOut.newLine();
                posCounter++;
            }
            
            txs++;
            if(txs%1000000==0)
                System.out.println(txs+" transactions/lines processed.");
        }
    
    
        br.close();
        bwTx.close();
        bwIn.close();
        bwOut.close();
        System.out.println("END - all "+txs+" transaction/lines processed in "+((System.currentTimeMillis()-time)/1000)+" seconds.");
        
    }
    
    
    /**
     * Given a list of transactions in the format generalInfo:inputs:outputs, one tx per line
     * builds a set of three csv files (generalInfos, inputs, and outputs) in the specified
     * directory. All files can be joined through the txId field.
     * Only transactions with blockheight up to the value provided (included) are considered.
     * @param inTxList
     * @param outCsvsFolder 
     */
    public static void fromTxListToCsvsMaxBlockHeight(String inTxList, int maxBlockHeight, String outCsvsFolder) throws IOException{
        long time=System.currentTimeMillis();
        System.out.println("START - writing all transactions up to block height "+maxBlockHeight+" from file "+ inTxList +" on files transactions.csv inputs.csv outputs.csv in the folder "+ outCsvsFolder +" .");
        outCsvsFolder = directory(outCsvsFolder);
        String txOutputFile = outCsvsFolder+"transactions.csv";
        String inOutputFile = outCsvsFolder+"inputs.csv";
        String outOutputFile = outCsvsFolder+"outputs.csv";
        BufferedWriter bwTx=new BufferedWriter(new FileWriter(txOutputFile));
        BufferedWriter bwIn=new BufferedWriter(new FileWriter(inOutputFile));
        BufferedWriter bwOut=new BufferedWriter(new FileWriter(outOutputFile));
        
        BufferedReader br= new BufferedReader(new FileReader(inTxList));
        int txs = 0;
        String line, txId;
        String[] lineArr, lineArrInner;
        int posCounter;
        while((line = br.readLine()) != null){
            lineArr=line.split(":");
            
            if(Integer.parseInt(lineArr[0].split(",")[1]) > maxBlockHeight)
                break;
            
            txId = lineArr[0].split(",")[2];
            
            //save general info
            bwTx.write(lineArr[0]);
            bwTx.newLine();
            
            //save all inputs (if any)
            if(!lineArr[1].equals("")){
                lineArrInner = lineArr[1].split(";");
                for(String input:lineArrInner){
                    bwIn.write(txId+","+input);
                    bwIn.newLine();
            }}
            
            //save all outputs
            lineArrInner = lineArr[2].split(";");
            posCounter = 0;
            for(String output:lineArrInner){
                bwOut.write(txId+","+posCounter+","+output);
                bwOut.newLine();
                posCounter++;
            }
            
            txs++;
            if(txs%1000000==0)
                System.out.println(txs+" transactions/lines processed.");
        }
    
    
        br.close();
        bwTx.close();
        bwIn.close();
        bwOut.close();
        System.out.println("END - only "+txs+" transaction/lines processed in "+((System.currentTimeMillis()-time)/1000)+" seconds.");
        
    }
    
    /**
     * Reads a transactions list and writes all transactions ignoring all txs:
     * - with timestamp strictly greater than maxTimestamp
     * - not connected to any other transaction, i.e. :
     *      - not spending any previous output
     *      - with only outputs that are never spent
     * @param inTxList
     * @param maxTimestamp
     * @param outTxList
     * @throws IOException 
     */
    public static void filterTxList(String inTxList, int maxTimestamp, String outTxList) throws IOException{
        //we scan all txs up to desired tiemstamp once to build a list of "connected" txIds
        //a txId is connected if the corresponding transaction:
        //1) has at least one input
        //2) has at least one output that will be spent within the maxTimestamp time
        //to compute 2) we do a first pass remembering all txIds of all inputs
        
        long timeALL=System.currentTimeMillis();
        System.out.println("START - reading all transactions from file "+ inTxList +" .");
        BufferedReader br= new BufferedReader(new FileReader(inTxList));
        int txs = 0;
        String line;
        String[] lineArr, lineArrInner;
        
        TreeSet<String> connectedTxIds = new TreeSet<String>();
        
        while((line = br.readLine()) != null){
            lineArr=line.split(":");
            if(Integer.parseInt(lineArr[0].split(",")[0]) > maxTimestamp)
                break;
            
            //if it has inputs
            if(!lineArr[1].equals("")){
                //save the current transaction txId as connected
                connectedTxIds.add(lineArr[0].split(",")[2]);
                //save all spent inputs transaction txId as connected
                lineArrInner = lineArr[1].split(";");
                for(String input:lineArrInner){
                    connectedTxIds.add(input.split(",")[2]);
                }
            }
            
            txs++;
            if(txs%1000000==0)
                System.out.println("FIRST SCAN - "+txs+" transactions/lines processed.");
        }
        br.close();
        long time1=System.currentTimeMillis();
        System.out.println("FIRST SCAN END - all "+txs+" transaction/lines with timestamp lesser or equal than "+maxTimestamp+" of which "+connectedTxIds.size()+" are connected, processed in "+((time1-timeALL)/1000)+" seconds.");
    
        br= new BufferedReader(new FileReader(inTxList));
        txs = 0;
        int savedTxs = 0;
        BufferedWriter bw=new BufferedWriter(new FileWriter(outTxList));
        
        while((line = br.readLine()) != null){
            lineArr=line.split(":");
            if(Integer.parseInt(lineArr[0].split(",")[0]) > maxTimestamp)
                break;
            
            //if it is connected write it to the output
            if(connectedTxIds.contains(lineArr[0].split(",")[2])){
                bw.write(line);
                bw.newLine();
                savedTxs++;
            }
            
            txs++;
            if(txs%1000000==0)
                System.out.println("SECOND SCAN - "+txs+" transactions/lines processed.");
        }
        br.close();
        bw.close();
        long time2=System.currentTimeMillis();
        System.out.println("SECOND SCAN END - all "+txs+" transaction/lines with timestamp lesser or equal than "+maxTimestamp+" of which "+connectedTxIds.size()+" are connected, processed in "+((time2-time1)/1000)+" seconds.");
        System.out.println("END - "+savedTxs+" transactions saved in file outTxList (of "+connectedTxIds.size()+" conencted) over "+txs+" transactions ("+(savedTxs/txs*100)+"%) in overall time "+((time2-timeALL)/1000)+" seconds.");       
    }
    
    /*
    long time=System.currentTimeMillis();
        //read TxList and produce edge list from it (unordered and with repetitions)
        int numAddresses=saveClusterELfromTxList(inBC,outMap+"TEMP1");
        System.out.println("Done obtaining EdgeList for clustering from TransactionList in time"+((System.currentTimeMillis()-time)/1000)+" seconds.");
        time=System.currentTimeMillis();
        //orders the edge list
        sortEL(outMap+"TEMP1",outMap+"TEMP2");
        System.out.println("Done sorting temporary clustering EdgeList in time"+((System.currentTimeMillis()-time)/1000)+" seconds.");
        time=System.currentTimeMillis();
        //removes repetitions
        removeRepetitionsFromEL(outMap+"TEMP2",outMap+"TEMP1");
        System.out.println("Done removing repetitions from temporary clustering EdgeList in time"+((System.currentTimeMillis()-time)/1000)+" seconds.");
        time=System.currentTimeMillis();
        //saves the graph in webgraph format
        WGConverter.convert(outMap+"TEMP1");
        //computes the connected components and saves the result on a file remembering the mapping
        WGComputeSCC(outMap+"TEMP1",outMap+".csv",numAddresses);
        System.out.println("Done obtaining mapping with WebGraph from temporary clustering EdgeList in time"+((System.currentTimeMillis()-time)/1000)+" seconds.");
        (new File(outMap+"TEMP1.graph")).delete();
        (new File(outMap+"TEMP1.offset")).delete();
        (new File(outMap+"TEMP1.properties")).delete();
        (new File(outMap+"TEMP1")).delete();
        (new File(outMap+"TEMP2")).delete();
    */
    
    // check if the string represents a valid directory path (not that exists), i.e.
    //checks only that ends with file separator, if not it adds it to the string
    public static String directory(String path) {
        if(path.endsWith(File.separator))
            return path;
        else
            return path+File.separator;
    }
    
    public static void checkTemporalConsistency(String inputsFile, String txsFile) throws FileNotFoundException, IOException{
        //load map with blockHeight,timestamp
        TreeMap<Integer, int[]> map = new TreeMap<Integer, int[]>();
        BufferedReader br= new BufferedReader(new FileReader(txsFile));
        int txs = 0;
        String line;
        String[] lineArr;
        int[] tempArr;
        
        while((line = br.readLine()) != null){
            lineArr=line.split(",");
            tempArr = new int[2];
            tempArr[0] = Integer.parseInt(lineArr[0]);
            tempArr[1] = Integer.parseInt(lineArr[1]);
            map.put(Integer.parseInt(lineArr[2]), tempArr);
            txs++;
            if(txs%1000000==0)
                System.out.println("FIRST SCAN - "+txs+" transactions/lines processed.");
        }
        br.close();
        System.out.println("FIRST SCAN END - all "+txs+" transaction/lines processed and added to the map.");
        
        br= new BufferedReader(new FileReader(inputsFile));
        txs = 0;
        int numTimeInconsistencies = 0, numHeightInconsistencies = 0, maxTimeInconsistency = 0;
        while((line = br.readLine()) != null){
            lineArr=line.split(",");
            //check that time is consistent
            //check that block height is consistent
            if(map.get(Integer.parseInt(lineArr[0]))[0]<map.get(Integer.parseInt(lineArr[1]))[0]){
                numTimeInconsistencies++;
                System.out.println("NEW time incosistency, transaction in block "+map.get(Integer.parseInt(lineArr[0]))[1]+" spending from block "+map.get(Integer.parseInt(lineArr[1]))[1]);
                if(maxTimeInconsistency<(map.get(Integer.parseInt(lineArr[1]))[0]-map.get(Integer.parseInt(lineArr[0]))[0]))
                    maxTimeInconsistency = map.get(Integer.parseInt(lineArr[1]))[0]-map.get(Integer.parseInt(lineArr[0]))[0];
            }
            if(map.get(Integer.parseInt(lineArr[0]))[1]<map.get(Integer.parseInt(lineArr[1]))[1])
                numHeightInconsistencies++;
            txs++;
            if(txs%1000000==0)
                System.out.println("FIRST SCAN - "+txs+" transactions/lines processed.");
        }
        br.close();
        System.out.println("FIRST SCAN END - all "+txs+" transaction/lines processed and added to the map.");
        System.out.println("FOUND: "+numTimeInconsistencies+" time incosistencies and "+numHeightInconsistencies+" block height incosistencies.");
        System.out.println("FOUND: "+maxTimeInconsistency+" maximum time incosistency.");
    }
}
