package nmtsimulation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Damiano DFM
 */
public class NMTSimulation {

    public static final int ETHMAXGASPERSEC = 2500000;//blockGasLimit/avgBlockGnerationTime
    public static final int BINANCESMARTCHAINMAXGASPERSEC = 140000000/3;//=46666667
    public static final int OPTIMISMMAINNETMAXGASPERSEC = 60000000/2;//=30000000
    public static final int POLYGONPOSCHAINMAXGASPERSEC = 30000000/2;//=15000000
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int NUMRUNS = 10;//100;
        int MAXTIME = 1209600;//86400; one day//2592000; one month//864000; ten days//604800 seven days  //  1209600 two weeks
        //int AGGR = 60;
        int NUMAGGR = 60;//1 seconds granularity / 60 minutes granularity / 3600 hours granularity
        //String outFile = "/home/brodo/Universita/TrustSense2024/simResults/simResultsTest1.tsv";
        //String dir = "/home/brodo/Universita/TrustSense2024/simResults/";
        String dir = "./";
        //String outFileAggr = "/home/brodo/Universita/TrustSense2024/simResults/simResultsTest1Aggr.tsv";
        String outFile = dir+"simResultsTest5Scaledt"+MAXTIME+"a"+NUMAGGR+".tsv";
        SimParams simToRun = new SimParams5Scaled();        
        runSimSpaceOptimisedAggregated(simToRun, NUMRUNS, MAXTIME, outFile, NUMAGGR);
        //runSimAggregated(simToRun, NUMRUNS, MAXTIME, outFileAggr, AGGR); 
        System.out.println("*** DONE SIM5Scaled AGGR ("+NUMAGGR+" seconds) "+(MAXTIME/86400)+" days ***");
        
        outFile = dir+"simResultsTest6Scaledt"+MAXTIME+"a"+NUMAGGR+".tsv";
        simToRun = new SimParams6Scaled();       
        runSimSpaceOptimisedAggregated(simToRun, NUMRUNS, MAXTIME, outFile, NUMAGGR);
        System.out.println("*** DONE SIM6Scaled AGGR ("+NUMAGGR+" seconds) "+(MAXTIME/86400)+" days ***"); 
        
        
        /*MAXTIME = 1209600;
        outFile = dir+"simResultsTest5Scaledt"+MAXTIME+"a"+NUMAGGR+".tsv";
        simToRun = new SimParams5Scaled();        
        runSimSpaceOptimisedAggregated(simToRun, NUMRUNS, MAXTIME, outFile, NUMAGGR);
        //runSimAggregated(simToRun, NUMRUNS, MAXTIME, outFileAggr, AGGR); 
        System.out.println("*** DONE SIM5Scaled AGGR ("+NUMAGGR+" seconds) "+(MAXTIME/86400)+" days ***");
        
        outFile = dir+"simResultsTest6Scaledt"+MAXTIME+"a"+NUMAGGR+".tsv";
        simToRun = new SimParams6Scaled();       
        runSimSpaceOptimisedAggregated(simToRun, NUMRUNS, MAXTIME, outFile, NUMAGGR);
        System.out.println("*** DONE SIM6Scaled AGGR ("+NUMAGGR+" seconds) "+(MAXTIME/86400)+" days ***"); */
        //************///////////
        
