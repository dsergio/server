import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class ThreadPool {
	int maxCapacity;		//maximum number of threads in the pool
	int actualNumberThreads;
	WorkerThread holders[];	//stores the worker thread references
	boolean stopped;		//used to receive a stop signal from main thread
	
	MyMonitor jobQueue;	//shared by all WorkerThread in the pool and ThreadManager 
	//and the main server thread
	
	int delay;
	
	public ThreadPool(MyMonitor jobQueue, int delay) {
		
		this.jobQueue = jobQueue;
		this.maxCapacity = 40;
		this.actualNumberThreads = 5;
		this.stopped = false;
		holders = new WorkerThread[maxCapacity];
		this.delay = delay;
	}
	
	private class WorkerThread extends Thread {
		//each Worker will grab a job in the jobQueue for 
		//processing if there are available jobs in the jobQueue.
		
		private boolean workerStopped;
		
		public WorkerThread() {
			workerStopped = false;
		}
		
		public void stopWorker() {
			workerStopped = true;
		}
		
		public boolean isWorkerStopped() {
			return workerStopped;
		}
		
		@Override
		public void run() {
			
			while (!workerStopped && !stopped) {
				
				
				ClientJob job = jobQueue.dequeue();
				
				if (job != null) {
					
					Socket socket = job.getSocket();
				
					try {
						
						PrintWriter out = job.getOut();
		
		                // Send a welcome message to the client.
						out.println("Hello, client #" + job.getClientId() + ", enter command: ADD,x,y  SUB,x,y  MUL,x,y  DIV,x,y  KILL.");
		                
						BufferedReader in = job.getIn();
		                
		                while (!workerStopped) {
		                	
		                    String input = "";
		                    
							try {
//								Logger.log("getting input");
								input = in.readLine();
								
							} catch (IOException e1) {
								// TODO Auto-generated catch block
//								e1.printStackTrace();
								
							}
							
							if (input == null || input.equals(".") || input.equals("")) {
//		                    	Logger.log("Closing client " + socket.toString());
		                    	out.println("Bye");
		                    	break;
		                        
		                    } else {
		                    	
		                    	if (input.toLowerCase().equals("kill")) {
			                    	Logger.log("CommandServer stopping");
			                    	out.println("Stopping the server, on your command.");
			                    	stopPool();
			                    	
			                    	break;
			                    	
			                    } else {
				                    
				                    // process command
				                    String output = CommandParser.parse(input, delay);
				                    
				                    Logger.log("Worker Thread ID=" + Thread.currentThread().getName() + " processed request ", input, output);
				                    
				                    out.println(output);
				                    
			                    }
		                    }
		                    
		                }
		            } finally {
		            	
		                try {
		                    socket.close();
		                } catch (IOException e) {
		                	Logger.log("Couldn't close a socket, what's going on?");
		                }
//		                Logger.log("Connection with client # " + job.getClientId() + " closed", stopped);
		            }
//					Logger.log("Just closed client " + socket.toString());
				}
			}
		}
	}
	
	public void startPool() {
		//start all available threads in the pool and Worker 
		//threads start to process jobs
		Logger.log("ThreadPool is starting");
		for (int i = 0; i < actualNumberThreads; i++) {
			holders[i] = new WorkerThread();
		}
		for (int i = 0; i < actualNumberThreads; i++) {
			holders[i].start();
		}
	}
	
	public synchronized void increaseThreadsInPool() {
		//double the threads in pool according to threshold
		
		if (actualNumberThreads < maxCapacity) {
			int newActualNumberThreads = actualNumberThreads * 2;
			
			for (int i = actualNumberThreads; i < newActualNumberThreads; i++) {
				holders[i] = new WorkerThread();
			}
			for (int i = actualNumberThreads; i < newActualNumberThreads; i++) {
				holders[i].start();
			}
			actualNumberThreads = newActualNumberThreads;
		}
		Logger.log("Post-check: ThreadManager doubled number of threads in pool", "actualNumberThreads: " + actualNumberThreads, holdersToString());
	}
	
	public synchronized void decreaseThreadsInPool() {
		//halve the threads in pool according to threshold
		
		if (actualNumberThreads > 5) {
			int newActualNumberThreads = actualNumberThreads / 2;
			
			for (int i = actualNumberThreads - 1; i >= newActualNumberThreads; i--) {
				holders[i].stopWorker();
			}
//			for (int i = actualNumberThreads - 1; i >= newActualNumberThreads; i--) {
//				try {
//					holders[i].join();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			actualNumberThreads = newActualNumberThreads;
		}
		Logger.log("Post-check: ThreadPool halved number of threads in the pool", "actualNumberThreads: " + actualNumberThreads, holdersToString());
		
	}
	
	public synchronized void stopPool() {
		Logger.log("Pre-check: ThreadPool is stopping", holdersToString());
		//terminate all threads in the pool gracefully
		//all threads in pool terminate when a command KILL is sent through the client //	to the server.
		
		stopped = true;
		
//		for (int i = 0; i < actualNumberThreads; i++) {
//			holders[i].stopWorker();
//		}
//		
//		for (int i = 0; i < actualNumberThreads; i++) {
//			holders[i].interrupt();
//		}
//		
//		for (int i = 0; i < actualNumberThreads; i++) {
//			holders[i] = null;
//		}
		
		for (int i = 0; i < 40; i++) {
			if (holders[i] != null) {
				holders[i].stopWorker();
			}
		}
		
		for (int i = 0; i < 40; i++) {
			if (holders[i] != null) {
				holders[i].interrupt();
			}
		}
		
		for (int i = 0; i < 40; i++) {
			if (holders[i] != null) {
				holders[i] = null;
			}
		}
		
		actualNumberThreads = 0;
		Logger.log("Post-check: ThreadPool is stopping", holdersToString());
		
	}
	
	public boolean isStopped() {
		return stopped;
	}
	
	public int numberThreadsRunning() {
		return actualNumberThreads;
	}
	
	public int maxCapacity() {
		return maxCapacity;
	}
	
	public String holdersToString() {
		ArrayList<String> threadList = new ArrayList<String>();
		
		for (WorkerThread t : holders) {
			if (t != null && !t.isWorkerStopped()) {
				threadList.add(t.getName());
			}
		}
		
		return threadList.toString();
	}

}
