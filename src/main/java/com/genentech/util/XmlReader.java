package com.genentech.util;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;

public class XmlReader {

	private Logger log = LogManager.getLogger(this.getClass());

	public ArrayList<String> readeXML(String path)
	{
	  ArrayList<String> urlList = new ArrayList<>();
	  try {

		File fXmlFile = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
	
		doc.getDocumentElement().normalize();
	
		log.info("Root element :" + doc.getDocumentElement().getNodeName());
	
		NodeList nList = doc.getElementsByTagName("url");
		
		log.info("----------------------------");
		log.info(nList.getLength());
		for (int temp = 0; temp < nList.getLength(); temp++) {

		Node nNode = nList.item(temp);

		System.out.println("\nCurrent Element :" + nNode.getNodeName());

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;
            urlList.add(temp, eElement.getElementsByTagName("loc").item(0).getTextContent());
			log.info("URL : " + eElement.getElementsByTagName("loc").item(0).getTextContent());

		}
	}
    } catch (Exception e) {
    	e.printStackTrace();
    }
	return urlList;
  }

}

