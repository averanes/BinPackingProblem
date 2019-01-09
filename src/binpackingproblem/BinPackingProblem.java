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
         DataLoad.fileAddress = "MPVSBPP_SET1_IT1000_ITV1_NT1_TS3_WT1_VT1_REP2.dat";//"MPVSBPP_SET1_IT200_ITV1_NT1_TS3_WT1_VT1_REP1.dat";
         
         ResolverController m = ResolverController.getInstance();
         
         long startTime = System.currentTimeMillis();
         System.out.println("Heuristic Method Value: "+m.heuristicResolver() + " Delay in milliseconds: "+(System.currentTimeMillis()-startTime));
         
         
         int countOfExecutions = 100;
         startTime = System.currentTimeMillis();
         int minValue = Integer.MAX_VALUE;
         
         for (int i = 0; i < countOfExecutions; i++) {
            int resultTemp = m.GRASP_metaHeuristicResolver();
            
            if(resultTemp < minValue){
            minValue = resultTemp;
            }
        }
         
         System.out.println("GRASP Meta Heuristic Method Value: "+ minValue+ " Executions Count: "+countOfExecutions+ " Delay in milliseconds: "+(System.currentTimeMillis()-startTime));
         
         
    }
    
}
