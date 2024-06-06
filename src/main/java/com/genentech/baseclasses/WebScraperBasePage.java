package com.genentech.baseclasses;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.genentech.util.ExcelReader;
import com.genentech.util.PageManager;
import com.genentech.util.PageObject;

public class WebScraperBasePage extends PageObject {
	
	private static List<String> urlList = new ArrayList<>();
	private static List<String> sourceURLListLinks = new ArrayList<>();
	private static List<String> targetURLListLinks = new ArrayList<>();
	private static List<String> linkTextList = new ArrayList<>();
	private static List<String> statusCodeListLinks = new ArrayList<>();
	private static List<String> statusMessageListLinks = new ArrayList<>();
	private static List<String> sourceURLListImages = new ArrayList<>();
	private static List<String> targetURLListImages = new ArrayList<>();
	private static List<String> imageTextList = new ArrayList<>();
	private static List<String> statusCodeListImages = new ArrayList<>();
	private static List<String> statusMessageListImages = new ArrayList<>();
	SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
	String date = df.format(new Date());
	
	BasePage basePage = new BasePage(pageManager, excelReader);	
	
	@FindBy(xpath = "//a[@href]")
	public List<WebElement> linksList;

	@FindBy(xpath = "//img[@src]")
	public List<WebElement> imagesList;

	public WebScraperBasePage(PageManager pm, ExcelReader xl) {
		super(pm, xl);
	}

	public void readData(){

		String data;
		for(int i=1;i<=excelReader.getRowCount("Sheet1");i++){
			 data = excelReader.getCellData("Sheet1", 0, i);
			 urlList.add(data);
		}
	}
	
	
	public void brokenLinksVerification() throws IOException, Exception {
		
		for (int i = 0; i < urlList.size(); i++) {
				basePage.navigateToURL(urlList.get(i));
				
				linksStatusCheck(urlList.get(i));
				
				imagesStatusCheck(urlList.get(i));
		}
	}
	
	
	public void linksStatusCheck(String urlCheck) throws IOException {
		int respCode = 200;
		//System.out.println("Total No. of Links: "+linksList.size());
		for (int i = 0; i < linksList.size(); i++) {
			if (linksList.get(i).getAttribute("href") != null
					&& (!linksList.get(i).getAttribute("href").contains("javascript"))
					&& (!linksList.get(i).getAttribute("href").contains("tel")) && (!linksList.get(i).getAttribute("href").contains("#"))) {
				if(linksList.get(i).isDisplayed()) {
					continue;
				} else {
					System.out.println(i+". "+linksList.get(i).getAttribute("innerText").trim());
					sourceURLListLinks.add(urlCheck);
					targetURLListLinks.add(linksList.get(i).getAttribute("href"));
					linkTextList.add(linksList.get(i).getAttribute("innerText").trim());
					Set<Cookie> cookieSet = pageManager.getDriver().manage().getCookies();
					String cookie = cookieSet.toString().replace("[", "");
					HttpURLConnection.setFollowRedirects(false);
					HttpURLConnection connection = (HttpURLConnection) new URL(
							linksList.get(i).getAttribute("href")).openConnection();
					connection.setRequestProperty("Cookie", cookie);
					connection.connect();
					respCode = connection.getResponseCode();
					String respCode1 = Integer.toString(respCode);
					statusCodeListLinks.add(respCode1);
					String respMessage = connection.getResponseMessage();
					statusMessageListLinks.add(respMessage);
					connection.disconnect();
				}
			}
		}
	}
	
