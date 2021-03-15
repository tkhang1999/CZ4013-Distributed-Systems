package backend;

import java.io.Serializable;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;


public class FacilityManager {
	
	private static FacilityManager facilityManager = null;
	private List<Facility> facilities = new ArrayList<>();
	private HashMap<String, List<BookingInfo>> userBookingMap = new HashMap<>();
//	private HashMap<>
	
	public static FacilityManager getInstance() {
		if (facilityManager == null) {
			facilityManager = new FacilityManager();
		}
		return facilityManager;
	}
	
	public HashMap<WeekDay, List<TimePeriod>> getAvailability(String facilityName, List<String> weekDays){
		Facility fac = getFacility(facilityName);
		if (fac != null)
			return fac.getAvailableBookingTime();
		return null;
	}
	
	public Message bookFacility(String user, String facilityName, WeekDay day, TimePeriod timePeriod) {
		boolean success = false;
		String message = "Incorrect facility name";
		Facility fac = getFacility(facilityName);
		if (fac != null) {
			success = fac.bookTime(timePeriod, day);
			if (success) {
				UUID id = UUID.randomUUID();
				message = id.toString();
				if (!userBookingMap.containsKey(user)) {
					userBookingMap.put(user, new ArrayList<>());
				}
				userBookingMap.get(user).add(new BookingInfo(id, day, timePeriod, fac.getName()));
			} else {
				message = "The time specified is not available";
			}
		}
		return new Message(success, message);
	}
	
	public Message shiftBooking(String user, String bookingID, boolean postpone, int shiftTime ) {
		String message = "Invalid Booking ID";
		boolean success = false;
		if (userBookingMap.containsKey(user)) {
			List<BookingInfo> bookingInfoList = userBookingMap.get(user);
			for (BookingInfo info: bookingInfoList) {
				if (info.getId().equals(bookingID)) {
					if (!postpone) {
						shiftTime = -shiftTime;
					}
					Facility fac = getFacility(info.getFacilityName());
					success = fac.shiftBookingTime(info.getDay(), info.getTimePeriod(), shiftTime);
					if (success) {
						message = "Successful change";
					} else {
						message = "The changed time is not available";
					}
					break;
				}
			}
		}
		
		return new Message(success, message);
	}
	
	public Facility getFacility(String facilityName) {
		for (Facility fac: facilities) {
			if (fac.getName().equals(facilityName))
				return fac;
		}
		return null;
	}
	
	
}
