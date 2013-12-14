/**
 * 
 */
package com.acertainbookstore.client.workloads;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.acertainbookstore.business.CertainBookStore;
import com.acertainbookstore.business.StockBook;
import com.acertainbookstore.client.BookStoreHTTPProxy;
import com.acertainbookstore.client.StockManagerHTTPProxy;
import com.acertainbookstore.interfaces.BookStore;
import com.acertainbookstore.interfaces.StockManager;

/**
 * 
 * CertainWorkload class runs the workloads by different workers concurrently.
 * It configures the environment for the workers using WorkloadConfiguration
 * objects and reports the metrics
 * 
 */
public class CertainWorkload {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int numConcurrentWorkloadThreads = 45;
		int numberOfBooks = 100;
		String serverAddress = "http://localhost:8081";
		boolean localTest = false;
		List<WorkerRunResult> workerRunResults = new ArrayList<WorkerRunResult>();
		List<Future<WorkerRunResult>> runResults = new ArrayList<Future<WorkerRunResult>>();

		System.out.println("Initializing data ...");
		initializeBookStoreData(serverAddress, localTest, numberOfBooks);
		System.out.println("Finished initializing data");
		
		ExecutorService exec = Executors
				.newFixedThreadPool(numConcurrentWorkloadThreads);

		for (int i = 0; i < numConcurrentWorkloadThreads; i++) {
			// The server address is ignored if localTest is true
			WorkloadConfiguration config = new WorkloadConfiguration(
					serverAddress, localTest);
			Worker workerTask = new Worker(config);
			// Keep the futures to wait for the result from the thread
			runResults.add(exec.submit(workerTask));
		}

		// Get the results from the threads using the futures returned
		for (Future<WorkerRunResult> futureRunResult : runResults) {
			WorkerRunResult runResult = futureRunResult.get(); // blocking call
			workerRunResults.add(runResult);
		}

		exec.shutdownNow(); // shutdown the executor
		System.out.println("Reporting metrics ...");
		reportMetric(workerRunResults, numberOfBooks);
		System.out.println("Finished reporting metrics ...");
	}

	/**
	 * Computes the metrics and prints them
	 * 
	 * @param workerRunResults
	 */
	public static void reportMetric(List<WorkerRunResult> workerRunResults, int numberOfBooks) {
		
		PrintWriter resultFile = null;
		try {
			resultFile = new PrintWriter(
					"data/clients_" + workerRunResults.size() 
					+ "_books_" + numberOfBooks);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
		
		int workerNr = 1;
		resultFile.println(
				"#WorkerNr "
				+ "TotalRuns "
				+ "SuccessfulInteractions "
				+ "TotalFrequentBookStoreInteractionRuns "
				+ "SuccessfulFrequentBookStoreInteractionRuns "
				+ "ElapsedTimeInNanoSecs");
		
		for (WorkerRunResult result : workerRunResults)
		{
			resultFile.println(
					workerNr + " " + result.getTotalRuns()
					+ " " + result.getSuccessfulInteractions() 
					+ " " + result.getTotalFrequentBookStoreInteractionRuns()
					+ " " + result.getSuccessfulFrequentBookStoreInteractionRuns()
					+ " " + result.getElapsedTimeInNanoSecs());
			workerNr++;
		}
		
		resultFile.close();
	}

	/**
	 * Generate the data in bookstore before the workload interactions are run
	 * 
	 * Ignores the serverAddress if its a localTest
	 * 
	 * @param serverAddress
	 * @param localTest
	 * @throws Exception
	 */
	public static void initializeBookStoreData(String serverAddress,
			boolean localTest, int numberOfBooks) throws Exception {
		BookStore bookStore = null;
		StockManager stockManager = null;
		// Initialize the RPC interfaces if its not a localTest
		if (localTest) {
			stockManager = CertainBookStore.getInstance();
			bookStore = CertainBookStore.getInstance();
		} else {
			stockManager = new StockManagerHTTPProxy(serverAddress + "/stock");
			bookStore = new BookStoreHTTPProxy(serverAddress);
		}

		BookSetGenerator bookgenerator = new BookSetGenerator();
		Set<StockBook> booksGenerated = bookgenerator.nextSetOfStockBooks(numberOfBooks);		
		stockManager.addBooks(booksGenerated);

		// Finished initialization, stop the clients if not localTest
		if (!localTest) {
			((BookStoreHTTPProxy) bookStore).stop();
			((StockManagerHTTPProxy) stockManager).stop();
		}

	}
}
