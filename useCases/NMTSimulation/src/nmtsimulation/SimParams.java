/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nmtsimulation;

/**
 *
 * @author brodo
 */
public class SimParams {
    
    public int GASdeployNMT(){return 0;}
    public int GASdeployMasterPolicy(){return 0;}
    public int GASupdateMasterPolicy(){return 0;}
    public ProbabilityFunction PROBupdateMasterPolicy(){return new UniformProbDistr(0.001);}
    public int GAScreatorPolicyUpdate(){return 0;}
    public ProbabilityFunction PROBcreatorPolicyUpdate(){return new UniformProbDistr(0.001);}
    public int GASnewAssetsCreation(){return 0;}
    public ProbabilityFunction PROBnewAssetsCreation(){return new UniformProbDistr(0.001);}
    public int GASholderPolicyUpdate(){return 0;}
    public ProbabilityFunction PROBholderPolicyUpdate(){return new UniformProbDistr(0.001);}
    public int GAScharacteristicUpdate(){return 0;}
    public ProbabilityFunction PROBcharacteristicUpdate(){return new UniformProbDistr(0.001);}
    public int GAStransfer(){return 0;}
    public ProbabilityFunction PROBtransfer(){return new UniformProbDistr(0.001);}
    
    public ProbabilityFunction PROBnewCreatorCreation(){return new UniformProbDistr(0.001);}
    
}
