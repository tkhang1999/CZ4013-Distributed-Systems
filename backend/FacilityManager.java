package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FacilityManager {
	
	private static FacilityManager facilityManager = null;
	private List<Facility> facilities = new ArrayList<>();
//	private HashMap<>
	
	public static FacilityManager getInstance() {
		if (facilityManager == null) {
			facilityManager = new FacilityManager();
		}
		return facilityManager;
	}
	
	public HashMap<WeekDay, List<TimePeriod>> getAvailability(String facilityName, List<String> weekDays){
		for (Facility fac: facilities) {
			if (fac.getName().equals(facilityName))
				return fac.getAvailableBookingTime();
		}
		return null;
	}
	
//	public String 
	
	
}
