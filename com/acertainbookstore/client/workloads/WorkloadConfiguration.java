package com.acertainbookstore.client.workloads;

import com.acertainbookstore.business.CertainBookStore;
import com.acertainbookstore.client.BookStoreHTTPProxy;
import com.acertainbookstore.client.StockManagerHTTPProxy;
import com.acertainbookstore.interfaces.BookStore;
import com.acertainbookstore.interfaces.StockManager;

/**
 * 
 * WorkloadConfiguration represents the configuration parameters to be used by
 * Workers class for running the workloads
 * 
 */
public class WorkloadConfiguration {
	private int numBooksToBuy = 5;
	private int numEditorPicksToGet = 10;
	private int numAddCopies = 10;
	private int numBooksToAdd = 5;
	private int warmUpRuns = 20;
	private int numActualRuns = 20;
	private float percentRareStockManagerInteraction = 10f;
	private float percentFrequentStockManagerInteraction = 40f;
	private BookSetGenerator bookSetGenerator = null;
	private StockManager stockManager = null;
	private BookStore bookStore = null;
	
	// This is the number k defined in the assignment text
	// describing runFrequentStockManagerInteraction().
	private int numBooksToAddCopiesTo = 10; 
	
	//Size n of the random set generated in runRareStockManagerInteraction()
	private int sizeNtoGenerate = 10;	

	public int getNumBooksToBuy() {
		return numBooksToBuy;
	}

	public void setNumBooksToBuy(int numBooksToBuy) {
		this.numBooksToBuy = numBooksToBuy;
	}

	public int getNumBooksToAdd() {
		return numBooksToAdd;
	}

	public void setNumBooksToAdd(int numBooksToAdd) {
		this.numBooksToAdd = numBooksToAdd;
	}

	public WorkloadConfiguration(String serverAddress, boolean localTest)
			throws Exception {
		// Create a new one so that it is not shared
		bookSetGenerator = new BookSetGenerator();
		// Initialize the RPC interfaces if its not a localTest
		if (localTest) {
			stockManager = CertainBookStore.getInstance();
			bookStore = CertainBookStore.getInstance();
		} else {
			stockManager = new StockManagerHTTPProxy(serverAddress + "/stock");
			bookStore = new BookStoreHTTPProxy(serverAddress);
		}
	}

	public StockManager getStockManager() {
		return stockManager;
	}

	public BookStore getBookStore() {
		return bookStore;
	}

	public float getPercentRareStockManagerInteraction() {
		return percentRareStockManagerInteraction;
	}

	public void setPercentRareStockManagerInteraction(
			float percentRareStockManagerInteraction) {
		this.percentRareStockManagerInteraction = percentRareStockManagerInteraction;
	}

	public float getPercentFrequentStockManagerInteraction() {
		return percentFrequentStockManagerInteraction;
	}

	public void setPercentFrequentStockManagerInteraction(
			float percentFrequentStockManagerInteraction) {
		this.percentFrequentStockManagerInteraction = percentFrequentStockManagerInteraction;
	}

	public int getWarmUpRuns() {
		return warmUpRuns;
	}

	public void setWarmUpRuns(int warmUpRuns) {
		this.warmUpRuns = warmUpRuns;
	}

	public int getNumActualRuns() {
		return numActualRuns;
	}

	public void setNumActualRuns(int numActualRuns) {
		this.numActualRuns = numActualRuns;
	}

	public int getNumBookToBuy() {
		return numBooksToBuy;
	}

	public void setNumBookToBuy(int numBookToBuy) {
		this.numBooksToBuy = numBookToBuy;
	}

	public int getNumEditorPicksToGet() {
		return numEditorPicksToGet;
	}

	public void setNumEditorPicksToGet(int numEditorPicksToGet) {
		this.numEditorPicksToGet = numEditorPicksToGet;
	}

	public int getNumAddCopies() {
		return numAddCopies;
	}

	public void setNumAddCopies(int numAddCopies) {
		this.numAddCopies = numAddCopies;
	}

	public BookSetGenerator getBookSetGenerator() {
		return bookSetGenerator;
	}

	public void setBookSetGenerator(BookSetGenerator bookSetGenerator) {
		this.bookSetGenerator = bookSetGenerator;
	}

	public int getNumBooksToAddCopiesTo() {
		return numBooksToAddCopiesTo;
	}

	public void setNumBooksToAddCopiesTo(int numBooksToAddCopiesTo) {
		this.numBooksToAddCopiesTo = numBooksToAddCopiesTo;
	}

	public int getSizeNtoGenerate() {
		return sizeNtoGenerate;
	}

	public void setSizeNtoGenerate(int sizeNtoGenerate) {
		this.sizeNtoGenerate = sizeNtoGenerate;
	}

}
