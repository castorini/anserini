package io.anserini.rerank.lib;

import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.AnalyzerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.*;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_BODY;


class BM25PRFSimilarity extends BM25Similarity {

    BM25PRFSimilarity(float k1, float b) {
        super(k1, b);
    }

    @Override
    // idf is not needed in BM25PRF
    protected float idf(long docFreq, long docCount) {
        return 1;
    }
}


public class BM25PRFReranker implements Reranker {
    private static final Logger LOG = LogManager.getLogger(BM25PRFReranker.class);

    private final int fbDocs;
    private final Analyzer analyzer;
    private final String field;
    private final boolean outputQuery;
    private final int fbTerms;
    private final float k1;
    private final float b;
    private final float newTermWeight;

    public BM25PRFReranker(Analyzer analyzer, String field, int fbTerms, int fbDocs, float k1, float b, float newTermWeight, boolean outputQuery) {
        this.analyzer = analyzer;
        this.outputQuery = outputQuery;
        this.field = field;
        this.fbTerms = fbTerms;
        this.fbDocs = fbDocs;
        this.k1 = k1;
        this.b = b;
        this.newTermWeight = newTermWeight;
    }

    @Override
    public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {

        // set similarity to BM25PRF
        IndexSearcher searcher = context.getIndexSearcher();
        BM25Similarity originalSimilarity = (BM25Similarity) searcher.getSimilarity();
        searcher.setSimilarity(new BM25PRFSimilarity(k1, b));
        IndexReader reader = searcher.getIndexReader();
        List<String> originalQueryTerms = AnalyzerUtils.tokenize(analyzer, context.getQueryText());

        PRFFeatures fv = expandQuery(originalQueryTerms, docs, reader);
        Query newQuery = fv.toQuery();

        if (this.outputQuery) {
            LOG.info("QID: " + context.getQueryId());
            LOG.info("Original Query: " + context.getQuery().toString(this.field));
            LOG.info("Running new query: " + newQuery.toString(this.field));
            LOG.info("Features: " + fv.toString());
        }

        TopDocs rs;

        try {
            rs = searcher.search(newQuery, context.getSearchArgs().hits);
        } catch (IOException e) {
            e.printStackTrace();
            return docs;
        }
        // set similarity back
        searcher.setSimilarity(originalSimilarity);
        return ScoredDocuments.fromTopDocs(rs, searcher);
    }


    class PRFFeature {
        int df;
        int dfRel;
        int numDocs;
        int numDocsRel;
        float weight;


        PRFFeature(int df, int dfRel, int numDocs, int numDocsRel, float weight) {
            this.df = df;
            this.dfRel = dfRel;
            this.numDocs = numDocs;
            this.numDocsRel = numDocsRel;
            this.weight = weight;
        }

        double getRelWeight() {
            double rw =  Math.log((dfRel + 0.5D) * (numDocs - df - numDocsRel + dfRel + 0.5D) /
                    ((df - dfRel + 0.5D) * (numDocsRel - dfRel + 0.5D))) * weight;
            return Math.max(rw, 1e-6);
        }

        double getOfferWeight() {
            return getRelWeight() * Math.log(Math.max(dfRel, 1e-6));
        }


        @Override
        public String toString() {
            return String.format("%d, %d, %d, %d, %f, %f, %f", df, dfRel, numDocs, numDocsRel, weight, getRelWeight(), getOfferWeight());
        }
    }


    class PRFFeatures {
        private HashMap<String, PRFFeature> features;

        PRFFeatures() {
            this.features = new HashMap<>();
        }

        void addFeature(String term, int df, int dfRel, int numDocs, int numDocsRel, float weight) {
            features.put(term, new PRFFeature(df, dfRel, numDocs, numDocsRel, weight));
        }


        void addFeature(String term, int df, int dfRel, int numDocs, int numDocsRel) {
            addFeature(term, df, dfRel, numDocs, numDocsRel, 1.0f);
        }


        public Query toQuery() {
            BooleanQuery.Builder feedbackQueryBuilder = new BooleanQuery.Builder();

            for (Map.Entry<String, PRFFeature> f : features.entrySet()) {
                String term = f.getKey();
                float rw = (float) f.getValue().getRelWeight();
                feedbackQueryBuilder.add(new BoostQuery(new TermQuery(new Term(field, term)), rw), BooleanClause.Occur.SHOULD);
            }
            return feedbackQueryBuilder.build();
        }


