package com.search.servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import com.search.resources.InvertedIndex;
import com.search.util.SearchEngineException;
import com.search.webcrawler.WebCrawlerManager;
import com.search.webcrawler.WebCrawlerNode;

//Servlet implementation 

@WebServlet("/WebSrcController")
public class WebController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static final String CRAWLER_NODES_FILE = "search";
	
	//Using inverted index for search
	
	private InvertedIndex invertedIndexEngine;
	public WebController() {
		super();
	}

	//Randomly building some data that will be used throughout the lifetime of the servlet
	
    @SuppressWarnings("unchecked")
	public void init() throws ServletException {
    List<WebCrawlerNode> nodesSaved =  new ArrayList<>();
		try {
			System.out.println("--- DEBUG - Servlet Initialization ---");
			nodesSaved = (List<WebCrawlerNode>)WebCrawlerManager.loadSerializedObject(CRAWLER_NODES_FILE, "LinkedList");
			if(nodesSaved.size() < 1)
				throw new  SearchEngineException("WebCrawler was not instantiated successfully!");
			System.out.println("-- DEBUG - WebCrawler Serialized file was loaded Successfully!");			
			invertedIndexEngine = new InvertedIndex();
			System.out.println("--- DEBUG - INVERTED INDEX Search Structure Instantiated");
			invertedIndexEngine.updatedloadData(nodesSaved);
		} catch (ClassNotFoundException | IOException  |SearchEngineException e) {
			e.printStackTrace();
		}
    }	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		
		switch(request.getParameter("act")) {
		
		case "prefix":
			if (request.getParameter("prefix") != null) {
				String prefixValue = request.getParameter("prefix");
				System.out.println("--- DEBUG Prefix = " + prefixValue.length());
				if (prefixValue.length() != 0) {
					JSONArray obj = new JSONArray();
					if (invertedIndexEngine.guessWord(prefixValue) != null) {
						for (String s : invertedIndexEngine.guessWord(prefixValue)) {
							obj.add(s);
						}
						System.out.println("Returning " + invertedIndexEngine.guessWord(prefixValue));
					}
					response.getWriter().print(obj);
				}
			}
			break;
		case "getTopUrl":			
			String prefixValue = request.getParameter("prefix");
			if (prefixValue != null) {
				System.out.println("--- DEBUG prefix = " + prefixValue.length());
				if (prefixValue.length() != 0) {
					JSONArray obj = new JSONArray();
					if (invertedIndexEngine.getTopUrls(prefixValue) != null) {
						for (String s : invertedIndexEngine.getTopUrls(prefixValue)) {
							if (s != null) {
								obj.add(s);
							}
						}
					}
					response.getWriter().print(obj);
				}
			}
			break;
		
		case "getCorWord" :
			 prefixValue = request.getParameter("prefix");
			System.out.println("--- DEBUG prefix = " + prefixValue.length());
			if (prefixValue != null) {
				if (prefixValue.length() != 0) {
					ArrayList<String> e = new ArrayList<String>();
					JSONArray obj = new JSONArray();
					if (invertedIndexEngine.findCorrection(prefixValue) != null) {
						for (String s : invertedIndexEngine.findCorrection(prefixValue)) {
							if (s != null) {
								obj.add(s);
							}
						}
					}
					response.getWriter().print(obj);
				}
			}
			break;

			default:
				response.getWriter().print("");
			
		}
	}	
}
