package backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Facility implements Serializable{
	private String name;
	public String getName() {
		return name;
	}
	public static final TimePeriod WORKING_HOURS = new TimePeriod(0, 1439);
	//Map weekday to a list of available booking times, e.g Monday -> [0->1439], 0 represent 00:00 while 1439 represent 23:59
	private HashMap<WeekDay, List<TimePeriod>> availableBookingTime;
	
	public Facility(String name) {
		this.name = name;
		this.availableBookingTime = new HashMap<>();
		for (WeekDay weekday: WeekDay.values()) {
			availableBookingTime.put(weekday, new ArrayList<>(Arrays.asList(WORKING_HOURS.clone())));
		}
	}
	
	public boolean bookTime(TimePeriod period, WeekDay weekday) {
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
	
	public void releaseBookingTime(TimePeriod period, WeekDay weekday) {
		int index = 0;
		for (TimePeriod availablePeriod: availableBookingTime.get(weekday)) {
			if (availablePeriod.compareTo(period) > 0) break;
			index++;
		}
		TimePeriod prev = (index>0)?availableBookingTime.get(weekday).get(index-1):null;
		TimePeriod after =(index<availableBookingTime.get(weekday).size())?availableBookingTime.get(weekday).get(index):null;
		if (prev != null) {
			if (prev.end == period.start && after != null && period.end == after.start) {
				prev.end = after.end;
				availableBookingTime.get(weekday).remove(index);
			} else if (prev.end == period.start) {
				prev.end = period.end;
			} else if (after != null && period.end == after.start) {
				after.start = period.start;
			} else {
				availableBookingTime.get(weekday).add(index, period.clone());
			}
		} else {
			if (after != null && period.end == after.start) {
				after.start = period.start;
			} else {
				availableBookingTime.get(weekday).add(index, period.clone());
			}
		}	
	}
	
	public boolean shiftBookingTime(WeekDay weekday, TimePeriod period, int shiftMins) {
		this.releaseBookingTime(period, weekday);
		TimePeriod newBookingPeriod = period.shiftBy(shiftMins);
		if (WORKING_HOURS.include(newBookingPeriod)) {
			if (bookTime(newBookingPeriod, weekday)) {
				return true;
			} else bookTime(period, weekday);
		}
		return false;
	}
	
	public boolean extendBookingTime(WeekDay weekday, TimePeriod period, boolean sooner, int extendTime) {
		this.releaseBookingTime(period, weekday);
		TimePeriod newBookingPeriod = period.clone();
		if (sooner) {
			newBookingPeriod.start -= extendTime;
		} else newBookingPeriod.end += extendTime;
		if (WORKING_HOURS.include(newBookingPeriod)) {
			if (bookTime(newBookingPeriod, weekday)) {
				return true;
			} else bookTime(period, weekday);
		}
		return false;
	}
	
	public HashMap<WeekDay, List<TimePeriod>> getAvailableBookingTime(){
		return availableBookingTime;
	}
	
}
