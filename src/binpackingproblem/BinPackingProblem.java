/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binpackingproblem;

import controller.ResolverController;
import java.io.File;
import java.io.FilenameFilter;

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

        //realizamos los algoritmos para cada dataset
        int countOfExecutionsGRASP = 50;
        for (int i = 0; i < files.length; i++) {

            System.out.println(files[i].getName());

            ResolverController m = ResolverController.getInstance(files[i]);

            long startTime = System.currentTimeMillis();
            System.out.println("Heuristic Method Value: " + m.heuristicResolver()+ " Delay in milliseconds: " + (System.currentTimeMillis() - startTime));

            //********* GRASP ******
            /*startTime = System.currentTimeMillis();
            int minValue = Integer.MAX_VALUE;

            for (int j = 0; j < countOfExecutionsGRASP; j++) {
                int resultTemp = m.GRASP_metaHeuristicResolver();

                if (resultTemp < minValue) {
                    minValue = resultTemp;
                }
            }

            System.out.println("GRASP Meta Heuristic Method Value: " + minValue + " Executions Count: " + countOfExecutionsGRASP + " Delay in milliseconds: " + (System.currentTimeMillis() - startTime));
            */System.out.println();
        }

    }

}
