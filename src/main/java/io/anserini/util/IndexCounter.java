package io.anserini;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;

public class IndexCounter {

	public void readIndex(String indexDir, String docIdPath) throws IOException{
		FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());
		DirectoryReader reader = DirectoryReader.open(dir);
		
		FileWriter fw = new FileWriter(new File(docIdPath));
		BufferedWriter bw = new BufferedWriter(fw);
		int len = reader.numDocs();
		for (int i = 0; i < len; i ++){
			String docName = reader.document(i).get("docname");
			bw.write(docName + "\n");
			//System.out.println("IndexCounter: " + i + " docs got");
			if ((i & 65535) == 0){
				System.out.println("IndexCounter: " + i + " docs got");
			}
			//System.out.println(docName);


		}
		bw.close();
	}
	
	public static void main(String[] clArgs) {
		Args args = new Args(clArgs);
		final String indexDir = args.getString("-indexPath") + "/index";
		final String docIdPath = args.getString("-docIdPath");
		
		args.check();
		
		System.out.println("Index path: " + indexDir);
		System.out.println("DocId path: " + docIdPath);
		final IndexCounter ic = new IndexCounter();
		try {
			ic.readIndex(indexDir, docIdPath);
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
}
