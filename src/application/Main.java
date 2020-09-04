package application;
	

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.stage.Stage;


public class Main extends Application {
	//https://stackoverflow.com/questions/55874952/javafx-propertyvaluefactory-is-not-able-to-retrieve-property
	@SuppressWarnings("unused")
	private List<ValidFile> datein = new ArrayList<>();
	
	public static volatile ObservableList<ValidFile> tableData;
	public static final int DEFAULT_VALID_TIME = 7; // Gültigkeit einer Datei
	public static final String IP = "192.168.178.63";
	public static final int PORT = 13268;

	
	@Override
	public void start(Stage primaryStage) {
		try {
			Programm p = new Programm();
			new UiEditor(p);
			
			if(p.getFilesFromServer()) {
			    File files[] = getFiles(new File("/files"));
				for(int i =0;i<files.length;i++) {
					//tableData.add(new ValidFile(files[i]));
					tableData.add(p.addValidToFile(files[i]));
				}
			} else {
				JOptionPane optionPane = new JOptionPane("Es konnte keine Verbindung hergestellt werden.\n\n " + IP + ":" + PORT, JOptionPane.ERROR_MESSAGE);    
				JDialog dialog = optionPane.createDialog("Fehler");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
				System.exit(1);
			}
		    

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
		
	  public static File[] getFiles(final File folder) {
		  File f = new File("files");
		  ArrayList<File> files = new ArrayList<>(Arrays.asList(f.listFiles()));
		  
		  File[] res = new File[files.size()];
		  res = files.toArray(res);
		  
		  return res;
	  }
	  
	  public static File getFile(final File folder, String name) {
		  File f = new File("files");
		  
		  File res = new File(f + File.separator + name);
		  
		  return res;
	  }
}
