package com.parser.xml.controller;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.parser.xml.exception.ResourceNotFoundException;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParseController {

	@Autowired
	private ResourceLoader resourceLoader;

	private static final Logger logger = LoggerFactory.getLogger(ParseController.class);

	@GetMapping("/Parsing/{id}")
	public String getTargetValue(@PathVariable("id") String id) throws ResourceNotFoundException {
		String targetVal = "";
		// Create a document builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Create a String Builder for CSV file
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("ID").append(",").append("Target").append("\n");
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Given Xml
			final Resource fileResource = resourceLoader.getResource("classpath:sma_gentext.xml");
			File xmlFile = fileResource.getFile();
			Document doc = builder.parse(xmlFile);

			// Normalize the xml
			doc.getDocumentElement().normalize();
			// Find the trans-unit element with the given id

			NodeList temp = doc.getElementsByTagName("trans-unit");

			// Iterate over each element

			for (int i = 0; i < temp.getLength(); i++) {
				Node n = temp.item(i);

				// Check if the node is an element node
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) n;

					// Check the id attribute
					if (element.getAttribute("id").equals(id)) {

						targetVal = element.getElementsByTagName("target").item(0).getTextContent();
						logger.info("Target value is " + targetVal);
						// Get the value of the id
						String identity = element.getAttribute("id");

						logger.info("Id value is " + identity);
						if (identity.equals("42007")) {
							stringBuilder.append(identity).append(",").append(targetVal).append("\n");

							// Write the value into a CSV file
							FileWriter fileWriter = new FileWriter("target.csv");
							fileWriter.write(stringBuilder.toString());

							// Close the file
							fileWriter.close();

							break;
						}

					}

				}
			}
			if (targetVal.equals("")) {
				throw new ResourceNotFoundException("Please provide Corrrect Id!");
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return targetVal;
	}
}
