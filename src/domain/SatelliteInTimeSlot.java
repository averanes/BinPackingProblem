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
public class SatelliteInTimeSlot {
    
    int capacity; //Depot Cap[timeslots] We deﬁne Ch the capacity of the satellite at the time slot h (This)
    int tarif; //The time-dependent tarif for renting the satellite in each timeslot (Tarif[timeslots])
    int timeSlot; //Time Slot necesary carry up the position for sort

    

    public SatelliteInTimeSlot(int timeSlot, int capacity, int tarif) {
        this.timeSlot = timeSlot;
        this.capacity = capacity;
        this.tarif = tarif;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getTarif() {
        return tarif;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setTarif(int tarif) {
        this.tarif = tarif;
    }

    public int getTimeSlot() {
        return timeSlot;
    }
    
    
    
    
}
