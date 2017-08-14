package org.chiwooplatform.samples.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.Data;

/**
 * OrderItem 앤티티는 아래와 같으며, "orderId" 속성은 HashKey, "itemId" 속성은 "RangeKey" 로 구성 된다.
 * 
 * <code>
{
  "orderId" : "ORD-1001",
  "itemId" : "NOTE-CBC-2003",
  "order" : 2,
  "options" : [ "BLACK", "BLUE", "YELLOW" ]
}</code>
 *
 */
@Data
@DynamoDBTable(tableName = OrderItem.TABLE_NAME)
public class OrderItem {

	public static final String TABLE_NAME = "OrderItem";

	@DynamoDBHashKey(attributeName = "orderId")
	private String orderId;

	@DynamoDBIndexRangeKey(localSecondaryIndexName = "itemId-index")
	@DynamoDBRangeKey(attributeName = "itemId")
	private String itemId;

	private Integer order;

	private Set<String> options;

	public OrderItem() {
		super();
	}

	private OrderItem(Builder builder) {
		this.orderId = builder.orderId;
		this.itemId = builder.itemId;
		this.order = builder.order;
		this.options = builder.options;
	}

	public static final class Builder {
		private String orderId;
		private String itemId;
		private Integer order;
		private Set<String> options;

		public static Builder create() {
			return new Builder();
		}

		public Builder orderId(String orderId) {
			this.orderId = orderId;
			return this;
		}

		public Builder itemId(String itemId) {
			this.itemId = itemId;
			return this;
		}

		public Builder order(Integer order) {
			this.order = order;
			return this;
		}

		public Builder options(Set<String> options) {
			this.options = options;
			return this;
		}

		public Builder options(String... options) {
			this.options = new HashSet<String>(Arrays.asList(options));
			return this;
		}

		public OrderItem build() {
			return new OrderItem(this);
		}
	}

}
