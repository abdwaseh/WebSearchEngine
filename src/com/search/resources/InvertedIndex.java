package com.search.resources;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;

//Here in this class we have defined procedures to store the data in the dictionary,
//to search the word, auto completion of the word(drop down), ranking of the urls
//and also to find the best fit in the trie and inverted index

import java.util.LinkedList;
import java.util.List;
import com.search.webcrawler.WebCrawlerNode;
//Class to implement Trie
class Tries implements Serializable  {
	private static final long serialVersionUID = 1L;
	char data;
	int count;
	boolean isEnd;
	int wordNumber;
	LinkedList<Tries> childNode;
	// Constructor
	public Tries(char n) {
		data = n;
		count = 0;
		isEnd = false;
		wordNumber = -1;
		childNode = new LinkedList<Tries>();
	}
//trying to get the char
	public Tries getChild(char c) {
		if (childNode != null) {
			for (Tries child : childNode) {
				if (child.data == c) {
					return child;
				}
			}
		}
		return null;
	}
}
//here we are implementing inverted index using trie
//creating dictionary
//searching dictionary
//prediction of words
//ranking urls
public class InvertedIndex implements Serializable {
	private static final long serialVersionUID = 1L;
	public static int currWordNumber;
	public static Tries root;
	public static HashMap<Integer, HashMap<String, Integer>> invertedIdxArray;
	public InvertedIndex() {
		root = new Tries(' ');
		invertedIdxArray = new HashMap<Integer, HashMap<java.lang.String, Integer>>();
		currWordNumber = 1;
	}

	private void updateWordOccurrence(int num, String url) {

		// if the word is already present
		if (invertedIdxArray.get(num) != null) {

			// check if the url was found earlier also
			if (invertedIdxArray.get(num).get(url) != null) {

				// update the occurrence of the word by 1
				invertedIdxArray.get(num).put(url, invertedIdxArray.get(num).get(url) + 1);
			} else {

				// word is found for the first time in this found url
				invertedIdxArray.get(num).put(url, 1);
			}
		} else {

			// if word is found for the first time
			HashMap<String, Integer> urlMap = new HashMap<String, Integer>();
			urlMap.put(url, 1);
			invertedIdxArray.put(num, urlMap);
		}
	}

	//trying to insert a word in the trie
	
	private void insertWord(String word, String url) {

		// if word found, update its occurrence counter
		int wordNum = search(word);
		
		if (wordNum != -1) {
			
			updateWordOccurrence(wordNum, url);
			return;
		}

		// If not found -- add new one
		Tries curr = root;
		for (char c : word.toCharArray()) {
			Tries child = curr.getChild(c);
			if (child != null) {
				curr = child;
			} else {
				curr.childNode.add(new Tries(c));
				curr = curr.getChild(c);
			}
			curr.count++;
		}

		// Update the invertedIndex list
		curr.isEnd = true;
		curr.wordNumber = currWordNumber;
		updateWordOccurrence(curr.wordNumber, url);
		currWordNumber++;
	}
	public int search(String word) {
		Tries curr = root;
		for (char c : word.toCharArray()) {
			if (curr.getChild(c) == null) {
				return -1;
			} else {
				curr = curr.getChild(c);
			}
		}
		if (curr.isEnd) {
			return curr.wordNumber;
		}

		return -1;
	}
//removing word from the trie operation
	public void remove(String word, String url) {
		// checking if the word is there
		int wordNum = search(word);
		if (wordNum == -1) {
			System.out.println("word not found");
			return;
		}

		// handling the invertedIndex
		invertedIdxArray.get(wordNum).remove(url);

		// handing the Trie
		Tries curre = root;
		for (char c : word.toCharArray()) {
			Tries child = curre.getChild(c);
			if (child.count == 1) {
				curre.childNode.remove();
				return;
			} else {
				child.count--;
				curre = child;
			}
		}
		curre.isEnd = false;
	}
	
	// Find the distance between two words
    //Edit Distance
	
	public int findEditDistance(String s1, String s2) {
		int distance[][] = new int[s1.length() + 1][s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			distance[i][0] = i;
		}
		for (int i = 0; i <= s2.length(); i++) {
			distance[0][i] = i;
		}
		for (int i = 1; i < s1.length(); i++) {
			for (int j = 1; j < s2.length(); j++) {
				if (s1.charAt(i) == s2.charAt(j)) {
					distance[i][j] = Math.min(Math.min((distance[i - 1][j]) + 1, (distance[i][j - 1]) + 1),
							(distance[i - 1][j - 1]));
				} else {
					distance[i][j] = Math.min(Math.min((distance[i - 1][j]) + 1, (distance[i][j - 1]) + 1),
							(distance[i - 1][j - 1]) + 1);
				}
			}
		}
		return distance[s1.length() - 1][s2.length() - 1];
	}

