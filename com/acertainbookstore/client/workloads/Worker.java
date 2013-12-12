/**
 * 
 */
package com.acertainbookstore.client.workloads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import com.acertainbookstore.business.Book;
import com.acertainbookstore.business.BookCopy;
import com.acertainbookstore.business.CertainBookStore;
import com.acertainbookstore.business.StockBook;
import com.acertainbookstore.interfaces.BookStore;
import com.acertainbookstore.interfaces.StockManager;
import com.acertainbookstore.utils.BookStoreException;

/**
 * 
 * Worker represents the workload runner which runs the workloads with
 * parameters using WorkloadConfiguration and then reports the results
 * 
 */
public class Worker implements Callable<WorkerRunResult> {
	private WorkloadConfiguration configuration = null;
	private int numSuccessfulFrequentBookStoreInteraction = 0;
	private int numTotalFrequentBookStoreInteraction = 0;

	public Worker(WorkloadConfiguration config) {
		configuration = config;
	}

	/**
	 * Run the appropriate interaction while trying to maintain the configured
	 * distributions
	 * 
	 * Updates the counts of total runs and successful runs for customer
	 * interaction
	 * 
	 * @param chooseInteraction
	 * @return
	 */
	private boolean runInteraction(float chooseInteraction) {
		try {
			if (chooseInteraction < configuration
					.getPercentRareStockManagerInteraction()) {
				runRareStockManagerInteraction();
			} else if (chooseInteraction < configuration
					.getPercentFrequentStockManagerInteraction()) {
				runFrequentStockManagerInteraction();
			} else {
				numTotalFrequentBookStoreInteraction++;
				runFrequentBookStoreInteraction();
				numSuccessfulFrequentBookStoreInteraction++;
			}
		} catch (BookStoreException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Run the workloads trying to respect the distributions of the interactions
	 * and return result in the end
	 */
	public WorkerRunResult call() throws Exception {
		int count = 1;
		long startTimeInNanoSecs = 0;
		long endTimeInNanoSecs = 0;
		int successfulInteractions = 0;
		long timeForRunsInNanoSecs = 0;

		Random rand = new Random();
		float chooseInteraction;

		// Perform the warmup runs
		while (count++ <= configuration.getWarmUpRuns()) {
			chooseInteraction = rand.nextFloat() * 100f;
			runInteraction(chooseInteraction);
		}

		count = 1;
		numTotalFrequentBookStoreInteraction = 0;
		numSuccessfulFrequentBookStoreInteraction = 0;

		// Perform the actual runs
		startTimeInNanoSecs = System.nanoTime();
		while (count++ <= configuration.getNumActualRuns()) {
			chooseInteraction = rand.nextFloat() * 100f;
			if (runInteraction(chooseInteraction)) {
				successfulInteractions++;
			}
		}
		endTimeInNanoSecs = System.nanoTime();
		timeForRunsInNanoSecs += (endTimeInNanoSecs - startTimeInNanoSecs);
		return new WorkerRunResult(successfulInteractions,
				timeForRunsInNanoSecs, configuration.getNumActualRuns(),
				numSuccessfulFrequentBookStoreInteraction,
				numTotalFrequentBookStoreInteraction);
	}

	/**
	 * Runs the new stock acquisition interaction
	 * 
	 * @throws BookStoreException
	 */
	private void runRareStockManagerInteraction() throws BookStoreException {
		//invoke getBooks
		List<StockBook> listBooks = configuration.getStockManager().getBooks();
				
		//gets a random set of books of size n
		Set<StockBook> randomBooks = new HashSet<StockBook>();
		randomBooks = configuration.getBookSetGenerator()
				.nextSetOfStockBooks(configuration.getSizeNtoGenerate());
		Set<Integer> isbns = new HashSet<Integer>();
		
		//Takes the ISBNs of the list and put it 
		//in an Integer ISBN set
				
		for (StockBook b : listBooks) {
			isbns.add(b.getISBN());
		}
		
		//Puts the ISBN which does not exist in the Integer set of ISBNs
		//in a new set, booksToAdd.
		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		for (Iterator<StockBook> i = randomBooks.iterator(); i.hasNext();) {
			StockBook elem = i.next();
			if(!isbns.contains(elem.getISBN()))
				booksToAdd.add(elem);
		}

		//Adds the new set of books that was not found 
		//in the list returned by getBooks
		configuration.getStockManager().addBooks(booksToAdd);
	}

	/**
	 * Runs the stock replenishment interaction
	 * 
	 * @throws BookStoreException
	 */
	private void runFrequentStockManagerInteraction() throws BookStoreException {
		StockManager stockManager = configuration.getStockManager();
		List<StockBook> books = stockManager.getBooks();
		
		Collections.sort(books, new Comparator<StockBook>(){
			@Override
			public int compare(StockBook book1, StockBook book2) {
				return Double.compare(book1.getNumCopies(), book2.getNumCopies());
			}
		});
		
		Set<BookCopy> bookCopiesToAdd = new HashSet<BookCopy>();
		for (int i = 0; i < configuration.getNumBooksToAddCopiesTo(); i++) 
		{
			bookCopiesToAdd.add(
					new BookCopy(
							books.get(i).getISBN(), 
							configuration.getNumAddCopies()));
		}
		
		stockManager.addCopies(bookCopiesToAdd);
	}

	/**
	 * Runs the customer interaction
	 * 
	 * @throws BookStoreException
	 */
	private void runFrequentBookStoreInteraction() throws BookStoreException {
		BookStore client = configuration.getBookStore();
		List<Book> editorPicks = client.getEditorPicks(configuration.getNumEditorPicksToGet());
		
		Set<Integer> ISBNs = new HashSet<Integer>();
		for (Book book : editorPicks)
			ISBNs.add(book.getISBN());
		
		Set<Integer> ISBNsToBuy = configuration.getBookSetGenerator().sampleFromSetOfISBNs(
				ISBNs, configuration.getNumBooksToBuy());
		
		Set<BookCopy> booksToBuy = new HashSet<BookCopy>();
		for (int ISBN : ISBNsToBuy)
		{	
			booksToBuy.add(new BookCopy(ISBN, 1));
		}
		
		client.buyBooks(booksToBuy);
	}

}
