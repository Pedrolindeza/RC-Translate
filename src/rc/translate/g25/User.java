package rc.translate.g25;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import rc.translate.g25.exception.*;

public class User {
	private static String TCSname = "127.0.0.1";
	private static int TCSport = 58025;
	private static ArrayList<String> languagesCache = null;

	private static ArrayList<String> getLanguages() throws IOException{
		ArrayList<String> languages = new ArrayList<String>();

		String response = sendUDPMessage("ULQ" + "\n");

		String[] split = response.replace("\n","").split(" ");
		if(split[0].equals("ULR")){
			for(int i = 2; i < split.length; i++)
				languages.add(split[i]);
		}

		languagesCache = languages;

		return languages;
	}

	private static TRSNode getTRSNode(String language) throws IOException, UNREOFException, UNRERRException{
		String response = sendUDPMessage("UNQ " + language + "\n");
		TRSNode node = null;

		String[] split = response.replace("\n","").split(" ");

		if(split[0].equals("UNR") && split.length == 3){
			node = new TRSNode(language, InetAddress.getByName(split[1]), Integer.parseInt(split[2]));
		}
		if(split[0].equals("UNR") && split.length > 1 && split[1].equals("EOF")){
			throw new UNREOFException();
		}
		if(split[0].equals("UNR") && split.length > 1 && split[1].equals("ERR")){
			throw new UNRERRException();
		}

		return node;
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
	
	private static String[] translateWords(String[] words, TRSNode node) throws TRRException, IOException, TRRNTAException, TRRERRException{
		
		String response = node.sendTCPMessage("TRQ t " + words.length + " " + implode(" ", words) + "\n");
		String[] split = response.replaceAll("\n", "").split(" ");
		
		if(!split[0].equals("TRR")){
			throw new TRRException(split[0]);
		}
		
		if(split[1].equals("NTA")){
			throw new TRRNTAException();
		}
		
		if(split[1].equals("ERR")){
			throw new TRRERRException();
		}
		
		String[] result = new String[split.length-3];
		
		for(int j = 0; j < split.length-3; j++){
			result[j] = split[j+3];
		}
		
		return result;
	}
	
	private static String translateFile(String filepath, TRSNode node) throws IOException, TRRERRException, TRRNTAException{
		File f = new File(filepath);
		
		if(!f.exists() || f.isDirectory()){
			throw new IOException("Invalid file name");
		}
		
		String result = node.sendFile(filepath);
		return result;
	}
	
	private static String implode(String delimeter, String[] array){
		String result = "";
		for(int i = 0; i < array.length; i++){
			result += array[i];
			if(i < array.length - 1){
				result += delimeter;
			}
		}
		return result;
	}

	public static void main(String args[]) throws InterruptedException, IOException {
        for(int i = 0; i < args.length-1; i++){
            if(args[i].equals("-n")){
                TCSname = args[i+1];
            }
            if(args[i].equals("-p")){
                TCSport = Integer.parseInt(args[i+1]);
            }
        }

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        while(true){
        	String input = stdin.readLine();
        	final String[] split = input.split(" ");

        	if(split[0].equals("list")){
        		Thread thread = new Thread(new Runnable() {
        		    public void run() {
						try {
	                		ArrayList<String> languages = getLanguages();
	                		for(int i = 0; i < languages.size(); i++){
	                			System.out.println(i+1 + "- " + languages.get(i));
	                		}
						} catch (IOException e) {
							System.out.println("LIST: Error fetching languages");
							e.printStackTrace();
						}
        		    }
        		});

        		thread.start();
        		thread.join(3000);

        		if(!thread.getState().equals(Thread.State.TERMINATED)){
        			thread.interrupt();
            		System.out.println("LIST: timeout");
        		}

        	}
        	
        	else if(split[0].equals("request")){
        		if(split.length < 4){
        			System.out.println("REQUEST: Arguments error");
        		}
        		else if(languagesCache == null){
        			System.out.println("REQUEST: Languages have not been fetched yet, type 'list' to fetch them");
        		}
        		else{
        			
        			
            		Thread thread = new Thread(new Runnable() {

            		    public void run() {
            		    	try {
            		    		String language = languagesCache.get(Integer.parseInt(split[1])-1);
            		    		
            		    		if(!split[2].equals("t") && !split[2].equals("f")){
            		    			System.out.println("REQUEST: Second argument is expected to be 't' or 'f'");
	            		    		return;
            		    		}
            		    		
								TRSNode node = User.getTRSNode(language);
			        			System.out.println(node.getAddress().getHostAddress() + " " + node.getPort());
			        			
			        			//Text
			        			if(split[2].equals("t")){
				        			String[] words = new String[split.length-3];
				        			for(int j = 0; j < split.length - 3; j++){
				        				words[j] = split[j+3];
				        			}
				        			
				        			String[] translatedWords = translateWords(words, node);
				        			System.out.println(node.getAddress().getHostAddress() + ":" + implode(" ", translatedWords));
			        			}
			        			//Image
			        			else{
			        				String filePath = split[3];
			        				
			        				File file = new File(filePath);
			        				System.out.println(file.length() + " Bytes to transmit");
			        					
			        				String newFile = translateFile(filePath, node);
			        				
			        				System.out.println("reveiced file " + newFile + " - " + (new File(newFile)).length() + " Bytes");
			        			}
			        			
			        			
			        			
            		    	} catch (NumberFormatException|IndexOutOfBoundsException e){
								System.out.println("REQUEST: First argument is expected to be a valid number");
							} catch (java.net.SocketException e) {
								System.out.println("REQUEST: TCP Socket Error");
								e.printStackTrace();
							} catch (IOException e) {
								System.out.println("REQUEST: " + e);
								e.printStackTrace();
							} catch (TRRException e) {
								System.out.println("REQUEST: Response header error, expected 'TRR', received '" + e.getInput() + "'");
							} catch (UNREOFException e) {
								System.out.println("REQUEST: Invalid language");
								e.printStackTrace();
							} catch (UNRERRException e) {
								System.out.println("REQUEST: Invalid request");
							} catch (TRRERRException e) {
								System.out.println("REQUEST: TCS sent TRR ERR");
							} catch (TRRNTAException e) {
								System.out.println("REQUEST: No translation available");
							}
            		    }
            		});

            		thread.start();
            		thread.join(3000);

            		if(!thread.getState().equals(Thread.State.TERMINATED)){
            			thread.interrupt();
                		System.out.println("REQUEST: timeout");
            		}
        		}
        	}
        	else if(split[0].equals("exit")){
        		break;
        	}
        	else{
        		System.out.println("Unknown command");
        	}
        }

        System.exit(0);
	}
}