        /*************
        //String outFile = "/home/brodo/Universita/TrustSense2024/simResults/simResultsTest1.tsv";
        //String dir = "/home/brodo/Universita/TrustSense2024/simResults/";
        String dir = "./";
        //String outFileAggr = "/home/brodo/Universita/TrustSense2024/simResults/simResultsTest1Aggr.tsv";
        String outFile = dir+"simResultsTest1t"+MAXTIME+"a"+NUMAGGR+".tsv";
        SimParams simToRun = new SimParams1();        
        runSimSpaceOptimisedAggregated(simToRun, NUMRUNS, MAXTIME, outFile, NUMAGGR);
        //runSimAggregated(simToRun, NUMRUNS, MAXTIME, outFileAggr, AGGR); 
        System.out.println("*** DONE SIM1 AGGR ("+NUMAGGR+" seconds) "+(MAXTIME/86400)+" days ***");
        
        outFile = dir+"simResultsTest2t"+MAXTIME+"a"+NUMAGGR+".tsv";
        simToRun = new SimParams2();       
        runSimSpaceOptimisedAggregated(simToRun, NUMRUNS, MAXTIME, outFile, NUMAGGR);
        System.out.println("*** DONE SIM2 AGGR ("+NUMAGGR+" seconds) "+(MAXTIME/86400)+" days ***"); 
        
        outFile = dir+"simResultsTest3t"+MAXTIME+"a"+NUMAGGR+".tsv";
        simToRun = new SimParams3();       
        runSimSpaceOptimisedAggregated(simToRun, NUMRUNS, MAXTIME, outFile, NUMAGGR);
        System.out.println("*** DONE SIM3 AGGR ("+NUMAGGR+" seconds) "+(MAXTIME/86400)+" days ***"); 
        
        MAXTIME = 1209600;
        outFile = dir+"simResultsTest1t"+MAXTIME+"a"+NUMAGGR+".tsv";
        simToRun = new SimParams1();        
        runSimSpaceOptimisedAggregated(simToRun, NUMRUNS, MAXTIME, outFile, NUMAGGR);
        //runSimAggregated(simToRun, NUMRUNS, MAXTIME, outFileAggr, AGGR); 
        System.out.println("*** DONE SIM1 AGGR ("+NUMAGGR+" seconds) "+(MAXTIME/86400)+" days ***");
        
        outFile = dir+"simResultsTest2t"+MAXTIME+"a"+NUMAGGR+".tsv";
        simToRun = new SimParams2();       
        runSimSpaceOptimisedAggregated(simToRun, NUMRUNS, MAXTIME, outFile, NUMAGGR);
        System.out.println("*** DONE SIM2 AGGR ("+NUMAGGR+" seconds) "+(MAXTIME/86400)+" days ***"); 
        
        outFile = dir+"simResultsTest3t"+MAXTIME+"a"+NUMAGGR+".tsv";
        simToRun = new SimParams3();       
        runSimSpaceOptimisedAggregated(simToRun, NUMRUNS, MAXTIME, outFile, NUMAGGR);
        System.out.println("*** DONE SIM3 AGGR ("+NUMAGGR+" seconds) "+(MAXTIME/86400)+" days ***");********/
        
        /*//String outFile = "/home/brodo/Universita/TrustSense2024/simResults/simResultsTest1.tsv";
        //String dir = "/home/brodo/Universita/TrustSense2024/simResults/";
        String dir = "./";
        //String outFileAggr = "/home/brodo/Universita/TrustSense2024/simResults/simResultsTest1Aggr.tsv";
        String outFile = dir+"simResultsTest1.tsv";
        SimParams simToRun = new SimParams1();        
        runSimSpaceOptimised(simToRun, NUMRUNS, MAXTIME, outFile);
        //runSimAggregated(simToRun, NUMRUNS, MAXTIME, outFileAggr, AGGR); 
        System.out.println("*** DONE SIM1 10 days ***");
        
        outFile = dir+"simResultsTest2.tsv";
        simToRun = new SimParams2();
        runSimSpaceOptimised(simToRun, NUMRUNS, MAXTIME, outFile);
        System.out.println("*** DONE SIM2 10 days ***"); 
        
        outFile = dir+"simResultsTest3.tsv";
        simToRun = new SimParams3();
        runSimSpaceOptimised(simToRun, NUMRUNS, MAXTIME, outFile);
        System.out.println("*** DONE SIM3 10 days ***"); */
        
        
        /*MAXTIME = 1209600;// 2 weeks
        
        outFile = dir+"simResultsTest1LONG.tsv";
        simToRun = new SimParams1();
        runSimSpaceOptimised(simToRun, NUMRUNS, MAXTIME, outFile);
        System.out.println("*** DONE SIM1 14 days ***"); 
        
        outFile = dir+"simResultsTest2LONG.tsv";
        simToRun = new SimParams2();
        runSimSpaceOptimised(simToRun, NUMRUNS, MAXTIME, outFile);
        System.out.println("*** DONE SIM2 14 days ***"); 
        
        outFile = dir+"simResultsTest3LONG.tsv";
        simToRun = new SimParams3();
        runSimSpaceOptimised(simToRun, NUMRUNS, MAXTIME, outFile);
        System.out.println("*** DONE SIM3 14 days ***"); */
        
        //randomGenerationEfficiencyTests(100000000);
        
