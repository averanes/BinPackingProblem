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
public class VeicType {
    
    int numOfVeicType; // Type of vehicle
    int VeicCost; // Cost for use the Vehicle
    int VeicVolume; // Volume of the Vehicle

    public VeicType( int VeicCost, int VeicVolume) {
        this.VeicCost = VeicCost;
        this.VeicVolume = VeicVolume;
    }
    
    public VeicType(int numOfVeicType, int VeicCost, int VeicVolume) {
        this.numOfVeicType = numOfVeicType;
        this.VeicCost = VeicCost;
        this.VeicVolume = VeicVolume;
    }

    public int getNumOfVeicType() {
        return numOfVeicType;
    }

    public int getVeicCost() {
        return VeicCost;
    }

    public int getVeicVolume() {
        return VeicVolume;
    }

    public void setNumOfVeicType(int numOfVeicType) {
        this.numOfVeicType = numOfVeicType;
    }

    public void setVeicCost(int VeicCost) {
        this.VeicCost = VeicCost;
    }

    public void setVeicVolume(int VeicVolume) {
        this.VeicVolume = VeicVolume;
    }
    
    
    
}
