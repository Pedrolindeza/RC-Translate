package rc.translate.g25;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Scanner;

import rc.translate.g25.exception.SRGNOKException;
import rc.translate.g25.exception.SRRERRException;

public class TRS {
	private static String TCSname = "127.0.0.1";
	private static int TCSport = 58025;
	private static String TRSname;
	private static int TRSport = 59000;
	private static String language;
	private static HashMap<String, String> words = new HashMap<String, String>();
	private static HashMap<String, String> files = new HashMap<String, String>();
	
	public static void register() throws IOException, SRRERRException, SRGNOKException {
		String message = "SRG " + language + " " + TRSname + " " + TRSport;
		System.out.println("[TRS --> TCS] " + message);
		String response = sendUDPMessage(message+"\n").replaceAll("\n", "");
		System.out.println("[TRS <-- TCS] " + response);
		
		String[] split = response.split(" ");
		if(split[0] == "SRR"){
			throw new SRRERRException();
		}
		else if(split[0] == "SRR" && split[1] == "NOK"){
			throw new SRGNOKException();
		}
	}
	
	public static void unregister() throws IOException, SRRERRException {
		String message = "SUN " + language + " " + TRSname + " " + TRSport;
		System.out.println("[TRS --> TCS] " + message);
		String response = sendUDPMessage(message+"\n").replaceAll("\n", "");
		System.out.println("[TRS <-- TCS] " + response);
	}
	
	public static void loadTranslations() throws FileNotFoundException {
		Scanner scanner =  new Scanner(new File("text_translation.txt"));
		
		while(scanner.hasNext()){
			String foreignlanguage = scanner.next();
			String portuguese = scanner.next();
			words.put(foreignlanguage, portuguese);
		}
		
		scanner =  new Scanner(new File("file_translation.txt"));
		
		while(scanner.hasNext()){
			String foreignlanguage = scanner.next();
			String portuguese = scanner.next();
			files.put(foreignlanguage, portuguese);
		}
		
		scanner.close();
	}
	
	private static String sendUDPMessage(String message) throws IOException{
		InetAddress address = InetAddress.getByName(TCSname);

		DatagramSocket clientSocket = new DatagramSocket();
		byte[] sendData = new byte[message.length()];
		byte[] receiveData = new byte[1024];

    	java.util.Arrays.fill(receiveData, (byte) 0);

		sendData = message.getBytes();

		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, TCSport);
		clientSocket.send(sendPacket);

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	    clientSocket.receive(receivePacket);

	    String response = new String(receivePacket.getData());
	    response = response.substring(0, response.indexOf(0));

	    clientSocket.close();

	    return response;
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
	
	public static void main(String[] args) {
		try {
			TRSname = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		//Arguments
		language = args[0];
        for(int i = 1; i < args.length-1; i++){
            if(args[i].equals("-p")){
                TRSport = Integer.parseInt(args[i+1]);
            }
            if(args[i].equals("-n")){
                TCSname = args[i+1];
            }
            if(args[i].equals("-e")){
                TCSport = Integer.parseInt(args[i+1]);
            }
        }
        
    	try {
			loadTranslations();
		} catch (FileNotFoundException e1) {
			System.out.println(e1);
			System.exit(1);
		}
        
        ServerSocket socket;
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	try {
					unregister();
				} catch (IOException|SRRERRException e) {
					e.printStackTrace();
				}
            }
        });
        
        try{
        	register();
        	
			socket = new ServerSocket(TRSport);
            
            while (true) {
            	boolean t = true;
            	Socket connectionSocket = socket.accept();
                DataInputStream dis = new DataInputStream(new BufferedInputStream(connectionSocket.getInputStream()));
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(connectionSocket.getOutputStream()));
                
                byte[] byteData = receiveData(dis);
                String clientRequestMessage = new String(byteData).trim();
                System.out.println("[TRS <-- User] " + clientRequestMessage);
                
                String[] split = clientRequestMessage.split(" ");
                String clientData = "TRR ERR\n";
                
                if(split[0].equals("TRQ")){
                	if(split[1].equals("t")){
                		int numWords = Integer.parseInt(split[2]);
                		String translatedWords = "";
                		int i;
                		for(i = 3; i < split.length; i++){
                			String word = words.get(split[i]);
                			if(word != null){
                				translatedWords += " " + words.get(split[i]);
                			}
                			else break;
                		}
                		if(i >= split.length){
                			clientData = "TRR t " + numWords + translatedWords + "\n";
                		}
                		else{
                			clientData = "TRR NTA\n";
                		}
                	}
                	else if(split[1].equals("f")){
                		String filename = split[2];
                		int filesize = Integer.parseInt(split[3]);
                		
                		if(files.get(filename) != null){	
                			byte[] file = new byte[filesize];
                			byte[] buffer = null;
                			for(int i = 0; i < filesize; i++){
                				if(i%1024 == 0){
                					buffer = receiveData(dis);
                				}
                				file[i] = buffer[i%1024];
                			}
                			
                    		FileOutputStream fos = new FileOutputStream(filename);
                    		fos.write(file);
                    		fos.close();
                    		
                    		file=null;
                    		buffer=null;
                    		
                    		File sendFile = new File(files.get(filename));
                            
                            dos.write(("TRR f " + sendFile.getName() + " " + sendFile.length() + " ").getBytes());
                            dos.flush();
                            
                            byte[] fileBytes = Files.readAllBytes(sendFile.toPath());
                            dos.write(fileBytes);
                            dos.write("\n".getBytes());
                            dos.flush();
                            t=false;
                            fileBytes=null;
                            System.out.println("[TRS --> User] TRR f " + sendFile.getName() + " " + sendFile.length() + " ");
                		}
                		else{
                			clientData = "TRR NTA\n";
                		}
                		
                	}
                }

                if(t){
	                // Sending Response to Client
	                sendData(dos, clientData.getBytes());
	                System.out.println("[TRS --> User] " + clientData.replace("\n", ""));
                }
            }
			
			
        } catch (IOException|SRRERRException|SRGNOKException e){
        	e.printStackTrace();
        	System.exit(1);
        }
        
        
        
	}
}
