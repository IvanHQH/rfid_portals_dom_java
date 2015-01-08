
public class OrdenEsD {
	public int id;	
	public int orden_es_m_id;
	public String epc;
	public int quantity;	
	public String created_at;
	public String updated_at;
	//public String deleted_at;
	public String upc;
	
	public OrdenEsD(String epc,int quantity)
	{
		String datetime = Methods.getCurrentDateTime();
		this.id = 0;
		this.orden_es_m_id = 0;
		this.epc = epc;
		this.quantity = quantity;		
		this.updated_at = datetime;
		this.created_at = datetime;		
		//this.deleted_at = null;
		this.upc = EPC.EPCToUPC(epc);
	}
		
}
