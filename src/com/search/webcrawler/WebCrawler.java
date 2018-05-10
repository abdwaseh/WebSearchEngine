package com.search.webcrawler;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.StringTokenizer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class WebCrawler {
//Creating a queue which maintains the details of the links that will be visited	
	
	private LinkedList<String> linksToVisit = new LinkedList<String>();
//creating a list to maintain the links that were already visited	
	private HashSet<String> visitedLinks = new HashSet<String>();

	//creating a list to maintain the links that were already visited	
	private HashSet<String>  linksToVisitCheckSet = new HashSet<String>();
	
	
	// List of visited and Built Nodes
	private Collection<WebCrawlerNode> webCrawledNodes = new LinkedList<WebCrawlerNode>();
	
	// Constants for validating the HREF URL Retrieved by the JSOUP.
	//	A crawler may only want to seek out HTML pages and avoid all other MIME types. In order to request only HTML resources, 
	//	a crawler may make an HTTP HEAD request to determine a Web resource's MIME type before requesting the entire resource with a GET request. 
	//	To avoid making numerous HEAD requests, a crawler may examine the URL and only request a resource if the URL ends with certain characters 
	//	such as .html, .htm, .asp, .aspx, .php, .jsp, .jspx or a slash. This strategy may cause numerous HTML Web resources to be unintentionally skipped.
	//	Some crawlers may also avoid requesting any resources that have a "?" in them (are dynamically produced) in order to avoid spider traps that may 
	//	cause the crawler to download an infinite number of URLs from a Web site. This strategy is unreliable if the site uses URL rewriting to simplify its URLs.
	private final String STANDARD_URL_REGEX = "\\b(https?)://[-a-zA-Z0-9|.]+[-a-zA-Z0-9#/_|.:]*(asp|aspx|asx|asmx|cfm|html|htm|xhtml|jhtml|jsp|jspx|wss|do|php|php4|php3|phtml|py|shtml){1}$";
	private final String STANDARD_URLFOLDER_REGEX = "\\b(https?)://[-a-zA-Z0-9|.]+[-a-zA-Z0-9#/_|.:]*(/){1}$";
	private final String STANDARD_URLOTHER_REGEX = "\\b(https?)://[-a-zA-Z0-9|.]+[-a-zA-Z0-9/_:]*";

	// Standard number of URL to be visited which we gave 500
	private int MAX_URL_VISITS = 500;
	
	// Quantity of Link Queued to be visited while Crawling
	private int QtyQueuedLinks = 0;
	
	
	// To setup if debug messages should be shown
	private boolean isPrintDebug = true;
	
	
	 	//Constructor of the WebCrawler. It will visit a Maximum of maxUrlVisits provided in the Constructor
	
	public WebCrawler(int maxURLVisits) {
		this.MAX_URL_VISITS = maxURLVisits;
	}

	
	//	Returns a Collections of WebCrawlerNodes 
	 
	public Collection<WebCrawlerNode> getWebCrawledNodes() {
		return webCrawledNodes;
	}

    
	public void buildWebCrawl(String url)  {
    	WebCrawlerNode crawlerNode = new WebCrawlerNode(url);
    	try {
	   		// Connects to the Provided URL to Visit
	    	Connection connectionToURL = Jsoup.connect(url);

	    	// Obtains the Document that in this case represents the HTML File
		    Document jSoupDoc = connectionToURL.get();
		    // Obtains all Tags of HREF type
		    Elements hreflinks = jSoupDoc.select("a[href]");
		    

	    	crawlerNode = new WebCrawlerNode(url);
	    	String fullHtmlTextFormat = jSoupDoc.text();
	    	StringTokenizer tokenizer = new StringTokenizer(fullHtmlTextFormat);
	    	
	    	// Tokenizing the TEXT from parsed from the HTML file 
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				// removing all Unwanted characters from String
				String cleanToken = token.replaceAll("[+.^:,?;!�()\n\r\"]","");
				crawlerNode.addTextContentToken(cleanToken);
			}

			// Saving all found links from HREF Tags in the HTML file
		    for (int i = 0; i < hreflinks.size(); i++) {
	    		Element link = hreflinks.get(i);
	    		crawlerNode.addNodeUrlLink(link.attr("abs:href"));
		    	if (QtyQueuedLinks <= this.MAX_URL_VISITS) {
		    		String hrefURL = link.attr("abs:href");
		    		if (validadeURLWebCrawler(hrefURL)) {
		    			if (!linksToVisitCheckSet.contains(hrefURL)) {
					    	linksToVisit.add(hrefURL);
					    	linksToVisitCheckSet.add(hrefURL);
					    	QtyQueuedLinks++;
					    	System.out.println("---- ADDED link to visit: " + hrefURL + "  Link index: " + QtyQueuedLinks);
		    			}else {
		    				System.out.println("---- Already QUEUED: " + hrefURL);
		    			}
		    		}else {
		    			System.out.println("---- Not valid: " + hrefURL);
		    		}
		    	}
		    }
    	} catch (IOException ex) {
    		// A connection happened to the URL so it is considered to be a BROKEN URL
    		// In this case this NODE is marked as BAD it may not be considered while building the anything on this node.
    		crawlerNode.setBadURL(true);
    	}
    	
    	// Adding this NODE to the list of Crawled nodes
    	webCrawledNodes.add(crawlerNode);
    	
	    // Recursively visits the first Sub-URL that was Queued
	    if (!linksToVisit.isEmpty()) {
	    	String linkToVisit = null;
	    	do {
	    		linkToVisit = linksToVisit.removeFirst();
	    		if (visitedLinks.contains(linkToVisit)) {
	    		}
	    	} while (!linksToVisit.isEmpty() && visitedLinks.contains(linkToVisit));
	    		    	
			visitedLinks.add(linkToVisit);
			buildWebCrawl(linkToVisit);
	    }
    }
    
//Using REGEX to test the url retrieved
    
    public boolean validadeURLWebCrawler(String URLToValidate) {
    	boolean URLMatches = false;
    	if (URLToValidate != null) {
    		URLMatches = URLMatches || URLToValidate.matches(STANDARD_URL_REGEX);
    		URLMatches = URLMatches || URLToValidate.matches(STANDARD_URLFOLDER_REGEX);
    		URLMatches = URLMatches || URLToValidate.matches(STANDARD_URLOTHER_REGEX);
    	}
		return URLMatches;
    }   

    public static void main (String[] args) {

    }
    
}