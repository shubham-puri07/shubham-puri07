package com.genentech.baseclasses;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.genentech.util.ExcelReader;
import com.genentech.util.PageManager;
import com.genentech.util.PageObject;

public class AltTagsBasePage extends PageObject{

	public AltTagsBasePage(PageManager pm, ExcelReader xl) {
		super(pm, xl);
	}
	
	private static List<String> urlsList = new ArrayList<>();
	private static List<String> resultURLList = new ArrayList<>();
	private static List<String> imagesList = new ArrayList<>();
	private static List<String> altTagsList = new ArrayList<>();
	List<WebElement> allImg;
	String actual_imageName = null;
	String actual_altTag = null;
	SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
	String date = df.format(new Date());
	
	BasePage basePage = new BasePage(pageManager, excelReader);	
	
	public void readData(){

		String data;
		for(int i=1;i<=excelReader.getRowCount("Sheet1");i++)
		{
			 data = excelReader.getCellData("Sheet1", "URL", i).toString();
			 urlsList.add(data);
		}
		//Remove Column Name items from list
		urlsList.remove(0);
	}
	
	public void getAltTags() throws InterruptedException {
		for(int i=0; i<urlsList.size();i++) {
			if(urlsList.get(i).startsWith("http://") || urlsList.get(i).startsWith("https://")) {
				basePage.navigateToURL(urlsList.get(i));
			} else {
				String source_url = "https://" + urlsList.get(i);
				basePage.navigateToURL(source_url);
			}
			
			System.out.println(pageManager.getDriver().getCurrentUrl());
			allImg = pageManager.getDriver().findElements(By.tagName("img"));
			System.out.println("Number of image tags - " + allImg.size());

			for (int j = 0; j < allImg.size(); j++) {
				try {
					actual_imageName = allImg.get(j).getAttribute("src");
					actual_imageName = actual_imageName.substring(actual_imageName.lastIndexOf("/") + 1);
					System.out.println(actual_imageName);
					actual_altTag = allImg.get(j).getAttribute("alt");
					System.out.println(actual_altTag);
					resultURLList.add(pageManager.getDriver().getCurrentUrl());
					imagesList.add(actual_imageName);
					altTagsList.add(actual_altTag);
				} catch (Exception exception) {
					System.out.println(exception.getMessage());
				}
			}
			allImg.clear();
		}
		
	}
	
	public void createOutputExcelFile() throws IOException {
		// Create new workbook
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet alttag_sheet;
		Row alttagRow;
		// Create a new font and alter it.
		XSSFFont font = workbook.createFont();
		font.setBold(true);

		// Fonts are set into a style so create a new one to use.
		CellStyle style = workbook.createCellStyle();
		style.setFont(font);
		
		alttag_sheet = workbook.createSheet("AltTags_Output");
		alttagRow = alttag_sheet.createRow(0);
		alttagRow.createCell(0).setCellValue("URL");
		alttagRow.createCell(1).setCellValue("Image Name");
		alttagRow.createCell(2).setCellValue("Alt tag");
		
		for (int i = 0; i < alttagRow.getLastCellNum(); i++) {
			alttagRow.getCell(i).setCellStyle(style);
		}
		
		for(int i=0; i<resultURLList.size();i++) {
			int rowNum_redirect = alttag_sheet.getLastRowNum();
			alttagRow = alttag_sheet.createRow(rowNum_redirect + 1);
			alttagRow.createCell(0).setCellValue(resultURLList.get(i));
			alttagRow.createCell(1).setCellValue(imagesList.get(i));
			alttagRow.createCell(2).setCellValue(altTagsList.get(i));
		}
		

		try {
			FileOutputStream out = new FileOutputStream(
					 System.getProperty("user.dir")+ "/ExcelOutput/AltTagsVerification/AltTagsTest_" + date + ".xlsx");
			workbook.write(out);
			workbook.close();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Excel Created!");
	}

}
