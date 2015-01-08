import java.util.ArrayList;


public class OrdenEsM {
	public int id;
	public int customer_id;
	public String folio;
	public int type;
	public String created_at;
	public String updated_at;
	public int warehouse_id;
	public ArrayList<OrdenEsD> orden_es_ds;
	public int pending;
	
	public OrdenEsM(int idCustomer,String folio,int type,int idWarehouse,String dateTime)
	{
		this.id = 0;
		this.customer_id = idCustomer;
		this.folio = folio;
		this.type = type;
		this.created_at = dateTime;	
		this.updated_at = dateTime;
		this.pending = 1;
		this.warehouse_id = idWarehouse;
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
			System.out.println(epc);
			return true;
		}
		return false;
	}
	
	private boolean containsEpc(String epc)
	{
		for(OrdenEsD orderD: orden_es_ds){
			if(orderD.epc.equals(epc))
				return true;
		}
		return false;
	}	
	
	/*public void CargaConstantes()
	{
		addEPC("30342848A80AA7C000000051");
		addEPC("30342848A80AC0400000024E");
		addEPC("30342848A80AB52000000065");
	}*/
	
}
