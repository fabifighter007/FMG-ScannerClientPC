package application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;

import javax.swing.JOptionPane;

public class Programm {
	
	private static final String ip = "192.168.178.63";
	private static final int port = 13268;

	public Programm() {
		System.out.println("Using " + ip + ":" + port);
	}
	
	public void sendFile(String name, String path) {
		
	}
	
	public void sendFileToServer(File file) {
		Socket socket = null;
		InputStream in = null;
		BufferedOutputStream out = null;
		try {
			socket = new Socket(ip, port);
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
	
	public void getFilesFromServer() {
		try {
			deliteCurrentFiles(new File("files/"));
			Socket sock = new Socket(ip, port-1);

			BufferedInputStream bis = new BufferedInputStream(sock.getInputStream());
			DataInputStream dis = new DataInputStream(bis);

			int filesCount = dis.readInt();
			File[] files = new File[filesCount];

			for(int i = 0; i < filesCount; i++ ) {
				    long fileLength = dis.readLong();
				    String fileName = dis.readUTF();
	
				    files[i] = new File("files/" + fileName);
	
				    FileOutputStream fos = new FileOutputStream(files[i]);
				    BufferedOutputStream bos = new BufferedOutputStream(fos);
	
				    for(int j = 0; j < fileLength; j++) bos.write(bis.read());
	
				    bos.close();
				    fos.close();
				
			}
			dis.close();
			bis.close();
			sock.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deliteCurrentFiles(File dir) {
		for(File file: dir.listFiles()) 
		    if (!file.isDirectory()) 
		        file.delete();	
		}
	
	public void sendFilesToServer(File[] files) throws IOException {
		Socket socket = new Socket(ip, port);
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
	
	public void addFiles(List<File> files) {
		if (files != null && !files.isEmpty()) {
			int i=0;
			for(File f : files) {
				if(f.exists() && f.getName().trim().toLowerCase().endsWith(".csv") || f.getName().trim().toLowerCase().endsWith(".txt")) {
    	            Main.tableData.add(f);
				} else {
					i++;
				}
			}
			if(i>0) {
        		JOptionPane.showMessageDialog(null, "Es wurden " + i + " ungültige Datei nicht hochgeladen.\nNur Datein im Format .csv sind erlaubt!");
			}
		}
		
	}
}
