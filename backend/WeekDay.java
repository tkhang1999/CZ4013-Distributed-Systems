package backend;

public enum WeekDay {
	MONDAY(0), TUESDAY(1), WEDNESDAY(2), THURSDAY(3), FRIDAY(4), SATURDAY(5), SUNDAY(6);
	
	private int value;
	private WeekDay(int value) {
		this.value = value;
	}

    public static WeekDay fromInt(int i) {
        for (WeekDay wd : WeekDay.values()) {
            if (wd.getIntValue() == i) {
                return wd;
            }
        }
        return null;
    }
	
	public int getIntValue() {
		return this.value;
	}
}
