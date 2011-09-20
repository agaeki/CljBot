
public class cljBotMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Beginning");
		CljBot bot = new CljBot();
		System.out.println("Connecting");
		bot.connect("codd.uwcs.co.uk", 6667);
		System.out.println("Successfully connected");
	}
}
