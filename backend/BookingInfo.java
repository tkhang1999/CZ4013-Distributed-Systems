package backend;

import java.io.Serializable;

public class BookingInfo implements Serializable{
	private String id;
	private WeekDay day;
	private TimePeriod timePeriod;
	private String facilityName;
	
	public BookingInfo(String id, WeekDay day, TimePeriod timePeriod, String facilityName) {
		this.id = id;
		this.day = day;
		this.timePeriod = timePeriod.clone();
		this.facilityName = facilityName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public WeekDay getDay() {
		return day;
	}

	public void setDay(WeekDay day) {
		this.day = day;
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}
	
}
