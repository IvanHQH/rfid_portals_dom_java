import java.io.IOException;
import java.util.List;

import org.llrp.ltk.generated.enumerations.*;
import org.llrp.ltk.generated.messages.*;
import org.llrp.ltk.generated.parameters.*;
import org.llrp.ltk.net.*;
import org.llrp.ltk.types.*;


public class ReaderRFID implements LLRPEndpoint
{	
	private LLRPConnection reader;
	private static final int TIMEOUT_MS = 1000;
	private static final int ROSPEC_ID = 123;
	private String folio = "0";
	private BatchFolio batchFolio;	
	public List<TagReportData> tags;
	public static boolean sendEpcs;
	//public int limitTimeSpanConect = 1000000;
	public int limitTimeSpanConnect = 1000000;
	
	public ReaderRFID(String clientUrl)
	{
		//client of BatchFolio is static
		BatchFolio.setClient(new HttpClient(clientUrl));
	}
	
	public void setWriteAlwaysJsonEpcs(boolean value)
	{
		BatchFolio.writeAlwaysJsonUpc = value;
	}
	
	/* Build the ROSpec.
	 * An ROSpec specifies start and stop triggers,
	 * tag report fields, antennas, etc.
	 */
	private ROSpec buildROSpec()
	{ 	
		System.out.println("Building the ROSpec.");
		
		// Create a Reader Operation Spec (ROSpec).
		ROSpec roSpec = new ROSpec();
		
		roSpec.setPriority(new UnsignedByte(0));
		roSpec.setCurrentState(new ROSpecState(ROSpecState.Disabled));
		roSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		
		// Set up the ROBoundarySpec
		// This defines the start and stop triggers.
		ROBoundarySpec roBoundarySpec = new ROBoundarySpec();
		
		// Set the start trigger to null.
		// This means the ROSpec will start as soon as it is enabled.
		ROSpecStartTrigger startTrig = new ROSpecStartTrigger();
		startTrig.setROSpecStartTriggerType(new ROSpecStartTriggerType
				(ROSpecStartTriggerType.Null));
		roBoundarySpec.setROSpecStartTrigger(startTrig);
		
		// Set the stop trigger is null. This means the ROSpec
		// will keep running until an STOP_ROSPEC message is sent.
		ROSpecStopTrigger stopTrig = new ROSpecStopTrigger();
		stopTrig.setDurationTriggerValue(new UnsignedInteger(0));
		stopTrig.setROSpecStopTriggerType
			(new ROSpecStopTriggerType(ROSpecStopTriggerType.Null));
		roBoundarySpec.setROSpecStopTrigger(stopTrig);
		
		roSpec.setROBoundarySpec(roBoundarySpec);
		
		// Add an Antenna Inventory Spec (AISpec).
		AISpec aispec = new AISpec();
		
		// Set the AI stop trigger to null. This means that
		// the AI spec will run until the ROSpec stops.
		AISpecStopTrigger aiStopTrigger = new AISpecStopTrigger();
		aiStopTrigger.setAISpecStopTriggerType
			(new AISpecStopTriggerType(AISpecStopTriggerType.Null));
		aiStopTrigger.setDurationTrigger(new UnsignedInteger(0));
		aispec.setAISpecStopTrigger(aiStopTrigger);
		
		// Select which antenna ports we want to use.
		// Setting this property to zero means all antenna ports.
		UnsignedShortArray antennaIDs = new UnsignedShortArray();
		antennaIDs.add(new UnsignedShort(0));
		aispec.setAntennaIDs(antennaIDs);
		
		// Tell the reader that we're reading Gen2 tags.
		InventoryParameterSpec inventoryParam = new InventoryParameterSpec();
		inventoryParam.setProtocolID
			(new AirProtocols(AirProtocols.EPCGlobalClass1Gen2));
		inventoryParam.setInventoryParameterSpecID(new UnsignedShort(1));
		aispec.addToInventoryParameterSpecList(inventoryParam);
		
		roSpec.addToSpecParameterList(aispec);
		
		// Specify what type of tag reports we want
		// to receive and when we want to receive them.
		ROReportSpec roReportSpec = new ROReportSpec();
		// Receive a report every time a tag is read.
		roReportSpec.setROReportTrigger
			(new ROReportTriggerType
			(ROReportTriggerType.Upon_N_Tags_Or_End_Of_ROSpec));
		roReportSpec.setN(new UnsignedShort(1));
		TagReportContentSelector reportContent =
		new TagReportContentSelector();
		// Select which fields we want in the report.
		reportContent.setEnableAccessSpecID(new Bit(0));
		reportContent.setEnableAntennaID(new Bit(0));
		reportContent.setEnableChannelIndex(new Bit(0));
		reportContent.setEnableFirstSeenTimestamp(new Bit(0));
		reportContent.setEnableInventoryParameterSpecID(new Bit(0));
		reportContent.setEnableLastSeenTimestamp(new Bit(1));
		reportContent.setEnablePeakRSSI(new Bit(0));
		reportContent.setEnableROSpecID(new Bit(0));
		reportContent.setEnableSpecIndex(new Bit(0));
		reportContent.setEnableTagSeenCount(new Bit(0));
		roReportSpec.setTagReportContentSelector(reportContent);
		roSpec.setROReportSpec(roReportSpec);		
		
		return roSpec;
	}
	
