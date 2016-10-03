package rc.translate.g25;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class TRSNode {
	private String language;
	private InetAddress address;
	private int port;
	
	public TRSNode(String language, InetAddress address,int port){
		this.language = language;
		this.address = address;
		this.port = port;
	}
	

	public String getLanguage(){
		return this.language;
	}
	
	public InetAddress getAddress(){
		return this.address;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public void setPort(int port) {
		this.port=port;
		
	}
	
	public void setLanguage(String language) {
		this.language=language;
		
	}
	
	public void setAddress(InetAddress address) {
		this.address=address;
		
	}

	public String sendTCPMessage(String message) throws IOException{
		Socket socket = new Socket(this.address, this.port);
		
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		BufferedReader output = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		dos.writeBytes(message);
		
		String response = output.readLine();
		
		socket.close();
		return response;
	}
}
