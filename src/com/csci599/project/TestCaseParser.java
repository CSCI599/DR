package com.csci599.project;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestCaseParser {

	public void parseTestCase(String path, String key, String value) {
		try {
			System.out.println("Generating new test case");

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// get current date time with Date()
			Date date = new Date();
			String timestamp = dateFormat.format(date);
			File input = new File(path);
			File output = new File(path + "_"+timestamp);
			
			Files.copy(input.toPath(), output.toPath());

			Document doc = Jsoup.parse(output, "UTF-8",
					"http://localhost:8080/Bookstore/");
			String title = doc.title();
			System.out.println("title : " + title);

			// get all links
			Elements links = doc.select("td");
			for (int i = 0; i < links.size(); i++) {
				Element link = links.get(i);
				if (link.text().equalsIgnoreCase(key)) {
					links.get(i + 1).text(value);
					// System.out.println("Changed Text");
				}
				// System.out.println("Text : " + link.text());

			}

			PrintWriter writer = new PrintWriter(output, "UTF-8");

			writer.write(doc.html());
			writer.flush();
			writer.close();
			//for (int i = 0; i < links.size(); i++) {
			//	Element link = links.get(i);

			//	System.out.println("Text : " + link.text());

			//}
			System.out.println("Generated new test case "+path + "_"+timestamp);

		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}
}
