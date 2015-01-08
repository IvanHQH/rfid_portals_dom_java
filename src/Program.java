import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.llrp.ltk.generated.parameters.TagReportData;

public class Program {
	
	private static JFrame win;
	private static JLabel label;
	private static Config conf;
	private static ReaderRFID reader;
	private static int secondsReadFolio = 10000;
	private static int secondsNotReadFolio = 2000;
	private static JTable table;
	
	public static void main(String[] argumentos) throws InterruptedException, IOException{						
		/*String upc;
		upc = EPC.EPCToUPC("0dfa25f20dfa25f20dfa25f2");
		upc = EPC.EPCToUPC("e2006bf300006bf300006bf3");
		upc = "";*/
		initializeWindow();
		conf = Methods.readJsonConfig();		
		reader = new ReaderRFID(conf.getServerName());
		//reader.setWriteAlwaysJsonEpcs(true);
		if(conf == null)
			label.setText("No se encontro archivo configuración ... ");
		else
		{
			if(conf.getIpReader().length() == 0 || conf.getServerName().length() == 0
					|| conf.getidCustomer() == 0 || conf.getidWarehouse() == 0)
				label.setText("Archivo de configuración incompleto... ");
			else
			{
				Methods.idCustomer = conf.getidCustomer();
				Methods.idWarehouse = conf.getidWarehouse();
				startReads();
			}
		}
	}
	
	private static void initializeWindow(){
		win = new JFrame("RFID");
		win.setSize(200, 100);
		win.setResizable(false);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		win.setLocation((dim.width-win.getSize().width)/2, (dim.height-win.getSize().height)/2);
		win.getContentPane().setLayout(new FlowLayout());
		win.setVisible(true);
		win.setDefaultCloseOperation(win.EXIT_ON_CLOSE);		
		label = new JLabel();	
		win.getContentPane().add(label);	
		
		/*String[] columnNames = {"Cont","EPC"};
		
		Object[][] data = {{"abc","1"},{"def","2"}};	
		
		table = new JTable(data, columnNames);
		win.getContentPane().add(table);
		
		DefaultTableModel modelo =  (DefaultTableModel) table.getModel();
		modelo.addRow(new Object[]{"ghi","jkl"});
		table.setModel(modelo);*/
		//Object[][] data2 = {{"abc","3"},{"def","4"}};	
		
		//table = new JTable(data2, columnNames);		
		//win.dispose();
		//win.doLayout();
		//win.repaint();
				
	}
	
	private static void startReads() throws InterruptedException, IOException
	{
		label.setText("Inicia lectura ... ");			
		reader.run(conf.getIpReader());
		while(true){
			label.setText("Leeyendo tags ... ");
			if(reader.sendEpcs == true)
				Thread.sleep(secondsNotReadFolio);
			else
				Thread.sleep(secondsReadFolio);
			if(reader.tags.size() == 0)
			{
				label.setText("No se encontraron tags ... ");	
			}
			else
			{
				label.setText("Tags leídas... ");
				//label.setText("");
				/*label.setText("");
				for (TagReportData tag : reader.tags)
				{			
					label.setText(label.getText() + "\n" + 
							tag.getEPCParameter().toString().substring(13, 37));	
				}*/
			}
			label.setText("Termino lectura ... ");			
			reader.sendBatchFolio();	
		}
	}
	
}
