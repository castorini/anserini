# Features
| Feature No.     | Description                                     |
|-----------------|-------------------------------------------------|
|      1          | [IBM Model1 in title\_unlemm](../src/main/java/io/anserini/ltr/feature/IBMModel1.java)            
|      2          | IBM Model1 in url\_unlemm
|      3          | IBM Model1 in body                
|      4          | IBM Model1 in text\_bert\_token          
|      5          | [Sum of BM25](../src/main/java/io/anserini/ltr/feature/BM25Stat.java)
|      6          | Average of BM25
|      7          | Median of BM25
|      8          | Max of BM25 
|      9          | Min of BM25 
|      10         | MaxMinRatio of BM25 
|      11         | [Sum of LMDir](../src/main/java/io/anserini/ltr/feature/LMDirStat.java)  
|      12         | Average of LMDir 
|      13         | Median of LMDir 
|      14         | Max of LMDir 
|      15         | Min of LMDir 
|      16         | MaxMinRatio of LMDir
|      17         | [Sum of DFR\_GL2](../src/main/java/io/anserini/ltr/feature/DFRGL2Stat.java)  
|      18         | Average of DFR\_GL2 
|      19         | Median of DFR\_GL2 
|      20         | Max of DFR\_GL2 
|      21         | Min of DFR\_GL2 
|      22         | MaxMinRatio of DFR\_GL2 
|      23         | [Sum of DFR\_in\_expB2](../src/main/java/io/anserini/ltr/feature/DFRInExpB2Stat.java)
|      24         | Average of DFR\_in\_expB2 
|      25         | Median of DFR\_in\_expB2 
|      26         | Max of DFR\_in\_expB2 
|      27         | Min of DFR\_in\_expB2 
|      28         | MaxMinRatio of DFR\_in\_expB2 
|      29         | [Sum of DPH](../src/main/java/io/anserini/ltr/feature/DPHStat.java)
|      30         | Average of DPH 
|      31         | Median of DPH 
|      32         | Max of DPH 
|      33         | Min of DPH 
|      34         | MaxMinRatio of DPH 
|      35         | [Sum of TF](../src/main/java/io/anserini/ltr/feature/TFStat.java)  
|      36         | Average of TF 
|      37         | Median of TF 
|      38         | Max of TF 
|      39         | Min of TF 
|      40         | MaxMinRatio of TF 
|      41         | [Sum of TFIDF](../src/main/java/io/anserini/ltr/feature/TFIDFStat.java)  
|      42         | Average of TFIDF 
|      43         | Median of TFIDF 
|      44         | Max of TFIDF 
|      45         | Min of TFIDF 
|      46         | MaxMinRatio of TFIDF 
|      47         | [Sum of Normalized TF](../src/main/java/io/anserini/ltr/feature/NormalizedTFStat.java)  
|      48         | Average of Normalized TF 
|      49         | Median of Normalized TF 
|      50         | Max of Normalized TF 
|      51         | Min of Normalized TF 
|      52         | MaxMinRatio of Normalized TF 
|      53         | [Sum of IDF](../src/main/java/io/anserini/ltr/feature/IDFStat.java)  
|      54         | Average of IDF 
|      55         | Median of IDF 
|      56         | Max of IDF 
|      57         | Min of IDF 
|      58         | MaxMinRatio of IDF 
|      59         | [Sum of ICTF](../src/main/java/io/anserini/ltr/feature/ICTFStat.java)  
|      60         | Average of ICTF 
|      61         | Median of ICTF 
|      62         | Max of ICTF 
|      63         | Min of ICTF 
|      64         | MaxMinRatio of ICTFs 
|      65         | [UnorderedSequentialPairs with gap 3](../src/main/java/io/anserini/ltr/feature/UnorderedSequentialPairs.java) 
|      66         | UnorderedSequentialPairs with gap 8
|      67         | UnorderedSequentialPairs with gap 15
|      68         | [OrderedSequentialPairs with gap 3](../src/main/java/io/anserini/ltr/feature/OrderedSequentialPairs.java)
|      69         | OrderedSequentialPairs with gap 8
|      70         | OrderedSequentialPairs with gap 15
|      71         | [UnorderedQueryPairs with gap 3](../src/main/java/io/anserini/ltr/feature/UnorderedQueryPairs.java)
|      72         | UnorderedQueryPairs with gap 8
|      73         | UnorderedQueryPairs with gap 15
|      74         | [OrderedQueryPairs with gap 3](../src/main/java/io/anserini/ltr/feature/OrderedQueryPairs.java)
|      75         | OrderedQueryPairs with gap 8
|      76         | OrderedQueryPairs with gap 15
|      77         | [Normalized TFIDF](../src/main/java/io/anserini/ltr/feature/NormalizedTFIDF.java) 
|      78         | [ProbabilitySum](../src/main/java/io/anserini/ltr/feature/ProbalitySum.java) 
|      79         | [Proximity](../src/main/java/io/anserini/ltr/feature/Proximity.java) 
|      80         | [BM25-TP score](../src/main/java/io/anserini/ltr/feature/TpScore.java)
|      81         | [TP distance](../src/main/java/io/anserini/ltr/feature/TpDist.java)  
|      82         | [Doc size](../src/main/java/io/anserini/ltr/feature/DocSize.java)
|      83         | [Query Length](../src/main/java/io/anserini/ltr/feature/QueryLength.java)  
|      84         | [Query Coverage Ratio](../src/main/java/io/anserini/ltr/feature/QueryCoverageRatio.java)  
|      85         | [Unique Term Count in Query](../src/main/java/io/anserini/ltr/feature/UniqueTermCount.java) 
|      86         | [Matching Term Count](../src/main/java/io/anserini/ltr/feature/MatchingTermCount.java) 
|      87         | [SCS](../src/main/java/io/anserini/ltr/feature/SCS.java) 