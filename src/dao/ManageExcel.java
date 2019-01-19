/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controller.ResolverController;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Addiel
 */
public class ManageExcel {

    public static void updateExcel(XSSFWorkbook workbook, String nameFileData, int minimumCost, long timeofExecution, int filesCount, Map<Integer, Integer> vehiclesCountByType, Map<Integer, Integer> demandCountByTimeSlot) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        try {
            /*
            FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            */
            
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> itRow = sheet.rowIterator();
            
            XSSFCellStyle hlinkstyle = workbook.createCellStyle();
            XSSFFont hlinkfont = workbook.createFont();
            hlinkfont.setUnderline(XSSFFont.U_SINGLE);
            /*
            short color = Short.valueOf("#0000ff");
            hlinkfont.setColor(color);
            */
            
            hlinkfont.setColor(IndexedColors.BLUE.getIndex());
            hlinkstyle.setFont(hlinkfont);

            int i = 0;
            nameFileData = nameFileData.replace(".dat", "");
            //String strFormula= "";

            while (itRow.hasNext() && i <= filesCount) {
                Row currentRow = itRow.next();

                if (currentRow.getCell(3) == null) {
                    currentRow.createCell(3);
                }
                currentRow.getCell(3).setCellType(CellType.STRING);
                if (currentRow.getCell(3).getStringCellValue().equals("") || currentRow.getCell(3).getStringCellValue().isEmpty()) {
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
                    
                    CreationHelper createHelper = workbook.getCreationHelper();
                    XSSFHyperlink link = (XSSFHyperlink)createHelper.createHyperlink(HyperlinkType.FILE);
                    link.setAddress(nameFileData + ".dat");
                    currentRow.getCell(3).setHyperlink(link);
                    currentRow.getCell(3).setCellStyle(hlinkstyle);
                    
                    link = (XSSFHyperlink)createHelper.createHyperlink(HyperlinkType.FILE);
                    link.setAddress(nameFileData + ".dat-result.txt");
                    currentRow.getCell(5).setHyperlink(link);
                    currentRow.getCell(5).setCellStyle(hlinkstyle);
                    
                    /*
                    CreationHelper createHelper = workbook.getCreationHelper();
                    Hyperlink link = createHelper.createHyperlink(HyperlinkType.FILE);
                    link.setAddress(nameFileData + ".dat-result.txt");
                    currentRow.getCell(3).setHyperlink((org.apache.poi.ss.usermodel.Hyperlink) link);
                    */
                    
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
                    //inicializando las celdas en cero
                    int c;
                    for (c = 11; c <= 18; c++) {
                        if (c != 15) {
                            if (currentRow.getCell(c) == null) {
                                currentRow.createCell(c);
                            }
                            currentRow.createCell(c);
                            currentRow.getCell(c).setCellType(CellType.NUMERIC);
                            currentRow.getCell(c).setCellValue(0);
                        }
                    }
                    
                    //inicializamos c en 10 pq es el numero de celda anterior a los tipos de vehiculos
                    c = 10;
                    for (Map.Entry<Integer, Integer> en : vehiclesCountByType.entrySet()) {
                        Integer key = en.getKey(); //type of vehicle
                        Integer value = en.getValue(); //cant of vehicles
                        currentRow.getCell(c + key).setCellValue(value);                        
                    }
                    
                    //inicializamos c en 10 pq es el numero de celda anterior a los intervalos de tiempo
                    c = 16;
                    for (Map.Entry<Integer, Integer> en : demandCountByTimeSlot.entrySet()) {
                        Integer key = en.getKey(); //time slot
                        Integer value = en.getValue(); //cant of demands
                        currentRow.getCell(c + key).setCellValue(value); 
                    }

                    i = filesCount + 1;
                }

                //System.out.println("ManageExcel" + i);
                i++;
            }
            
            /*
            inputStream.close();
            FileOutputStream outputStream = new FileOutputStream(excelFilePath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            */
        } catch (Exception ex) {
            Logger.getLogger(ResolverController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
