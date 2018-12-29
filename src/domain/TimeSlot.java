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
public class TimeSlot {
    
    int tarif;

    /** Each time slot h has a tarif for renting a unitary space unit Th. */
    public TimeSlot(int tarif) {
        this.tarif = tarif;
    }

    public int getTarif() {
        return tarif;
    }

    public void setTarif(int tarif) {
        this.tarif = tarif;
    }
    
}