	// function to be exposed to load the data
	
	public void updatedloadData(List<WebCrawlerNode> e) {

		e.forEach(item -> item.getTextContentsTokens().
				forEach(item2 -> insertWord(item2, item.getNodeBaseUrl())));

	}

	// This function here, returns the top urls that were found
	//according to the query searched by the user

	public String[] getTopUrls(String word) {
		int docNumb = search(word);
			if (docNumb != -1) {

			int topk = 5;
			int i = 0;

			// Get all the url for the matching word
			HashMap<String, Integer> foundUrl = invertedIdxArray.get(docNumb);

			// prepare the array for the QuickSelect with word frequency
			final int[] frequency = new int[foundUrl.size()];
			for (final int value : foundUrl.values()) {
				frequency[i++] = value;
			}

			// Calling QuickSelect to get the 10th largest occurrence
			QuickSelectAlgo obj = new QuickSelectAlgo();
			final int kthLargestFreq = obj.findKthLargest(frequency, topk);

			// Populating the local array with the URL's having frequency
			// greater than the k-1th largest element
			final String[] topKElements = new String[topk];
			i = 0;
			for (final java.util.Map.Entry<String, Integer> entry : foundUrl.entrySet()) {
				if (entry.getValue().intValue() >= kthLargestFreq) {
					topKElements[i++] = entry.getKey();
					if (i == topk) {
						break;
					}
				}
			}
			return topKElements;
		} else {
			System.out.println("No word found");
			return null;
		}
	}


	// predicting the word
	
	public String[] guessWord(String prefix) {
		Tries curr = root;
		int wordLength = 0;
		String predictedWords[] = null;
		
		// get the counter of number of word occurences available
		
		for (int i = 0; i < prefix.length(); i++) {
			if (curr.getChild(prefix.charAt(i)) == null) {
				return null;
			} else if (i == (prefix.length() - 1)) {
				curr = curr.getChild(prefix.charAt(i));
			
				wordLength = curr.count;
			} else {
				curr = curr.getChild(prefix.charAt(i));
			}
		}
		
		predictedWords = new String[wordLength];
		for (int i = 0; i < predictedWords.length; i++) {
			predictedWords[i] = prefix;
		}

		// Temp array list to find all childs
		java.util.ArrayList<Tries> currentChildBuffer = new java.util.ArrayList<Tries>();
		java.util.ArrayList<Tries> nextChildBuffer = new java.util.ArrayList<Tries>();
		HashMap<Integer, String> wordCompleted = new HashMap<Integer, String>();

		// get the prefix child
		int counter = 0;
		if (curr.childNode != null) {
			for (Tries e : curr.childNode) {
				currentChildBuffer.add(e);
			}
		}

		// iterating all the children
		while (currentChildBuffer.size() != 0) {
			for (Tries e : currentChildBuffer) {

				// populate the string word
				while (wordCompleted.get(counter) != null) {
					counter++;
				}
				for (int j = 0; j < e.count; j++) {
					
					 //fixing to get the corrcet word
					if (e.isEnd && j == (e.count-1)) {
						wordCompleted.put(counter, "done");
					}
					predictedWords[counter] = predictedWords[counter] + e.data;
					counter++;
				}

				// iterating the child of each char
				for (Tries e1 : e.childNode) {
					nextChildBuffer.add(e1);
				}
			}

			counter = 0;
		
			currentChildBuffer = new java.util.ArrayList<Tries>();
			if (nextChildBuffer.size() > 0) {
				currentChildBuffer = nextChildBuffer;
				nextChildBuffer = new java.util.ArrayList<Tries>();
			}
		}
		
		return predictedWords;
	}

	public String[] findCorrection(String word) {
		String suggestion[] = guessWord(word.substring(0, 1));
		ArrayList<String> correction = new ArrayList<String>();
		for (String s : suggestion) {
			if (findEditDistance(word, s) == 1) {
				correction.add(s);
			}
		}

		String suggestedWord[] = (String[]) correction.toArray(new String[0]);
		System.out.println("Here's a correction");
		for (String s : suggestedWord) {
			System.out.println(s);
		}

		return suggestedWord;

	}
	
	// Main function to run the search engine
	public static void main(String[] arr) {
		InvertedIndex t = new InvertedIndex();
	}
}
