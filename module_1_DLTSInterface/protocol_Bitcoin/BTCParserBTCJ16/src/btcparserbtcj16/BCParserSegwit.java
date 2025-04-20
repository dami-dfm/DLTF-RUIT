/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btcparserbtcj16;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;
 
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.utils.BlockFileLoader;
import org.bitcoinj.core.TransactionWitness;
 
/**
 * Substitues undecodable addresses with #id, with incremental value of id. Such
 * values are associated with regular ids during substitution as usual
 *
 * @author brodo
 */
public class BCParserSegwit {
    // Location of block files
    String folder;
    int DEBUGtotalTxs;
    int DEBUGcoinbaseCounter;
    int nullAddressesNum;
    int witnessInputsNum;
    int DEBUGwitnessTxs;
    int[] DEBUGscriptTypes;
    
    public BCParserSegwit(String f){
        folder=f;DEBUGtotalTxs=0;DEBUGcoinbaseCounter=0;nullAddressesNum=0; witnessInputsNum=0;
        DEBUGwitnessTxs=0;
        DEBUGscriptTypes=new int[ScriptType.SUPPORTEDSCRIPTTYPES];
        for(int i=0;i<DEBUGscriptTypes.length;i++) DEBUGscriptTypes[i]=0;
    }
    
    //Parse without substitutions and no UTXO
    //no UTXO without substitutions because it would be very memory intensive
    public void parseExactNoUtxo(File out,int n) throws IOException {
        NetworkParameters np = new MainNetParams();
        Context.getOrCreate(MainNetParams.get());
        //Creates a BlockFileLoader object by passing a list of .dat files.
        BlockFileLoader loader = new BlockFileLoader(np,Utilities.buildList(folder));
        BufferedWriter bw=new BufferedWriter(new FileWriter(out));
                
        int blockCounter=0;
        //NOTE: blocks are not ordered, so this is NOT the same as block height!!
        for (Block block : loader) {
            if(blockCounter>=n)
                break;
            //if(DEBUGtotalTxs-DEBUGcoinbaseCounter>=5)
            //    break;
            
            if(blockCounter%20000==0){
                System.out.println("Analysed "+blockCounter+" NOT ORDERED blocks.");
                System.out.println(blockCounter+" - "+block.getHashAsString());
            }
            //parseBlockExact(block,bw,blockCounter);
            parseBlockExact(block,bw);
            blockCounter++;
            
            /*if(DEBUGwitnesTxs>=DEBUGwitnesLimit){//DEBUG//////
                System.out.println("Stopping after "+DEBUGwitnesLimit+" segwit transactions found.");
                break;//DEBUG//////
            }//DEBUG//////*/
            /*if(DEBUGwitnesTxs>=2){//DEBUG//////
                System.out.println("Stopping after the two segwit transactions found.");
                break;//DEBUG//////
            }*///DEBUG//////
            
        } // End of iteration over blocks
        bw.close();
        System.out.println("TotalTxs "+DEBUGtotalTxs+" , of which coinbases are "+DEBUGcoinbaseCounter+ " and "+DEBUGwitnessTxs+" are witness transactions (with a total of "+witnessInputsNum+" witness inputs found).");
        System.out.println("Scripts found :");
        int ttemp=0;
        for(int i=0;i<DEBUGscriptTypes.length;i++){
            System.out.println(DEBUGscriptTypes[i]+" "+ScriptType.typeName(i));
            ttemp+=DEBUGscriptTypes[i];
        }System.out.println("Total : "+ttemp+" ("+nullAddressesNum+" null addresses).");
    }  

