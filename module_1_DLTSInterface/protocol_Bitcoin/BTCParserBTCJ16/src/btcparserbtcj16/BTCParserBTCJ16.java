package btcparserbtcj16;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.bitcoinj.core.PrunedException;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.store.BlockStoreException;
/**
 * 
 * @author brodo
 */
public class BTCParserBTCJ16 {

    /**
     * @param args the command line arguments
     * 
     * 
     */
    public static void main(String[] args) throws VerificationException, SecurityException {
        try {
            boolean flag=true;
            /*if(true){
                String testscript1="001427d112e02bf8636a6b2119a264632ef296fdf1b2";
                byte[] testBytesScript = Utilities.hexStringToByteArray(testscript1);
                String res = ScriptParser.addrFromOut(testBytesScript);
                System.out.println("Result: '"+res+"'");
            }else{*/
            
            //build the script only transaction list file from the blk#####.dat files
            //args[0] contains the path of the directory containing the blk#####.dat files
            //args[1] contains the name of the file where to save the BC results
            //args[2] contains the name of the file where to save the blockHeight mapping
            //args[3] contains the max number of blocks to parse
            
            //DEBUG//////
            //DEBUG//////String args0="/home/brodo/Università/Dottorato/NewBTCgraphAnalysis/chaindata/";
            //DEBUG//////String args1="/home/brodo/Università/Dottorato/NewBTCgraphAnalysis/canctest1";
            //DEBUG//////String args3="500000";
            /*long time=System.currentTimeMillis();
            String tempAddr= Utilities.directory(args[4])+"tempAddr";
            String finalBC= Utilities.directory(args[4])+"finalSegwitBC";
            System.out.println("Begin filling with UTXO.");
            BCParserSegwit.fillWithUtxo(tempAddr,finalBC);
            //////////////(new File(tempAddr)).delete();
            System.out.println("Done filling UTXO in time"+((System.currentTimeMillis()-time)/1000)+" seconds.");
            flag=false;
            //DEBUG ABOVE!!!!!!!!!!!!!
            */
            
            
            
            
            if(flag){
            System.out.println("MAIN: starting with parameters:");
            System.out.println("MAIN: in blocks folder "+args[0]);
            System.out.println("MAIN: out noUtxoBlockchain file "+args[1]);
            System.out.println("MAIN: out blocks index file "+args[2]);
            System.out.println("MAIN: in max block to read "+args[3]);
            System.out.println("MAIN: out final result files folder "+args[4]);
            //DEBUG//////BCParser bcp=new BCParser(Utilities.directory(args[0]));
            BCParserSegwit bcp=new BCParserSegwit(Utilities.directory(args[0]));
            bcp.parseExactNoUtxo(new File(args[1]),Integer.parseInt(args[3]));
            System.out.println("MAIN: done reading blocks to generate bcNoUtxo");
//Es: java -jar -Xmx64G BTCScriptExtractor.jar /mnt/nfs/dd534/blocks/ /mnt/nfs/dd534/BCscripts 480000 &>/mnt/nfs/dd534/log_parseExactNoUtxo
            bcp.inferrBlockHeightMap(new File(args[2]),Integer.parseInt(args[3]));
            System.out.println("MAIN: done reading blocks to generate blockHeights");
//Es: java -jar -Xmx64G BTCScriptExtractor.jar /mnt/nfs/dd534/mapBlockHeight.csv 480000 &>/mnt/nfs/dd534/log_saveHeightMapFirstNBlocks
            
            //args[2] contains the file with the blockHash,blockHeight mapping
            //args[4] contains the path of the directory where to save the ouput files
            bcp.readExactNoUtxoAndSubstituteIdsAndFill(args[1],args[2],Utilities.directory(args[4]));            
            System.out.println("MAIN: done reading blocks to generate all files.");
//Es: java -jar -Xmx64G BTCBlockchainParser.jar /mnt/nfs/dd534/BCExactNoUtxo /mnt/nfs/dd534/mapBlockHeightSorted.csv /mnt/nfs/dd534/ &>/mnt/nfs/dd534/log_readExactNoUtxoAndSubstituteIdsAndFill
            
//ES: java -jar -Xmx7G BTCScriptExtractor.jar /mnt/nfs/dd534/blocks/ /mnt/nfs/dd534/BCscripts 480000 &>/mnt/nfs/dd534/log_parseExactNoUtxo            
            
            }            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.err.println("Argument '" + args[3] + "' must be an integer.");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception encountered.");
            System.err.println("Usage:");
            System.err.println("   args[0] contains the path of the directory containing the blk#####.dat files");
            System.err.println("   args[1] contains the name of the file where to save the BC results");
            System.err.println("   args[2] contains the name of the file where to save the blockHeight mapping");
            System.err.println("   args[3] contains the max number of blocks to parse (as appear in the .dat files, NOT ordered)");
            System.exit(1);
        }
    }
    
}
