/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nmtsimulation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.SplittableRandom;

/**
 *All public for testing, add proper setters and getters.
 * 
 * @author brodo
 */
public class SimRoundResults {
    
    int NUMRUNS;
    SimParams sim;
    
    private LinkedList<Integer>[] creators;
    private LinkedList<Integer>[] assets;
    
    //gas expenditures for this round
    public long[] gasTotal;//currentResults;
    public long[] gasNewCreator;
    public long[] gasNewAsset;
    public long[] gasHolderPolicyUpdate;
    public long[] gasCharacteristicUpdate;
    public long[] gasTransfer;
    
    public SimRoundResults(int _NUMRUNS, SimParams _sim){
        NUMRUNS = _NUMRUNS;
        sim = _sim;
        creators = new LinkedList[NUMRUNS];
        assets = new LinkedList[NUMRUNS];
        for(int j=0;j<NUMRUNS;j++){
            creators[j] = new LinkedList<Integer>();
            assets[j] = new LinkedList<Integer>();
        }
    }
    
    
    private void initialise(long[] vect){
        for(int i = 0; i<vect.length; i++)
            vect[i] = 0;
    }
    
    
    public void computeSimStepNoMasterFixedRandom(int time){
        gasTotal = new long[NUMRUNS];
        initialise(gasTotal);
        gasNewCreator = new long[NUMRUNS];
        initialise(gasNewCreator);
        gasNewAsset = new long[NUMRUNS];
        initialise(gasNewAsset);
        gasHolderPolicyUpdate = new long[NUMRUNS];
        initialise(gasHolderPolicyUpdate);
        gasCharacteristicUpdate = new long[NUMRUNS];
        initialise(gasCharacteristicUpdate);
        gasTransfer = new long[NUMRUNS];
        initialise(gasTransfer);
        
        SplittableRandom splittableRandom = new SplittableRandom();
        
        double randomDouble;
        
        for(int i = 0; i < NUMRUNS; i++){
            
            randomDouble = splittableRandom.nextDouble();
        
            //System.out.print(result+"->");
            if(time == 0){
                gasTotal[i] += sim.GASdeployNMT();
            }      
            //System.out.print(result+"->");
            //random creation of new creator
            if(randomDouble<=sim.PROBnewCreatorCreation().getProb(time)){
                creators[i].add(time);
            }        
            for(Integer creator:creators[i]){
                //result += sim.GAScreatorPolicyUpdate*sim.PROBcreatorPolicyUpdate.getProb(time-creator.startTime);
                if(randomDouble<=sim.PROBcreatorPolicyUpdate().getProb(time-creator)){
                    gasTotal[i] += sim.GAScreatorPolicyUpdate();
                    gasNewCreator[i] += sim.GAScreatorPolicyUpdate();
                }
            }
            for(Integer creator:creators[i]){
                //result += sim.GASnewAssetsCreation*sim.PROBnewAssetsCreation.getProb(time-creator.startTime);
                if(randomDouble<=sim.PROBnewAssetsCreation().getProb(time-creator)){
                    assets[i].add(time);
                    gasTotal[i] += sim.GASnewAssetsCreation();
                    gasNewAsset[i] += sim.GASnewAssetsCreation();
                }
            }
            //System.out.print(result+"->");
            for(Integer asset:assets[i]){
                //result += sim.GASholderPolicyUpdate*sim.PROBholderPolicyUpdate.getProb(time-asset.startTime);
                //result += sim.GAScharacteristicUpdate*sim.PROBcharacteristicUpdate.getProb(time-asset.startTime);
                //result += sim.GAStransfer*sim.PROBtransfer.getProb(time-asset.startTime);
                if(randomDouble<=sim.PROBholderPolicyUpdate().getProb(time-asset)){
                    gasTotal[i] += sim.GASholderPolicyUpdate();
                    gasHolderPolicyUpdate[i] += sim.GASholderPolicyUpdate();
                }
                if(randomDouble<=sim.PROBcharacteristicUpdate().getProb(time-asset)){
                    gasTotal[i] += sim.GAScharacteristicUpdate();
                    gasCharacteristicUpdate[i] += sim.GAScharacteristicUpdate();
                }
                if(randomDouble<=sim.PROBtransfer().getProb(time-asset)){
                    gasTotal[i] += sim.GAStransfer();
                    gasTransfer[i] += sim.GAStransfer();
                }
            }
        
        }
    }
    
    public static double computeAvg(long[] values){
        long sumAvg = 0;
        for(int j=0;j<values.length;j++){
            sumAvg += values[j];
        }
        return (1.0 * sumAvg) / values.length;        
    }
    
    public static double computeStd(long[] values, double mean){
        double sumStd = 0;
        for(int j=0;j<values.length;j++){
             sumStd += (values[j] - mean)*(values[j] - mean);
        }
        return Math.sqrt(sumStd / values.length);  
    }
    
    public String getTSVInfoCreatorsAssetsStats(){
        StringBuilder str = new StringBuilder();
            int numCreators = 0,numAssets = 0;
            int maxCreators = 0, maxAssets = 0;
            int minCreators = -1, minAssets = -1;
            for(int j=0;j<NUMRUNS;j++){
                numCreators += creators[j].size(); numAssets += assets[j].size();
                if(creators[j].size() > maxCreators){ maxCreators = creators[j].size();}
                if(assets[j].size() > maxAssets){ maxAssets = assets[j].size();}
                if((minCreators == -1)||(creators[j].size() < minCreators)){ minCreators = creators[j].size();}
                if((minAssets == -1)||(assets[j].size() < minAssets)){ minAssets = assets[j].size();}            
            }
            double avgCreators = (1.0*numCreators)/NUMRUNS, avgAssets = (1.0*numAssets)/NUMRUNS;
            double stdCreators = 0, stdAssets = 0;
            for(int j=0;j<NUMRUNS;j++){
                    stdCreators += (creators[j].size() - avgCreators)*(creators[j].size() - avgCreators);
                    stdAssets += (assets[j].size() - avgAssets)*(assets[j].size() - avgAssets);
                }
            stdCreators = Math.sqrt(stdCreators/NUMRUNS);
            stdAssets = Math.sqrt(stdAssets/NUMRUNS);
            str.append("\t"+numCreators);
            str.append("\t"+maxCreators);
            str.append("\t"+minCreators);
            str.append("\t"+avgCreators);
            str.append("\t"+stdCreators);
            str.append("\t"+numAssets);
            str.append("\t"+maxAssets);
            str.append("\t"+minAssets);
            str.append("\t"+avgAssets);
            str.append("\t"+stdAssets);
            return str.toString();
    }
    
}
