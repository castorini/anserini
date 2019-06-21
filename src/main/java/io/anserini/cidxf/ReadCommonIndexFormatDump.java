package io.anserini.cidxf;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;

public class ReadCommonIndexFormatDump {
  public static void main(String[] argv) throws Exception {
    FileInputStream fileIn = new FileInputStream("postings.dat");
    CodedInputStream input = CodedInputStream.newInstance(fileIn);

    for (int i=0; i<10; i++ ) {
      CommonIndexFormat.PostingsList pl = CommonIndexFormat.PostingsList.parseDelimitedFrom(fileIn);
      System.out.println(pl.getTerm());
    }
    fileIn.close();
  }
}
