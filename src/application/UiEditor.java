package application;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class UiEditor {

	private Stage stage;
	@SuppressWarnings("unused")
	private Programm programm;
	private TableView<ValidFile> tableView;
	
	public UiEditor(Programm programm) {
		this.programm = programm;
		stage = new Stage();
		stage.setTitle("Scanner Client - PC");
        stage.getIcons().add(new Image("icons/scanner.png"));
        
		BorderPane mainPane = new BorderPane();
		
		stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
		  @Override
		  public void changed(ObservableValue<? extends Boolean> ov, Boolean onHidden, Boolean onShown) {
		    tableView.refresh();	// updates the table after getting / losing focus
		    for(ValidFile f : Main.tableData) {
		    	if(f.wasChanged()) {
		    		programm.addValidStamp(f);
		    		f.setChanged(false);
		    	}
		    }
		  }});		
		
		mainPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != mainPane
                        && event.getDragboard().hasFiles()) {
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });
		
		mainPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                	programm.addFiles(db.getFiles());

                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
        });
		
		tableView = createTable();
		FlowPane bottom = new FlowPane();

		// bottom Pane
		Button addButton = createButton(createIcon("add.png"));
		addButton.setOnAction(e -> {
			 FileChooser fileChooser = new FileChooser();
			 fileChooser.setTitle(stage.getTitle().replace("PC", "").replace("-", "").trim() + " - Datei auswählen");
			 fileChooser.getExtensionFilters().addAll(
			         new ExtensionFilter("CSV Datein", "*.csv"),
			         new ExtensionFilter("Text Datein", "*.txt", "*.csv"),
			         new ExtensionFilter("Alle Datein", "*.*"));
			 List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
			 programm.addFiles(selectedFiles);
			 
			//ValidFile i = new ValidFile(fileChooser.showOpenDialog(stage));
			//programm.addFiles(i);
		});
		
		Button delButton = createButton(createIcon("remove.png"));
		delButton.setOnAction(e -> {
			File selectedFile = tableView.getSelectionModel().getSelectedItem();
  			
  			if (selectedFile == null) {
  				return;
  			}
  			Main.tableData.remove(selectedFile);
		});
		
		Button syncButton = createButton(createIcon("update.png"));
		syncButton.setText("empfangen");
		syncButton.setOnAction(e -> {
			Main.tableData.clear();
			programm.getFilesFromServer();
			
			File[] files = Main.getFiles(new File("/files"));
			for(int i =0;i<files.length;i++) {
				Main.tableData.add(programm.addValidToFile(files[i]));
			}
		});
		
		Button updateButton = createButton(createIcon("send.png"));
		updateButton.setText("Datein an server senden");
		updateButton.setOnAction(e -> {
			File[] files =  new File[Main.tableData.size()]; 
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
		stage.setWidth(1024);
		stage.setHeight(512 + 256);
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
	
	public void hide() {
		stage.hide();
	}
	
	public void close() {
		stage.close();
	}
	
	private TableView<ValidFile> createTable() {
		
		/*
		 * Daten für Tabelle erzeugen
		 */
		Main.tableData = FXCollections.observableArrayList();
		//refreshTableData(this);
		/*
		 * TableView erzeugen und Daten setzen
		 */
		TableView<ValidFile> tableView = new TableView<>(Main.tableData);
		/*
		 * Spalten definieren und der tableView bekannt machen
		 */
		
        TableColumn<ValidFile, String> name = new TableColumn<>("Dateiname");
        name.setCellValueFactory(
				new PropertyValueFactory<ValidFile, String>("name"));
        
        TableColumn<ValidFile, String> pathtofile = new TableColumn<>("Pfad");
		pathtofile.setCellValueFactory(
				new PropertyValueFactory<ValidFile, String>("path"));
		
		TableColumn<ValidFile, String> valid = new TableColumn<>("Gültig bis");
		valid.setCellValueFactory(
				new PropertyValueFactory<ValidFile, String>("Date"));

		
		tableView.getColumns().add(name);
		tableView.getColumns().add(pathtofile);
		tableView.getColumns().add(valid);
		
		/*name.prefWidthProperty().bind(tableView.widthProperty().multiply(0.4));
		pathtofile.prefWidthProperty().bind(tableView.widthProperty().multiply(0.6));*/
		
        tableView.setEditable(false);

        /*
         * Doppelklick-Handler setzen
         */
        tableView.setOnMouseClicked(e -> {
        	ValidFile p = tableView.getSelectionModel().getSelectedItem();
  			
  			if (p == null) {
  				return;
  			}
  			if (e.getClickCount() == 2) {
  				System.out.println("double klick!");
  				new EditWindow(stage, p);
  			}
        });
        
        tableView.setPlaceholder(new Label("Keine Daten auf dem Server vorhanden."));   
        return tableView;
	}
}