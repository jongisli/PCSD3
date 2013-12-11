package com.acertainbookstore.client.workloads;

import java.util.HashSet;

import com.acertainbookstore.business.StockBook;

public class ISBNHashSet extends HashSet<StockBook > {
    public boolean containsISBN( StockBook b ) {
        for( StockBook i : this ) {
            if( i.getISBN() == b.getISBN() ) {
                return true;
            }
        }
        return false;
    }
}