        //printProbTesting();
        
    }
    public static void printProbTesting(){
        String outFileHead = "/home/brodo/Universita/Donini/simResults/simProb";
        String outFileTail = ".tsv";
        int maxTime = 3600;//2 hours
        printProb(new ExponentialProbDistr(0.0001, 0.01), maxTime, outFileHead+"exp1"+outFileTail);
        printProb(new ExponentialProbDistr(0.001, 0.01), maxTime, outFileHead+"exp2"+outFileTail);
        printProb(new ExponentialProbDistr(0.01, 0.01), maxTime, outFileHead+"exp3"+outFileTail);
        printProb(new ExponentialProbDistr(0.1, 0.01), maxTime, outFileHead+"exp4"+outFileTail);
        printProb(new ExponentialProbDistr(1, 0.01), maxTime, outFileHead+"exp5"+outFileTail);        
        printProb(new ExponentialProbDistr(10, 0.01), maxTime, outFileHead+"exp6"+outFileTail);
        printProb(new ExponentialProbDistr(100, 0.01), maxTime, outFileHead+"exp7"+outFileTail);
        
        printProb(new NormalProbDistr(100,10, 1), maxTime, outFileHead+"norm1"+outFileTail);
        printProb(new NormalProbDistr(100,10, 0.1), maxTime, outFileHead+"norm2"+outFileTail);
        printProb(new NormalProbDistr(100,10, 0.01), maxTime, outFileHead+"norm3"+outFileTail);
        printProb(new NormalProbDistr(100,100, 1), maxTime, outFileHead+"norm4"+outFileTail);
        printProb(new NormalProbDistr(100,100, 0.1), maxTime, outFileHead+"norm5"+outFileTail);
        printProb(new NormalProbDistr(100,100, 0.01), maxTime, outFileHead+"norm6"+outFileTail);
        
        
        printProb(new LognormalProbDistr(10,10, 0.1), maxTime, outFileHead+"lognorm1"+outFileTail);
        printProb(new LognormalProbDistr(10,10, 0.01), maxTime, outFileHead+"lognorm2"+outFileTail);
        printProb(new LognormalProbDistr(10,10, 0.1), maxTime, outFileHead+"lognorm3"+outFileTail);
        printProb(new LognormalProbDistr(1,1, 0.1), maxTime, outFileHead+"lognorm4"+outFileTail);
        printProb(new LognormalProbDistr(1,1, 0.01), maxTime, outFileHead+"lognorm5"+outFileTail);
        printProb(new LognormalProbDistr(1,1, 0.001), maxTime, outFileHead+"lognorm6"+outFileTail);
        
        
        
        printProb(new LognormalProbDistrScaled(1,1, 0.01, 1), maxTime, outFileHead+"lognormScaled"+outFileTail);
        printProb(new NormalProbDistrScaled(100,10, 0.04,4), maxTime, outFileHead+"normScaled"+outFileTail);
        printProb(new ExponentialProbDistrScaled(0.1, 0.01, 2), maxTime, outFileHead+"expScaled"+outFileTail);
        printProb(new UniformProbDistr(0.0001), maxTime, outFileHead+"uniformScaled"+outFileTail);

    }
        
    public static void printProb(ProbabilityFunction prob, int MAXTIME, String outFile){
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(outFile));
        for(int j=0;j<MAXTIME;j++){
            bw.write(j+"\t"+prob.getProb(j));
            bw.newLine();
        }
        
        bw.close();
        } catch (IOException ex) {
            System.err.println("ERROR WHILE WRITING TO FILE.");
            ex.printStackTrace();
        }
    }
    
    //1) all we care about is the current time results (we discard the historical serie)
    //2) all we care about for creators and assets is the time they where created, so we model them as repeatable ints (as longs are not yet needed as of the time of writing and ints are twice as small than longs)
    public static void runSimSpaceOptimised(SimParams simToRun, int NUMRUNS, int MAXTIME, String outFile){
        //Master is implicit at time 0
        SimRoundResults sRounds = new SimRoundResults(NUMRUNS, simToRun); 
        double resultMean, resultStd;
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))) {
            
            bw.write("time\tgasTotalMean\tgasTotalStd");
            bw.write("\tgasNewCreatorMean\tgasNewCreatorStd");
            bw.write("\tgasNewAssetMean\tgasNewAssetStd");
            bw.write("\tgasHolderPolicyUpdateMean\tgasHolderPolicyUpdateStd");
            bw.write("\tgasCharacteristicUpdateMean\tgasCharacteristicUpdateStd");
            bw.write("\tgasTransferMean\tgasTransferStd");
            bw.write("\tTotalNumCreators\tmaxCreators\tminCreators\tavgCreators\tstdCreators\tTotalNumAssets\tmaxAssets\tminAssets\tavgAssets\tstdAssets");
            bw.newLine();
        
            long time = System.currentTimeMillis();
            
        for(int i=0;i<MAXTIME;i++){
            if(i%100000==0){
                System.out.println("Sim run at time "+i+"/"+MAXTIME+" in time "+ (System.currentTimeMillis()-time)+" ms.");
                time = System.currentTimeMillis();
            }
            sRounds.computeSimStepNoMasterFixedRandom(i);                
            
            bw.write(""+i);
            
            //total gas stats
            resultMean = SimRoundResults.computeAvg(sRounds.gasTotal);
            resultStd = SimRoundResults.computeStd(sRounds.gasTotal, resultMean);
            bw.write("\t"+resultMean+"\t"+resultStd);
            
            //individual operation gas stats
            resultMean = SimRoundResults.computeAvg(sRounds.gasNewCreator);
            resultStd = SimRoundResults.computeStd(sRounds.gasNewCreator, resultMean);
            bw.write("\t"+resultMean+"\t"+resultStd);
            resultMean = SimRoundResults.computeAvg(sRounds.gasNewAsset);
            resultStd = SimRoundResults.computeStd(sRounds.gasNewAsset, resultMean);
            bw.write("\t"+resultMean+"\t"+resultStd);
            resultMean = SimRoundResults.computeAvg(sRounds.gasHolderPolicyUpdate);
            resultStd = SimRoundResults.computeStd(sRounds.gasHolderPolicyUpdate, resultMean);
            bw.write("\t"+resultMean+"\t"+resultStd);
            resultMean = SimRoundResults.computeAvg(sRounds.gasCharacteristicUpdate);
            resultStd = SimRoundResults.computeStd(sRounds.gasCharacteristicUpdate, resultMean);
            bw.write("\t"+resultMean+"\t"+resultStd);
            resultMean = SimRoundResults.computeAvg(sRounds.gasTransfer);
            resultStd = SimRoundResults.computeStd(sRounds.gasTransfer, resultMean);
            bw.write("\t"+resultMean+"\t"+resultStd);
            
            bw.write(sRounds.getTSVInfoCreatorsAssetsStats());
            
            bw.newLine();
        }
        
        } catch (IOException ex) {
            System.err.println("ERROR WHILE WRITING TO FILE.");
            ex.printStackTrace();
        }
        
        
        System.out.println("Ending sim of "+NUMRUNS+" runs.");
        //System.out.println("TotalNumCreators "+numCreators+" , maxCreators "+maxCreators+" , minCreators "+minCreators+" , avgCreators "+avgCreators+" , stdCreators "+stdCreators); 
        //System.out.println("TotalNumAssets "+numAssets+" , maxAssets "+maxAssets+" , minAssets "+minAssets+" , avgAssets "+avgAssets+" , stdAssets "+stdAssets); 
        
    }
    
    
    
    //1) all we care about is the current time results (we discard the historical serie)
    //2) all we care about for creators and assets is the time they where created, so we model them as repeatable ints (as longs are not yet needed as of the time of writing and ints are twice as small than longs)
    public static void runSimSpaceOptimisedAggregated(SimParams simToRun, int NUMRUNS, int MAXTIME, String outFile, int NUMAGGR){
        //Master is implicit at time 0
        SimRoundResultsAggregated sRounds = new SimRoundResultsAggregated(NUMRUNS, simToRun, NUMAGGR); 
        double resultMean, resultStd;
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))) {
            
            bw.write("time\tgasTotalMean\tgasTotalStd");
            bw.write("\tgasNewCreatorMean\tgasNewCreatorStd");
            bw.write("\tgasNewAssetMean\tgasNewAssetStd");
            bw.write("\tgasHolderPolicyUpdateMean\tgasHolderPolicyUpdateStd");
            bw.write("\tgasCharacteristicUpdateMean\tgasCharacteristicUpdateStd");
            bw.write("\tgasTransferMean\tgasTransferStd");
            bw.write("\tTotalNumCreators\tmaxCreators\tminCreators\tavgCreators\tstdCreators\tTotalNumAssets\tmaxAssets\tminAssets\tavgAssets\tstdAssets");
            bw.newLine();
        
            long time = System.currentTimeMillis();
            
        for(int i=0;i<MAXTIME;i = i+NUMAGGR){
            if(i%(NUMAGGR*2000)==0){
                System.out.println("Sim run at time "+i+"/"+MAXTIME+" in time "+ (System.currentTimeMillis()-time)+" ms.");
                time = System.currentTimeMillis();
            }
            sRounds.computeSimStepNoMasterFixedRandomAggregated(i);                
            
            bw.write(""+i);
            
            //total gas stats
            resultMean = SimRoundResults.computeAvg(sRounds.gasTotal);
            resultStd = SimRoundResults.computeStd(sRounds.gasTotal, resultMean);
            bw.write("\t"+resultMean+"\t"+resultStd);
            
            //individual operation gas stats
            resultMean = SimRoundResults.computeAvg(sRounds.gasNewCreator);
            resultStd = SimRoundResults.computeStd(sRounds.gasNewCreator, resultMean);
            bw.write("\t"+resultMean+"\t"+resultStd);
            resultMean = SimRoundResults.computeAvg(sRounds.gasNewAsset);
            resultStd = SimRoundResults.computeStd(sRounds.gasNewAsset, resultMean);
            bw.write("\t"+resultMean+"\t"+resultStd);
            resultMean = SimRoundResults.computeAvg(sRounds.gasHolderPolicyUpdate);
            resultStd = SimRoundResults.computeStd(sRounds.gasHolderPolicyUpdate, resultMean);
            bw.write("\t"+resultMean+"\t"+resultStd);
            resultMean = SimRoundResults.computeAvg(sRounds.gasCharacteristicUpdate);
            resultStd = SimRoundResults.computeStd(sRounds.gasCharacteristicUpdate, resultMean);
            bw.write("\t"+resultMean+"\t"+resultStd);
            resultMean = SimRoundResults.computeAvg(sRounds.gasTransfer);
            resultStd = SimRoundResults.computeStd(sRounds.gasTransfer, resultMean);
            bw.write("\t"+resultMean+"\t"+resultStd);
            
            bw.write(sRounds.getTSVInfoCreatorsAssetsStats());
            
            bw.newLine();
        }
        } catch (IOException ex) {
            System.err.println("ERROR WHILE WRITING TO FILE.");
            ex.printStackTrace();
        }
        System.out.println("Ending sim of "+NUMRUNS+" runs.");
    } 
    
    public static void randomGenerationEfficiencyTests(int num){
        
        int n = num;
        double randomDouble = 0;
        long startTime, endTime, avg;
        
        // Using ThreadLocalRandom
        startTime = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            randomDouble = ThreadLocalRandom.current().nextDouble();
        }
        endTime = System.currentTimeMillis();
        avg = (endTime - startTime)/n;
        System.out.println("ThreadLocalRandom time: " + (endTime - startTime) + " ms (" + avg+ " per call)");

        // Using a shared Random instance
        startTime = System.currentTimeMillis();
        Random sharedRandom = new Random();
        for (int i = 0; i < n; i++) {
            randomDouble = sharedRandom.nextDouble();
        }
        endTime = System.currentTimeMillis();
        avg = (endTime - startTime)/n;
        System.out.println("Shared Random time: " + (endTime - startTime) + " ms (" + avg+ " per call)");
        
        // Using Math Random
        startTime = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            randomDouble = Math.random();
        }
        endTime = System.currentTimeMillis();
        avg = (endTime - startTime)/n;
        System.out.println("Math Random time: " + (endTime - startTime) + " ms (" + avg+ " per call)");
        
        // Using a SplittableRandom instance
        startTime = System.currentTimeMillis();
        SplittableRandom splittableRandom = new SplittableRandom();
        for (int i = 0; i < n; i++) {
            randomDouble = splittableRandom.nextDouble();
        }
        endTime = System.currentTimeMillis();
        avg = (endTime - startTime)/n;
        System.out.println("Splittable Random time: " + (endTime - startTime) + " ms (" + avg+ " per call)");   
        
    }
    
    
    /*public static void runSim(SimParams simToRun, int NUMRUNS, int MAXTIME, String outFile){
        //Master is implicit at time 0
        HashSet<Entity> creatorsJacket;
        HashSet<Entity> assetsJacket;
        
        double resultsJacketTemp;
        //double[] resultJacket = new double[MAXTIME];
        double[][] resultJacketMatrix = new double[NUMRUNS][MAXTIME];
        
        double numCreators = 0, numAssets = 0;
        int maxCreators = 0, maxAssets = 0;
        int minCreators = -1, minAssets = -1;
        
        
        for(int j=0;j<NUMRUNS;j++){
            
            System.out.println("Starting sim run "+(j+1)+" out of "+NUMRUNS);
            creatorsJacket = new HashSet();
            assetsJacket = new HashSet();
            
            for(int i=0;i<MAXTIME;i++){
                if(i%100000==0)
                    System.out.println("Sim run "+(j+1)+"/"+NUMRUNS+" : time "+i+"/"+MAXTIME);
                
                resultsJacketTemp = computeSimStepJacketNoMasterFixedRandom(i, creatorsJacket, assetsJacket, simToRun);
                resultJacketMatrix[j][i] = resultsJacketTemp;
                *//*if(j==0){
                    resultJacket[i] = resultsJacketTemp;
                }else{
                    resultJacket[i] = (resultsJacketTemp + resultJacket[i]*j)/(j+1);
                }*//*
                //System.out.println(resultsJacketTemp +" - "+resultJacket[i]);
                
            
            }
            numCreators += creatorsJacket.size(); numAssets += assetsJacket.size();
            if(creatorsJacket.size() > maxCreators){ maxCreators = creatorsJacket.size();}
            if(creatorsJacket.size() > maxAssets){ maxAssets = creatorsJacket.size();}
            if((minCreators == -1)||(assetsJacket.size() < minCreators)){ minCreators = assetsJacket.size();}
            if((minAssets == -1)||(assetsJacket.size() < minAssets)){ minAssets = assetsJacket.size();}
            System.out.println("Ending sim run "+(j+1)+" out of "+NUMRUNS+" - creators "+creatorsJacket.size()+" assets "+assetsJacket.size());
        }
        
        
        double[] resultJacketMean = new double[MAXTIME];
        double[] resultJacketStd = new double[MAXTIME];
        double mean, std;
        for(int i=0;i<MAXTIME;i++){
            mean = 0;
            std = 0;
            for(int j=0;j<NUMRUNS;j++){
                mean += resultJacketMatrix[j][i];
            }
            mean = mean / NUMRUNS;
            resultJacketMean[i] = mean;
            for(int j=0;j<NUMRUNS;j++){
                std += (resultJacketMatrix[j][i] - mean)*(resultJacketMatrix[j][i] - mean);
            }
            std = Math.sqrt(std/NUMRUNS);
            resultJacketStd[i] = std;
            
            *//*if((i<=400)&&(mean>0)){
                System.out.println("Found at i "+i);
                for(int j=0;j<NUMRUNS;j++){
                    System.out.println(resultJacketMatrix[j][i]);
                }
            }*//*
        }
        
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(outFile));
        for(int j=0;j<MAXTIME;j++){
            //bw.write(j+"\t"+resultJacket[j]+"\t"+resultJacketMean[j]+"\t"+resultJacketStd[j]+"\t"+ETHMAXGASPERSEC+"\t"+BINANCESMARTCHAINMAXGASPERSEC+"\t"+OPTIMISMMAINNETMAXGASPERSEC+"\t"+POLYGONPOSCHAINMAXGASPERSEC);
            bw.write(j+"\t"+resultJacketMean[j]+"\t"+resultJacketStd[j]);
            bw.newLine();
        }
        
        bw.close();
        } catch (IOException ex) {
            System.err.println("ERROR WHILE WRITING TO FILE.");
            ex.printStackTrace();
        }
        System.out.println("Ending sim of "+NUMRUNS+" runs.");
        System.out.println("NumCreators "+numCreators+" , maxCreators "+maxCreators+" , minCreators "+minCreators); 
        System.out.println("NumAssets "+numAssets+" , maxAssets "+maxAssets+" , minAssets "+minAssets); 
    }*/
    
    /*public static void runSimAggregated(SimParams simToRun, int NUMRUNS, int MAXTIME, String outFile, int AGGR){
        //Master is implicit at time 0
        HashSet<Entity> creatorsJacket;
        HashSet<Entity> assetsJacket;
        
        double resultsJacketTemp;
        //double[] resultJacket = new double[MAXTIME];
        double[][] resultJacketMatrix = new double[NUMRUNS][MAXTIME];
        
        
        
        for(int j=0;j<NUMRUNS;j++){
            
            System.out.println("Starting sim run "+(j+1)+" out of "+NUMRUNS);
            creatorsJacket = new HashSet();
            assetsJacket = new HashSet();
            
            for(int i=0;i<MAXTIME;i++){
                if(i%5000==0)
                    System.out.println("Sim run "+(j+1)+"/"+NUMRUNS+" : time "+i+"/"+MAXTIME);
                
                resultsJacketTemp = computeSimStepJacketNoMaster(i, creatorsJacket, assetsJacket, simToRun);
                resultJacketMatrix[j][i] = resultsJacketTemp;
                //System.out.println(resultsJacketTemp +" - "+resultJacket[i]);
                
            
            }
            System.out.println("Ending sim run "+(j+1)+" out of "+NUMRUNS+" - creators "+creatorsJacket.size()+" assets "+assetsJacket.size());
        }
        
        
        double[] resultJacketMean = new double[MAXTIME];
        double[] resultJacketStd = new double[MAXTIME];
        double mean, std;
        //compute mean and std over AGGR sec intervals
        int aggrSize;
        if(MAXTIME%AGGR == 0)
            aggrSize = MAXTIME/AGGR;
        else
            aggrSize = MAXTIME/AGGR + 1;
        double[] resultJacketMeanAggr = new double[aggrSize];
        double[] resultJacketStdAggr = new double[aggrSize];
        for(int i=0;i<MAXTIME;i++){
            mean = 0;
            std = 0;
            for(int j=0;j<NUMRUNS;j++){
                mean += resultJacketMatrix[j][i];
            }
            mean = mean / NUMRUNS;
            resultJacketMean[i] = mean;
            for(int j=0;j<NUMRUNS;j++){
                std += (resultJacketMatrix[j][i] - mean)*(resultJacketMatrix[j][i] - mean);
            }
            std = Math.sqrt(std/NUMRUNS);
            resultJacketStd[i] = std;
            
            //if((i<=400)&&(mean>0)){
            //    System.out.println("Found at i "+i);
            //    for(int j=0;j<NUMRUNS;j++){
            //        System.out.println(resultJacketMatrix[j][i]);
            //    }
            //}
            if((i+1)%AGGR == 0){
                resultJacketMeanAggr[(i+1)/AGGR - 1] = 0;
                for(int k=0;k<AGGR;k++){
                    resultJacketMeanAggr[(i+1)/AGGR - 1] += resultJacketMean[i-k];
                }
                //to sum std we sum its squares and then sqrt them
                resultJacketStdAggr[(i+1)/AGGR - 1] = 0;
                for(int k=0;k<AGGR;k++){
                    resultJacketStdAggr[(i+1)/AGGR - 1] += resultJacketStd[i-k]*resultJacketStd[i-k];
                }
                resultJacketMeanAggr[(i+1)/AGGR - 1] = Math.sqrt(resultJacketStdAggr[(i+1)/AGGR - 1]);
            }
        }
        
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(outFile));
        for(int j=0;j<aggrSize;j++){
            //bw.write(j+"\t"+resultJacket[j]+"\t"+resultJacketMean[j]+"\t"+resultJacketStd[j]+"\t"+ETHMAXGASPERSEC+"\t"+BINANCESMARTCHAINMAXGASPERSEC+"\t"+OPTIMISMMAINNETMAXGASPERSEC+"\t"+POLYGONPOSCHAINMAXGASPERSEC);
            bw.write(((j+1)*AGGR)+"\t"+resultJacketMeanAggr[j]+"\t"+resultJacketStdAggr[j]);
            bw.newLine();
        }
        
        bw.close();
        } catch (IOException ex) {
            System.err.println("ERROR WHILE WRITING TO FILE.");
            ex.printStackTrace();
        }
        System.out.println("Ending sim of "+NUMRUNS+" runs.");
    }*/
    
    /*
    gasConsumption(t) = C_{NMT}(t) + C_{creators}(t) + C_{assetCreations}(t) + C_{assetOperations}(t)
    C_{NMT}(t) = C_{deployNMT} iff t==0
    C_{creators}(t) = C_{creatorPolicyUpdate} * \sum_{i=0...|C(t)|}P_{creatorPolicyUpdate}(t-T(c_i))
    C_{assetCreations}(t) = C_{newAssetsCreation}* \sum_{i=0...|C(t)|}(P_{newAssetCreation}(t-T(c_i))
    C_{assetOperations}(t)= \sum_{i=0...|A(t)|}( C_{holderPolicyUpdate}*P_{holderPolicyUpdate}(t-T(a_i)) + C_{characteristicUpdate}*P_{characteristicUpdate}(t-T(a_i)) 
        + C_{transfer}*P_{transfer}(t-T(a_i)))
    */
    /*private static double computeSimStepJacketNoMaster(int time, HashSet<Entity> creators, HashSet<Entity> assets, SimParams sim){
        double result = 0;
        //System.out.print(result+"->");
        if(time == 0){
            result += sim.GASdeployNMT();
        }      
        //System.out.print(result+"->");
        //random creation of new creator
        if(Math.random()<=sim.PROBnewCreatorCreation().getProb(time)){
            creators.add(new Entity(time));
        }        
        for(Entity creator:creators){
            //result += sim.GAScreatorPolicyUpdate*sim.PROBcreatorPolicyUpdate.getProb(time-creator.startTime);
            if(Math.random()<=sim.PROBcreatorPolicyUpdate().getProb(time-creator.startTime)){
                result += sim.GAScreatorPolicyUpdate();
            }
        }
        for(Entity creator:creators){
            //result += sim.GASnewAssetsCreation*sim.PROBnewAssetsCreation.getProb(time-creator.startTime);
            if(Math.random()<=sim.PROBnewAssetsCreation().getProb(time-creator.startTime)){
                assets.add(new Entity(time));
                result += sim.GASnewAssetsCreation();
            }
        }
        //System.out.print(result+"->");
        for(Entity asset:assets){
            //result += sim.GASholderPolicyUpdate*sim.PROBholderPolicyUpdate.getProb(time-asset.startTime);
            //result += sim.GAScharacteristicUpdate*sim.PROBcharacteristicUpdate.getProb(time-asset.startTime);
            //result += sim.GAStransfer*sim.PROBtransfer.getProb(time-asset.startTime);
            if(Math.random()<=sim.PROBholderPolicyUpdate().getProb(time-asset.startTime)){
                result += sim.GASholderPolicyUpdate();
            }
            if(Math.random()<=sim.PROBcharacteristicUpdate().getProb(time-asset.startTime)){
                result += sim.GAScharacteristicUpdate();
            }
            if(Math.random()<=sim.PROBtransfer().getProb(time-asset.startTime)){
                result += sim.GAStransfer();
            }
        }
        //System.out.println(result);
        return result;
    }*/
    
    /*private static double computeSimStepJacketNoMasterFixedRandom(int time, HashSet<Entity> creators, HashSet<Entity> assets, SimParams sim){
        double result = 0;
        
        double random = Math.random();
        
        //System.out.print(result+"->");
        if(time == 0){
            result += sim.GASdeployNMT();
        }      
        //System.out.print(result+"->");
        //random creation of new creator
        if(random<=sim.PROBnewCreatorCreation().getProb(time)){
            creators.add(new Entity(time));
        }        
        for(Entity creator:creators){
            //result += sim.GAScreatorPolicyUpdate*sim.PROBcreatorPolicyUpdate.getProb(time-creator.startTime);
            if(random<=sim.PROBcreatorPolicyUpdate().getProb(time-creator.startTime)){
                result += sim.GAScreatorPolicyUpdate();
            }
        }
        for(Entity creator:creators){
            //result += sim.GASnewAssetsCreation*sim.PROBnewAssetsCreation.getProb(time-creator.startTime);
            if(random<=sim.PROBnewAssetsCreation().getProb(time-creator.startTime)){
                assets.add(new Entity(time));
                result += sim.GASnewAssetsCreation();
            }
        }
        //System.out.print(result+"->");
        for(Entity asset:assets){
            //result += sim.GASholderPolicyUpdate*sim.PROBholderPolicyUpdate.getProb(time-asset.startTime);
            //result += sim.GAScharacteristicUpdate*sim.PROBcharacteristicUpdate.getProb(time-asset.startTime);
            //result += sim.GAStransfer*sim.PROBtransfer.getProb(time-asset.startTime);
            if(random<=sim.PROBholderPolicyUpdate().getProb(time-asset.startTime)){
                result += sim.GASholderPolicyUpdate();
            }
            if(random<=sim.PROBcharacteristicUpdate().getProb(time-asset.startTime)){
                result += sim.GAScharacteristicUpdate();
            }
            if(random<=sim.PROBtransfer().getProb(time-asset.startTime)){
                result += sim.GAStransfer();
            }
        }
        //System.out.println(result);
        return result;
    }*/
    
    /*
    gasConsumption(t) = C_{master}(t) + C_{creators}(t) + C_{assetCreations}(t) + C_{assetOperations}(t)
    C_{master}(t) = C_{updateMasterPolicy}*P_{masterPolicyUpdate}(t)
    C_{creators}(t) = C_{creatorPolicyUpdate} * \sum_{i=0...|C(t)|}P_{creatorPolicyUpdate}(t-T(c_i))
    C_{assetCreations}(t) = C_{newAssetsCreation}* \sum_{i=0...|C(t)|}(P_{newAssetCreation}(t-T(c_i))
    C_{assetOperations}(t)= \sum_{i=0...|A(t)|}( C_{holderPolicyUpdate}*P_{holderPolicyUpdate}(t-T(a_i)) + C_{characteristicUpdate}*P_{characteristicUpdate}(t-T(a_i)) 
        + C_{transfer}*P_{transfer}(t-T(a_i)))
    */
    /*private static double computeSimStep(int time, HashSet<Entity> creators, HashSet<Entity> assets, SimParams sim){
        double result = 0;
        //System.out.print(result+"->");
        if(time == 0){
            result += sim.GASdeployNMT()+sim.GASdeployMasterPolicy();
        }
        //System.out.println("sim "+sim.GASdeployNMT()+" time "+time+" result "+result+"->");
        //result += sim.GASupdateMasterPolicy*sim.PROBupdateMasterPolicy.getProb(time);
        if(Math.random()<=sim.PROBupdateMasterPolicy().getProb(time)){
            result += sim.GASupdateMasterPolicy();
        }        
        //System.out.print(result+"->");
        //random creation of new creator
        if(Math.random()<=sim.PROBnewCreatorCreation().getProb(time)){
            creators.add(new Entity(time));
        }        
        for(Entity creator:creators){
            //result += sim.GAScreatorPolicyUpdate*sim.PROBcreatorPolicyUpdate.getProb(time-creator.startTime);
            if(Math.random()<=sim.PROBcreatorPolicyUpdate().getProb(time-creator.startTime)){
                result += sim.GAScreatorPolicyUpdate();
            }
        }
        for(Entity creator:creators){
            //result += sim.GASnewAssetsCreation*sim.PROBnewAssetsCreation.getProb(time-creator.startTime);
            if(Math.random()<=sim.PROBnewAssetsCreation().getProb(time-creator.startTime)){
                assets.add(new Entity(time));
                result += sim.GASnewAssetsCreation();
            }
        }
        //System.out.print(result+"->");
        for(Entity asset:assets){
            //result += sim.GASholderPolicyUpdate*sim.PROBholderPolicyUpdate.getProb(time-asset.startTime);
            //result += sim.GAScharacteristicUpdate*sim.PROBcharacteristicUpdate.getProb(time-asset.startTime);
            //result += sim.GAStransfer*sim.PROBtransfer.getProb(time-asset.startTime);
            if(Math.random()<=sim.PROBholderPolicyUpdate().getProb(time-asset.startTime)){
                result += sim.GASholderPolicyUpdate();
            }
            if(Math.random()<=sim.PROBcharacteristicUpdate().getProb(time-asset.startTime)){
                result += sim.GAScharacteristicUpdate();
            }
            if(Math.random()<=sim.PROBtransfer().getProb(time-asset.startTime)){
                result += sim.GAStransfer();
            }
        }
        //System.out.println(result);
        return result;
    }*/
    
}
