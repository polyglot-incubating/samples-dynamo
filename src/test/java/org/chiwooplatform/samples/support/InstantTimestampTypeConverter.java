package org.chiwooplatform.samples.support;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class InstantTimestampTypeConverter
		implements DynamoDBTypeConverter<String, Instant> {

	private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	@Override
	public String convert(Instant instant) {
		if (instant == null) {
			return null;
		}
		final LocalDateTime localDtm = LocalDateTime.ofInstant(instant,
				ZoneId.systemDefault());
		return DateUtils.getFormattedString(localDtm, PATTERN);
	}

	@Override
	public Instant unconvert(String value) {
		if (value == null || value.length() < 4) {
			return null;
		}
		final LocalDateTime localDtm = DateUtils.toLocalDtm(value, PATTERN);
		final Instant instant = ZonedDateTime.of(localDtm, ZoneId.systemDefault())
				.toInstant();
		return instant;
	}

}