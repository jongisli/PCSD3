package com.acertainbookstore.client.workloads;

import java.util.HashSet;
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

		Set<Integer> selectedISBNs = new HashSet<Integer>(num);

		int size = isbns.size();
		int i = 0;
		int isbn = 0;

		for (int m = 0; m < num; m++) {
			int item = new Random().nextInt(size);

			for (Integer obj : isbns) {
				if (i == item)
					isbn = obj;
				i = i + 1;
			}
			if (!selectedISBNs.contains(isbn)) {
				selectedISBNs.add(isbn);
			} else {
				m = m - 1;
			}
		}

		return selectedISBNs;
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
			
			ISBN = new Random().nextInt(maxISBN);
			price = new Random().nextFloat() * maxPrice;
			numCopies = new Random().nextInt(maxNumCopies);
			editorPick = new Random().nextBoolean();
			

			Random r = new Random(); // perhaps make it a class variable so you
										// don't make a new one every time
			StringBuilder titleBuilder  = new StringBuilder();
			StringBuilder authorBuilder = new StringBuilder();
			
			for (int i = 0; i < titleLength; i++) {
				char c = (char) (r.nextInt((int) (Character.MAX_VALUE)));
				titleBuilder.append(c);
			}
			title = titleBuilder.toString();
			
			for (int i = 0; i < authorLength; i++) {
				char ch = (char) (r.nextInt((int) (Character.MAX_VALUE)));
				authorBuilder.append(ch);
			}
			author = authorBuilder.toString();
			

			booksGenerated.add(new ImmutableStockBook(ISBN, title, author,
					price, numCopies, saleMisses, timesRated, totalRating,
					editorPick));

		}

		return booksGenerated;
	}

}
