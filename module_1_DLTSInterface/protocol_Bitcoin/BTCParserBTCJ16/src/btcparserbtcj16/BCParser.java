/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btcparserbtcj16;
import java.io.BufferedWriter;
import java.io.File;
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
 
/**
 * Substitues undecodable addresses with #id, with incremental value of id. Such
 * values are associated with regular ids during substitution as usual
 *
 * OLD DEPERECATED PRE SEGWIT
 * 
 * @author brodo
 * @deprecated 
 */
public class BCParser {
    // Location of block files
    String folder;
    int DEBUGtotalTxs;
    int DEBUGcoinbaseCounter;
    int nullAddressesNum;
    
    
    public BCParser(String f){
        folder=f;DEBUGtotalTxs=0;DEBUGcoinbaseCounter=0;nullAddressesNum=0;
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
            
            if(blockCounter%1000==0){
                System.out.println("Analysed "+blockCounter+" NOT ORDERED blocks.");
                System.out.println(blockCounter+" - "+block.getHashAsString());
            }
            //parseBlockExact(block,bw,blockCounter);
            parseBlockExact(block,bw);
            blockCounter++;
        } // End of iteration over blocks
        bw.close();
        System.out.println("TotalTxs "+DEBUGtotalTxs+" , of which coinbases are "+DEBUGcoinbaseCounter);
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
            //timestamp,blockhash,txHash,isCoinbase,estimatedSize:
            line = new StringBuilder();
            line.append(block.getTimeSeconds()+","+block.getHashAsString()+","+tx.getHashAsString()+",");
            if(tx.isCoinBase()){
                isCoinbase=true;
                line.append("1");
            }else{
                isCoinbase=false;
                line.append("0");
            }line.append(",");line.append(tx.getOptimalEncodingMessageSize());line.append(":");

            if(isCoinbase){
                DEBUGcoinbaseCounter++;
            }else{
                //not coinbase so there is at least one input
                //save inputs in the format
                //[inScriptBytes,prevTx_Id,prevTxPos[;]]*:
                first=true;
                for(TransactionInput ii:tx.getInputs()){
                    if(first)
                        first=false;
                    else
                        line.append(";");
                    //line.append(Utilities.toHexString(ii.getScriptBytes()));
                    line.append(Utilities.byteArrayToHexString(ii.getScriptBytes()));
                    
                    line.append(",");
                    line.append(ii.getOutpoint().getHash().toString());
                    line.append(",");
                    line.append(ii.getOutpoint().getIndex());
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
                //line.append(Utilities.toHexString(oo.getScriptBytes()));
                line.append(Utilities.byteArrayToHexString(oo.getScriptBytes()));
                line.append(",");
                line.append(oo.getValue().getValue());
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
}
