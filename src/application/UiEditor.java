package application;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class UiEditor {

	private Stage stage;
	private boolean rand = false;
	private ImageView randOn, randOff;
	private Programm programm;
	private Liste list;
	
	@SuppressWarnings("null")
	public UiEditor(Programm programm, Liste list) {
		this.programm = programm;
		this.list = list;
		randOn = createIcon("random.png");
		randOff = createIcon("random_no.png");
		stage = new Stage();
		stage.setTitle("Scanner Client - PC");
        stage.getIcons().add(new Image("icons/scanner.png"));
        
		BorderPane mainPane = new BorderPane();
		TableView<File> tableView = createTable();
		FlowPane bottom = new FlowPane();

		// bottom Pane
		Button addButton = createButton(createIcon("add.png"));
		addButton.setOnAction(e -> {
			 FileChooser fileChooser = new FileChooser();
			 fileChooser.setTitle(stage.getTitle().replace("PC", "").replace("-", "").trim() + " - Datei auswählen");
			 fileChooser.getExtensionFilters().addAll(
			         new ExtensionFilter("CSV Files", "*.csv"),
			         new ExtensionFilter("Text Files", "*.txt", "*.csv"),
			         new ExtensionFilter("All Files", "*.*"));
			 		//File selectedFile = fileChooser.showOpenDialog(stage);
			 List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
			
				if (selectedFiles != null && !selectedFiles.isEmpty()) {
					int i=0;
					for(File f : selectedFiles) {
						if(f.exists() && f.getName().trim().toLowerCase().endsWith(".csv") || f.getName().trim().toLowerCase().endsWith(".txt")) {
							list.add(f);
		    	            Main.tableData.add(f);
						} else {
							i++;
						}
					}
					if(i!=0) {
	            		JOptionPane.showMessageDialog(null, "Es wurden " + i + " ungültige Datei nicht hochgeladen.\nNur Datein im Format .csv sind erlaubt!");
					}
				}
			

		});
		
		Button delButton = createButton(createIcon("remove.png"));
		delButton.setOnAction(e -> {
			File selectedFile = tableView.getSelectionModel().getSelectedItem();
  			
  			if (selectedFile == null) {
  				return;
  			}
  			list.remove(selectedFile);
  			Main.tableData.remove(selectedFile);
		});
		
		Button syncButton = createButton(createIcon("update.png"));
		syncButton.setText("empfangen");
		syncButton.setOnAction(e -> {
			list.clear();
			Main.tableData.clear();
			programm.getFilesFromServer();
			
			File files[] = Main.getFiles(new File("/files"));
			for(int i =0;i<files.length;i++) {
				Main.tableData.add(files[i]);
			}
		});
		
		Button updateButton = createButton(createIcon("send.png"));
		updateButton.setText("Datein an server senden");
		updateButton.setOnAction(e -> {
			File[] files =  new File[Main.tableData.size()]; // for testing...
			files = Main.tableData.toArray(files);

			try {
				programm.sendFilesToServer(files);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		bottom.getChildren().addAll(addButton, delButton, syncButton, updateButton);
		bottom.setAlignment(Pos.CENTER);
		FlowPane.setMargin(addButton, new Insets(8, 5, 8, 5));
		FlowPane.setMargin(delButton, new Insets(8, 5, 8, 5));
		FlowPane.setMargin(syncButton, new Insets(8, 5, 8, 5));
		FlowPane.setMargin(updateButton, new Insets(8, 5, 8, 5));
		bottom.setVgap(10);
		
		mainPane.setCenter(tableView);
		mainPane.setBottom(bottom);
		
		stage.setScene(new Scene(mainPane));
		stage.setWidth(512.);
		stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {

		});		
		stage.show();
	}
	
	private ImageView createIcon(String iconfile) {
		ImageView imageView = null;
		
		try {
			URL url = getClass().getResource("/icons/" + iconfile);
			Image icon = new Image(url.toString());
			imageView = new ImageView(icon);
			imageView.setFitHeight(48);	
			imageView.setFitWidth(48);	
		} catch (Exception e) {
			System.out.println("Image " + "icons/" + iconfile + " nicht gefunden!");
			e.printStackTrace();
		}
		return imageView;
	}
	
	private Button createButton(ImageView icon) {
		Button button = null;
		button = new Button("", icon);
		button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		return button;
	}
	
	public void show() {
		refreshTableData();
		
		stage.show();
	}

	public void hide() {
		stage.hide();
	}
	
	public void close() {
		stage.close();
	}
	
	private TableView<File> createTable() {
		/*
		 * Daten für Tabelle erzeugen
		 */
		Main.tableData = FXCollections.observableArrayList();
		refreshTableData();
		/*
		 * TableView erzeugen und Daten setzen
		 */
		TableView<File> tableView = new TableView<>(Main.tableData);
		/*
		 * Spalten definieren und der tableView bekannt machen
		 */
        TableColumn<File, String> name = new TableColumn<>("Dateiname");
        name.setCellValueFactory(
				new PropertyValueFactory<File, String>("name"));
		TableColumn<File, String> pathtofile = new TableColumn<>("Pfad");
		pathtofile.setCellValueFactory(
				new PropertyValueFactory<File, String>("path"));
		
		
		/*
		TableColumn<File, String> albumColumn = new TableColumn<>("letzte Änderung");
		albumColumn.setCellValueFactory(
				new PropertyValueFactory<File, String>("lastModified"));
		
		
		
		TableColumn<File, String> laengeColumn = new TableColumn<>("Länge");
		laengeColumn.setCellValueFactory(
				new PropertyValueFactory<File, String>("laenge"));
		*/
		
		tableView.getColumns().add(name);
		tableView.getColumns().add(pathtofile);
		//tableView.getColumns().add(albumColumn);
		//tableView.getColumns().add(laengeColumn);
        tableView.setEditable(false);
        

        /*
         * Doppelklick-Handler setzen
         */
        
        tableView.setOnMouseClicked(e -> {
        	File p = tableView.getSelectionModel().getSelectedItem();
  			
  			if (p == null) {
  				return;
  			}
  			if (e.getClickCount() == 2) {
  				programm.setCurrent(p);
  				System.out.println("double klick!");
  			}
        });
   
        return tableView;
	}
	
	private void refreshTableData() {		
		Main.tableData.clear();
		//for (File af : list) {
			for (File af : Main.tableData) {

			Main.tableData.add(af);
		}
	}
}