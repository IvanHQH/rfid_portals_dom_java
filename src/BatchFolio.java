import java.awt.Toolkit;
import java.io.IOException;

import org.json.JSONObject;

import com.google.gson.Gson;

public class BatchFolio {
	
	private static HttpClient client;
	private OrdenEsM ordenM; 
	public static boolean writeAlwaysJsonUpc = false;
	
	public static void setClient(HttpClient pClient){client = pClient;}	
	
	public BatchFolio(String folio,String dateTime,int type,int idWarehouse,int idCustomer,int idClient)
	{
		ordenM = new OrdenEsM(idClient,idCustomer,folio,type,idWarehouse,dateTime);
	}
	
	public boolean addEPC(String epc)
	{
		return ordenM.addEPC(epc);
	}
	
	public int sizeEpcsBatch()
	{
		return ordenM.orden_es_ds.size();
	}		
	
	public void sendBacthEPCsVersion1() throws IOException
	{		
		JSONObject json = new JSONObject();
		json.put("client_id", Methods.idClient);
		//System.out.println(json.toString());
		String response = client.sendJson("/variables/get_var_read", json);
		if(response.equals("1\n")){
			ReaderRFID.sendEpcs = true;
			serverSetNotRead();
		}
		else if(ReaderRFID.sendEpcs == true)
		{
			Toolkit.getDefaultToolkit().beep();
			ReaderRFID.sendEpcs = false;
			if(ordenM.orden_es_ds.isEmpty() == false)
			{
				uploadOrderM();
			}
		}					
	}	
			
	public void sendBacthEPCsVersion4() throws IOException
	{		
		JSONObject json = new JSONObject();
		json.put("client_id", Methods.idClient);
		//System.out.println(json.toString());
		String response = client.sendJson("/variables/get_var_read", json);
		if(response.equals("1\n")){
			Toolkit.getDefaultToolkit().beep();
			ReaderRFID.sendEpcs = false;
			if(ordenM.orden_es_ds.isEmpty() == false)
			{			
				Gson gs = new Gson();		
				response = client.sendJson("/order_pending", json);
				if(response.equals("0\n")){
					uploadOrderM();			
				}else{
					//int idOrder = Integer.parseInt(response.replaceAll("\n", ""));
					updateOrderM();
				}
			}
		}
		else{
			serverSetNotRead();
		} 				
	}
	
	private void updateOrderM()
	{
		JSONObject json = new JSONObject();
		Gson gs = new Gson();
		String res;
		//json = new JSONObject(gs.toJson(ordenM));
		json.put("client_id", Methods.idClient);
		String response = new String();
		Toolkit.getDefaultToolkit().beep();
		try {
			res = client.sendJson("update_ordenesd", json);//delete and insert in orderm pending
		} catch (IOException e) {e.printStackTrace();}
		//AGREGAR CLIENT ID PARA OBTENER LA ORDERM CORREPONSIENTE
		for(OrdenEsD orden :ordenM.orden_es_ds)
		{
			json = new JSONObject(gs.toJson(orden));
			try {
				json.put("client_id", Methods.idClient);
				res = client.sendJson("ordenesd", json);
			} catch (IOException e) {e.printStackTrace();}	
		}				
	}
	
	private void uploadOrderM()
	{
		JSONObject json = new JSONObject();
		Gson gs = new Gson();
		json = new JSONObject(gs.toJson(ordenM));
		String response = new String();
		try {
			response = client.sendJson("ordenesm", json);
		} catch (IOException e) {e.printStackTrace();}
		if(response.equals("yes save\n"))
		{
			Toolkit.getDefaultToolkit().beep();
			for(OrdenEsD orden :ordenM.orden_es_ds)
			{
				json = new JSONObject(gs.toJson(orden));
				try {
					json.put("client_id", Methods.idClient);
					client.sendJson("ordenesd", json);
				} catch (IOException e) {e.printStackTrace();}	
			}			
		}				
	}
	
	private void serverSetNotRead()
	{
		JSONObject json = new JSONObject();
		String msj;
		try {
			json = new JSONObject();
			json.put("client_id", Methods.idClient);					
			msj = client.sendJson("/variables/set_no_read", json);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public static boolean testConectionServer() throws IOException
	{
		return client.testConection("test_conection");
	}
	
}
