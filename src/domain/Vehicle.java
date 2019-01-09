/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.List;

/**
 *
 * @author Adrian
 */
public class Vehicle {
   
    int position;
    //int [] capacity; //capacity associated to the vehicle k (This) at the time slot h
    //int [] ﬁxed_cost;//ﬁxed cost associated to the vehicle k (This) at the time slot h
    List<Integer> cost_per_stop;//Let ch k be the cost per stop.
    
    VeicType vtype; // 

    public Vehicle(int position, List<Integer> cost_per_stop, VeicType vtype) {
        this.position = position;
        this.cost_per_stop = cost_per_stop;
        this.vtype = vtype;
    }

    
    
    

    public VeicType getVtype() {
        return vtype;
    }

    public int getPosition() {
        return position;
    }

 
    public List<Integer> getCost_per_stop() {
        return cost_per_stop;
    }

    
    
}
