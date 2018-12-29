/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

/**
 *
 * @author Adrian
 */
public class Vehicle {
   
    int position;
    int [] capacity; //capacity associated to the vehicle k (This) at the time slot h
    int [] ﬁxed_cost;//ﬁxed cost associated to the vehicle k (This) at the time slot h
    int [] cost_per_stop;//Let ch k be the cost per stop.
    
    VeicType vtype; // 

    public Vehicle(int position, int[] capacity, int[] ﬁxed_cost, int[] cost_per_stop, VeicType vtype) {
        this.position = position;
        this.capacity = capacity;
        this.ﬁxed_cost = ﬁxed_cost;
        this.cost_per_stop = cost_per_stop;
        this.vtype = vtype;
    }
    
    public Vehicle(int position, VeicType vtype) {
        this.position = position;
        this.vtype = vtype;
    }

    public VeicType getVtype() {
        return vtype;
    }

    public int getPosition() {
        return position;
    }

    
    
}
