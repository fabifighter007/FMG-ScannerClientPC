package application;
	
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;


public class Main extends Application {
	private static final String ip = "192.168.178.63";
	private static final int port = 13268;
	private static String s = "C:/Users/Fabian/Documents/_Dokumente/_Studium/FMG/test.txt";
	private static File file = new File("C:/Users/Fabian/Documents/_Dokumente/_Studium/FMG/test.txt");
	private List<File> datein = new ArrayList<>();
	private ObservableList<File> list = FXCollections.observableArrayList(datein);
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Stage window = new Stage();
		    window.setTitle("Scanner");
		    BorderPane bPLaunch = new BorderPane();
		    
		    ListView<File> l = new ListView<>();
		    l.setItems(list);
		    
		    list.addListener((ListChangeListener<File>) change -> {
	            while (change.next()) {
	            	System.out.println("Waiting");
	                if (change.wasAdded()) {
	                    System.out.println(change.getAddedSubList().get(0)
	                            + " was added to the list!");
	                } else if (change.wasRemoved()) {
	                    System.out.println(change.getRemoved().get(0)
	                            + " was removed from the list!");
	                }
	            }
	        });
		    
		    
	        l.setOnMouseClicked(e -> {
	        	System.out.println("Klicked");
	        });
		    

		    
		    
			
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
	        uploadBtn.setText("synchronisieren");
	        uploadBtn.setOnAction(new EventHandler<ActionEvent>() {            
	            @Override
	            public void handle(ActionEvent event) {


	            }
	        });
	        
	        Button searchBtn = new Button();
	        searchBtn.setText("neue Datei hinzufügen");
	        searchBtn.setOnAction(new EventHandler<ActionEvent>() {            
	            @Override
	            public void handle(ActionEvent event) {
	            	File f = locateFile(bPLaunch);
	            	System.out.println(f);
	            	if(f.exists() && f.isFile() && f.getName().toLowerCase().endsWith(".csv")) {
	            		list.add(f);
		            	l.refresh();
		        		sendFileToServer(f);
	            	} else {
	            		JOptionPane.showMessageDialog(null, "Ungültige Datei. Bitte erneut versuchen.\nNur Datein im Format .csv sind erlaubt.");
	            	}
	            	
	            }
	        });
	        
	        HBox btnBox = addHBox();
	        btnBox.setStyle("-fx-background-color: #1300ff;");
	        btnBox.getChildren().add(uploadBtn);
	        btnBox.getChildren().add(searchBtn);
	        
		    bPLaunch.setBottom(btnBox);
		    
		    
		    bPLaunch.setCenter(l);
		    //bPLaunch.setTop(mBLaunch);
		    Scene launch = new Scene(bPLaunch);
		    window.setScene(launch);
		    window.setMinHeight(500);
		    window.setMinWidth(500);
		    window.show();
			
			
			
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
	
	public static void sendFileToServer(File file) {
		Socket socket = null;
	 	@SuppressWarnings("unused")
		InputStream in = null;
		BufferedOutputStream out = null;
		try {
			socket = new Socket(ip, port);
			out = new BufferedOutputStream(socket.getOutputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try (DataOutputStream d = new DataOutputStream(out)) {
		    d.writeUTF(file.getName());
		    Files.copy(file.toPath(), d);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void sendFileToServerALT() {		
		 	Socket socket = null;
		 	InputStream in = null;
		 	OutputStream out = null;
	        try {
				socket = new Socket(ip, port);
		        File file = new File(s);
		        // Get the size of the file
		        @SuppressWarnings("unused")
				long length = file.length();
		        byte[] bytes = new byte[16 * 1024];
		        in = new FileInputStream(file);
		        out = socket.getOutputStream();

		        int count;
		        while ((count = in.read(bytes)) > 0) {
		            out.write(bytes, 0, count);
		        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					 out.close();
				     in.close();
				     socket.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
	}
}
