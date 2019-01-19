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
public class PosValue {
    
    public int pos;
    public int value;

    public PosValue(int pos, int value) {
        this.pos = pos;
        this.value = value;
    }

    public void sumValue(int value) {
        this.value += value;
    }
    
    
}
