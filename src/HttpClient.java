import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import java.net.URL;
import java.io.IOException;

import org.json.JSONObject;


public class HttpClient {
	
	private String url;
	
	public HttpClient(String url)
	{
		this.url = url;
	}
	
	public String sendJson(String endPoint,JSONObject json) 
			throws IOException
	{
		URL object = new URL(url+"/"+endPoint);
		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestMethod("POST");
		
		OutputStreamWriter wr= new OutputStreamWriter
				(con.getOutputStream());
		wr.write(json.toString());
		wr.flush();
		StringBuilder sb = new StringBuilder();  
		int HttpResult =con.getResponseCode(); 
		if(HttpResult ==HttpURLConnection.HTTP_OK)
		{
			String s = con.getResponseMessage();
		    BufferedReader br = new BufferedReader(
		    		new InputStreamReader(con.getInputStream(),"utf-8"));  
		    String line = null;  
		    while ((line = br.readLine()) != null) 
		    {  
		    	sb.append(line + "\n");  
		    }  
		    br.close();  
		    //System.out.println(""+sb.toString());  
		    return sb.toString();
		}else
		{
		    //System.out.println(con.getResponseMessage());
		    return con.getResponseMessage();
		}		
	}	
		
	public boolean testConection(String endPoint) throws IOException
	{
		URL object = new URL(url+"/"+endPoint);
		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestMethod("GET");
		
		StringBuilder sb = new StringBuilder();
		int HttpResult =con.getResponseCode(); 
		if(HttpResult ==HttpURLConnection.HTTP_OK)
		    return true; 
		else
			return false;
	}	
	
}
