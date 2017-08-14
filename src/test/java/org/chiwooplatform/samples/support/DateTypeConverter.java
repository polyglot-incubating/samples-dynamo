package org.chiwooplatform.samples.support;

import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class DateTypeConverter implements DynamoDBTypeConverter<String, Date> {

	private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

	@Override
	public String convert(Date date) {
		if (date == null) {
			return null;
		}
		// System.out.println("XXX-----------" + DateUtils.getFormattedString(date,
		// PATTERN));
		return DateUtils.getFormattedString(date, PATTERN);
	}

	@Override
	public Date unconvert(String value) {
		if (value == null) {
			return null;
		}
		final Date date = DateUtils.toDate(value, PATTERN);
		return date;
	}

}