	/*
	 * Add the ROSpec to the reader.
	 */
	private void addROSpec()
	{
		ADD_ROSPEC_RESPONSE response;
		
		ROSpec roSpec = buildROSpec();
		System.out.println("Adding the ROSpec.");
		try
		{
			ADD_ROSPEC roSpecMsg = new ADD_ROSPEC();
			roSpecMsg.setROSpec(roSpec);
			response = (ADD_ROSPEC_RESPONSE)
			reader.transact(roSpecMsg, TIMEOUT_MS);
			System.out.println(response.toXMLString());
			
			// Check if the we successfully added the ROSpec.
			StatusCode status = response.getLLRPStatus().getStatusCode();
			if (status.equals(new StatusCode("M_Success")))
			{
				System.out.println("Successfully added ROSpec.");
			}
			else
			{
				System.out.println("Error adding ROSpec.");
				System.exit(1);
			}
		}
		catch (Exception e)
		{
			System.out.println("Error adding ROSpec.");
			e.printStackTrace();
		}
	}
	
	/*
	 * Enable the ROSpec.
	 */
	private void enableROSpec()
	{
		ENABLE_ROSPEC_RESPONSE response;
		
		System.out.println("Enabling the ROSpec.");
		ENABLE_ROSPEC enable = new ENABLE_ROSPEC();
		enable.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		try
		{
			response = (ENABLE_ROSPEC_RESPONSE)
			reader.transact(enable, TIMEOUT_MS);
			System.out.println(response.toXMLString());
		}
		catch (Exception e)
		{
			System.out.println("Error enabling ROSpec.");
			e.printStackTrace();
		}
	}
	
	/*
	 * Start the ROSpec.
	 */
	private void startROSpec()
	{
		START_ROSPEC_RESPONSE response;
		System.out.println("Starting the ROSpec.");
		START_ROSPEC start = new START_ROSPEC();
		start.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		try
		{
			response = (START_ROSPEC_RESPONSE)
			reader.transact(start, TIMEOUT_MS);
			System.out.println(response.toXMLString());			
		}
		catch (Exception e)
		{
			System.out.println("Error deleting ROSpec.");
			e.printStackTrace();
		}
	}
	
	/*
	 *  Delete all ROSpecs from the reader.
	 */
	private void deleteROSpecs()
	{
		DELETE_ROSPEC_RESPONSE response;
		
		System.out.println("Deleting all ROSpecs.");
		DELETE_ROSPEC del = new DELETE_ROSPEC();
		// Use zero as the ROSpec ID.
		// This means delete all ROSpecs.
		del.setROSpecID(new UnsignedInteger(0));
		try
		{
			response = (DELETE_ROSPEC_RESPONSE)
			reader.transact(del, TIMEOUT_MS);
			System.out.println(response.toXMLString());
		}
		catch (Exception e)
		{
			System.out.println("Error deleting ROSpec.");
			e.printStackTrace();
		}
	}
	
	/*
	 * This function gets called asynchronously
	 * when a tag report is available.
	 */
	public void messageReceived(LLRPMessage message)
	{
		if (message.getTypeNum() == RO_ACCESS_REPORT.TYPENUM)
		{
			String epc;
			// The message received is an Access Report.
			RO_ACCESS_REPORT report = (RO_ACCESS_REPORT) message;
			// Get a list of the tags read.
			tags =report.getTagReportDataList();
			// Loop through the list and get the EPC of each tag.
			//Is new batch folio
			//this can start with readings or no readings			
			if(batchFolio == null)
			{
				batchFolio = new BatchFolio(folio,Methods.getCurrentDateTime(),
						1,Methods.idWarehouse,Methods.idCustomer,Methods.idClient);
			}
			for (TagReportData tag : tags)
			{			
				epc = tag.getEPCParameter().toString().substring(13, 37);		
				batchFolio.addEPC(epc);				
				//System.out.println(epc);
				//System.out.println(tag.getAntennaID().getAntennaID().toString());
				//System.out.println(tag.getLastSeenTimestampUTC());				
			}
		}		
	}		

	public void sendBatchFolio() throws IOException
	{		
		//batchFolio.NextId();
		if(batchFolio != null)
			if(batchFolio.sizeEpcsBatch() > 0)
			{
				if(Methods.version == 1){
					batchFolio.sendBacthEPCsVersion1();
					batchFolio = null;
				}
				else if(Methods.version == 4){
					batchFolio.sendBacthEPCsVersion4();
					batchFolio = null;				
				}
			}
	}
	
	/*
	 * This function gets called asynchronously(non-Javadoc)
	 * when an error occurs.
	 */
	public void errorOccured(String s)
	{
		System.out.println("An error occurred: " + s);
	}
	
	/* 
	 * Connect to the reader
	 */
	private void connect(String hostname)
	{
		// Create the reader object.
		reader = new LLRPConnector(this, hostname);
		
		// Try connecting to the reader.
		try
		{
			System.out.println("Connecting to the reader.");
			((LLRPConnector) reader).connect(limitTimeSpanConnect);
		}
		catch (LLRPConnectionAttemptFailedException e1)
		{
			e1.printStackTrace();
			System.exit(1);
		}
	}
	
	/*
	 *  Disconnect from the reader
	 */
	private void disconnect()
	{
		((LLRPConnector) reader).disconnect();
	}
	
	/*
	 *  Connect to the reader, setup the ROSpec
	 *  and run it.
	 */
	public void run(String hostname)
	{
		connect(hostname);
		deleteROSpecs();
		addROSpec();
		enableROSpec();
		startROSpec();
	}
	
	/* 
	 * Cleanup. Delete all ROSpecs
	 * and disconnect from the reader.
	 */
	public void stop()
	{
		deleteROSpecs();
		disconnect();
	}
	
	public boolean conectionServer() throws IOException
	{
		return BatchFolio.testConectionServer();
	}
}