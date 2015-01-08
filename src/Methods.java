import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.google.gson.Gson;

public class Methods {
	
	public static int idCustomer;
	
	public static int idWarehouse;
	/*
	 * returns the current date and time format yyyy-MM-dd hh:mm:ss
	 * yyyy-mm-dd hh:mm:ss format to MySQL
	 */
    public static String getCurrentDateTime() {
        return getCurrentDate() + " " + getCurrentTime();
    }	
	
	/*
	 * returns the current date format yyyy-MM-dd
	 */
    public static String getCurrentDate() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dt =format.format(now); 
        return dt;
    }

	/*
	 * returns the current time format hh:mm:ss
	 */
    public static String getCurrentTime() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        return format.format(now);
    }        
    
    public static byte[] GetBytes(long value) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteStream);
        try {
            stream.writeLong(value);
        } catch (IOException e) {
            return new byte[4];
        }
        return byteStream.toByteArray();
    }
    
    public static String toBinary( byte b )
    {
        StringBuilder sb = new StringBuilder(Byte.SIZE);        
        sb.append((b << 0 % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }    
    
	public static Config readJsonConfig()
	{
		Config conf = null;
		Gson gs = new Gson();
		try {
			FileReader fr = new FileReader ("config.json");
			BufferedReader br = new BufferedReader(fr);

			String jsStr = "";
			String line = "";
			while((line = br.readLine())!=null)
				jsStr += line;
			conf= gs.fromJson(jsStr, Config.class);
			jsStr = "";
 
		} catch (IOException e) {
			//manejar error
		}		
		return conf;
	}
	
    /*	
	public void createJson()
	{
		Gson gs = new Gson();
		JSONObject json = new JSONObject(gs.toJson(this));
		try {
			 
			FileWriter file = new FileWriter("config.json");
			file.write(json.toString());
			file.flush();
			file.close();
 
		} catch (IOException e) {
			//manejar error
		}		
	}
	*/	
	
}
