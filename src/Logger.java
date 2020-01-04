import java.util.Date;

public class Logger {
	
	public static void log(String str, Object... extra) {
		String ret = str + " at " + (new Date()).toString();
		if (extra.length > 1) {
			ret += "\n";
			for (int i = 0; i < extra.length - 1; i++) {
				ret += "..." + extra[i].toString() + "\n";
			}
		}
		if (extra.length == 1) {
			ret += "\n";
		}
		if (extra.length > 0) {
			ret += "..." + extra[extra.length - 1].toString();
		}
		System.out.println(ret);
	}
}
