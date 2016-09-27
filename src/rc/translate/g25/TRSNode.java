package rc.translate.g25;
import java.net.InetAddress;

public class TRSNode {
	private String language;
	private InetAddress address;
	private int port;
	private int TCSport;
	
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
	
	public void setPort(int porto) {
		port=porto;
		
	}
	public void setLanguage(String language) {
		this.language=language;
		
	}
	public void setAddress(InetAddress address) {
		this.address=address;
		
	}


}
