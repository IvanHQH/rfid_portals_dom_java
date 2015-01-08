import java.awt.Toolkit;
import java.io.IOException;

import org.json.JSONObject;

import com.google.gson.Gson;

public class BatchFolio {
	
	private static HttpClient client;
	private OrdenEsM ordenM; 
	public static boolean writeAlwaysJsonUpc = false;
	
	public static void setClient(HttpClient pClient){client = pClient;}	
	
	public BatchFolio(String folio,String dateTime,int type,int idWarehouse,int idCustomer)
	{
		ordenM = new OrdenEsM(idCustomer,folio,type,idWarehouse,dateTime);
	}
	
	public boolean addEPC(String epc)
	{
		return ordenM.addEPC(epc);
	}
	
	public int sizeEpcsBatch()
	{
		return ordenM.orden_es_ds.size();
	}		
	
	public void sendBacthEPCs() throws IOException
	{		
		JSONObject json = new JSONObject();
		json.put("idCustomer", 1);
		System.out.println(json.toString());
		String response = client.sendJson("/variables/get_var_read", json);
		if(response.equals("1\n"))
		{
			ReaderRFID.sendEpcs = true;
			serverSetNotRead();
		}
		else if(ReaderRFID.sendEpcs == true)
		{
			Toolkit.getDefaultToolkit().beep();
			ReaderRFID.sendEpcs = false;
			if(ordenM.orden_es_ds.isEmpty() == false)
			{							
				Gson gs = new Gson();
				json = new JSONObject(gs.toJson(ordenM));
				try {System.out.println(json.toString());
					response = client.sendJson("ordenesm", json);
				} catch (IOException e) {e.printStackTrace();}
				if(response.equals("yes save\n"))
				{
					for(OrdenEsD orden :ordenM.orden_es_ds)
					{
						json = new JSONObject(gs.toJson(orden));
						try {System.out.println(json.toString());
							client.sendJson("ordenesd", json);
						} catch (IOException e) {e.printStackTrace();}	
					}											
					writeJsonTags(ordenM);
				}
			}
		}					
	}	
	
	private void serverSetNotRead()
	{
		JSONObject json = new JSONObject();
		try {
			json = new JSONObject();
			json.put("idCustomer", 1);					
			System.out.println(json.toString());
			client.sendJson("/variables/set_no_read", json);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private void writeJsonTags(OrdenEsM ordenM)
	{
		Gson gs = new Gson();		
		JSONObject json = new JSONObject(gs.toJson(ordenM));
		try {
			System.out.println(json.toString());
			client.sendJson("writeJsonTags", json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*public void CargaConstantes()
	{
		ordenM.CargaConstantes();
	}*/
	
}
