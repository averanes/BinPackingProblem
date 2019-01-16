/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controller.ResolverController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Addiel
 */
public class ManageExcel {

    public static void updateExcel(String excelFilePath, String nameFileData, int minimumCost, long timeofExecution, int filesCount) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        try {
            FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> itRow = sheet.rowIterator();

            int i = 0;
            nameFileData = nameFileData.replace(".dat", "");
            //String strFormula= "";

            while (itRow.hasNext() && i <= filesCount) {
                Row currentRow = itRow.next();

                if (currentRow.getCell(3) == null) {
                    currentRow.createCell(3);
                }
                currentRow.getCell(3).setCellType(CellType.STRING);
                if(currentRow.getCell(3).getStringCellValue().equals("") || currentRow.getCell(3).getStringCellValue().isEmpty()){
                  //escribe el valor nombre del fichero
                    //currentRow.getCell(3).setCellType(CellType.STRING);
                    currentRow.getCell(3).setCellValue(nameFileData);
                }
                
                // busca cual es la fila que tiene el nombre del archivo
                if (currentRow.getCell(3).getStringCellValue().equals(nameFileData)) {
                    if (currentRow.getCell(5) == null) {
                        currentRow.createCell(5);
                    }

                    //escribe el valor del costo minimo dos celdas a la derecha
                    currentRow.getCell(5).setCellType(CellType.NUMERIC);
                    currentRow.getCell(5).setCellValue(minimumCost);

                    if (currentRow.getCell(7) == null) {
                        currentRow.createCell(7);
                    }
                    currentRow.getCell(7).setCellType(CellType.NUMERIC);
                    currentRow.getCell(7).setCellValue(timeofExecution);

                    /*
                    if(currentRow.getCell(6) == null){
                        currentRow.createCell(6);
                    }
                    
                    strFormula = "=";
                    //=F2-B2                    
                    currentRow.getCell(6).setCellType(CellType.FORMULA);
                    currentRow.getCell(6).setCellFormula(strFormula);
                     */
                    i = filesCount + 1;
                }

                i++;
            }
            inputStream.close();

            FileOutputStream outputStream = new FileOutputStream(excelFilePath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception ex) {
            Logger.getLogger(ResolverController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