    /**
     * Outputs script info for the given block as:
     * one tx per line
     * generalInfo ':' InputsInfo (empty if coinbase) ':' OutputsInfo
     * generalInfo :=  timeStamp',' blockHash',' txHash',' isCoinbase',' txSizeEStimate
     * 
     * 
     * @param block
     * @param bw
     * @throws IOException 
     */
    public void parseBlockExact(Block block,BufferedWriter bw) throws IOException{
        boolean isCoinbase;
        boolean first;
        StringBuilder line;
        for ( Transaction tx: block.getTransactions() ) {
            DEBUGtotalTxs++;
            
            //write tx general infos:
            //timestamp,blockhash,txHash,isCoinbase,estimatedSize,hasWitness:
            line = new StringBuilder();
            line.append(block.getTimeSeconds());line.append(",");line.append(block.getHashAsString());line.append(",");line.append(tx.getTxId().toString());line.append(",");
            if(tx.isCoinBase()){
                isCoinbase=true;
                line.append("1");
            }else{
                isCoinbase=false;
                line.append("0");
            }line.append(",");line.append(tx.getOptimalEncodingMessageSize());
            line.append(",");
            if(tx.hasWitnesses()){
                DEBUGwitnessTxs++;
                line.append("1");
            }else{
                line.append("0");
            }
            //if((tx.getHashAsString()/*getTxId().toString()*/.equalsIgnoreCase("d46caae5a82266bc459c0caa7d1d8165eca315f62a0155e896c3b9b2be66aa87"))||(tx.getHashAsString()/*getTxId().toString()*/.equalsIgnoreCase("5cf160eebefc296965fad192e642817a40747e6290e7fce78f37c2424f66b026"))){//DEBUG//////
            /*if((tx.getTxId().toString().equalsIgnoreCase("d46caae5a82266bc459c0caa7d1d8165eca315f62a0155e896c3b9b2be66aa87"))||(tx.getTxId().toString().equalsIgnoreCase("5cf160eebefc296965fad192e642817a40747e6290e7fce78f37c2424f66b026"))){//DEBUG//////
                DEBUGwitnesTxs++;//DEBUG//////
            }else{//DEBUG//////
                continue;//DEBUG//////
            }//DEBUG//////
            */
            line.append(":");

            if(isCoinbase){
                DEBUGcoinbaseCounter++;
            }else{
                //not coinbase so there is at least one input
                //save inputs in the format
                //[addr,amount,prevTx_Id,prevTxPos[;]]*:
                first=true;
                for(TransactionInput ii:tx.getInputs()){
                    if(first)
                        first=false;
                    else
                        line.append(";");
                    //line.append(Utilities.byteArrayToHexString(ii.getScriptBytes()));
                    //leaves void (as "-"), unfillable fields without a UTXO map
                    line.append("-,-");
                    line.append(",");
                    line.append(ii.getOutpoint().getHash().toString());
                    line.append(","+ii.getOutpoint().getIndex());
                    if(ii.hasWitness()){
                        witnessInputsNum++;
                        //line.append(",");
                        //line.append(ii.getWitness().toString());
                    }
                }
            }
            line.append(":");
            //save outputs in the format
            //[outScriptBytes,amount][;outScriptBytes,amount]*
            //there is always at least one output
            first=true;
            for(TransactionOutput oo:tx.getOutputs()){
                if(first)
                    first=false;
                else
                    line.append(";");
                //line.append(Utilities.byteArrayToHexString(oo.getScriptBytes()));
                byte[] outScript=oo.getScriptBytes();
                String outAddr=ScriptParser.addrFromOut(outScript);
                int outType=ScriptParser.typeFromOut(outScript);
                DEBUGscriptTypes[outType]++;
                if(outAddr==null){
                    //writes '#num' as address if not decodable
                    outAddr="#"+nullAddressesNum;
                    nullAddressesNum++;
                }
                line.append(outAddr);
                line.append(",");
                line.append(oo.getValue().getValue());
                line.append(",");
                line.append(outType);
            }
            bw.write(line.toString());
            bw.newLine();
            }
    }
    //read blocks and finds the block height by building the predecessor->successor list
    public void inferrBlockHeightMap(File out,int n) throws IOException, InconsistencyException {
        System.out.println("Start inferring and saving block hash to block height mapping.");
        NetworkParameters np = new MainNetParams();
        Context.getOrCreate(MainNetParams.get());
        //String genesisHash="000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f";
        org.bitcoinj.core.Sha256Hash genesisBlockHash=org.bitcoinj.core.Sha256Hash.wrap("000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f");
        //System.out.println("hash "+hash);
        //Creates a BlockFileLoader object by passing a list of .dat files.
        BlockFileLoader loader = new BlockFileLoader(np,Utilities.buildList(folder));
        TreeMap<org.bitcoinj.core.Sha256Hash,org.bitcoinj.core.Sha256Hash> successors=new TreeMap<org.bitcoinj.core.Sha256Hash,org.bitcoinj.core.Sha256Hash>();
        int blockCounter=0;
        //NOTE: blocks are not ordered, so this is NOT the same as block height!!
        for (Block block : loader) {
            if(blockCounter>=n)
                break;
            if(blockCounter%20000==0){
                System.out.println("Analysed "+blockCounter+" NOT ORDERED blocks.");
                System.out.println("Current block: "+blockCounter+" - "+block.getHashAsString());
            }
            //for genesis block there is no predecessor
            if(!block.getHash().equals(genesisBlockHash)){
                if(successors.containsKey(block.getPrevBlockHash()))
                    throw(new InconsistencyException("CHAIN INCONSISTENCY:: Block "+block.getHashAsString()+" has predecessor "+block.getPrevBlockHash().toString()+" wich is already predecessor of "+successors.get(block.getPrevBlockHash())));
                else
                    successors.put(block.getPrevBlockHash(), block.getHash());
            }blockCounter++;
        } // End of iteration over blocks
        System.out.println("Done reading "+blockCounter+" blocks.");
        BufferedWriter bw=new BufferedWriter(new FileWriter(out));
        bw.write(genesisBlockHash.toString()+",0");
        bw.newLine();
        blockCounter=1;org.bitcoinj.core.Sha256Hash LastHash=genesisBlockHash;
        while(successors.containsKey(LastHash)){
            bw.write(successors.get(LastHash).toString()+","+blockCounter);
            bw.newLine();
            blockCounter++;
            LastHash=successors.get(LastHash);
        }
        bw.close();
        System.out.println("Last blockHeight connected found : "+(blockCounter-1));
    }
    
