package backend;

import java.io.Serializable;

public class TimePeriod implements Comparable, Serializable{
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
	
	public TimePeriod shiftBy(int shiftTime) {
		return new TimePeriod(start+shiftTime, end+shiftTime);
	}
	
	public String toString() {
		return Utils.convertMinutesToTime(start)+"-"+Utils.convertMinutesToTime(end);
	}
	
}
