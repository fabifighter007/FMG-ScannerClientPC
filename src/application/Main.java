package application;
	

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;


public class Main extends Application {
	private static final String ip = "192.168.178.63";
	private static final int port = 13268;
	private static String s = "C:/Users/Fabian/Documents/_Dokumente/_Studium/FMG/test.txt";
	private static File file = new File("C:/Users/Fabian/Documents/_Dokumente/_Studium/FMG/test.txt");
	private List<File> datein = new ArrayList<>();
	private ObservableList<File> list = FXCollections.observableArrayList(datein);
	
	public static ObservableList<File> tableData;

	
	@Override
	public void start(Stage primaryStage) {
		try {
			Programm p = new Programm();
			new UiEditor(p, new Liste());
			

			p.getFilesFromServer();
			File files[] = getFiles(new File("/files"));
			for(int i =0;i<files.length;i++) {
				tableData.add(files[i]);
			}
			
		    /*
			//MENU
		    MenuBar mBLaunch = new MenuBar();
		    Menu fileLaunch = new Menu("File");
		    MenuItem saveLaunch = new MenuItem("Save");
		    MenuItem exitLaunch = new MenuItem("Exit");
		    fileLaunch.getItems().addAll(saveLaunch, new SeparatorMenuItem(), exitLaunch);
		    mBLaunch.getMenus().add(fileLaunch);
		    */
		    
	        Button uploadBtn = new Button();
	        uploadBtn.setText("editieren");
	        uploadBtn.setOnAction(new EventHandler<ActionEvent>() {            
	            @Override
	            public void handle(ActionEvent event) {
	            	new UiEditor(new Programm(), new Liste());

	            }
	        });
	        
	        Button searchBtn = new Button();
	        searchBtn.setText("neue Datei hinzufügen");
	        searchBtn.setOnAction(new EventHandler<ActionEvent>() {            
	            @Override
	            public void handle(ActionEvent event) {
	            	File f = locateFile(null);
	            	System.out.println(f);
	            	if(f.exists() && f.isFile() && f.getName().toLowerCase().endsWith(".csv")) {
	            		list.add(f);
	            	} else {
	            		JOptionPane.showMessageDialog(null, "Ungültige Datei. Bitte erneut versuchen.\nNur Datein im Format .csv sind erlaubt.");
	            	}
	            	
	            }
	        });
	        
	        
	        
	        HBox btnBox = addHBox();
	        btnBox.setStyle("-fx-background-color: #1300ff;");
	        btnBox.getChildren().add(uploadBtn);
	        btnBox.getChildren().add(searchBtn);
	        
	        
	        /*
		    bPLaunch.setBottom(btnBox);
		    
		    
		    bPLaunch.setCenter(l);
		    //bPLaunch.setTop(mBLaunch);
		    Scene launch = new Scene(bPLaunch);
		    window.setScene(launch);
		    window.setMinHeight(500);
		    window.setMinWidth(500);
		    window.show();
			
			*/
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public HBox addHBox() {
	    HBox hbox = new HBox();
	    hbox.setPadding(new Insets(15, 12, 15, 12));
	    hbox.setSpacing(10);
	    hbox.setStyle("-fx-background-color: #336699;");


	    return hbox;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	protected File locateFile(BorderPane root) {
	    FileChooser chooser = new FileChooser();
	    chooser.setTitle("Open File");
	    return chooser.showOpenDialog(root.getScene().getWindow());
	}
	
	  public static File[] getFiles(final File folder) {
		  File f = new File("files");
		  ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
		  
		  File[] res = new File[files.size()];
		  res = files.toArray(res);
		  
		  return res;
	  }
}
