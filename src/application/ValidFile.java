package application;

import java.io.File;
import java.time.LocalDate;

public class ValidFile extends java.io.File {
	LocalDate date;

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
	
	public boolean isValid() {
		return date.isAfter(LocalDate.now());
	}

}
