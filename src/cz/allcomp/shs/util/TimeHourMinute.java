/**
 * Copyright (c) 2015, Samuel Trávníček
 * All rights reserved.
 * 
 * Any use, modification or implementation of this source code in your 
 * own or third-party applications without permission is prohibited.
 */

package cz.allcomp.shs.util;

import cz.allcomp.shs.logging.Messages;

public class TimeHourMinute {
	
	private int hour, minute;

	public TimeHourMinute(String timeHourMinute) {
		String[] hourMinute = timeHourMinute.split(":");
		if(hourMinute.length == 2) {
			try {
				int hour = Integer.parseInt(hourMinute[0]);
				if(hour < 0 || hour > 23)
					this.hour = -1;
				else
					this.hour = hour;
			} catch (NumberFormatException e) {
				Messages.warning(Messages.getStackTrace(e));
				this.hour = -1;
			}
			
			try {
				int minute = Integer.parseInt(hourMinute[1]);
				if(minute < 0 || minute > 59)
					this.minute = -1;
				else
					this.minute = minute;
			} catch (NumberFormatException e) {
				Messages.warning(Messages.getStackTrace(e));
				this.minute = -1;
			}
		} else {
			this.hour = -1;
			this.minute = -1;
		}
	}
	
	public TimeHourMinute(int hour, int minute) {
		if(hour < 0 || hour > 23)
			this.hour = -1;
		else
			this.hour = hour;
		
		if(minute < 0 || minute > 59)
			this.minute = -1;
		else
			this.minute = minute;
	}
	
	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}
	
	public String getHourMinute() {
		return this.hour + ":" + this.minute;
	}
	
	public static long computeTimeDifferenceInMinutes(TimeHourMinute timeStart, TimeHourMinute timeStop) {
		if(timeStart.getHour() <= timeStop.getHour()) {
			return ((timeStop.getHour()*60 + timeStop.getMinute())
					- (timeStart.getHour()*60 + timeStart.getMinute()));
		} else {
			return (23*60 + 59 - (timeStart.getHour()*60 + timeStart.getMinute()) 
					+ timeStop.getHour()*60 + timeStop.getMinute());
		}
	}
}
