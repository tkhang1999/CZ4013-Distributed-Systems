package backend;

import java.io.Serializable;
import java.util.UUID;

public class BookingInfo implements Serializable{
	private UUID id;
	private WeekDay day;
	private TimePeriod timePeriod;
	private String facilityName;
	
	public BookingInfo(UUID id, WeekDay day, TimePeriod timePeriod, String facilityName) {
		this.id = id;
		this.day = day;
		this.timePeriod = timePeriod.clone();
		this.facilityName = facilityName;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
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
