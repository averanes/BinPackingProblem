/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adrian
 */
public class CreateFileResult {
    
    String nameFile="";
    String valueToPrint="";
    
    public static void main(String[] args) {
        
      CreateFileResult c=  new CreateFileResult("prueba");
      c.setValueToPrint("1sdfs");
      c.setValueToPrint("2sdfs");
      c.saveFile();
      
    }

    public CreateFileResult(String nameFile) {
        this.nameFile = nameFile;
    }
    

    public void setValueToPrint(String valueToPrint) {
        this.valueToPrint += valueToPrint+"\n";
    }
    
    public void saveFile(){
    
        FileWriter fw = null;
        try {
            File f = new File(nameFile+"-result.txt");
            fw = new FileWriter(f);
            
            fw.write(valueToPrint);
            
            
        } catch (IOException ex) {
            Logger.getLogger(CreateFileResult.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(CreateFileResult.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
    }
    
    
    
    
}
