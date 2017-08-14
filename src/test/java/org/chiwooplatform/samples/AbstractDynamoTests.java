package org.chiwooplatform.samples;

import java.io.StringWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import org.junit.runner.RunWith;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootConfiguration
@SpringBootTest(classes = { AbstractDynamoTests.class,
		AbstractDynamoTests.DynamoConfiguration.class })
public abstract class AbstractDynamoTests<T> {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AmazonDynamoDB amazonDynamoDB;

	@Autowired
	private DynamoDBMapper dynamoDBMapper;

	abstract protected T model();

	protected AmazonDynamoDB dynamoDB() {
		return this.amazonDynamoDB;
	}

	protected DynamoDBMapper mapper() {
		return this.dynamoDBMapper;
	}

	protected ObjectMapper objectMapper() {
		return this.objectMapper;
	}

	protected void printJson(Object model) {
		try {
			objectMapper.writeValue(System.out, model);
		}
		catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	protected String toJson(Object model) {
		try {
			StringWriter sw = new StringWriter();
			objectMapper.writeValue(sw, model);
			return sw.toString();
		}
		catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Configuration
	public class DynamoConfiguration {

		@Value("${amazon.dynamodb.endpoint}")
		private String amazonDynamoDBEndpoint;

		@Value("${amazon.aws.accesskey:accessKey}")
		private String amazonAWSAccessKey;

		@Value("${amazon.aws.secretkey:secretkey}")
		private String amazonAWSSecretKey;

		@Value("${amazon.aws.region:secretkey}")
		private String amazonAWSRegion;

		@Bean
		public ObjectMapper objectMapperBuilder() {
			Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
			builder.serializationInclusion(JsonInclude.Include.NON_NULL);
			builder.indentOutput(true);
			builder.failOnUnknownProperties(false);
			return builder.build();
		}

		@Bean
		public AWSCredentials awsCredentials() {
			return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
		}

		@Bean
		public AmazonDynamoDB amazonDynamoDB() {
			AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard();
			AWSCredentials awsCredentials = awsCredentials();
			if (awsCredentials != null) {
				builder.withCredentials(
						new AWSStaticCredentialsProvider(awsCredentials()));
			}
			if (!StringUtils.isEmpty(amazonDynamoDBEndpoint)) {
				builder.setEndpointConfiguration(new EndpointConfiguration(
						amazonDynamoDBEndpoint, amazonAWSRegion));
			}
			final AmazonDynamoDB amazonDynamoDB = builder.build();
			return amazonDynamoDB;
		}

		@Bean
		public DynamoDBMapper dynamoDBMapper() {
			final DynamoDBMapperConfig dbMapperConfig = DynamoDBMapperConfig.builder()
					.withTableNameOverride(TableNameOverride.withTableNamePrefix(""))
					.build();
			final DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB(),
					dbMapperConfig);
			return mapper;
		}

	}
}
