package rc.translate.g25;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;

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
		
		 DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
         DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
         
         dos.write(message.getBytes());
         dos.flush();
         
         byte[] inputData = new byte[1024];
         dis.read(inputData);
         
         socket.close();
         return new String(inputData).trim();
	}
	
	public String sendFile(String path) throws IOException {
		Socket socket = new Socket(this.address, this.port);
		
		DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        
        File file = new File(path);
        
        dos.write(("TRQ f " + file.getName() + " " + file.length() + " ").getBytes());
        dos.flush();
        
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        dos.write(fileBytes);
        dos.write("\n".getBytes());
        dos.flush();
        
        byte[] inputData = new byte[1024];
        dis.read(inputData);
        
        socket.close();
        return "hello";
	}
}