    //read blocks and finds the block height by building the predecessor->successor list
    public void inferrBlockHeightMapDEBUG(File out,int n) throws IOException, InconsistencyException {
        System.out.println("Start inferring and saving block hash to block height mapping.");
        NetworkParameters np = new MainNetParams();
        Context.getOrCreate(MainNetParams.get());
        //String genesisHash="000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f";
        org.bitcoinj.core.Sha256Hash genesisBlockHash=org.bitcoinj.core.Sha256Hash.wrap("000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f");
        
        org.bitcoinj.core.Sha256Hash lastBlockHash=org.bitcoinj.core.Sha256Hash.wrap("0000000000000000000d76d0994a7b93d14308450abd49b6d331ee5d7dd87e1a");
        org.bitcoinj.core.Sha256Hash missedBlockHash=org.bitcoinj.core.Sha256Hash.wrap("0000000000000000001bbb529c64ddf55edec8f4ebc0a0ccf1d3bb21c278bfa7");
        org.bitcoinj.core.Sha256Hash nextMissedBlockHash=org.bitcoinj.core.Sha256Hash.wrap("000000000000000000475404549c265739ae2da0cd176f6a60a3f2cff8c4823c");
        
        //Creates a BlockFileLoader object by passing a list of .dat files.
        BlockFileLoader loader = new BlockFileLoader(np,Utilities.buildList(folder));
        TreeMap<org.bitcoinj.core.Sha256Hash,org.bitcoinj.core.Sha256Hash> successors=new TreeMap<org.bitcoinj.core.Sha256Hash,org.bitcoinj.core.Sha256Hash>();
        int blockCounter=0;
        
///////////////save each encountered block info
        BufferedWriter bwDebug=new BufferedWriter(new FileWriter(new File(out.getPath()+"_DEBUGTEST")));
        bwDebug.write("ReadIndex,BlockHash,PrevBlockHash");
        bwDebug.newLine();
        
        
        //NOTE: blocks are not ordered, so this is NOT the same as block height!!
        for (Block block : loader) {
            if(blockCounter>=n)
                break;
            if(blockCounter%40000==0){
                System.out.println("Analysed "+blockCounter+" NOT ORDERED blocks.");
                System.out.println("Current block: "+blockCounter+" - "+block.getHashAsString());
            }
            //for genesis block there is no predecessor
            if(!block.getHash().equals(genesisBlockHash)){
                bwDebug.write(blockCounter+","+block.getHash().toString()+","+block.getPrevBlockHash().toString());////////////////
                if(successors.containsKey(block.getPrevBlockHash()))
                    throw(new InconsistencyException("CHAIN INCONSISTENCY:: Block "+block.getHashAsString()+" has predecessor "+block.getPrevBlockHash().toString()+" wich is already predecessor of "+successors.get(block.getPrevBlockHash())));
                else
                    successors.put(block.getPrevBlockHash(), block.getHash());
                if(block.getHash().equals(lastBlockHash)||block.getHash().equals(missedBlockHash)||block.getHash().equals(nextMissedBlockHash)){
                    System.out.println("Interesting block "+block.getHashAsString()+" found. Predecessor is "+block.getPrevBlockHash()+" block.");
                }
            }else{
                bwDebug.write(blockCounter+","+block.getHash().toString()+",null");////////////////
            }blockCounter++;
            bwDebug.newLine();//////////////////
        } // End of iteration over blocks
        System.out.println("Done reading "+blockCounter+" blocks.");
        bwDebug.close();
        
        BufferedWriter bw=new BufferedWriter(new FileWriter(out));
        bw.write(genesisBlockHash.toString()+",0");
        bw.newLine();
        blockCounter=1;org.bitcoinj.core.Sha256Hash LastHash=genesisBlockHash;
        while(successors.containsKey(LastHash)){
            bw.write(successors.get(LastHash).toString()+","+blockCounter);
            bw.newLine();
            blockCounter++;
            LastHash=successors.get(LastHash);
        }
        bw.close();
        System.out.println("Last blockHeight connected found : "+(blockCounter-1));
    }
    
