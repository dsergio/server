import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientJob {
	
	private int clientId;
	private Socket socket;
	private String command;
	private BufferedReader in;
	private PrintWriter out;
	
	public ClientJob(Socket socket, int clientId, BufferedReader in, PrintWriter out) {
		this.socket = socket;
		this.clientId = clientId;
		this.command = "";
		this.in = in;
		this.out = out;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public BufferedReader getIn() {
		return in;
	}
	
	public PrintWriter getOut() {
		return out;
	}
	
	public String getCommand() {
		return command;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public int getClientId() {
		return clientId;
	}
}
