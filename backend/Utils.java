package backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
	public static final List<String> WEEKDAYS = new ArrayList<>(Arrays.asList("Mon", "Tue", "Wed", "Thurs", "Fri", "Sat", "Sun"));
	
	
	//example: convert 1439 to 23:59
	public String convertMinutesToTime(int numberOfMinutes) {
		return String.valueOf(numberOfMinutes / 60)+":"+String.valueOf(numberOfMinutes % 60);
	}
	
	//example: convert 23:59 to 1439
	public int convertTimeToMinutes(String timeStr) throws Exception{
		if (!checkTimeStrFormat(timeStr)) {
			throw new Exception("Incorrect time format");
		}
		String[] time = timeStr.split(":");
		return Integer.parseInt(time[0].trim())*60+Integer.parseInt(time[1].trim());
	}
	
	//check time string represenation format
	public boolean checkTimeStrFormat(String timeStr) {
		return timeStr.matches("[0-2][0-9]:[0-5][0-9]");
	}
}
