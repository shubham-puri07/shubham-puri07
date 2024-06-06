package com.genentech.baseclasses;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Cookie;

import com.genentech.util.ExcelReader;
import com.genentech.util.PageManager;
import com.genentech.util.PageObject;

public class PdfReaderBasePage extends PageObject{

	public PdfReaderBasePage(PageManager pm, ExcelReader xl) {
		super(pm, xl);
		// TODO Auto-generated constructor stub
	}
	
	private static List<String> fullPagePathURLList = new ArrayList<>();
	private static List<String> expectedTitleTagList = new ArrayList<>();
	private static List<String> expectedSubjectList = new ArrayList<>();
	private static List<String> expectedKeywordsList = new ArrayList<>();
	private static List<String> actualTitleTagList = new ArrayList<>();
	private static List<String> actualSubjectList = new ArrayList<>();
	private static List<String> actualKeywordsList = new ArrayList<>();
	private static List<String> titleTagCompareResultList = new ArrayList<>();
	private static List<String> subjectCompareResultList = new ArrayList<>();
	private static List<String> keywordsCompareResultList = new ArrayList<>();
	SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
	String date = df.format(new Date());
	
	public void readData(){

		String data;
		for(int i=1;i<=excelReader.getRowCount("Sheet1");i++)
		{
			 data = excelReader.getCellData("Sheet1", "Full Page Path URL", i).toString();
			 fullPagePathURLList.add(data);
			 data = excelReader.getCellData("Sheet1", "Title Tag", i).toString();
			 expectedTitleTagList.add(data);
			 data = excelReader.getCellData("Sheet1", "Subject (Meta Description)", i).toString();
			 expectedSubjectList.add(data);
			 data = excelReader.getCellData("Sheet1", "Keywords", i).toString();
			 expectedKeywordsList.add(data);
		}
		//Remove Column Name items from list
		fullPagePathURLList.remove(0);
		expectedTitleTagList.remove(0);
		expectedSubjectList.remove(0);
		expectedKeywordsList.remove(0);
	}
	
	public void verifyPDFReader() throws MalformedURLException, IOException {
		for (int i=0; i<fullPagePathURLList.size();i++) {
			pageManager.navigate(fullPagePathURLList.get(i));
			if(!(fullPagePathURLList.get(i).contains("uat") ||fullPagePathURLList.get(i).contains("preview")) && i==0) {
				pageManager.waitForSeconds(10000);
			}
			pageManager.waitForSeconds(5000);
			pageManager.waitForPageLoad();
			pageManager.waitForPageLoaded();
			
			if (pageManager.getDriver().getCurrentUrl().endsWith(".pdf")) {
				String url = pageManager.getDriver().getCurrentUrl();

				HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
				// to get cookies
				Set<Cookie> cookieSet = pageManager.getDriver().manage().getCookies();
				String cookie = cookieSet.toString().replace("[", "");
				connection.setRequestProperty("Cookie", cookie);
				BufferedInputStream fileParse = new BufferedInputStream(connection.getInputStream());
				PDDocument document = Loader.loadPDF(fileParse);
				PDDocumentInformation info = document.getDocumentInformation();
				
				if(info.getTitle()!=null) {
					actualTitleTagList.add(info.getTitle());
				} else {
					actualTitleTagList.add("");
				}
				if(info.getSubject()!=null) {
					actualSubjectList.add(info.getSubject());
				} else {
					actualSubjectList.add("");
				}
				if(info.getKeywords()!=null) {
					actualKeywordsList.add(info.getKeywords());
				} else {
					actualKeywordsList.add("");
				}
				
				if(expectedTitleTagList.get(i).trim().equals(actualTitleTagList.get(i).trim())) {
					titleTagCompareResultList.add("Pass");
				} else {
					titleTagCompareResultList.add("Fail");
				}
				
				if(expectedSubjectList.get(i).trim().equals(actualSubjectList.get(i).trim())) {
					subjectCompareResultList.add("Pass");
				} else {
					subjectCompareResultList.add("Fail");
				}
				
				if(expectedKeywordsList.get(i).trim().equals(actualKeywordsList.get(i).trim())) {
					keywordsCompareResultList.add("Pass");
				} else {
					keywordsCompareResultList.add("Fail");
				}
				
				
			}
		}
	}
	
	public void createOutputExcelFile() throws IOException {
		XSSFWorkbook outputWorkbook = new XSSFWorkbook();
		XSSFSheet outputSheet;
		Row outputRow;
		
		// Create a new font and alter it.
		XSSFFont font = outputWorkbook.createFont();
		font.setBold(true);

		// Fonts are set into a style so create a new one to use.
		CellStyle style = outputWorkbook.createCellStyle();
		style.setFont(font);
				

		outputSheet = outputWorkbook.createSheet("PDF_Reader_Output");
		outputRow = outputSheet.createRow(0);
		outputRow.createCell(0).setCellValue("Full Page Path URL");
		outputRow.createCell(1).setCellValue("Expected Title Tag");
		outputRow.createCell(2).setCellValue("Actual Title Tag");
		outputRow.createCell(3).setCellValue("Status Title Tag");
		outputRow.createCell(4).setCellValue("Expected Subject (Meta Description)");
		outputRow.createCell(5).setCellValue("Actual Subject (Meta Description)");
		outputRow.createCell(6).setCellValue("Status Subject (Meta Description)");
		outputRow.createCell(7).setCellValue("Expected Keywords");
		outputRow.createCell(8).setCellValue("Actual Keywords");
		outputRow.createCell(9).setCellValue("Status Keywords");
		
		for (int i = 0; i < outputRow.getLastCellNum(); i++) {
			outputRow.getCell(i).setCellStyle(style);
		}
		
		for(int i=0; i<fullPagePathURLList.size();i++) {
			int rowNum = outputSheet.getLastRowNum();
			outputRow = outputSheet.createRow(rowNum+ 1);
			outputRow.createCell(0).setCellValue(fullPagePathURLList.get(i));
			outputRow.createCell(1).setCellValue(expectedTitleTagList.get(i));
			outputRow.createCell(2).setCellValue(actualTitleTagList.get(i));
			outputRow.createCell(3).setCellValue(titleTagCompareResultList.get(i));
			outputRow.createCell(4).setCellValue(expectedSubjectList.get(i));
			outputRow.createCell(5).setCellValue(actualSubjectList.get(i));
			outputRow.createCell(6).setCellValue(subjectCompareResultList.get(i));
			outputRow.createCell(7).setCellValue(expectedKeywordsList.get(i));
			outputRow.createCell(8).setCellValue(actualKeywordsList.get(i));
			outputRow.createCell(9).setCellValue(keywordsCompareResultList.get(i));
		}
		FileOutputStream out = new FileOutputStream(System.getProperty("user.dir") + "/ExcelOutput/PDFReaderVerification/PdfReaderTest_" + date + ".xlsx");
		outputWorkbook.write(out);
		outputWorkbook.close();
		out.close();
		System.out.println("Excel Created!");
	}

}
