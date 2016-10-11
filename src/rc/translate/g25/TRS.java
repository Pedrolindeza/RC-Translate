package rc.translate.g25;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.DataOutputStream;


public class TRS {
	private static DatagramSocket clientSocket;
    private static byte[] sendData = new byte[1024];
    private static byte[] receiveData = new byte[1024];

    
	public static void main(String args[]) throws Exception
	   {
	   	  clientSocket= new DatagramSocket();
	      HashMap<String,String> translations = new HashMap<>();
	      fillMap(translations);		  
	      int TRSport = 59000;
	      InetAddress IPAddress = InetAddress.getLocalHost();
	      InetAddress TCSIP = InetAddress.getLocalHost();
	      int TCSport = 58025;
	      String language=args[0];
	      
	      	for(int i=1; i<args.length -1;i++){
	      		System.out.println(args[i]);
	      		if(args[i].equals("-p")){
	      			TRSport=Integer.parseInt(args[++i]);
	      		}
	      		if(args[i].equals("-n")){
	      			TCSIP=InetAddress.getByName(args[++i]);
	      		}
	      		if(args[i].equals("-e")){
	      			TCSport=Integer.parseInt(args[++i]);
	      		}
	      		
	      	}
	      	
	      register(language, TCSIP,IPAddress, TRSport,TCSport);
	      String confirmation=receiveConfirmation();
	      String[] msg = confirmation.split(" ");
	      
	      if (msg[1].equals("OK\n")){
	    	  ServerSocket TCPsocket = new ServerSocket(TRSport);
	    	  while(true){
	    		  
	    		  Socket connectTCP = TCPsocket.accept();
	    		  BufferedReader message = new BufferedReader(new InputStreamReader(connectTCP.getInputStream()));
	    		  DataOutputStream toclient = new DataOutputStream(connectTCP.getOutputStream());
	    		  String tobesplit = message.readLine();
	    		  
	    		  String[] splitted= tobesplit.split(" ");

	    		  if (splitted[0].equals("exit")){ TCPsocket.close(); break; }

	    		  if(splitted[0].equals("TRQ")){
	    			  if (splitted[1].equals("t")){
	    				  System.out.println(translations);
	    				  toclient.writeBytes(translate(splitted,translations));

	    			  }
	    			  
	    			  else if (splitted[1].equals("f")){
                            
	    			  		int size = Integer.parseInt(splitted[3]);
	    			  		File file = new File(splitted[2]);
	    			  		FileOutputStream fos = new FileOutputStream(file);
	    			  		fos.write(splitted[4].getBytes());
	    			  		

	    			  		 if(translations.containsKey(splitted[2])){
		  						String fileToSend = translations.get(splitted[2]);
	    			  			 System.out.println("Translation for file "+fileToSend);
		  						 sendFile(connectTCP,fileToSend);
	    				  
		  					}
		  					else{ System.out.println("ERROR: No such file");}
	    			  		
	    			  }

	    			  else{ System.out.println("ERROR Invalid Command after TRQ");}
	    			  
	    		  }
	    		  else{ System.out.println("ERROR: Invalid Command");}
	    	  }
	    	  
	      }
	      else{	
	    	  System.out.println("TCS nao deu o OK");	  
	      }
	      System.out.println("FROM SERVER:" + confirmation);
	      clientSocket.close();
	   }


public static void register(String language,InetAddress TCSIP, InetAddress IPAddress, int TRSport,int TCSport){
	
    String tosend= new String();
    tosend="SRG "+ language +" "+IPAddress.getHostAddress()+" "+TRSport+"\n";
    System.out.println(tosend);
    sendData = tosend.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, TCSIP, TCSport);
    try {
		clientSocket.send(sendPacket);
	} catch (IOException e) {
		System.out.println("Error sending message to TCS");
	}
    
}


public static void fillMap (Map<String,String>translations){
	Scanner scanner=null;
	try{
		  scanner = new Scanner(new File("translations.txt"));
	      }
	      catch(Exception e){ System.out.println("ERROR: File not found");}
	while(scanner.hasNext()){
	  String foreignlanguage = scanner.next();
	  String portuguese = scanner.next();
	  translations.put(foreignlanguage, portuguese);
	}
}


public static String receiveConfirmation (){

	java.util.Arrays.fill(receiveData, (byte) 0);
	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	
	try {
		clientSocket.receive(receivePacket);
	} catch (IOException e) {
		System.out.println("Error receiving confirmation from TCS");
		e.printStackTrace();
	}
	String modifiedSentence = new String(receivePacket.getData());
	modifiedSentence = modifiedSentence.substring(0, modifiedSentence.indexOf(0));
	
	return modifiedSentence;
}
public static String translate(String[] splitted, HashMap<String,String> translations){
	
	int numwords = Integer.parseInt(splitted[2]);
	String toreturn = "TRR t "+ numwords;
	int count=3;
	  while(count<numwords+3){
		  String word= splitted[count];
		  
		  if(translations.containsKey(word)){
		  	toreturn+=" " +translations.get(word);
		  }
		  count++;
	
	  }
	  
	  toreturn+="\n";
	  return toreturn;
}

public static void sendFile(Socket connectTCP, String filename) throws IOException{
	File file = new File(filename);
	FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis); 
        OutputStream os = connectTCP.getOutputStream();
        
        byte[] contents;
        long fileLength = file.length(); 
       	long current = 0;
        String toWrite = "TRR d "+filename+" "+Long.toString(fileLength)+" ";
        os.write(toWrite.getBytes()); 
        while(current!=fileLength){ 
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
        
        os.flush(); 
        connectTCP.close();
	
}

}
