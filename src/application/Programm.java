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
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class Programm {
	
	private static final String ip = "192.168.178.63";
	private static final int port = 13268;
	private static String s = "C:/Users/Fabian/Documents/_Dokumente/_Studium/FMG/test.txt";
	private static File file = new File("C:/Users/Fabian/Documents/_Dokumente/_Studium/FMG/test.txt");
	private File p;
	private File p2;
	public Programm() {
		
	}
	
	public void sendFile(String name, String path) {
		
	}

	public void setCurrent(File p2) {
		// TODO Auto-generated method stub
		this.p = p2;
	}
	
	public void sendFileToServer(File file) {
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
		    d.writeLong(file.length());
		    Files.copy(file.toPath(), d);
			d.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				socket.close();
				out.close();
				in.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void getFilesFromServer() {
		try {
			Socket sock = new Socket(ip, port-1);

			BufferedInputStream bis = new BufferedInputStream(sock.getInputStream());
			DataInputStream dis = new DataInputStream(bis);

			int filesCount = dis.readInt();
			File[] files = new File[filesCount];

			for(int i = 0; i < filesCount; i++) {
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
		/*
		socket.close();
		bos.close();
		dos.close();
		*/
	}
	
	public void sendFilesToServer_backup(File[] files) throws IOException {
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
		socket.close();
		bos.close();
		dos.close();
	}
}
