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
	private static JLabel label,label2;
	private static Config conf;
	private static ReaderRFID reader;
	private static int secondsReadFolio = 5000;
	private static int secondsNotReadFolio = 2000;
	private static JTable table;
	
	public static void main(String[] argumentos) throws InterruptedException, IOException{						
		initializeWindow();
		label.setText("Comenzando ... ");
		conf = Methods.readJsonConfig();	
		Methods.idClient = conf.getidClient();
		Methods.idWarehouse = conf.getidWarehouse();
		Methods.version = conf.getVersion();
		if(conf != null)
			label.setText(conf.getIpReader()+" - "+conf.getServerName());
		else
			label.setText("configuración no cargada");
		reader = new ReaderRFID(conf.getServerName());
		if(reader.conectionServer())
			label2.setText("Servidor conectado");
		else
			label2.setText("Servidor no conectado");
		label.setText("Lector OK ");
		if(conf != null)
		{
			if(conf.getIpReader().length() == 0 || conf.getServerName().length() == 0
					|| conf.getidWarehouse() == 0)
				label.setText("Archivo de configuración incompleto... ");
			else
			{
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
		label2 = new JLabel();	
		win.getContentPane().add(label2);				
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
			if(reader.tags.size() == 0){
				label.setText("No se encontraron tags ... ");	
			}
			else{
				label.setText("Tags leídas... ");
			}
			reader.sendBatchFolio();
			label.setText("Termino lectura ... ");
		}
	}
	
}
