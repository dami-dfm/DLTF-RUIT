/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nmtsimulation;

/**
 * Best fits are 0.1 and 0.01
 * 
 * 
 * very likely at the beginning and falls fast after 40
 * (0.1, 0.01)
 * 
 * very flat and relatively unlikely in the beginning:
 * (0.01, 0.01)
 * 
 * certain at the beginning but then rapidly impossible:
 * (1, 0.01)
 * 
 * @author brodo
 */
public class ExponentialProbDistr extends ProbabilityFunction{
    double rate;
    double scalingFactor;
    
    public ExponentialProbDistr(double _rate, double _scaling){
        rate = _rate; scalingFactor = _scaling;
    }
    
    @Override
    public double getProb(int time){
        double realTime = time*scalingFactor;
        return rate*Math.exp(-1*rate*realTime); 
    }
}
