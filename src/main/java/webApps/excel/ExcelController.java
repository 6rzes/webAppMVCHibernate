package webApps.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public  class ExcelController {
private static String data = "";
private static List dataList= new ArrayList();
private static String dataString[][];
private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static String[][] readXLSFile(String fileName) throws IOException
    {
        InputStream ExcelFileToRead = new FileInputStream(fileName);
        HSSFWorkbook wb = new HSSFWorkbook(ExcelFileToRead);

        HSSFSheet sheet=wb.getSheetAt(0);
        HSSFRow row;
        HSSFCell cell;

        //Init of dataString array
        int numberOfRows =sheet.getPhysicalNumberOfRows();
        int numberOfCol= sheet.getRow(0).getPhysicalNumberOfCells();
        dataString = new String[numberOfRows][numberOfCol];

        Iterator rows = sheet.rowIterator();

        while (rows.hasNext())
        {
        row=(HSSFRow) rows.next();
        Iterator cells = row.cellIterator();

        while (cells.hasNext())
        {
            cell=(HSSFCell) cells.next();

            if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING){
                dataString[cell.getRowIndex()][cell.getColumnIndex()]= cell.getStringCellValue();
            }
            else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC){
                dataString[cell.getRowIndex()][cell.getColumnIndex()]= String.valueOf( cell.getNumericCellValue());
                if(DateUtil.isCellDateFormatted(cell)) {
                    dataString[cell.getRowIndex()][cell.getColumnIndex()]= format.format(cell.getDateCellValue());
                }
            }
            else{
                //other cell types
            }
        }

    }
        return dataString;
    }

    public static String[][] readXLSXFile(String fileName) throws IOException
    {
        InputStream ExcelFileToRead = new FileInputStream(fileName);
        XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);

        XSSFWorkbook test = new XSSFWorkbook();

        XSSFSheet sheet = wb.getSheetAt(0);

        //Init of dataString array
        int numberOfRows =sheet.getPhysicalNumberOfRows();
        int numberOfCol= sheet.getRow(0).getPhysicalNumberOfCells();
        dataString = new String[numberOfRows][numberOfCol];

        XSSFRow row;
        XSSFCell cell;

        Iterator rows = sheet.rowIterator();

        while (rows.hasNext())
        {
            row=(XSSFRow) rows.next();
            Iterator cells = row.cellIterator();
            while (cells.hasNext())
            {
                cell=(XSSFCell) cells.next();

                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING){
                   dataString[cell.getRowIndex()][cell.getColumnIndex()]= cell.getStringCellValue();
                }
                else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC){
                    dataString[cell.getRowIndex()][cell.getColumnIndex()]= String.valueOf( cell.getNumericCellValue());
                    if(DateUtil.isCellDateFormatted(cell)) {
                      dataString[cell.getRowIndex()][cell.getColumnIndex()]= format.format(cell.getDateCellValue());
                    }
                }
                else{
                    //other cell types
                }
            }
        }

        return dataString;
    }
    public static List getDataList() {
        return dataList;
    }
}
