import java.util.ArrayList;

public class OrdenEsM {
	public int id;
	public int customer_id;
	public int client_id;
	public String folio;
	public int type;
	public String created_at;
	public String updated_at;
	public int warehouse_id;
	public ArrayList<OrdenEsD> orden_es_ds;
	public int pending;
	
	public OrdenEsM(int idClient,int idCustomer,String folio,int type,int idWarehouse,String dateTime)
	{
		this.id = 0;
		this.customer_id = idCustomer;
		this.folio = folio;
		this.type = type;
		this.created_at = dateTime;	
		this.updated_at = dateTime;
		this.pending = 1;
		this.warehouse_id = idWarehouse;
		this.client_id = idClient;
		orden_es_ds = new ArrayList<OrdenEsD>();
	}	
	
	public int sizeEpcsBatch()
	{
		return orden_es_ds.size();
	}
	
	public boolean addEPC(String epc)
	{
		if(containsEpc(epc) == false)
		{
			OrdenEsD ordenD = new OrdenEsD(epc,1);
			orden_es_ds.add(ordenD);
			return true;
		}
		return false;
	}
	
	private boolean containsEpc(String epc)
	{
		System.out.println(orden_es_ds.size());
		for(OrdenEsD orderD: orden_es_ds){
			//System.out.println(orderD.epc);
			if(orderD.epc.equals(epc))
				return true;
		}
		return false;
	}	
	
	
}
