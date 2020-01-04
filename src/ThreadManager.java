import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;

public class ThreadManager extends Thread {
	
	private ThreadPool threadPool;
	private MyMonitor jobQueue;
	private ServerSocket listener;
	private boolean stopped;
	private int T1;
	private int T2;
	private int V;
	
	public ThreadManager(ThreadPool threadPool, MyMonitor jobQueue, ServerSocket listener, int T1, int T2, int V) {
		this.threadPool = threadPool;
		this.jobQueue = jobQueue;
		this.listener = listener;
		
		if (T1 < 1 || T1 > 40) {
			throw new RuntimeException("T1 out of range");
		}
		if (T2 < 1 || T2 > 40) {
			throw new RuntimeException("T2 out of range");
		}
		if (T1 > T2) {
			throw new RuntimeException("T1 must be less than T2");
		}
		if (V <= 0) {
			throw new RuntimeException("V must be positive");
		}
		this.T1 = T1;
		this.T2 = T2;
		this.V = V;
	}
	
	public boolean isStopped() {
		return stopped;
	}
	public void stopManager() {
		Logger.log("ThreadManager is going down");
		stopped = true;
		try {
			listener.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			
			threadPool.startPool();
			
			while (!stopped) {
				
				if (threadPool.isStopped()) {
					stopManager();
					break;
				}
				
				int numberJobs = jobQueue.size();
				
				if (numberJobs <= T1) {
					
					if (threadPool.numberThreadsRunning() > 5) {
						Logger.log("Pre-check: ThreadManager to halve number of threads in pool", "actualNumberThreads: " + threadPool.numberThreadsRunning(), "jobQueue.size(): " + numberJobs);
						threadPool.decreaseThreadsInPool();
					}
					
				} else if (numberJobs > T1 && numberJobs <= T2) {
					
					if (threadPool.numberThreadsRunning() == 5) {
						Logger.log("Pre-check: ThreadManager to double number of threads in pool", "actualNumberThreads: " + threadPool.numberThreadsRunning(), "jobQueue.size(): " + numberJobs);
						threadPool.increaseThreadsInPool();
					} else if (threadPool.numberThreadsRunning() > 10) {
						Logger.log("Pre-check: ThreadManager to halve number of threads in pool", "actualNumberThreads: " + threadPool.numberThreadsRunning(), "jobQueue.size(): " + numberJobs);
						threadPool.decreaseThreadsInPool();
					}
					
				} else if (numberJobs > T2 && numberJobs < 50) {
					
					if (threadPool.numberThreadsRunning() < 20) {
						Logger.log("Pre-check: ThreadManager to double number of threads in pool", "actualNumberThreads: " + threadPool.numberThreadsRunning(), "jobQueue.size(): " + numberJobs);
						threadPool.increaseThreadsInPool();
					} else if (threadPool.numberThreadsRunning() == 40) {
						Logger.log("Pre-check: ThreadManager to halve number of threads in pool", "actualNumberThreads: " + threadPool.numberThreadsRunning(), "jobQueue.size(): " + numberJobs);
						threadPool.decreaseThreadsInPool();
					}
					
				} else if (numberJobs >= 50) {
					
//					if (threadPool.numberThreadsRunning() < 40) {
//						Logger.log("Pre-check: ThreadManager to double number of threads in pool", "actualNumberThreads: " + threadPool.numberThreadsRunning(), "jobQueue.size(): " + numberJobs);
//						threadPool.increaseThreadsInPool();
//					}
				}
				
				
				if (numberJobs == 50) {
//					Logger.log("I'm the threadManager", "threadPool.numberThreadsRunning(): " + threadPool.numberThreadsRunning(), "jobQueue.size(): " + numberJobs);
				}
				
				Thread.sleep(V);
				
			}
			
		} catch (InterruptedException e) {
			System.out.println("ThreadManager Interrupted...");
			e.printStackTrace();
		}
		
	}

}
