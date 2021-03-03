package backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Facility {
	private String name;
	public static TimePeriod workingHours = new TimePeriod(0, 1439);
	//Map weekday to a list of available booking times, e.g Mon -> [0->1439], 0 represent 00:00 while 1439 represent 23:59
	private HashMap<String, List<TimePeriod>> availableBookingTime;
	
	public Facility(String name) {
		this.name = name;
		this.availableBookingTime = new HashMap<>();
		for (String weekday: Utils.WEEKDAYS) {
			availableBookingTime.put(weekday, new ArrayList<>(Arrays.asList(workingHours.clone())));
		}
	}
	
	public boolean bookTime(TimePeriod period, String weekday) {
		int index = 0;
		for (TimePeriod availablePeriod: availableBookingTime.get(weekday)) {
			if (availablePeriod.include(period)) {
				if (period.start == availablePeriod.start && period.end == availablePeriod.end) {
					availableBookingTime.get(weekday).remove(index);
				} else if (period.start == availablePeriod.start) {
					availableBookingTime.get(weekday).get(index).start = period.end;
				} else if (period.end == availablePeriod.end) {
					availableBookingTime.get(weekday).get(index).end = period.start;
				} else {
					int end = availableBookingTime.get(weekday).get(index).end;
					availableBookingTime.get(weekday).get(index).end = period.start;
					availableBookingTime.get(weekday).add(index+1, new TimePeriod(period.end, end));
				}
				return true;
			}
			index++;
		}
		return false;
	}
	
	public void releaseBookingTime(TimePeriod period, String weekday) {
		int index = 0;
		for (TimePeriod availablePeriod: availableBookingTime.get(weekday)) {
			if (availablePeriod.compareTo(period) > 0) break;
			index++;
		}
		TimePeriod prev = (index>0)?availableBookingTime.get(weekday).get(index-1):null;
		TimePeriod after =(index<availableBookingTime.get(weekday).size())?availableBookingTime.get(weekday).get(index):null;
		if (prev != null) {
			if (prev.end == period.start && period.end == after.start) {
				prev.end = after.end;
				availableBookingTime.get(weekday).remove(index+1);
			} else if (prev.end == period.start) {
				prev.end = period.end;
			} else if (after != null && period.end == after.start) {
				after.start = period.start;
			} else {
				availableBookingTime.get(weekday).add(index, period);
			}
		} else {
			if (after != null && period.end == after.start) {
				after.start = period.start;
			} else {
				availableBookingTime.get(weekday).add(index, period);
			}
		}	
	}
	
	public boolean shiftBookingTime(String weekday, TimePeriod period, int shiftMins) {
		this.releaseBookingTime(period, weekday);
		TimePeriod newBookingPeriod = period.clone();
		newBookingPeriod.start += shiftMins;
		newBookingPeriod.end += shiftMins;
		if (workingHours.include(newBookingPeriod)) {
			if (bookTime(newBookingPeriod, weekday)) {
				return true;
			} else bookTime(period, weekday);
		}
		return false;
	}
	
	
	public HashMap<String, List<TimePeriod>> getAvailableBookingTime(){
		return availableBookingTime;
	}
	public static class TimePeriod implements Comparable{
		public int start;
		public int end;
		
		public TimePeriod(int s, int e) {
			start = s;
			end = e;
		}
		
		public int compareTo(Object o) {
			TimePeriod t2 = (TimePeriod) o;
			return this.start - t2.start;
		}
		
		public boolean include(TimePeriod period) {
			return period.start >= this.start && period.end <= this.end;
		}
		
		public TimePeriod clone() {
			return new TimePeriod(this.start, this.end);
		}
		
	}
}
