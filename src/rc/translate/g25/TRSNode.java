package rc.translate.g25;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
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
	
	public String sendFile(String path) throws IOException{    
		Socket socket = new Socket(this.address, this.port);
		
		//Upload File		
		File file = new File(path);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        OutputStream os = socket.getOutputStream();

        long fileLength = file.length(); 
        long current = 0;
        
        os.write(("TRQ f " + file.getName() + fileLength + " ").getBytes());
        
        byte[] contents;
        while(current != fileLength){ 
            int size = 10000;
            
            if(fileLength - current >= size)
                current += size;
            else{ 
                size = (int)(fileLength - current); 
                current = fileLength;
            } 
            
            contents = new byte[size]; 
            bis.read(contents, 0, size); 
            os.write(contents);
        }
        
        bis.close();
        
        os.write("\n".getBytes());
        os.flush();       
        
        
        
        //Download File
        InputStream is = socket.getInputStream();
        
        byte[] chunk = new byte[1000];        
        int chunkLen = is.read(chunk);

<<<<<<< HEAD
        String chunkString = chunk.toString();
        String[] split = chunkString.split(" ");
        if(!split[0].equals("TRR") || !split[1].equals("f")){
        	socket.close();
        	return null;
        }
        
    	String filename = split[2];
    	int size = Integer.parseInt(split[3]);
        
    	File newFile = new File(filename);
    	FileOutputStream fos = new FileOutputStream(newFile);

    	fos.write(split[4].getBytes());
    	
        while((chunkLen = is.read(chunk)) != -1) {
        	for(byte b : chunk){
        		if(b != (byte) '\n'){
        			fos.write(b);
        		}
        	}
        }
        
        fos.close();
=======
        return null;
>>>>>>> adee0d15cd758d069dec7bd4b105b8e8bd87222f
        
        socket.close();
        return filename + " " + size;
	}
}
