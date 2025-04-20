/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nmtsimulation;

/**
 *1,1 falls fast after 30
 * 
 * 10,10 best remains reasonable until 30
 * 
 * almost certain at the beginning and quickly falls to very low prob after 30 
 * (1,10, 0.1)
 * 
 * fairly probable at the beginning and then very unlikely after 40
 * (10,10, 0.1)
 * 
 * @author brodo
 */
public class LognormalProbDistr extends ProbabilityFunction{
    double mean;
    double std;
    double scalingFactor;
    
    public LognormalProbDistr(double _mean, double _std, double _scaling){
        mean = _mean; std = _std; scalingFactor = _scaling;
    }
    
    @Override
    public double getProb(int time){
        double realTime = time*scalingFactor;
        if(realTime == 0)
            return 0;
        double exp = -1 * (Math.log(realTime) - mean) * (Math.log(realTime) - mean) / (2*std*std);
        double ratio = realTime * std * Math.sqrt(2*Math.PI);
        return 1/ratio * Math.exp(exp); 
    }
}