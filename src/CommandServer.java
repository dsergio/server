import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CommandServer {

	public static void main(String[] args) throws Exception {
		
		int delay = 0;
		if (args.length == 1) {
			delay = Integer.parseInt(args[0]);
		}

		Logger.log("CommandServer is running.");

		int clientNumber = 0;
		ServerSocket listener = new ServerSocket(9898, 1000);

		MyMonitor jobQueue = new MyMonitor();
		ThreadPool threadPool = new ThreadPool(jobQueue, delay);
		ThreadManager threadManager = new ThreadManager(threadPool, jobQueue, listener, 10, 20, 5);

		threadManager.start();

		try {
			
			while (!threadManager.isStopped()) {
				Socket socket = listener.accept();
				
				clientNumber++;

				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				ClientJob job = new ClientJob(socket, clientNumber, in, out);

				jobQueue.enqueue(job);
			}

		} catch (Exception e) {
//			System.out.println("Exception in CommandServer: " + e.getMessage());

		} finally {
			listener.close();
		}

		threadManager.join();
		Logger.log("All done");
	}
}
