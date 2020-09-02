package application;
	

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.stage.Stage;


public class Main extends Application {
	@SuppressWarnings("unused")
	private List<File> datein = new ArrayList<>();
	
	public static volatile ObservableList<File> tableData;

	
	@Override
	public void start(Stage primaryStage) {
		try {
			Programm p = new Programm();
			new UiEditor(p);
			
			p.getFilesFromServer();
		    LocalDate inputDate = LocalDate.of(2021,12,4);
		     
			File files[] = getFiles(new File("/files"));
			for(int i =0;i<files.length;i++) {
				tableData.add(files[i]);
				p.addValidStamp(tableData.get(i),inputDate);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
		
	  public static File[] getFiles(final File folder) {
		  ValidFile f = new ValidFile("files");
		  ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
		  
		  ValidFile[] res = new ValidFile[files.size()];
		  res = files.toArray(res);
		  
		  return res;
	  }
}
