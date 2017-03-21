//Andrew VanNess 430 Project 2 PingClient

import java.net.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.text.DateFormat;

public class PingClient implements Runnable {
	private static final int TIMEOUT = 1000; // milliseconds
	private static final int DELAY = 1000; // milliseconds
	private static final int PACKETS = 10;

	public static void main(String[] args) {
		//String host = args[0];
		//int port = Integer.parseInt(args[1]);
		String host = "127.0.0.1";
		int port = 2014;
		Runnable start = new PingClient(host, port);
		start.run();
	}

	private String hostname;
	private InetAddress host;
	private int port;
	private DatagramSocket socket;
	private int packets;

	public PingClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		packets = PACKETS;
	}

	public void run() {
		if (host == null) {
			try {
				host = InetAddress.getByName(hostname);
			} catch (UnknownHostException e) {
				System.err.println(hostname+": Host name lookup failure");
				return;
			}
		}
		//final String hostSig = hostname+" ("+host+")";
		final String hostSig = hostname;

		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}

		int seq = 1;

		while (true) {
			String sendString = generateSendString(seq);
			byte[] sendData = sendString.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, host, port);
			long sendTime = System.nanoTime();
			long ping;
			try {
				socket.send(sendPacket);
			} catch (IOException e) {
				System.err.println("In sending ping request to "+hostSig+": "+e);
			}
			
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				socket.receive(receivePacket);
				ping = System.nanoTime()-sendTime;
				ping=ping/1000;
				
				String message = new String(receiveData);
				message=message.trim();
				
				System.out.println("Attempt "+seq+" | "+message+" "+receiveData.length+" bytes from "+hostSig+" "+"time="+ping+" microseconds"+"\n");
				
			} catch (SocketTimeoutException e) {

				System.out.println("Attempt "+seq+" | "+"Request timed out from "+hostSig+"\n");
			} catch (IOException e) {
				System.err.println("In receiving ping response from "+hostSig+": "+e);
			}
			++seq;
			--packets;
			if (packets == 0) break;
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				System.err.println("something went wrong");
			}
		}
	}

	private static String generateSendString(int seq) {
		StringBuffer buf = new StringBuffer("hello ");
		//buf.append(seq);
		buf.append(' ');
		
		DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
	    Date dateobj = new Date();
	    System.out.println(df.format(dateobj));
	    //System.out.println("Attempt: "+seq+" ");

		
		//buf.append(df.format(dateobj));
		buf.append('\n');
		return buf.toString();
		

		
		
		
	}
}