package backend;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


public class FacilityManager {
	
	private static FacilityManager facilityManager = null;
	private List<Facility> facilities = new ArrayList<>();
	private String updatedFacility = null;
	//map user to their booking
	public HashMap<String, List<BookingInfo>> userBookingMap = new HashMap<>();
	
	
	//map facility to users who register that facility
	private Hashtable<String, Set<RegisteredClientInfo>> mapFacilityUser = new Hashtable<>(); 
	
	public static void main(String[] args) {
		FacilityManager manager = getInstance();
		manager.initializeDummyData();
		System.out.println(manager.getAvailabilityInString("PDR 1", new ArrayList<>(Arrays.asList(WeekDay.MONDAY, WeekDay.THURSDAY))));
		int start = Utils.convertTimeToMinutes("7:00");
		int end = Utils.convertTimeToMinutes("11:00");
		System.out.println("Booking PDR 1 from 7:00 to 11:00");
		manager.bookFacility("Lam", "PDR 1", WeekDay.MONDAY , new TimePeriod(start, end));
		System.out.println(manager.getAvailabilityInString("PDR 1", new ArrayList<>(Arrays.asList(WeekDay.MONDAY, WeekDay.THURSDAY))));
		System.out.println("delay by 30 mins");
		Message message = manager.shiftBooking("Lam" , manager.userBookingMap.get("Lam").get(0).getId().toString(), true, 30);
		System.out.println(message.getMessage());
		System.out.println(manager.getAvailabilityInString("PDR 1", new ArrayList<>(Arrays.asList(WeekDay.MONDAY, WeekDay.THURSDAY))));
		System.out.println("advance by 60 mins");
		message = manager.shiftBooking("Lam" , manager.userBookingMap.get("Lam").get(0).getId().toString(), false, 60);
		System.out.println(message.getMessage());
		System.out.println(manager.getAvailabilityInString("PDR 1", new ArrayList<>(Arrays.asList(WeekDay.MONDAY, WeekDay.THURSDAY))));
		
		System.out.println("extend booking sooner by 60 mins");
		message = manager.extendBookingTime("Lam", manager.userBookingMap.get("Lam").get(0).getId(), true, 60);
		System.out.println(message.getMessage());
		System.out.println(manager.getAvailabilityInString("PDR 1", new ArrayList<>(Arrays.asList(WeekDay.MONDAY, WeekDay.THURSDAY))));
		
		System.out.println("extend booking later by 120 mins");
		message = manager.extendBookingTime("Lam", manager.userBookingMap.get("Lam").get(0).getId(), false, 120);
		System.out.println(message.getMessage());
		System.out.println(manager.getAvailabilityInString("PDR 1", new ArrayList<>(Arrays.asList(WeekDay.MONDAY, WeekDay.THURSDAY))));
		
		System.out.println("Booking PDR 1 from 7:00 to 11:00");
		message = manager.bookFacility("Khang", "PDR 1", WeekDay.MONDAY , new TimePeriod(start, end));
		System.out.println(message.getMessage());
		System.out.println(manager.getAvailabilityInString("PDR 1", new ArrayList<>(Arrays.asList(WeekDay.MONDAY, WeekDay.THURSDAY))));
	}
	
	
	public static FacilityManager getInstance() {
		if (facilityManager == null) {
			facilityManager = new FacilityManager();
		}
		return facilityManager;
	}
	
	private FacilityManager() {
		Thread thread = new Thread() {
			public void run() {
				while (true) {
					if (mapFacilityUser != null) {
						for (Map.Entry<String, Set<RegisteredClientInfo>> entry: mapFacilityUser.entrySet()) {
							for (RegisteredClientInfo info: entry.getValue()) {
								if (System.currentTimeMillis()/1000.d - info.getCreatedTime() > info.getInterval()) {
									entry.getValue().remove(info);
								}
							}
						}
					}
				}
			};
		};
		thread.start();
	}
	
	public String getUpdatedFacility() {
		return updatedFacility;
	}
	
	public void setUpdatedFacility(String fac) {
		updatedFacility = fac;
	}
	
	public void initializeDummyData() {
		facilities.add(new Facility("Table tennis 1"));
		facilities.add(new Facility("Table tennis 2"));
		facilities.add(new Facility("Table tennis 3"));
		facilities.add(new Facility("PDR 1"));
		facilities.add(new Facility("PDR 2"));
		facilities.add(new Facility("LT 1"));
		facilities.add(new Facility("LT 2"));
	}
	
	public HashMap<WeekDay, List<TimePeriod>> getAvailability(String facilityName, List<WeekDay> weekDays){
		facilityName = facilityName.toUpperCase();
		Facility fac = getFacility(facilityName);
		HashMap<WeekDay, List<TimePeriod>> result = new HashMap<>();
		if (fac != null) {
			for (WeekDay weekDay: weekDays) {
				result.put(weekDay, fac.getAvailableBookingTime().get(weekDay));
			}
			return result;
		}
		return null;
	}
	
