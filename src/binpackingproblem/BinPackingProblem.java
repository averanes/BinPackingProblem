/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binpackingproblem;

import controller.ResolverController;
import dao.CreateFileResult;
import dao.ManageExcel;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;

/**
 *
 * @author Adrian
 */
public class BinPackingProblem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //obtenemos la lista de datasets
        File[] files = new File(".").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".dat");
            }
        });

       //MPVSBPP_SET1_IT5000_ITV1_NT2_TS3_WT1_VT1_REP1.dat Se utilizan 2 tiempos porque se llena el satelite en el 1er tiempo
       // System.out.println("Heuristic Method Value: " + ResolverController.getInstance(new File("MPVSBPP_SET1_IT5000_ITV1_NT2_TS3_WT1_VT1_REP1.dat")).heuristicResolver());
        
        
        //realizamos los algoritmos para cada dataset
       // int countOfExecutionsGRASP = 50;
       
        int minimumCost;
        int sumCost=0; //   FULL-RESULT 31731024
        String excelFilePath = "results-assignment.xlsx";
        for (int i = 0; i < files.length; i++) {

            ResolverController m = ResolverController.getInstance(files[i]);
            //ResolverController m = ResolverController.getInstance(new File("MPVSBPP_SET1_IT1000_ITV1_NT2_TS3_WT1_VT1_REP9.dat"));
            m.c=new CreateFileResult(files[i].getName());
             
            m.runAndPrintSolution = 2; //0 solo run, 1 run and update the value for excel and 2 1 run, update the value for excel and print in Console
           

            long startTime = System.currentTimeMillis();
            minimumCost = m.heuristicResolverPerfect();
            long timeofExecution = System.currentTimeMillis() - startTime;
            
            sumCost+=minimumCost;
            
            print(m.c, files[i].getName());
            print(m.c,"Heuristic Method Value: " + minimumCost + " Delay in milliseconds: " + timeofExecution);
            
            m.c.saveFile();
            
            
           /* for (Map.Entry<Integer, Integer> en : m.vehiclesCountByType.entrySet()) {
                Integer key = en.getKey(); //type of vehicle
                Integer value = en.getValue(); //cant of vehicles
            }
            
            for (Map.Entry<Integer, Integer> en : m.demandCountByTimeSlot.entrySet()) {
                Integer key = en.getKey(); //time slot
                Integer value = en.getValue(); //cant of demands
            }
            */
            
            //ManageExcel.updateExcel(excelFilePath, files[i].getName(), minimumCost, timeofExecution, files.length);
            

            
            
         //System.out.println();
        }
        
        System.out.println("sumCost "+sumCost );

    }
    
    public static void print(CreateFileResult c, String text){
        c.setValueToPrint(text);
        //System.out.println(text);
    }

}
