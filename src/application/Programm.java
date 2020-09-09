package application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

public class Programm {
	


	public Programm() {
		System.out.println("Using " + Main.IP + ":" + Main.PORT);
	}

	public void sendFileToServer(File file) {
		Socket socket = null;
		InputStream in = null;
		BufferedOutputStream out = null;
		try {
			socket = new Socket(Main.IP, Main.PORT);
			out = new BufferedOutputStream(socket.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try (DataOutputStream d = new DataOutputStream(out)) {
		    d.writeUTF(file.getName());
		    d.writeLong(file.length());
		    Files.copy(file.toPath(), d);
			d.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
				out.close();
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean getFilesFromServer() {
		boolean res = false;
		try {
			deliteCurrentFiles(new File("files/"));
			Socket sock = new Socket(Main.IP, Main.PORT-1);
  
			BufferedInputStream bis = new BufferedInputStream(sock.getInputStream());
			DataInputStream dis = new DataInputStream(bis);
			int filesCount = dis.readInt();
			ValidFile[] files = new ValidFile[filesCount];

			for(int i = 0; i < filesCount; i++ ) {
				    long fileLength = dis.readLong();
				    String fileName = dis.readUTF();
	
				    files[i] = new ValidFile("files/" + fileName);
	
				    FileOutputStream fos = new FileOutputStream(files[i]);
				    BufferedOutputStream bos = new BufferedOutputStream(fos);
	
				    for(int j = 0; j < fileLength; j++) bos.write(bis.read());
	
				    bos.close();
				    fos.close();
				    files[i] = addValidToFile(files[i]);
			}
			dis.close();
			bis.close();
			sock.close();
			res = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public void deliteCurrentFiles(File dir) {
		for(File file: dir.listFiles()) 
		    if (!file.isDirectory()) 
		        file.delete();	
		}
	
	public void sendFilesToServer(File[] files) throws IOException {
		Socket socket = new Socket(Main.IP, Main.PORT);
		BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
		DataOutputStream dos = new DataOutputStream(bos);

		dos.writeInt(files.length);

		for(File file : files) {
		    long length = file.length();
		    dos.writeLong(length);

		    String name = file.getName();
		    dos.writeUTF(name);

		    FileInputStream fis = new FileInputStream(file);
		    BufferedInputStream bis = new BufferedInputStream(fis);

		    int theByte = 0;
		    while((theByte = bis.read()) != -1) bos.write(theByte);

		    bis.close();
		    fis.close();
		}
		dos.close();
		bos.close();
		socket.close();
	}
	
	public void addFiles(List<File> list) {
		if (list != null && !list.isEmpty()) {
			int i=0;
			for(File f : list) {
				if(f.exists() && f.getName().trim().toLowerCase().endsWith(".csv") || f.getName().trim().toLowerCase().endsWith(".txt")) {
					ValidFile res = new ValidFile(f);
					res.setValid(LocalDate.now().plusDays(Main.DEFAULT_VALID_TIME));
					addValidStamp(res);
    	            Main.tableData.add(res);
				} else {
					i++;
				}
			}
			if(i>0) {
        		JOptionPane.showMessageDialog(null, "Es wurden " + i + " ung�ltige Datei nicht hochgeladen.\nNur Datein im Format .csv sind erlaubt!");
			}
		}
	}
	
	public void addFiles(ValidFile f) {
			int i=0;
				if(f.exists() && f.getName().trim().toLowerCase().endsWith(".csv") || f.getName().trim().toLowerCase().endsWith(".txt")) {
					ValidFile res = new ValidFile(f);
					res.setValid(LocalDate.now().plusDays(Main.DEFAULT_VALID_TIME));
					addValidStamp(res);
    	            Main.tableData.add(res);				} else {
	        		JOptionPane.showMessageDialog(null, "Es wurden " + i + " ung�ltige Datei nicht hochgeladen.\nNur Datein im Format .csv sind erlaubt!");
				}
	}
	
	public void addValidStamp(ValidFile f, LocalDate date) {
		File temp = new File(f.getPath() + ".tmp");
		if(!temp.exists()) {
			try {
				temp.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {			
			LinkedList<String> lines = readFile(f);
			lines.addFirst("");
			lines.addFirst("");
			lines.addFirst("###################################################");
			lines.addFirst("#valid until: " + date.toString());
			lines.addFirst("#                   DO NOT EDIT                   #");
			lines.addFirst("###################################################");

			Files.write(temp.toPath(), lines, StandardCharsets.UTF_8);
			renameFile(temp, f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addValidStamp(ValidFile f) {
		if(f.getDate()!=null) {
			File temp = new File(f.getPath() + ".tmp");
			if(!temp.exists()) {
				try {
					temp.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {			
				LinkedList<String> lines = readFile(f);
				lines.addFirst("");
				lines.addFirst("");
				lines.addFirst("###################################################");
				lines.addFirst("#valid until: " + f.getDate());
				lines.addFirst("#                   DO NOT EDIT                   #");
				lines.addFirst("###################################################");
	
				Files.write(temp.toPath(), lines, StandardCharsets.UTF_8);
				renameFile(temp, f);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static ValidFile addValidToFile(File f) {
		ValidFile res = new ValidFile(f);
		  try(BufferedReader br = new BufferedReader(new FileReader(f))) {
			    for(String line; (line = br.readLine()) != null; ) {
			        if(line.startsWith("#") && line.contains("valid until")) {
			        	line = line.replace("#valid until: ", "");
			        	String[] test = line.split("-");
			        	LocalDate inputDate = LocalDate.of(Integer.parseInt(test[0]), Integer.parseInt(test[1]), Integer.parseInt(test[2]));
			        	res.setValid(inputDate);
			        	break;
			        } else {
			        	
			        }
			    }
			} catch (IOException e) {
				e.printStackTrace();
			}
		  return res;
	}
	
	public static void renameFile(File toBeRenamed, File new_name) {
		try {
			Files.move(toBeRenamed.toPath(), new_name.toPath(), StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
	
	public LinkedList<String> readFile(File f) {
		LinkedList<String> res = new LinkedList<String>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	if(!line.startsWith("#") && !line.trim().equalsIgnoreCase("")) {
				      res.add(line);
		    	}
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
}
