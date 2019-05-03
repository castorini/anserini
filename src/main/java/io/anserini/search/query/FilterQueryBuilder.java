package io.anserini.search.query;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermInSetQuery;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * Wrap a query into a filter query
 */
public class FilterQueryBuilder {


    /***
     * Creates a query that match any documents whose value for field <b>fld</b> is on the <b>values</b> set.
     * This is useful to restrict scoring to certain list of documents.
     * @param fld to the field to use in the query
     * @param values list of values to match on
     * @return
     */
    public static Query buildSetQuery(String fld,Iterable<String> values)  {
        Set<BytesRef> idsSet = new HashSet();
        for (String id: values){
            idsSet.add(new BytesRef(id));
        }
        TermInSetQuery termInSetQuery = new TermInSetQuery(fld,idsSet);

        return termInSetQuery;
    }


    /***
     * Adds a <b>filter</b> query to the <b>query</b>. The methods creates a new boolean query then add the query is a must clause and the filter as a filter clause
     * @param query  the query to which if the filter is added
     * @param filter the query filter
     * @return the original query with the filter query added to it.
     */
    public static Query addFilterQuery(Query query, Query filter) {

        Query finalQuery = null;
        BooleanQuery.Builder bqBuilder = new BooleanQuery.Builder();
        bqBuilder.add(query, BooleanClause.Occur.MUST);
        bqBuilder.add(filter, BooleanClause.Occur.FILTER);
        finalQuery = bqBuilder.build();
        return finalQuery;
    }


}
