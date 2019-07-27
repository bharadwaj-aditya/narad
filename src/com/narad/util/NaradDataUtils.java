package com.narad.util;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NaradDataUtils {
	private static final Logger logger = LoggerFactory.getLogger(NaradMapUtils.class);
	
	public static final String[] DATE_PATTERNS = new String[] { DateFormatUtils.ISO_DATETIME_FORMAT.getPattern() };

	public static Date getDate(Object object) {
		if (object instanceof Date) {
			return (Date) object;
		} else if (object instanceof String) {
			try {
				return DateUtils.parseDate((String) object, DATE_PATTERNS);
			} catch (ParseException e) {
				logger.debug("Cannot parse date from : {} due to : {}", object, e.getMessage());
			}
		}
		return null;
	}

	public static String getDateString(Date date) {
		if (date == null) {
			return null;
		}
		return DateFormatUtils.format(date, DateFormatUtils.ISO_DATE_FORMAT.getPattern());
	}

}
