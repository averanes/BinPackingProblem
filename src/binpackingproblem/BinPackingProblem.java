/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binpackingproblem;

import controller.ResolverController;
import dao.CreateFileResult;
import dao.ManageExcel;
import java.io.FilenameFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
                return name.endsWith(".dat") && !name.startsWith("MPVSBPP_SET1_IT5000");
            }
        });

        //MPVSBPP_SET1_IT5000_ITV1_NT2_TS3_WT1_VT1_REP1.dat Se utilizan 2 tiempos porque se llena el satelite en el 1er tiempo
        // System.out.println("Heuristic Method Value: " + ResolverController.getInstance(new File("MPVSBPP_SET1_IT5000_ITV1_NT2_TS3_WT1_VT1_REP1.dat")).heuristicResolver());
        //realizamos los algoritmos para cada dataset
        int minimumCost;
        int sumCost = 0;  //sumCost 7183055   FULL-RESULT 31731024
        String excelFilePath = "results-assignment.xlsx";

        try {
            FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            for (int i = 0; i < files.length; i++) {

                ResolverController m = ResolverController.getInstance(files[i]);
                //ResolverController m = ResolverController.getInstance(new File("MPVSBPP_SET1_IT1000_ITV1_NT2_TS3_WT1_VT1_REP9.dat"));
                m.c = new CreateFileResult(files[i].getName());

                m.runAndPrintSolution = 2; //0 solo run, 1 run and update the value for excel and 2 1 run, update the value for excel and print in Console

                long startTime = System.currentTimeMillis();
                minimumCost = m.heuristicResolverDoble();
                long timeofExecution = System.currentTimeMillis() - startTime;

                sumCost += minimumCost;

                if (m.runAndPrintSolution == 2) {
                    print(m.c, files[i].getName());
                    print(m.c, "Heuristic Result: " + minimumCost + "\n" + "Delay in milliseconds after print: " + timeofExecution);
                    m.c.saveFile();
                }  
                
                if (m.runAndPrintSolution != 0) {
                    ManageExcel.updateExcel(workbook, files[i].getName(), minimumCost, timeofExecution, files.length, m.vehiclesCountByType, m.demandCountByTimeSlot);
                }
                
               
            }

            inputStream.close();
            FileOutputStream outputStream = new FileOutputStream(excelFilePath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            
        } catch (Exception ex) {
            Logger.getLogger(BinPackingProblem.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("sumCost " + sumCost);

    }

    public static void print(CreateFileResult c, String text) {
        c.setValueToPrint(text);
        //System.out.println(text);
    }

}
