package com.search.webcrawler;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

public class WebCrawlerNode implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	// Base of the visited node
	private String baseNodeUrl;
	
	// Tokens of the parsed HTML file into Text format
	private List<String> textContentsTokens;
	
	// All links found in the base URL of this node from the HTML received
	private List<String> nodeUrlLinks;
	
	// It informs if the Node is valid or node. It is FALSE, it mean that failed to connect and retrieve the HTML data
	private boolean isBadURL = false;

	/**
	 *      Builds a Node Based on the URL that is being visited to extract the TEXT
	 * 
	 * @param newBaseUrl
	 */
	public WebCrawlerNode(String newBaseUrl) {
		this.baseNodeUrl = newBaseUrl;
		this.textContentsTokens = new ArrayList<String>();
		this.nodeUrlLinks = new ArrayList<String>();		
	}
	
	
	/**
	 * 
	 * 	Returns the Tokens from the parsed TEXT from the HTML
	 * 
	 * @return
	 */
	public List<String> getTextContentsTokens() {
		return this.textContentsTokens;
	}
	
	/**
	 * 
	 * 	Returns ALL the LINKs found in the Base URL of the Node. They will be added to the WebCrawler search depending on the Queue size limit.
	 * 
	 * @return
	 */
	public List<String> getNodeUrlLinks() {
		return this.nodeUrlLinks;
	}
	
	public void addTextContentToken(String stringToken) {
		this.textContentsTokens.add(stringToken);
	}
	
	public void addNodeUrlLink(String stringUrlLink) {
		this.nodeUrlLinks.add(stringUrlLink);
	}	
	

	public String getNodeBaseUrl() {
		return this.baseNodeUrl;
	}
	
	public void setBadURL(boolean isBad) {
		this.isBadURL = isBad;
	}
	
	public boolean isBadURL() {
		return this.isBadURL;
	}

}

