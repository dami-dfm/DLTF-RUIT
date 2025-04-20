/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commoninputsclustering;
import com.martiansoftware.jsap.JSAPException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.Transform;
import it.unimi.dsi.webgraph.algo.ConnectedComponents;

/**
 *
 * @author brodo
 */
public class ClusteringManagerWG {

    //DEBUG///////////////
    public static void main(String[] args) throws Exception{
        String txlist="/home/brodo/Università/Dottorato/NewBTCgraphAnalysis/txlist.csv";
        String outmap="/home/brodo/Università/Dottorato/NewBTCgraphAnalysis/clusterMap";
        fromTxList2ClusteringMap(txlist,outmap);
    }
    
    /**
     * Implements the "common inputs clustering heuristic" by taking in input
     * the complete transaction list and producing an edge list in webgraph tab
     * separated accepted format representing the connected components as same
     * inputs of transactions.
     * 
     * INPUT:
     * Transaction list is in the format general:inputs:outputs (one transaction
     * per line):
     * 
     * timestamp,blockheight,tx_Id,isCoinbase,feeAmount,estimatedSize:
     * [inAddr_Id,amount,prevTx_Id,prevTxPos[;]]*:
     * [outAddr_Id,amount,script_Id][;outAddr_Id,amount,script_Id]*
     * 
     * OUTPUT:
     * Mapping addrId-> cluserId it belongs to
     * cluster are computed from the connected component finding of the induced 
     * graph from common imputs. 
     * 
     * @param inBC transaction list representing the BC
     * @param outMap path+base name (ONLY!!) of mapping addrId->clustId
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     * @throws btcblockchainparser.InconsistencyException
     */
    public static void fromTxList2ClusteringMap(String inBC, String outMap) throws InterruptedException, IOException, InconsistencyException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, JSAPException{
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
    }
    
    private static int saveClusterELfromTxList(String in,String out) throws FileNotFoundException, IOException, InconsistencyException{
        BufferedReader br = new BufferedReader(new FileReader(new File(in)));
        BufferedWriter bw=new BufferedWriter(new FileWriter(new File(out)));
        int lastAddrId=0;//for sure the last seen addr is an output (since inputs
        // can only spend already existing outputs)
        int tempMaxAddr=0;
        String line;
        String[] lineArrOuter,lineArrInner; String firstInAddr;
        while((line = br.readLine()) != null){
            lineArrOuter=line.split(":");
            if((lineArrOuter[1]==null)||(lineArrOuter[1].equals(""))){
                //do nothing if it is a coinbase
            }else{
                //there is at least one input
                //create a new edge between the first input and all others
                lineArrInner=lineArrOuter[1].split(";");
                firstInAddr=lineArrInner[0].split(",")[0];
                for(int i=1;i<lineArrInner.length;i++){
                    if(!firstInAddr.equals(lineArrInner[i])){
                        //avoid to save useless self loops
                        bw.write(firstInAddr+"\t");
                        bw.write(lineArrInner[i].split(",")[0]);
                        bw.newLine();
                    }
                }
            }
            tempMaxAddr=getMaxOutAddrId(lineArrOuter[2]);
                if(lastAddrId<tempMaxAddr)
                    lastAddrId=tempMaxAddr;
        }
        br.close();bw.close();
        return lastAddrId+1;
    }
    private static int getMaxOutAddrId(String outs){
        int res=0;
        for(String out:outs.split(";")){
            if(Integer.parseInt(out.split(",")[0])>res)
                res=Integer.parseInt(out.split(",")[0]);
        }
         return res;       
    }
    
    private static void WGComputeSCC(String basename,String mapOut,int numAddresses) throws IOException{
        System.out.println( "Loading the graph..." );
        ImmutableGraph graph = BVGraph.load( basename );
        System.out.println( "Symmetrizing the graph..." );
        ImmutableGraph graphSymmetric = Transform.symmetrize( graph );
        System.out.println( "Computing the WCC..." );
        ConnectedComponents cc = ConnectedComponents.compute(graphSymmetric,0,null);
        System.out.println( "NumberOfComponents (i.e. Clusters) " + cc.numberOfComponents + " over " + numAddresses + " Addresses.");
        saveClustMapping(mapOut,cc.component,cc.numberOfComponents,numAddresses);
    }
    //saves the mapping addrId->clusterId from the arry into the file
    private static void saveClustMapping(String mapFile,int[] components,int numClusters, int numAddresses) throws IOException{
        BufferedWriter bwout=new BufferedWriter(new FileWriter(mapFile));
        for( int i=0; i<components.length; i++) {
            bwout.write(i + "," + components[i]);
            bwout.newLine();
            //System.out.println( i + " " + roles[i] );
        }//now all addresses in the cluter graph has a component, BUT all nodes
        //not in the graph, i.e. singletons with id > last component in graph DO NOT APPEAR!
        
        for( int i=components.length; i<numAddresses; i++) {
            bwout.write(i + "," + numClusters);
            bwout.newLine();
            numClusters++;
            //System.out.println( i + " " + roles[i] );
        }
        
        bwout.close();
    }
    public static String getDirFromPath(String path){
        return getDirFromFile(new File(path));
    }
    public static String getDirFromFile(File f){
        return f.getAbsolutePath().substring(0,f.getAbsolutePath().lastIndexOf(File.separator)+1);
    }
    //sorts EL according to inputs first
    private static void sortEL(String in,String out) throws InterruptedException, IOException{
        Process p = Runtime.getRuntime().exec(new String[]{"bash","-c","sort --field-separator=',' -T "+getDirFromPath(out)+" -k 1n,1 -k 2n,2 "+in+" -o "+out});
        p.waitFor();
    }
    
    //PRE: in is ordered (with respect to the first column)
    private static void removeRepetitionsFromEL(String in,String out) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(in)));
        BufferedWriter bw=new BufferedWriter(new FileWriter(new File(out)));
        HashSet<String> list=new HashSet<String>(10000);
        String current="";//remembers the current input
        String line; String[] tokens;
        while((line = br.readLine()) != null){
            tokens=line.split("\t");
            if(!current.equals(tokens[0])){
                //we have a new input
                current=tokens[0];
                list=new HashSet<String>(10000);
            }
            if(!tokens[0].equals(tokens[1])){//not a self loop
                //check if it is a repeated edge
                if(list.add(tokens[1])){
                    //true iff the set did not already contain the specified element
                    bw.write(tokens[0]+"\t"+tokens[1]);
                    bw.newLine();
                }
            }
        }
        br.close();bw.close();   
    }
}