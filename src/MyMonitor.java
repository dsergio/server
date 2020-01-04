import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class MyMonitor {
	
	private int maxCapacity;
	private Queue<ClientJob> jobQueue;
	private boolean stopped;
	
	public MyMonitor() {
		this.maxCapacity = 50;
		jobQueue = new LinkedList<ClientJob>();
		stopped = false;
	}
	
	public synchronized void enqueue(ClientJob job) {
		
		if (stopped) {
			return;
		}
		
		Socket socket = job.getSocket();
		PrintWriter out = job.getOut();
		BufferedReader in  = job.getIn();
		
		int numberJobs = jobQueue.size();
		
		if (numberJobs >= maxCapacity) {
			try {
				
				out.println("The server is currently busy, please connect later!");
				Logger.log("Client # " + job.getClientId() + " rejected");
				socket.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			
			
//			String input = "";
//			try {
//				input = in.readLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			job.setCommand(input);
			
//			out.println("Hello, you are client #" + job.getClientId() + ".");
			jobQueue.add(job);
			
//			Logger.log("Enqueue client # " + job.getClientId());
			
		}
		
		notifyAll();
	}
	
	public synchronized ClientJob dequeue() {
		
		if (stopped) {
			return null;
		}
//		Logger.log("trying to dequeue... size: " + jobQueue.size());
		while (jobQueue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
//				Logger.log("dequeue interrupted");
				return null;
			}
		}
		
		ClientJob job = jobQueue.remove();
//		Logger.log("Dequeue client # " + job.getClientId());
		
		notifyAll();
		return job;
	}
	
	public synchronized void stopMonitor() {
		jobQueue = new LinkedList<ClientJob>();
		stopped = true;
		notifyAll();
	}
	
	public synchronized int size() {
		return jobQueue.size();
	}
	
	public synchronized boolean isEmpty() {
		return jobQueue.isEmpty();
	}
	
	
}
