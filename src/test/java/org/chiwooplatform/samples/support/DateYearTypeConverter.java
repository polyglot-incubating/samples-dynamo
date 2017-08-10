package org.chiwooplatform.samples.support;

import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class DateYearTypeConverter implements DynamoDBTypeConverter<String, Date> {

	@Override
	public String convert(Date date) {
		if (date == null) {
			return null;
		}
		return DateUtils.getFormattedString(date, "yyyy");
	}

	@Override
	public Date unconvert(String value) {
		if (value == null || value.length() < 4) {
			return null;
		}
		return DateUtils.toDate(value, "yyyy");
	}

}