    //PRE: outDir is a valid directory path (including separator)
    //Reads the file without substitutions and UTXO and subtitutes txHashes and 
    //addresses with incremental ids starting both from 0 
    //Then it parses the file again and fills it using an UTXO (efficient over ids instead of hashes)
    public static void readExactNoUtxoAndSubstituteIdsAndFill(String inBC, String mapBlockHash, String outDir) throws IOException, FileNotFoundException, InterruptedException {
        long time=System.currentTimeMillis();
        String mapTxHash=outDir+"mapTxHash2Ids.csv";
        String mapAddr=outDir+"mapAddr2Ids.csv";
        String tempTx= outDir+"tempTx";
        String tempBlk= outDir+"tempBlk";
        String tempBlkSorted= outDir+"tempBlkSorted";
        String tempAddr= outDir+"tempAddr";
        String finalBC= outDir+"finalSegwitBC";
        //replace blockHash with blockHeight
        System.out.println("Begin replacing block hashes.");
        //replaceHashes(tempTx,mapBlockHash,1,tempBlk);
        replaceBlockHashes(inBC,mapBlockHash,tempBlk);
        //sorting the transaction list according to block height
        sortPreservingTxOrder(tempBlk,tempBlkSorted);
        //////////////(new File(tempBlk)).delete();
        System.out.println("Done replacing block hashes in time"+((System.currentTimeMillis()-time)/1000)+" seconds.");
        time=System.currentTimeMillis();
        //replace txHash with txIds
        System.out.println("Begin replacing transaction hashes.");
        replaceHashes(tempBlkSorted,mapTxHash,2,tempTx);
        //////////////(new File(tempBlkSorted)).delete();
        //replaces addresses and null_addresses (FROM OUTPUTS ONLY) with addrIds
        System.out.println("Done replacing transaction hashes in time"+((System.currentTimeMillis()-time)/1000)+" seconds.");
        time=System.currentTimeMillis();
        System.out.println("Begin replacing addresses.");
        replaceAddresses(tempTx,mapAddr,tempAddr);
        //////////////(new File(tempTx)).delete();
        //fills inputs and fees building an UTXO
        System.out.println("Done replacing addresses in time"+((System.currentTimeMillis()-time)/1000)+" seconds.");
        time=System.currentTimeMillis();
        System.out.println("Begin filling with UTXO.");
        fillWithUtxo(tempAddr,finalBC);
        //////////////(new File(tempAddr)).delete();
        System.out.println("Done filling UTXO in time"+((System.currentTimeMillis()-time)/1000)+" seconds.");
    }
    private static void replaceHashes(String inBC,String outMap,int idx,String outBC) throws FileNotFoundException, IOException{
        BufferedReader br= new BufferedReader(new FileReader(inBC));
        BufferedWriter bw=new BufferedWriter(new FileWriter(outBC));
        BufferedWriter mapw=new BufferedWriter(new FileWriter(outMap));
        //map to remember already seen strings
        TreeMap<String,Integer> map=new TreeMap<String,Integer>();
        
        int txs=0;
        System.out.println("START - replacing hashes and saving mapping.");
        String line;int counter=0; String[] lineArrOuter,lineArrInner;
        while((line = br.readLine()) != null){
            //select the general infos (first ':' block) and then relevant info
            lineArrOuter=line.split(":");
            lineArrInner=lineArrOuter[0].split(",");
            if(!map.containsKey(lineArrInner[idx])){
                map.put(lineArrInner[idx],counter);
                mapw.write(lineArrInner[idx]+","+counter);
                mapw.newLine();
                counter++;
            }//if map contains the hash already there is nothing to be done
            for(int i=0;i<lineArrInner.length;i++){
                if(i!=0) bw.write(",");
                if(i==idx){
                    bw.write(""+map.get(lineArrInner[idx]));
                }else{
                    bw.write(lineArrInner[i]);
                }
            }
            bw.write(":");
            if((idx==2)&&(lineArrInner[3].equals("0"))){
                //If replacing txHashes we need to replace them also in the inputs
                //if it is not a coinbase
                //[inAddr_Id,amount,prevTx_Id,prevTxPos[;]]*:
                boolean first=true;
                for(String input:lineArrOuter[1].split(";")){
                    if(first)
                        first=false;
                    else
                        bw.write(";");
                    lineArrInner=input.split(",");
                    bw.write(lineArrInner[0]+","+lineArrInner[1]+",");
                    if(!map.containsKey(lineArrInner[2])){
                        map.put(lineArrInner[2], counter);
                        mapw.write(lineArrInner[idx]+","+counter);
                        mapw.newLine();
                        counter++;
                    }//if map contains the hash already there is nothing to be done
                    bw.write(map.get(lineArrInner[2])+","+lineArrInner[3]);                
                }
            }else{
                bw.write(lineArrOuter[1]);
            }
            bw.write(":");bw.write(lineArrOuter[2]);
            bw.newLine();
            txs++;
            if(txs%10000000==0)
                System.out.println(txs+" transactions processed.");
        }
        System.out.println("END - replaced "+counter+" hashes and saved mapping in "+outMap);
        br.close();
        bw.close();
        mapw.close();
    }
    //PRE: each transaction hash is unique [note this is not necessary true for
    // older transactions, but it respects semantic of how Bitcoin protocl handles them]
    //NOTE: based on the observation that unique hashes are needed in the future
    // exactly once per each output. Then they can be removed form the map, allowing
    // for an efficient memory usage
    private static void replaceHashesEfficient(String inBC,String outMap,String outBC) throws FileNotFoundException, IOException, InconsistencyException{
        BufferedReader br= new BufferedReader(new FileReader(inBC));
        BufferedWriter bw=new BufferedWriter(new FileWriter(outBC));
        BufferedWriter mapw=new BufferedWriter(new FileWriter(outMap));
        //map to remember already seen strings
        TreeMap<String,Couple<Integer,Integer>> map=new TreeMap<String,Couple<Integer,Integer>>();
        
        int txs=0;
        System.out.println("START - replacing hashes and saving mapping.");
        String line;int counter=0; String[] lineArrOuter,lineArrInner;
        int numOutputs; Couple<Integer,Integer> temp;
        while((line = br.readLine()) != null){
            //select the general infos (first ':' block) and then relevant info
            lineArrOuter=line.split(":");
            numOutputs=lineArrOuter[2].split(";").length;
            lineArrInner=lineArrOuter[0].split(",");
            if(!map.containsKey(lineArrInner[2])){
                map.put(lineArrInner[2],new Couple(counter,numOutputs));
                mapw.write(lineArrInner[2]+","+counter);
                mapw.newLine();
                counter++;
            }//if map contains the hash already there is nothing to be done
            for(int i=0;i<lineArrInner.length;i++){
                if(i!=0) bw.write(",");
                if(i==2){
                    bw.write(""+map.get(lineArrInner[2]).getFirst());
                }else{
                    bw.write(lineArrInner[i]);
                }
            }
            bw.write(":");
            if((lineArrInner[3].equals("0"))){
                //If replacing txHashes we need to replace them also in the inputs
                // if it is not a coinbase
                //[inAddr_Id,amount,prevTx_Id,prevTxPos[;]]*:
                boolean first=true;
                for(String input:lineArrOuter[1].split(";")){
                    if(first)
                        first=false;
                    else
                        bw.write(";");
                    lineArrInner=input.split(",");
                    bw.write(lineArrInner[0]+","+lineArrInner[1]+",");
                    if(!map.containsKey(lineArrInner[2])){
                        throw(new InconsistencyException("TXhash "+lineArrInner[2]+" inexistent in mapping! Used as input but never seen before!"));
                    }
                    bw.write(map.get(lineArrInner[2]).getFirst()+","+lineArrInner[3]);
                    //check if map can be cleaned form the element
                    temp=map.remove(lineArrInner[2]);
                    //NOTE: at least 1 output always, so getSecond is always >=1
                    if(temp.getSecond()!=1){
                        //more than one output left, so decrease and keep element
                        map.put(lineArrInner[2], new Couple<Integer,Integer>(temp.getFirst(),temp.getSecond()-1));
                    }
                }
            }else{
                bw.write(lineArrOuter[1]);
            }
            bw.write(":");bw.write(lineArrOuter[2]);
            bw.newLine();
            txs++;
            if(txs%10000000==0)
                System.out.println(txs+" transactions processed.");
        }
        System.out.println("END - replaced "+counter+" hashes and saved mapping in "+outMap);
        br.close();
        bw.close();
        mapw.close();
    }
    
