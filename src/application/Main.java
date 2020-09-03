package application;
	

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.stage.Stage;


public class Main extends Application {
	//https://stackoverflow.com/questions/55874952/javafx-propertyvaluefactory-is-not-able-to-retrieve-property
	@SuppressWarnings("unused")
	private List<ValidFile> datein = new ArrayList<>();
	
	public static volatile ObservableList<ValidFile> tableData;
	public static final int DEFAULT_VALID_TIME = 7; // Gültigkeit einer Datei

	
	@Override
	public void start(Stage primaryStage) {
		try {
			Programm p = new Programm();
			new UiEditor(p);
			
			p.getFilesFromServer();
		    
		    File files[] = getFiles(new File("/files"));
			for(int i =0;i<files.length;i++) {
				//tableData.add(new ValidFile(files[i]));
				tableData.add(p.addValidToFile(files[i]));
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
}