        private List<KeyValuePair> getOrderedFeatures() {
            List<KeyValuePair> kvpList = new ArrayList<KeyValuePair>(features.size());
            for (String feature : features.keySet()) {
                PRFFeature value = features.get(feature);
                KeyValuePair keyValuePair = new KeyValuePair(feature, value);
                kvpList.add(keyValuePair);
            }

            Collections.sort(kvpList, new Comparator<KeyValuePair>() {
                public int compare(KeyValuePair x, KeyValuePair y) {
                    double xVal = x.getValue();
                    double yVal = y.getValue();

                    return (Double.compare(yVal, xVal));
                }
            });

            return kvpList;
        }


        PRFFeatures pruneToSize(int k) {
            List<KeyValuePair> pairs = getOrderedFeatures();
            HashMap<String, PRFFeature> pruned = new HashMap<>();

            for (KeyValuePair pair : pairs) {
                if (pruned.size() >= k) {
                    break;
                }
                pruned.put(pair.getKey(), pair.getFeature());
            }

            this.features = pruned;
            return this;
        }


        private class KeyValuePair {
            private String key;
            private PRFFeature value;

            public KeyValuePair(String key, PRFFeature value) {
                this.key = key;
                this.value = value;
            }

            public String getKey() {
                return key;
            }

            @Override
            public String toString() {
                return value + "\t" + key;
            }

            public float getValue() {
                return (float) value.getOfferWeight();
            }


            public PRFFeature getFeature() {
                return value;
            }
        }


        @Override
        public String toString() {
            List<String> strBuilder = new ArrayList<String>();
            List<KeyValuePair> pairs = getOrderedFeatures();

            for (KeyValuePair pair : pairs) {
                strBuilder.add(pair.getKey() + "," + pair.getFeature());
            }

            return String.join("||", strBuilder);
        }
    }


    private PRFFeatures expandQuery(List<String> originalTerms, ScoredDocuments docs, IndexReader reader) {
        PRFFeatures newFeatures = new PRFFeatures();

        Set<String> vocab = new HashSet<>();

        Map<Integer, Set<String>> docToTermsMap = new HashMap<>();
        int numRelDocs = docs.documents.length < fbDocs ? docs.documents.length : fbDocs;
        int numDocs = reader.numDocs();

        for (int i = 0; i < numRelDocs; i++) {
            try {
                Terms terms = reader.getTermVector(docs.ids[i], field);
                Set<String> termsStr = getTermsStr(terms);
                docToTermsMap.put(docs.ids[i], termsStr);
                vocab.addAll(termsStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Set<String> originalTermsSet = new HashSet<>(originalTerms);

        // Add New Terms
        for (String term : vocab) {
            if (originalTermsSet.contains(term)) continue;
            if (term.length() < 2 || term.length() > 20) continue;
            if (!term.matches("[a-z0-9]+")) continue;
            if (term.matches("[0-9]+")) continue;

            try {
                int df = reader.docFreq(new Term(FIELD_BODY, term));
                int dfRel = 0;

                for (int i = 0; i < numRelDocs; i++) {
                    Set<String> terms = docToTermsMap.get(docs.ids[i]);
                    if (terms.contains(term)) {
                        dfRel++;
                    }
                }

                if (dfRel < 2) {
                    continue;
                }
                newFeatures.addFeature(term, df, dfRel, numDocs, numRelDocs, newTermWeight);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        newFeatures.pruneToSize(fbTerms);

        for (String term : originalTerms) {
            try {
                int df = reader.docFreq(new Term(FIELD_BODY, term));
                int dfRel = 0;

                for (int i = 0; i < numRelDocs; i++) {
                    Set<String> terms = docToTermsMap.get(docs.ids[i]);
                    if (terms.contains(term)) {
                        dfRel++;
                    }
                }
                newFeatures.addFeature(term, df, dfRel, numDocs, numRelDocs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newFeatures;
    }


    @Override
    public String tag() {
        return "BM25PRF(fbDocs=" + fbDocs + ",fbTerms=" + fbTerms + ",k1=" + k1 + ",b=" + b + ",newTermWeight=" + newTermWeight;
    }


    private Set<String> getTermsStr(Terms terms) {
        Set<String> termsStr = new HashSet<>();

        try {
            TermsEnum termsEnum = terms.iterator();

            BytesRef text;
            while ((text = termsEnum.next()) != null) {
                String term = text.utf8ToString();
                termsStr.add(term);

            }
        } catch (Exception e) {
            e.printStackTrace();
            // Return empty feature vector
            return termsStr;
        }

        return termsStr;
    }

}