    //PRE:
    //[outAddr_Id,amount,script_Id][;outAddr_Id,amount,script_Id]*
    //there is always at least one output
    private static void replaceAddresses(String inBC,String outMap,String outBC) throws FileNotFoundException, IOException{
        BufferedReader br= new BufferedReader(new FileReader(inBC));
        BufferedWriter bw=new BufferedWriter(new FileWriter(outBC));
        BufferedWriter mapw=new BufferedWriter(new FileWriter(outMap));
        //map to remember already seen addresses
        TreeMap<StringAsByteArray,Integer> map=new TreeMap<StringAsByteArray,Integer>();
        
        int txs=0;
        System.out.println("START - replacing addresses and saving mapping.");
        String line;int counter=0; String[] lineArrOuter,lineArrInner;
        while((line = br.readLine()) != null){
            //select the outputs (third ':' block)
            lineArrOuter=line.split(":");
            bw.write(lineArrOuter[0]);bw.write(":");
            bw.write(lineArrOuter[1]);bw.write(":");
            boolean first=true;
            for(String output:lineArrOuter[2].split(";")){
                if(first)
                    first=false;
                else
                    bw.write(";");
                lineArrInner=output.split(",");
                if(!map.containsKey(new StringAsByteArray(lineArrInner[0]))){
                    map.put(new StringAsByteArray(lineArrInner[0]),counter);
                    mapw.write(lineArrInner[0]+","+counter);
                    mapw.newLine();
                    counter++;
                }//if map contains the address already there is nothing to be done
                bw.write(map.get(new StringAsByteArray(lineArrInner[0]))+","+lineArrInner[1]+","+lineArrInner[2]);                
            }
            bw.newLine();
            txs++;
            if(txs%10000000==0)
                System.out.println(txs+" transactions processed.");
        }
        System.out.println("END - replaced "+counter+" addresses and saved mapping in "+outMap);
        br.close();
        bw.close();
        mapw.close();
    }
    //PRE:
    //[outAddr_Id,amount,script_Id][;outAddr_Id,amount,script_Id]*
    //there is always at least one output
    /*private static void replaceAddressesEfficient(String inBC,String outMap,String outBC) throws FileNotFoundException, IOException{
        BufferedReader br= new BufferedReader(new FileReader(inBC));
        BufferedWriter bw=new BufferedWriter(new FileWriter(outBC));
        BufferedWriter mapw=new BufferedWriter(new FileWriter(outMap));
        //map to remember already seen addresses
        ObjectOpenHashSet<MappedAddress> set=new ObjectOpenHashSet<MappedAddress>();
        
        int txs=0;
        System.out.println("START - replacing addresses and saving mapping.");
        String line;int counter=0; String[] lineArrOuter,lineArrInner;
        while((line = br.readLine()) != null){
            //select the outputs (third ':' block)
            lineArrOuter=line.split(":");
            bw.write(lineArrOuter[0]);bw.write(":");
            bw.write(lineArrOuter[1]);bw.write(":");
            boolean first=true;
            for(String output:lineArrOuter[2].split(";")){
                if(first)
                    first=false;
                else
                    bw.write(";");
                lineArrInner=output.split(",");
                if(!set.contains(new MappedAddress(lineArrInner[0]))){
                    set.add(new MappedAddress(lineArrInner[0],counter));
                    mapw.write(lineArrInner[0]+","+counter);
                    mapw.newLine();
                    counter++;
                }//if map contains the address already there is nothing to be done
                bw.write(set.get(new MappedAddress(lineArrInner[0])).getId()+","+lineArrInner[1]+","+lineArrInner[2]);                
            }
            bw.newLine();
            txs++;
            if(txs%10000000==0)
                System.out.println(txs+" transactions processed.");
        }
        System.out.println("END - replaced "+counter+" addresses and saved mapping in "+outMap);
        br.close();
        bw.close();
        mapw.close();
    }*/
    //PRE:
    //[outAddr_Id,amount,script_Id][;outAddr_Id,amount,script_Id]*
    //there is always at least one output
    private static void replaceAddressesEfficient(String inBC,String outMap,String outBC) throws FileNotFoundException, IOException{
        BufferedReader br= new BufferedReader(new FileReader(inBC));
        BufferedWriter bw=new BufferedWriter(new FileWriter(outBC));
        BufferedWriter mapw=new BufferedWriter(new FileWriter(outMap));
        //map to remember already seen addresses
        Object2IntLinkedOpenHashMap<StringAsByteArray> map=new Object2IntLinkedOpenHashMap<StringAsByteArray>(24000000);
        
        int txs=0;
        System.out.println("START - replacing addresses and saving mapping.");
        String line;int counter=0; String[] lineArrOuter,lineArrInner;
        while((line = br.readLine()) != null){
            //select the outputs (third ':' block)
            lineArrOuter=line.split(":");
            bw.write(lineArrOuter[0]);bw.write(":");
            bw.write(lineArrOuter[1]);bw.write(":");
            boolean first=true;
            for(String output:lineArrOuter[2].split(";")){
                if(first)
                    first=false;
                else
                    bw.write(";");
                lineArrInner=output.split(",");
                if(!map.containsKey(new StringAsByteArray(lineArrInner[0]))){
                    map.put(new StringAsByteArray(lineArrInner[0]),counter);
                    mapw.write(lineArrInner[0]+","+counter);
                    mapw.newLine();
                    counter++;
                }//if map contains the address already there is nothing to be done
                bw.write(map.getInt(new StringAsByteArray(lineArrInner[0]))+","+lineArrInner[1]+","+lineArrInner[2]);                
            }
            bw.newLine();
            txs++;
            if(txs%10000000==0)
                System.out.println(txs+" transactions processed.");
        }
        System.out.println("END - replaced "+counter+" addresses and saved mapping in "+outMap);
        br.close();
        bw.close();
        mapw.close();
    }
    /*//PRE:
    //[outAddr_Id,amount,script_Id][;outAddr_Id,amount,script_Id]*
    //there is always at least one output
    private static void replaceAddressesUglyButEfficient(String inBC,String outMap,String outBC) throws FileNotFoundException, IOException, InconsistencyException{
        BufferedReader br= new BufferedReader(new FileReader(inBC));
        BufferedWriter bw=new BufferedWriter(new FileWriter(outBC));
        BufferedWriter mapw=new BufferedWriter(new FileWriter(outMap));
        //map to remember already seen addresses
        //28,20000
        AddressesSet map=new AddressesSet(29,200000000);       
        
        int txs=0;
        System.out.println("START - replacing addresses and saving mapping.");
        String line;int counter=0; String[] lineArrOuter,lineArrInner;
        while((line = br.readLine()) != null){
            //select the outputs (third ':' block)
            lineArrOuter=line.split(":");
            bw.write(lineArrOuter[0]);bw.write(":");
            bw.write(lineArrOuter[1]);bw.write(":");
            boolean first=true;
            for(String output:lineArrOuter[2].split(";")){
                if(first)
                    first=false;
                else
                    bw.write(";");
                lineArrInner=output.split(",");
                if(map.add(new MappedAddress(lineArrInner[0],counter))){
                    mapw.write(lineArrInner[0]+","+counter);
                    mapw.newLine();
                    counter++;
                }//if map contains the address already there is nothing to be done
                bw.write(map.get(new MappedAddress(lineArrInner[0])).getId()+","+lineArrInner[1]+","+lineArrInner[2]);                
            }
            bw.newLine();
            txs++;
            if(txs%10000000==0){
                System.out.println(txs+" transactions processed, longestConsecutiveOccupiedStreak found so far has length "+map.getLongestConsecutiveOccupiedStreak()+".");
                //try to force garbage collection to free up space
                Utilities.runGC();
            }
        }
        System.out.println("END - replaced "+counter+" addresses and saved mapping in "+outMap);
        br.close();
        bw.close();
        mapw.close();
    }*/
    //PRE:
    //timestamp,blockheight,tx_Id,isCoinbase,feeAmount,estimatedSize,hasWitness:
    //[inAddr_Id,amount,prevTx_Id,prevTxPos[;]]*:
    //[outAddr_Id,amount,script_Id][;outAddr_Id,amount,script_Id]*
    //and need to fill the '-', i.e.
    //feeAmount
    //inAddr_Id,amount
    public static void fillWithUtxo(String inBC,String outBC) throws FileNotFoundException, IOException{
        BufferedReader br= new BufferedReader(new FileReader(inBC));
        BufferedWriter bw=new BufferedWriter(new FileWriter(outBC));
        //map to remember already seen addresses
        TreeMap<TxOutputIds,TxOutputCouple> utxo=new TreeMap<TxOutputIds,TxOutputCouple>();
        //key=txid,pos
        //item=addrId,value
        
        int txs=0;long fee;
        System.out.println("START - filling with UTXO.");
        String line; String[] lineArrOuter,lineArrInner;
        StringBuilder sb; TxOutputIds tempCouple; TxOutputCouple tempAddrAmount; int txhashid;
        while((line = br.readLine()) != null){
            //select the outputs (third ':' block)
            lineArrOuter=line.split(":");
            sb=new StringBuilder();
            boolean first=true;
            if((!lineArrOuter[1].equals(""))&&(lineArrOuter[1]!=null)){
                for(String input:lineArrOuter[1].split(";")){
                    if(first)
                        first=false;
                    else
                        sb.append(";");
                    lineArrInner=input.split(",");
                    //////System.out.println(input+"---");//////
                    tempAddrAmount=utxo.remove(new TxOutputIds(Integer.parseInt(lineArrInner[2]),Integer.parseInt(lineArrInner[3])));
                    sb.append(tempAddrAmount.getAddrId()+",");
                    sb.append(tempAddrAmount.getAmount()+",");
                    sb.append(lineArrInner[2]+","+lineArrInner[3]);                
                }
            }fee=computeFee(sb.toString(),lineArrOuter[2]);
            sb.append(":");
            //retrieve txhashid
            txhashid=Integer.parseInt(lineArrOuter[0].split(",")[2]);
            lineArrInner=lineArrOuter[2].split(";");
            first=true;
            for(int i=0;i<lineArrInner.length;i++){
                //retrieve txhashid
                tempCouple=new TxOutputIds(txhashid,i);
                //each couple txid,pos must be unique!!
                if(utxo.containsKey(tempCouple)){
                    System.out.println("ALERT:UTXO incosistency, "+lineArrOuter[0]+" exists already with output "+lineArrInner[i]);
                    //throw(new IOException("UTXO incosistency, "+lineArrOuter[0]+" exists already with output "+lineArrInner[i]));
                }
                utxo.put(tempCouple, new TxOutputCouple(Integer.parseInt(lineArrInner[i].split(",")[0]),Long.parseLong(lineArrInner[i].split(",")[1])));
                if(first)
                    first=false;
                else
                    sb.append(";");
                sb.append(lineArrInner[i]);
            }
            lineArrInner=lineArrOuter[0].split(",");
            bw.write(lineArrInner[0]+","+lineArrInner[1]+",");
            bw.write(lineArrInner[2]+","+lineArrInner[3]+",");
            bw.write(fee+","+lineArrInner[4]+","+lineArrInner[5]+":");
            bw.write(sb.toString());
            bw.newLine();
            txs++;
            if(txs%10000000==0)
                System.out.println(txs+" transactions processed.");
        }
        System.out.println("END - filled with utxo and saved in "+outBC);
        br.close();
        bw.close();
    }
    //PRE: ins = [inAddr_Id,amount,prevTx_Id,prevTxPos[;]]*:
    //PRE: outs = [outAddr_Id,amount,script_Id][;outAddr_Id,amount,script_Id]*
    private static long computeFee(String ins,String outs){
        //if is coinbase no fees!
        if((ins!=null)&&(!ins.equals(""))){
            long res=0;
            for(String in:ins.split(";")){
                res+=Long.parseLong(in.split(",")[1]);
            }
            for(String out:outs.split(";")){
                res-=Long.parseLong(out.split(",")[1]);
            }
            if(res<0)
                System.out.println("ALERT: negative fee!!! \n"+ins+"\n"+outs);
            return res;
        }else{
            return 0;            
        }
    }
    private static void replaceBlockHashes(String inBC,String inBlockMap,String outBC) throws FileNotFoundException, IOException{
        BufferedReader bmap= new BufferedReader(new FileReader(inBlockMap));
        //mapping blockHash -> blockHeight
        TreeMap<String,Integer> map=new TreeMap<String,Integer>();
        int max=-1; int height;
        System.out.println("Starting populating block hash to height mapping.");
        String line;String[] lineArr;
        while((line = bmap.readLine()) != null){
            lineArr=line.split(",");
            height=Integer.parseInt(lineArr[1]);
            map.put(lineArr[0],height);
            if(height>max)
                max=height;
        }
        bmap.close();
        System.out.println("Done populating mapping (max height found "+max+") starting substituting block hashes with heights. ");
        
        BufferedReader br= new BufferedReader(new FileReader(inBC));
        BufferedWriter bw=new BufferedWriter(new FileWriter(outBC));
        int txs=0; int counter=0;int skipped=0;
        String[] lineArrInner;
        String lastSkippedBlock="";
        while((line = br.readLine()) != null){
            //select the general infos (first ':' block) and then relevant info
            lineArr=line.split(":");
            lineArrInner=lineArr[0].split(",");
            if(!map.containsKey(lineArrInner[1])){
                if(!lastSkippedBlock.equals(lineArrInner[1])){
                    System.out.println("Skipping unknown block "+lineArrInner[1]);
                    lastSkippedBlock=lineArrInner[1];
                }
                skipped++;
            }else{
                for(int i=0;i<lineArrInner.length;i++){
                    if(i!=0) bw.write(",");
                    if(i==1){
                        bw.write(""+map.get(lineArrInner[1]));
                        counter++;
                    }else{
                        bw.write(lineArrInner[i]);
                    }
                }
                bw.write(":");bw.write(lineArr[1]);
                bw.write(":");bw.write(lineArr[2]);
                bw.newLine();
            }txs++;
            if(txs%10000000==0)
                System.out.println(txs+" transactions processed.");
        }
        System.out.println(txs+" transactions processed - END - replaced "+counter+" hashes ("+skipped+" transactions with unknow block height skipped) and saved result in "+outBC);
        br.close();
        bw.close();
    }
    
