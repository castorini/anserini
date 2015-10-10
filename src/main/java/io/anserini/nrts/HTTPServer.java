package io.anserini.nrts;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer extends Thread {
  private ServerSocket server;
  private int numThreads = 50;

  public HTTPServer(int port) throws IOException {
    this.server = new ServerSocket(port);
  }

  public void run() {
    for (int i = 0; i < numThreads; i++) {
      Thread strThread = new Thread(new SearchTweetsHTTP());
      strThread.start();
    }

    System.out.println("Accepting connections on port " + server.getLocalPort());
    while (true) {
      try {
        Socket request = server.accept();
        SearchTweetsHTTP.processRequest(request);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}
