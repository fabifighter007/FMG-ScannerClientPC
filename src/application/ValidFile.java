package application;

import java.io.File;
import java.time.LocalDate;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


@SuppressWarnings("serial")
public class ValidFile extends java.io.File {
	private final StringProperty isValid = new SimpleStringProperty();
	private LocalDate date;
	private boolean changed=false;
	
    public boolean wasChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public ValidFile(File f) {
        super(f.getAbsolutePath());
    }

	public ValidFile(String pathname) {
		super(pathname);
		// TODO Auto-generated constructor stub
	}

	public ValidFile(File parent, String child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}
	

	public void setValid(LocalDate date) {
		this.date = date;
	}
	
	public LocalDate getValid() {
		return date;
	}
	
	public boolean isValid() {
		return date.isAfter(LocalDate.now());
	}
	
	public String getDate() {
		if(date==null) {
			return "---";
		}
		return date.toString();
	}
	
	public String getIsValid() {
		return date.toString();
	}
	
	
	
	public final StringProperty projectProperty() {
		   return isValid;
		}

		public final String getProject() {
		   return isValid.get();
		}

		public final void setProject(String value) {
			isValid.set(value);
		}
}
