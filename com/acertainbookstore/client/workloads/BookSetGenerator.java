package com.acertainbookstore.client.workloads;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.acertainbookstore.business.CertainBookStore;
import com.acertainbookstore.business.ImmutableStockBook;
import com.acertainbookstore.business.StockBook;
import com.acertainbookstore.client.BookStoreHTTPProxy;
import com.acertainbookstore.client.StockManagerHTTPProxy;

/**
 * Helper class to generate stockbooks and isbns modelled similar to Random
 * class
 */
public class BookSetGenerator {

	public BookSetGenerator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns num randomly selected isbns from the input set
	 * 
	 * @param num
	 * @return
	 */
	public Set<Integer> sampleFromSetOfISBNs(Set<Integer> isbns, int num) {

		if (isbns.size() < num)
			return null;
		
		Object[] ISBNsList = isbns.toArray();
		Set<Integer> sampleISBNs = new HashSet<Integer>();
		Random rnd = new Random();
		
		while (sampleISBNs.size() <= num)
		{
			int randomIdx = rnd.nextInt(ISBNsList.length);
			int sampleISBN = (Integer) ISBNsList[randomIdx];
			if (!sampleISBNs.contains(sampleISBN))
				sampleISBNs.add(sampleISBN);
		}

		return sampleISBNs;
	}

	/**
	 * Return num stock books. For now return an ImmutableStockBook
	 * 
	 * @param num
	 * @return
	 */
	public Set<StockBook> nextSetOfStockBooks(int num) {

		Set<StockBook> booksGenerated = new HashSet<StockBook>(num);

		int maxISBN = 100000000;
		int maxNumCopies = 10000;
		float maxPrice = 900;
		int ISBN = 0;
		String title = null;
		String author = null;
		float price = 0;
		int numCopies = 0;
		int saleMisses = 0;
		int timesRated = 0;
		int totalRating = 0;
		boolean editorPick = false;
		int titleLength = 40;
		int authorLength = 20;

		for (int n = 0; n < num; n++) {
			
			ISBN = new Random().nextInt(maxISBN) + 1;
			price = new Random().nextFloat() * maxPrice + 1;
			numCopies = new Random().nextInt(maxNumCopies) + 1;
			editorPick = new Random().nextBoolean();
			

			StringBuilder titleBuilder  = new StringBuilder();
			StringBuilder authorBuilder = new StringBuilder();
			
			
			char[] titleGenerated = "abcdefghijklmnopqrstuvwxyz".toCharArray();
			char[] authorGenerated = "abcdefghijklmnopqrstuvwxyz".toCharArray();
			Random random = new Random();
			
			for (int i = 0; i < titleLength; i++) {
			    char c = titleGenerated[random.nextInt(titleGenerated.length)];
			    titleBuilder.append(c);
			}
			
			for (int i = 0; i < authorLength; i++) {
			    char ch = authorGenerated[random.nextInt(authorGenerated.length)];
			    authorBuilder.append(ch);
			}
			
			title  = titleBuilder.toString();
			author = authorBuilder.toString();
			
			booksGenerated.add(new ImmutableStockBook(ISBN, title, author,
					price, numCopies, saleMisses, timesRated, totalRating,
					editorPick));

		}

		return booksGenerated;
	}

}
