package application;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class EditWindow extends Application {
	public EditWindow(Stage owner, File f) {
		try {
			start(new Stage(), owner, f);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void start(Stage stage, Stage owner, File f) throws Exception {
		TextField inputName = new TextField();
		inputName.setText(f.getName());
		
		 final DatePicker datePicker = new DatePicker();
		 datePicker.setOnAction(new EventHandler() {
		     public void handle(Event t) {
		         LocalDate date = datePicker.getValue();
		         System.err.println("Selected date: " + date);
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

		FlowPane bottom = new FlowPane();
		bottom.getChildren().addAll(datePicker, spinner);
		bottom.setAlignment(Pos.CENTER);
		FlowPane.setMargin(datePicker, new Insets(8, 5, 8, 5));
		FlowPane.setMargin(spinner, new Insets(8, 5, 8, 5));
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


	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
