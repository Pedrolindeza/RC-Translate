package rc.translate.g25;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;

import rc.translate.g25.exception.TRRERRException;
import rc.translate.g25.exception.TRRNTAException;

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
	
	public static byte[] receiveData(DataInputStream is) throws IOException {
		byte[] inputData = new byte[1024];
		is.read(inputData);
		return inputData;
	}
	
	public static synchronized void sendData(DataOutputStream os, byte[] byteData) throws IOException {
        if (byteData == null) {
        	return;
        }
        os.write(byteData);
        os.flush();
	}

	public String sendTCPMessage(String message) throws IOException{
		Socket socket = new Socket(this.address, this.port);
		
		 DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
         DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
         
         sendData(dos, message.getBytes());
         
         byte[] inputData = receiveData(dis);
         socket.close();
         return new String(inputData).trim();
	}
	
	public String sendFile(String path) throws IOException, TRRERRException, TRRNTAException {
		Socket socket = new Socket(this.address, this.port);
		
		DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        
        File file = new File(path);
        
        sendData(dos, ("TRQ f " + file.getName() + " " + file.length() + " ").getBytes());
        
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        dos.write(fileBytes);
        dos.write("\n".getBytes());
        dos.flush();

        byte[] byteData = receiveData(dis);
        String clientRequestMessage = new String(byteData).trim();
        String[] split = clientRequestMessage.split(" ");
        if(split[0].equals("TRR")){
        	if(split[1].equals("f")){
        		String filename = split[2];
        		int filesize = Integer.parseInt(split[3]);
        		
        		byte[] newFileBytes = new byte[filesize];
    			byte[] buffer = null;
    			for(int i = 0; i < filesize; i++){
    				if(i%1024 == 0){
    					buffer = receiveData(dis);
    				}
    				newFileBytes[i] = buffer[i%1024];
    			}
    			
        		FileOutputStream fos = new FileOutputStream(filename);
        		fos.write(newFileBytes);
        		fos.close();
        		
        		newFileBytes=null;
        		buffer=null;
                fileBytes=null;
                socket.close();
                return filename;
        	}
        	else if(split[1].equals("ERR")){
                socket.close();
        		throw new TRRERRException();
        	}
        	else if(split[1].equals("NTA")){
                socket.close();
        		throw new TRRNTAException();
        	}
        	
        }
        socket.close();
        return null;
	}
}
