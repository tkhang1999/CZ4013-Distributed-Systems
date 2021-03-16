package backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
	public static final List<WeekDay> WEEKDAYS = new ArrayList<>(Arrays.asList(WeekDay.values()));
	
	
	//example: convert 1439 to 23:59
	public static String convertMinutesToTime(int numberOfMinutes) {
		String hour = String.valueOf(numberOfMinutes / 60);

		String mins = String.valueOf(numberOfMinutes % 60);
		if (mins.length() < 2) {
			mins = "0"+mins;
		}
		return hour+":"+mins;
	}
	
	//example: convert 23:59 to 1439
	public static int convertTimeToMinutes(String timeStr){
		
		String[] time = timeStr.split(":");
		return Integer.parseInt(time[0].trim())*60+Integer.parseInt(time[1].trim());
	}
	
	//check time string represenation format
	public static boolean checkTimeStrFormat(String timeStr) {
		return timeStr.matches("[0-2][0-9]:[0-5][0-9]");
	}
}
