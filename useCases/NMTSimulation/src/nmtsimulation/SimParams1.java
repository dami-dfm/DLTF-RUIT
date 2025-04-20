/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nmtsimulation;

/**
 *
 * @author brodo
 */
public class SimParams1 extends SimParams {
    
    //exponential -> the lower the rate, the less steep the curve
    //normal -> the higher the variance, the flatter it gets
    //lognormal -> the higher the variance, the less steep it is
    
    public int GASdeployNMT(){ return 2466753;}
    public int GASdeployMasterPolicy(){ return 285213;}
    public int GAScreatorPolicyUpdate(){ return 30072 + 496366;}
    public ProbabilityFunction PROBcreatorPolicyUpdate(){ return new UniformProbDistr(0.0001);}
    public int GASnewAssetsCreation(){ return 1025897;}
    public ProbabilityFunction PROBnewAssetsCreation(){ return new UniformProbDistr(0.001);}//return new LognormalProbDistr(10,100);}
    public int GASholderPolicyUpdate(){ return 30072 + 381453;}
    public ProbabilityFunction PROBholderPolicyUpdate(){ return new UniformProbDistr(0.0001);}
    public int GAScharacteristicUpdate(){ return 90893;}
    public ProbabilityFunction PROBcharacteristicUpdate(){ return new UniformProbDistr(0.001);}
    public int GAStransfer(){ return 98730;}
    public ProbabilityFunction PROBtransfer(){ return new UniformProbDistr(0.001);}
    
    public ProbabilityFunction PROBnewCreatorCreation(){ return new UniformProbDistr(0.0001);}//return new LognormalProbDistr(10,100);}
    
}
