import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.llrp.ltk.generated.parameters.TagReportData;

public class Program {
	private static JFrame win;
	private static JLabel label,label2;
	private static Config conf;
	private static ReaderRFID reader;
	private static int secondsReadFolioV1 = 5000;
	private static int secondsNotReadFolioV1 = 2000;
	private static int secondsReadFolioV4 = 3000;
	private static int secondsNotReadFolioV4 = 2000;	
	private static JTable table;
	private static JButton buttonConfig;
	private static JPanel panelMain;
	
	//Frame Config
	private static JPanel panelConf;
	private static JFrame winConf;		
	private static JPanel panel;
	
	private static JLabel lblUrl; 
	private static JLabel lblClient;
	private static JLabel lblUseMode;
	private static JLabel lblWarehouse;
	private static JLabel lblIpReader;
	
	private static JTextField txtUrl; 
	private static JTextField txtIdClient;
	private static JTextField txtUseMode;
	private static JTextField txtIdWarehouse;
	private static JTextField txtIpReader;
	
	private static JButton bttnSaveConfig;
	//End Frame Config
	
	public static void main(String[] args) throws InterruptedException, IOException{
		initializeFrames();
		label.setText("Comenzando ... ");
		conf = Methods.readJsonConfig();	
		Methods.idClient = conf.getidClient();
		Methods.idWarehouse = conf.getidWarehouse();
		Methods.version = conf.getVersion();
		if(conf != null){
			label.setText(conf.getIpReader());
			label2.setText(conf.getServerName());
		}
		else
			label.setText("configuración no cargada");
		reader = new ReaderRFID(conf.getServerName());
		if(reader.conectionServer())
			label2.setText("Servidor conectado");
		else
			label2.setText("Servidor no conectado");
		label.setText("Lector OK ");
		if(conf != null){
			if(conf.getIpReader().length() == 0 || conf.getServerName().length() == 0
					|| conf.getidWarehouse() == 0)
				label.setText("Archivo de configuración incompleto... ");
			else{
				Methods.idWarehouse = conf.getidWarehouse();
				startReads();
			}
		}
	}

	private static void initializeFrames(){
		win = new JFrame("RFID");
		win.setSize(200, 140);
		win.setResizable(false);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		win.setLocation((dim.width-win.getSize().width)/2, (dim.height-win.getSize().height)/2);
		win.getContentPane().setLayout(new FlowLayout());
		
		win.setDefaultCloseOperation(win.EXIT_ON_CLOSE);		
		
		panelMain = new JPanel();
		win.add(panelMain);		
		panelMain.setLayout(new GridLayout(3, 1));
		
		label = new JLabel();	
		label.setBounds(5, 5, 100, 25);
		panelMain.add(label);
		
		label2 = new JLabel();	
		panelMain.add(label2);
		label.setBounds(5, 40, 100, 25);		
		
		buttonConfig =  new JButton("Config");
		buttonConfig.setBounds(5, 75, 30, 35);
		panelMain.add(buttonConfig);
		
		//win.setBackground(Color.WHITE);
		panelMain.setBackground(Color.WHITE);
		
		win.setVisible(true);
		
		buttonConfig.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			initializeFrameConfig();
		}          
		});	
	}
	
	private static void initializeFrameConfig(){
		int xini = 10,yini = 10;
		int wtxt = 160,htxt = 25;
		int wlbl = 100,hlbl = 25;		
		
		winConf = new JFrame("Config");
		winConf.setSize(300, 250);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();		
		winConf.setLocation((dim.width-win.getSize().width)/2, (dim.height-win.getSize().height)/2);		

		panelConf = new JPanel();
		winConf.add(panelConf);				
		
		panelConf.setLayout(null);

		lblUrl = new JLabel("URL");
		lblUrl.setBounds(xini,yini,wlbl,hlbl);			
		txtUrl = new JTextField(conf.getServerName());
		txtUrl.setBounds(xini + wlbl + 10,yini,wtxt,htxt);
		panelConf.add(lblUrl);
		panelConf.add(txtUrl);
		
		lblClient = new JLabel("Id Client");
		lblClient.setBounds(xini,yini+hlbl+10,wlbl,hlbl);				
		txtIdClient = new JTextField(Integer.toString(conf.getidClient()));
		txtIdClient.setBounds(xini + wlbl + 10,yini + htxt + 10 ,wtxt,htxt);	
		panelConf.add(lblClient);
		panelConf.add(txtIdClient);			
		
		lblUseMode = new JLabel("Use Mode");
		lblUseMode.setBounds(xini,yini+2 *(hlbl+10),wlbl,hlbl);				
		txtUseMode = new JTextField(Integer.toString(conf.getVersion()));
		txtUseMode.setBounds(xini + wlbl + 10,yini + 2 *(htxt + 10) ,wtxt,htxt);		
		panelConf.add(lblUseMode);
		panelConf.add(txtUseMode);	
		
		lblWarehouse = new JLabel("Id Warehouse");
		lblWarehouse.setBounds(xini,yini+3 *(hlbl+10),wlbl,hlbl);				
		txtIdWarehouse = new JTextField(Integer.toString(conf.getidWarehouse()));
		txtIdWarehouse.setBounds(xini + wlbl + 10,yini + 3 *(htxt + 10) ,wtxt,htxt);		
		panelConf.add(lblWarehouse);
		panelConf.add(txtIdWarehouse);		
		
		lblIpReader = new JLabel("Ip Reader");
		lblIpReader.setBounds(xini,yini+4 *(hlbl+10),wlbl,hlbl);				
		txtIpReader = new JTextField(conf.getIpReader());
		txtIpReader.setBounds(xini + wlbl + 10,yini + 4 *(htxt + 10) ,wtxt,htxt);		
		panelConf.add(lblIpReader);
		panelConf.add(txtIpReader);		
		
		bttnSaveConfig = new JButton("Guardar");
		bttnSaveConfig.setBounds(xini, yini+5*(htxt+10), 80, 25);
		panelConf.add(bttnSaveConfig);		
		
		bttnSaveConfig.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			try{
				conf.setIdClient(Integer.parseInt(txtIdClient.getText()));
				conf.setIdWarehouse(Integer.parseInt(txtIdWarehouse.getText()));
				conf.setVersion(Integer.parseInt(txtUseMode.getText()));
				conf.setServerName(txtUrl.getText());
				conf.setIpReader(txtIpReader.getText());
				
				conf.createJsonConfig();		
				System.exit(0);
			}catch(Exception exc){
				JOptionPane.showMessageDialog(null, "Error al guardar configuración");
			}
		}          
		});					
		winConf.setVisible(true);			
	}	
	
	private static void startReads() throws InterruptedException, IOException
	{
		label.setText("Inicia lectura ... ");			
		reader.run(conf.getIpReader());
		while(true){
			label.setText("Leeyendo tags ... ");
			if(reader.sendEpcs == true){
				if(Methods.version == 1)
					Thread.sleep(secondsNotReadFolioV1);
				else if(Methods.version == 4)
					Thread.sleep(secondsNotReadFolioV4);
			}
			else{
				if(Methods.version == 1)
					Thread.sleep(secondsReadFolioV1);
				else if(Methods.version == 4)
					Thread.sleep(secondsReadFolioV4);				
			}
			if(reader.tags.size() == 0)
				label.setText("No se encontraron tags ... ");	
			else
				label.setText("Tags leídas... ");
			if(reader.sendBatchFolio())
				panelMain.setBackground(Color.YELLOW);
			else
				panelMain.setBackground(Color.WHITE);
			label.setText("Termino lectura ... ");
		}
	}	
	
}
