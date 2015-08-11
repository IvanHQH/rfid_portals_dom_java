import java.awt.Toolkit;
import java.io.IOException;

import org.json.JSONObject;

import com.google.gson.Gson;

public class BatchFolio {
	
	private static HttpClient client;
	private OrdenEsM ordenM; 
	public static boolean writeAlwaysJsonUpc = false;
	public static boolean clearReads = false;
	
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
				
	public boolean sendBacthEPCsVersion4() throws IOException
	{		
		JSONObject json = new JSONObject();
		json.put("client_id", Methods.idClient);
		String response = client.sendJson("/variables/get_var_read", json);
		boolean res = false;
		if(response.equals("1\n")){
			Toolkit.getDefaultToolkit().beep();
			ReaderRFID.sendEpcs = false;
			if(ordenM.orden_es_ds.isEmpty() == false)
			{			
				Gson gs = new Gson();		
				response = client.sendJson("order_pending", json);
				if(response.equals("1\n")){
					res = updateOrderM();		
				}			
			}
		}
		else{
			serverSetNotRead();
			clearReads = true;
		}
		return res;
	}
	
	private boolean updateOrderM()
	{
		JSONObject json = new JSONObject();
		Gson gs = new Gson();
		String res;
		String response = new String();
		Toolkit.getDefaultToolkit().beep();
		boolean resb = false;
		json = new JSONObject(gs.toJson(ordenM));
		try {
			res = client.sendJson("update_ordenesd_v4", json);//delete and insert in orderm pending
			//AGREGAR CLIENT ID PARA OBTENER LA ORDERM CORREPONSIENTE
			if(res.equals("1\n")){
				resb = true;
			}
		}catch (IOException e) {e.printStackTrace();}					
		return resb;			
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