	public void imagesStatusCheck(String urlCheck) throws IOException 
	{
		//System.out.println("Total No. of Images: "+imagesList.size());
		for(int i=0;i<imagesList.size();i++) {	
			WebElement ele= imagesList.get(i);
			String url=ele.getAttribute("src");
			String imageName = url.substring(url.lastIndexOf("/")+1);
			System.out.println(imageName);
			try {
				if(url != null	&& (!url.contains("javascript"))&& (!url.contains("tel"))) {
					Set<Cookie> cookieSet = pageManager.getDriver().manage().getCookies();
					String cookie = cookieSet.toString().replace("[", "");
					HttpURLConnection huc1 = (HttpURLConnection)(new URL(url).openConnection());
					huc1.setRequestProperty("Cookie", cookie);
					huc1.setConnectTimeout(5000);           
					huc1.connect();
		        	sourceURLListImages.add(urlCheck);
		        	targetURLListImages.add(url);
		        	imageTextList.add(imageName);
					int respCode = huc1.getResponseCode();
		        	String respCode1 = Integer.toString(respCode);
		        	statusCodeListImages.add(respCode1);
		        	String text = huc1.getResponseMessage();
		        	statusMessageListImages.add(text);
		        	huc1.disconnect();
				}
			}
				catch (IOException e) {
	                e.printStackTrace();
	         }
		}
	}
	
	public void createOutputExcelFile() throws IOException {
		// Create new workbook
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet linkSheet_links, linkSheet_images;
		Row linkRow,imageRow;
		// Create a new font and alter it.
		XSSFFont font = workbook.createFont();
		font.setBold(true);

		// Fonts are set into a style so create a new one to use.
		CellStyle style = workbook.createCellStyle();
		style.setFont(font);
		
		linkSheet_links = workbook.createSheet("Links_Output");
		linkRow = linkSheet_links.createRow(0);
		linkRow.createCell(0).setCellValue("Site");
		linkRow.createCell(1).setCellValue("Url");
		linkRow.createCell(2).setCellValue("Link_Text");
		linkRow.createCell(3).setCellValue("Tag_Name");
		linkRow.createCell(4).setCellValue("Status");
		linkRow.createCell(5).setCellValue("Message");
		
		for (int i = 0; i < linkRow.getLastCellNum(); i++) {
			linkRow.getCell(i).setCellStyle(style);
		}

		linkSheet_images = workbook.createSheet("Images_Output");
		imageRow = linkSheet_images.createRow(0);
		imageRow.createCell(0).setCellValue("Site");
		imageRow.createCell(1).setCellValue("Url");
		imageRow.createCell(2).setCellValue("Link_Text");
		imageRow.createCell(3).setCellValue("Tag_Name");
		imageRow.createCell(4).setCellValue("Status");
		imageRow.createCell(5).setCellValue("Message");
		
		for (int i = 0; i < imageRow.getLastCellNum(); i++) {
			imageRow.getCell(i).setCellStyle(style);
		}
		
		/*System.out.println(sourceURLListLinks.size());
		System.out.println(targetURLListLinks.size());
		System.out.println(linkTextList.size());
		System.out.println(statusCodeListLinks.size());
		System.out.println(statusMessageListLinks.size());*/
		
		for(int i=0; i<sourceURLListLinks.size();i++) {
			int rowNumLinks = linkSheet_links.getLastRowNum();
			linkRow = linkSheet_links.createRow(rowNumLinks+ 1);
			linkRow.createCell(0).setCellValue(sourceURLListLinks.get(i));
			linkRow.createCell(1).setCellValue(targetURLListLinks.get(i));
			linkRow.createCell(2).setCellValue(linkTextList.get(i));
			linkRow.createCell(3).setCellValue("a");
			linkRow.createCell(4).setCellValue(statusCodeListLinks.get(i));
			linkRow.createCell(5).setCellValue(statusMessageListLinks.get(i));
		}
		
		for(int i=0; i<sourceURLListImages.size();i++) {
			int rowNumImages = linkSheet_images.getLastRowNum();
			imageRow = linkSheet_images.createRow(rowNumImages+ 1);
			imageRow.createCell(0).setCellValue(sourceURLListImages.get(i));
			imageRow.createCell(1).setCellValue(targetURLListImages.get(i));
			imageRow.createCell(2).setCellValue(imageTextList.get(i));
			imageRow.createCell(3).setCellValue("img");
			imageRow.createCell(4).setCellValue(statusCodeListImages.get(i));
			imageRow.createCell(5).setCellValue(statusMessageListImages.get(i));
		}
		
		try {
			FileOutputStream out = new FileOutputStream(
					 System.getProperty("user.dir")+ "/ExcelOutput/WebscraperVerification/WebscraperTest_" + date + ".xlsx");
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