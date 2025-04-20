/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nmtsimulation;

/**
 *
 * @author brodo
 */
public class SimParamsEvent extends SimParams {
    
    public int GASdeployNMT(){ return 2754564;}
    public int GASdeployMasterPolicy(){ return 341736;}
    public int GASupdateMasterPolicy(){ return 29792 + 341736;}
    public ProbabilityFunction PROBupdateMasterPolicy(){ return new UniformProbDistr(0.00001);}
    public int GAScreatorPolicyUpdate(){ return 29792 + 700889;}
    public ProbabilityFunction PROBcreatorPolicyUpdate(){ return new ExponentialProbDistr(0.001, 1);}
    public int GASnewAssetsCreation(){ return 1292537;}
    public ProbabilityFunction PROBnewAssetsCreation(){ return new LognormalProbDistr(0.01,100, 1);}
    public int GASholderPolicyUpdate(){ return 29792 + 318518;}
    public ProbabilityFunction PROBholderPolicyUpdate(){ return new ExponentialProbDistr(0.001, 1);}
    public int GAScharacteristicUpdate(){ return 100596;}
    public ProbabilityFunction PROBcharacteristicUpdate(){ return new NormalProbDistr(0.01,0.01, 1);}
    public int GAStransfer(){ return 95997;}
    public ProbabilityFunction PROBtransfer(){ return new UniformProbDistr(0.0001);}
    
    public ProbabilityFunction PROBnewCreatorCreation(){ return new NormalProbDistr(0.001,1000, 1);}
    
}
