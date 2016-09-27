package rc.translate.g25;
import java.net.InetAddress;

public class TRSNode {
	private String language;
	private InetAddress address;
	private String name;
	private int port;
	
	public TRSNode(String language, InetAddress address, String name,int port){
		this.language = language;
		this.address = address;
		this.name=name;
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
	public String getName(){
		return this.name;
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
	public void setName(String name) {
		this.name=name;
		
	}
}
