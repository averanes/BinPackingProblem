/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binpackingproblem;

import controller.ResolverController;
import dao.DataLoad;

/**
 *
 * @author Adrian
 */
public class BinPackingProblem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         DataLoad.fileAddress = "MPVSBPP_SET1_IT200_ITV2_NT2_TS3_WT1_VT1_REP7.dat";//"MPVSBPP_SET1_IT200_ITV1_NT1_TS3_WT1_VT1_REP1.dat";
        
         ResolverController m = ResolverController.getInstance();
         
         System.out.println(m.heuristicResolver());
    }
    
}
