import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.lang.Thread;
import clojure.lang.RT;
import clojure.lang.Var;

public class CljBot {
	private Socket connection;
	private BufferedReader in;
	private PrintWriter out;
	private LinkedList<String> outbox;
	private Thread sender;
	
	public CljBot() {
			outbox = new LinkedList<String>();
			System.out.println("Initialised");
	}
	
	public void connect(String server, int port) {
		try {
			connection = new Socket(server, port);
			System.out.println("Connected");
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			out = new PrintWriter(connection.getOutputStream(), true);
			System.out.println("Streams tethered");
			sender = new Thread(new send());
			System.out.println("sender init");
			RT.loadResourceScript("functions.clj");
			System.out.println("functions loaded");
			sender.start();
			System.out.println("sender running");
			Thread receive = new Thread(new receive());
			System.out.println("receive init");
			receive.start();
			System.out.println("Sending and Receiving");
			out.println("USER cljBot 0 * :KludgeBot");
			out.println("NICK cljBot");
			Thread.sleep(10000l);
			outbox.addLast("JOIN #lemonparty");
			System.out.println("Done");
		} catch (UnknownHostException e) {
			System.err.println("Server not found, please try again.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("IOException occurred, please try again.");
			System.exit(1);
		} catch (InterruptedException e) {
		} catch (Exception e) {}
	}
	
	private class send implements Runnable {
		public send() {}
		
		public void run() {
			while(true) {
				if (outbox.size() > 0) {
					String msg = outbox.poll();
					if (!msg.equals("-1")) {
						System.out.println("Sending " + msg);
						out.println(msg);
					} else if (msg.equals("-2")) {
						return;
					}
				} 
				try {
					Thread.sleep(1000l);
				} catch (InterruptedException e) {}
			}
		}
	}
	
	private class process implements Runnable {
		private String mes;
		private Var fun;
		
		public process(String[] msg) {
			fun = RT.var("cljBot", "process-" + msg[1]);
			try {
				mes = msg[2];
			} catch (ArrayIndexOutOfBoundsException e) {
				if (msg[0].equals("PING")) {
					mes = "";
					out.println("PONG " + msg[1]);
					fun = RT.var("cljBot", "pong");
				}
			}
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				System.out.println("Invoking " + fun.toString());
				String msg = fun.invoke(mes).toString();
				System.out.println("Generated reply " + msg + " from " + fun.toString());
				outbox.addLast(msg);
			} catch(Exception e) {}
		}
	}
	
	private class receive implements Runnable {

		String msg;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				try {
					if (in.ready()) {
						msg = in.readLine();
						System.out.println(msg);
						Thread proc = new Thread(new process(msg.split(" ", 3)));
						proc.start();
					}
				} catch (IOException e) {
					System.err.print("IOException at ");
					e.printStackTrace();
					return;
				}
			}
		}
	}
}