	public String getAvailabilityInString(String facilityName, List<WeekDay> weekDays) {
		facilityName = facilityName.toUpperCase();
		HashMap<WeekDay, List<TimePeriod>> availability = getAvailability(facilityName, weekDays);
		if (availability == null) return "Incorrect facility name";
		StringBuilder sb = new StringBuilder();
		weekDays.sort((WeekDay d1, WeekDay d2) -> d1.getIntValue()-d2.getIntValue());
		for (WeekDay day: weekDays) {
			sb.append(day.toString()+": ");
			if (availability.get(day) == null || availability.get(day).size() == 0) {
				sb.append("Not available\n");
				continue;
			}
			List<String> timeStrings = availability.get(day).stream().map(Object::toString).collect(Collectors.toList());
			sb.append(String.join(", " , timeStrings));
			sb.append("\n");
		}
		return sb.toString();
	}

	public Message bookFacility(String user, String facilityName, WeekDay day, String timePeriod) {
		facilityName = facilityName.toUpperCase();
		String[] time = timePeriod.split(" ");
		int start = Utils.convertTimeToMinutes(time[0]);
		int end = Utils.convertTimeToMinutes(time[1]);
		if (start > end) {
			return new Message(false, "Invalid time input");
		}
		return this.bookFacility(user, facilityName, day, new TimePeriod(start, end));
	}
	
	
	public Message bookFacility(String user, String facilityName, WeekDay day, TimePeriod timePeriod) {
		boolean success = false;
		String message = "Incorrect facility name";
		facilityName = facilityName.toUpperCase();
		Facility fac = getFacility(facilityName);
		if (fac != null) {
			success = fac.bookTime(timePeriod, day);
			if (success) {
				UUID id = UUID.randomUUID();
				message = "Success with booking id: "+id.toString();
				if (!userBookingMap.containsKey(user)) {
					userBookingMap.put(user, new ArrayList<>());
				}
				userBookingMap.get(user).add(new BookingInfo(id.toString(), day, timePeriod, fac.getName()));
				updatedFacility = facilityName;
//				getNotifiedMessage(facilityName);
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
						info.setTimePeriod(info.getTimePeriod().shiftBy(shiftTime));
						
						updatedFacility = info.getFacilityName();
//						getNotifiedMessage(info.getFacilityName());
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
		facilityName = facilityName.toUpperCase();
		for (Facility fac: facilities) {
			if (fac.getName().equals(facilityName))
				return fac;
		}
		return null;
	}
	
	public Message registerUser(String address, int port, String facilityName, int interval) {
		boolean success = false;
		String message = "Incorrect facility name";
		facilityName = facilityName.toUpperCase();
		if (getFacility(facilityName) != null) {
			if (!mapFacilityUser.containsKey(facilityName)) {
				mapFacilityUser.put(facilityName, Collections.synchronizedSet(new HashSet<>()));
			}
			mapFacilityUser.get(facilityName).add(new RegisteredClientInfo(address, port, interval));
			message = "Successfully registered";
		}
		return new Message(success, message);
	}
	
	public Hashtable<String, Set<RegisteredClientInfo>> getMapFacilityUser() {
		return mapFacilityUser;
	}

	public String getNotifiedMessage(String facilityName) {
		facilityName = facilityName.toUpperCase();
		String message = getAvailabilityInString(facilityName, new ArrayList(Arrays.asList(WeekDay.values())));
		return message;
	}
	
	
	//Idempotent feature
	public Message cancelBooking(String user, String bookingID) {
		String message = "Invalid Booking ID";
		boolean success = false;
		bookingID = bookingID.trim();
		if (userBookingMap.containsKey(user)) {
			List<BookingInfo> bookingInfoList = userBookingMap.get(user);
			int index = 0;
			for (BookingInfo info: bookingInfoList) {
				if (info.getId().equals(bookingID)) {
					Facility fac = getFacility(info.getFacilityName());
					bookingInfoList.remove(index);
					fac.releaseBookingTime(info.getTimePeriod(), info.getDay());
					success = true;
					message = "Successfully cancel";
					updatedFacility = info.getFacilityName();
					break;
				}
				index++;
			}
		}
		
		return new Message(success, message);
	}
	
	
	//Non idempotent feature
	public Message extendBookingTime(String user, String bookingID, boolean sooner, int extendTime) {
		String message = "Invalid Booking ID";
		boolean success = false;
		if (userBookingMap.containsKey(user)) {
			List<BookingInfo> bookingInfoList = userBookingMap.get(user);
			int index = 0;
			for (BookingInfo info: bookingInfoList) {
				if (info.getId().equals(bookingID)) {
					Facility fac = getFacility(info.getFacilityName());
					success = fac.extendBookingTime(info.getDay(), info.getTimePeriod(), sooner, extendTime);
					if (success) {
						int start = info.getTimePeriod().start;
						int end = info.getTimePeriod().end;
						if (sooner)
							info.setTimePeriod(new TimePeriod(start-extendTime, end));
						else info.setTimePeriod(new TimePeriod(start, end+extendTime));
						message = "Successfully extend";
						updatedFacility = info.getFacilityName();
//						getNotifiedMessage(info.getFacilityName());
					} else message = "Cannot extend due to unavailable time";
					break;
				}
				index++;
			}
		}
		
		return new Message(success, message);
	}
}
