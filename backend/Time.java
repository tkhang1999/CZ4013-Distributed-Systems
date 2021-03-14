package backend;

public class Time {
	private WeekDay day;
	private int hour;
	private int mins;
	
	public Time(WeekDay day, int hour, int mins) {
		this.day = day;
		this.hour = hour;
		this.mins = mins;
	}

	public WeekDay getDay() {
		return day;
	}

	public int getHour() {
		return hour;
	}

	public int getMins() {
		return mins;
	}
	
	@Override
	public String toString() {
		return String.format("%s/%d/%d", day.toString(), hour, mins);
	}
	
}
