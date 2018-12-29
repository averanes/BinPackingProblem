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
public class Order {
     int demand;

    /** For each order i a demand di (demand[orders]). */
    public Order(int demand) {
        this.demand = demand;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

   
    
    
}
