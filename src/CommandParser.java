
public class CommandParser {
	
	public static String parse(String input, int delay) {
		
//		if (input.contains(":")) {
//			String[] inputParts = input.split(":");
//			input = inputParts[1];
//		}
		
		String retString = input;
		
		String[] parts = input.split(",");
		
		if (parts.length == 3) {
			String command = parts[0];
			String arg1 = parts[1];
			String arg2 = parts[2];
			
			try {
				Integer a1 = Integer.parseInt(arg1);
				Integer a2 = Integer.parseInt(arg2);
				
				if (command.toLowerCase().equals("add")) {
					retString = arg1 + " + " + arg2 + " = " + (a1 + a2);
				} else if (command.toLowerCase().equals("sub")) {
					retString = arg1 + " - " + arg2 + " = " + (a1 - a2);
				} else if (command.toLowerCase().equals("mul")) {
					retString = arg1 + " * " + arg2 + " = " + (a1 * a2);
				} else if (command.toLowerCase().equals("div")) {
					retString = arg1 + " / " + arg2 + " = " + (a1 / a2);
				}
			} catch (Exception e) {
				return "Your input: " + input + " contained bad data. (" + e.getMessage() + ")";
			}
		}
		
		if (delay > 0) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e1) {
				return retString;
			}
		}
		
		return retString;
	}
	

}
