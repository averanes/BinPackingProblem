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
public class TimeValue {
    
    public int time;
    public int value;

    public TimeValue(int pos, int value) {
        this.time = pos;
        this.value = value;
    }

    public void sumValue(int value) {
        this.value += value;
    }
    
    
}
