import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;
import com.google.gson.Gson;

public class Config {
	private String ipReader;
	public String getIpReader()
		{return ipReader;}
	public void setIpReader(String ipReader)
		{this.ipReader = ipReader;}	
	
	
	private String serverName;
	public String getServerName()
		{return serverName;}
	public void setServerName(String serverName)
		{this.serverName = serverName;}
	
	
	private int idWarehouse;
	public int getidWarehouse()
		{return idWarehouse;}
	public void setIdWarehouse(int idWarehouse)
		{this.idWarehouse = idWarehouse;}	
	
	
	private int idClient;
	public int getidClient()
		{return idClient;}
	public void setIdClient(int idClient)
		{this.idClient = idClient;}
	
	
	private int version;
	public int getVersion()
		{return version;}
	public void setVersion(int version)
		{this.version = version;}
	
	public void createJsonConfig()
	{
		Gson gs = new Gson();
		JSONObject json = new JSONObject(gs.toJson(this));
		try {
			 
			FileWriter file = new FileWriter("config.json");
			file.write(json.toString());
			file.flush();
			file.close();
 
		} catch (IOException e) {
			
		}		
	}	
	
}
