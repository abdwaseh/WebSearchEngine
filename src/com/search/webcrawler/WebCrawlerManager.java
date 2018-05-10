package com.search.webcrawler;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class WebCrawlerManager {
	
	private static final String FILE_PREFIX = "WebCrawlerNodes";
	private static final String FILE_TYPE = ".ser";

	  
	public static boolean saveSerializableObject(String suffixName, Object objectToSave) throws IOException {
		boolean saved = false; 
		FileOutputStream fileOut = new FileOutputStream("D:\\" + objectToSave.getClass().getSimpleName() + "-" + suffixName + FILE_TYPE);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(objectToSave);
		out.close();
		fileOut.close();
		saved = true;
		System.out.println("Serialization Done at: " + fileOut.toString());
		return saved;
	}
	
	public static Object loadSerializedObject (String suffixName, String className) throws IOException, ClassNotFoundException {
		Object loadedObject = null;
		System.out.println("Serialized Class File to open is: " + className + "-" + suffixName + FILE_TYPE);
		//FileInputStream fileIn = new FileInputStream(className + "-" + suffixName + FILE_TYPE);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream(className + "-" + suffixName + FILE_TYPE);
		
		ObjectInputStream in = new ObjectInputStream(input);
		loadedObject = in.readObject();
		System.out.println("Object loaded !! => " + loadedObject);
		in.close();
		//fileIn.close();    	  
	    return loadedObject;
	}   

//throws invalid url exception
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		/**
		 *  Create an object WebCrawler and build  a list of LInks size N times based on the Constructor object
		 *  These LInks will be visited, their HTML will be parsed to TEXT and saved as tokens.
		 *  And then will QUEUE all links of this page. The limit of pages QUEUED is N
		 *  No link is visited Twice, it is guaranteed by a HashSet of links
		 */
		
		WebCrawler webCrawler = new WebCrawler(1000);
//using w3schools web pages for the web crawler
		webCrawler.buildWebCrawl("https://www.w3schools.com/");

		System.out.println("List size : " + webCrawler.getWebCrawledNodes().size());

		WebCrawlerManager.saveSerializableObject("search", webCrawler.getWebCrawledNodes());	

   }
   
}

