package application;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class EditWindow extends Application {
	@SuppressWarnings("unused")
	private ValidFile file;
	public EditWindow(Stage owner, ValidFile f) {
		try {
			start(new Stage(), owner, f);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void start(Stage stage, Stage owner, ValidFile f) throws Exception {
		 this.file = f;
		 TextField inputName = new TextField();
	     inputName.setText(f.getName());
		
		 final DatePicker datePicker = new DatePicker();
		 datePicker.setValue(f.getValid());
		 datePicker.setOnAction(new EventHandler() {
		     public void handle(Event t) {
		         LocalDate date = datePicker.getValue();
		     }
		 });	
		 
		 datePicker.focusedProperty().addListener(new ChangeListener<Boolean>() {
		        @Override
		        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		            if (!newValue){
		            	try {
			            	datePicker.setValue(datePicker.getConverter().fromString(datePicker.getEditor().getText()));
		            	} catch(DateTimeParseException e) {
		            		datePicker.setValue(f.getValid());
		            	}
		            }
		        }
		    });
		 
		TimeSpinner spinner = new TimeSpinner();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        spinner.valueProperty().addListener((obs, oldTime, newTime) -> 
            System.out.println(formatter.format(newTime)));
        
		VBox vbox = new VBox();
		vbox.setSpacing(50);
		vbox.setPadding(new Insets(10));   //10 px "buffer" around button
		vbox.getChildren().add(inputName);
		
		Button okButton = createButton(createIcon("checktickgreenx64.png"));
		okButton.setOnAction(e -> {
			if(!f.getName().equals(inputName.getText())) {
				String text = inputName.getText();
				if(text.endsWith(".csv") || text.endsWith(".txt")) {
					
				} else {
					text += ".txt";
				}
				Main.tableData.remove(f);
				
				Programm.renameFile(f, new File(f.getParent() + File.separatorChar + text));
				f.delete();
				Main.tableData.add(Programm.addValidToFile(Main.getFile(null, text)));
			}
			f.setValid(datePicker.getValue());
			f.setChanged(true);
			stage.close();
		});

		FlowPane bottom = new FlowPane();
		bottom.getChildren().addAll(datePicker, spinner, okButton);
		bottom.setAlignment(Pos.CENTER);
		FlowPane.setMargin(datePicker, new Insets(8, 5, 8, 5));
		FlowPane.setMargin(spinner, new Insets(8, 5, 8, 5));
		FlowPane.setMargin(okButton, new Insets(8, 5, 8, 5));
		bottom.setVgap(10);
		
		BorderPane mainPane = new BorderPane();
		mainPane.setCenter(vbox);
		mainPane.setBottom(bottom);
		
		stage.setTitle("Datei bearbeiten: " + f.getName());
        stage.getIcons().add(new Image("icons/scanner.png"));
        stage.setScene(new Scene(mainPane));
		stage.setWidth(512.);
		stage.setHeight(512.);
		stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {

		});		
		
		stage.initOwner(owner);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.showAndWait();		
	}

	private Button createButton(ImageView icon) {
		Button button = null;
		button = new Button("", icon);
		button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		return button;
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


	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
