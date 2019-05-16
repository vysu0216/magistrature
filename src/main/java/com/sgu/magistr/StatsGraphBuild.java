package com.sgu.magistr;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * Created by vysu0216 on 16.05.2019.
 */
public class StatsGraphBuild {

    static File file;
    static InputStream inputStream;
    static XSSFWorkbook workbook;
    static XSSFSheet sheet;
    static XSSFCell cell;

    static {
        file = new File("D:\\Imitation_Model\\magistrature\\CharacteristicsDependancy.xlsx");
        try {
            inputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sheet = workbook.getSheetAt(0);
    }

    public static void updateCell(int rowNum, int cellNum, double value) throws IOException {
        cell = sheet.getRow(rowNum).createCell(cellNum);
        cell.setCellValue(value);
        inputStream.close();
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();
    }

    public static void main(String[] args) throws IOException {
        updateCell(1,1,0.1111111);
    }
}