    //before sorting adds an index to all transactions to rpeserve their relative order
    private static void sortPreservingTxOrder(String inBC,String outBC) throws FileNotFoundException, IOException, InterruptedException{
        BufferedReader br= new BufferedReader(new FileReader(inBC));
        File temp1=new File(outBC+"TEMP1");
        BufferedWriter bw=new BufferedWriter(new FileWriter(temp1));
        int txs=0;
        String line; String[] lineArr;
        while((line = br.readLine()) != null){
            //select the general infos (first ':' block) and then relevant info
            lineArr=line.split(":");
            bw.write(lineArr[0]);bw.write(","+txs);
            bw.write(":");bw.write(lineArr[1]);
            bw.write(":");bw.write(lineArr[2]);
            bw.newLine();
            txs++;
            if(txs%10000000==0)
                System.out.println(txs+" transactions processed.");
        }
        System.out.println(txs+" transactions enriched with relative id.");
        br.close();
        bw.close();
        Process p = Runtime.getRuntime().exec(new String[]{"bash","-c","sort --field-separator=',' -T "+Utilities.getDirFromFile(new File(outBC))+" -k 2n,2 -k 7n,7 "+outBC+"TEMP1"+" -o "+outBC+"TEMP2"});
        int procRet=p.waitFor();
        //int procRet=Utilities.runExternalProcess(new String[]{"bash","-c","sort --field-separator=',' -T "+outBC+" -k 2n,2 -k 7n,7 "+outBC+"TEMP1"+" -o "+outBC+"TEMP2"});
        System.out.println(procRet+" - Sorted according to block height preserving transactions relative ordering inside blocks.");
        //////////////temp1.delete();//////////////
        File temp2=new File(outBC+"TEMP2");
        br= new BufferedReader(new FileReader(temp2));
        bw=new BufferedWriter(new FileWriter(outBC));
        txs=0;String[] lineArrInner;
        while((line = br.readLine()) != null){
            //select the general infos (first ':' block) and then relevant info
            lineArr=line.split(":");
            lineArrInner=lineArr[0].split(",");
            //skips the last one
            for(int i=0;i<lineArrInner.length-1;i++){
                if(i!=0) bw.write(",");
                bw.write(lineArrInner[i]);
            }
            bw.write(":");bw.write(lineArr[1]);
            bw.write(":");bw.write(lineArr[2]);
            bw.newLine();
            txs++;
            if(txs%10000000==0)
                System.out.println(txs+" transactions processed.");
        }
        br.close();
        bw.close();
        System.out.println(txs+" transactions cleaned from relative id.");
        //////////////temp2.delete();
        System.out.println("Done ordering, result saved in "+outBC);
    }
}
