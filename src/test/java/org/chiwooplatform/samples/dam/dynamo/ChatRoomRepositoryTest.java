package org.chiwooplatform.samples.dam.dynamo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;

import org.chiwooplatform.samples.AbstractDynamoTests;
import org.chiwooplatform.samples.entity.ChatRoom;
import org.junit.Test;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;

import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles(profiles = {
		// "home",
		"default"
		// xxx
})
@Import(ChatRoomRepositoryTest.Config.class)
public class ChatRoomRepositoryTest extends AbstractDynamoTests<ChatRoom> {

	@EnableDynamoDBRepositories(basePackages = "org.chiwooplatform.samples.dam.dynamo")
	@Configuration
	static class Config {
		/*
		 * @Bean public String hello() { return new String("HELLO"); }
		 */
	}

	private final String id = "welcome";

	@Override
	protected ChatRoom model() {
		return ChatRoom.builder().name(id).description("This is a welcome room.")
				.recentMessageAuthor("lamp").recentMessageText("Hi~~~").build();
	}

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Test
	public void ut0000_checkInstances() throws Exception {
		log.info("repository: {}", chatRoomRepository);
	}

	@Test
	public void ut1000_createTableIfNotExists() throws Exception {
		log.info("createTableIfNotExists");
		final DeleteTableRequest deleteTableRequest = new DeleteTableRequest(
				ChatRoom.TABLE_NAME);
		TableUtils.deleteTableIfExists(dynamoDB(), deleteTableRequest);
		CreateTableRequest createTableRequest = mapper()
				.generateCreateTableRequest(ChatRoom.class);
		createTableRequest.setProvisionedThroughput(new ProvisionedThroughput()
				.withReadCapacityUnits(5L).withWriteCapacityUnits(5L));
		List<GlobalSecondaryIndex> gsi = createTableRequest.getGlobalSecondaryIndexes();
		if (gsi != null) {
			final ProvisionedThroughput idxThroughput = new ProvisionedThroughput()
					.withReadCapacityUnits(5L).withWriteCapacityUnits(5L);
			for (GlobalSecondaryIndex gs : gsi) {
				final String indexName = gs.getIndexName();
				log.info("GlobalSecondaryIndex: {}", indexName);
				gs.setProvisionedThroughput(idxThroughput);
			}
		}
		TableUtils.createTableIfNotExists(dynamoDB(), createTableRequest);
	}

	@Test
	public void ut1001_lisetTable() throws Exception {
		dynamoDB().listTables().getTableNames()
				.forEach((v) -> log.info("tableName: {}", v));
	}

	@Test
	public void ut1002_save() throws Exception {
		try {
			ChatRoom newItem = model();
			// newItem.setRecentMessageDateTime(new Date());
			chatRoomRepository.save(newItem);
			ChatRoom result = chatRoomRepository.findOne(newItem.getName());
			log.info("result: {}", result);
			printJson(result);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1002_findOne() throws Exception {
		try {
			ChatRoom result = chatRoomRepository.findOne(id);
			log.info("result: {}", result);
			printJson(result);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1003_save() throws Exception {
		try {
			ChatRoom entity = model();
			entity.setRecentMessageText("Hi there~~~");
			chatRoomRepository.save(entity);
			ChatRoom result = chatRoomRepository.findOne(entity.getName());
			log.info("result: {}", result);
			printJson(result);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1004_batchSave() throws Exception {
		try {
			List<ChatRoom> rooms = Arrays.asList(
					ChatRoom.builder().name("Room").recentMessageAuthor("lamp")
							.recentMessageText("Hi~~~")
							.description("This is a welcome room.").build(),
					ChatRoom.builder().name("Room 1001").recentMessageAuthor("lamp")
							.recentMessageText("Hi~~~")
							.description("This is a welcome room 1.").build(),
					ChatRoom.builder().name("Room 1002").recentMessageAuthor("lamp1")
							.recentMessageText("Hi~~~")
							.description("This is a welcome room 2.").build(),
					ChatRoom.builder().name("Room 1003").recentMessageAuthor("lamp1")
							.recentMessageText("Hi~~~")
							.description("This is a welcome room 3.").build(),
					ChatRoom.builder().name("Room 1004").recentMessageAuthor("lamp1")
							.recentMessageText("Hi~~~")
							.description("This is a welcome room 4.").build(),
					ChatRoom.builder().name("Room 1005").recentMessageAuthor("lamp")
							.recentMessageText("Hi~~~")
							.description("This is a welcome room 5.").build(),
					ChatRoom.builder().name("Room 1006").recentMessageAuthor("lamp")
							.recentMessageText("Hi~~~")
							.description("This is a welcome room 6.").build(),
					ChatRoom.builder().name("Room 1007").recentMessageAuthor("lamp")
							.recentMessageText("Hi~~~")
							.description("This is a welcome room 7.").build(),
					ChatRoom.builder().name("Room 1008").recentMessageAuthor("lamp")
							.recentMessageText("Hi~~~")
							.description("This is a welcome room 8.").build(),
					ChatRoom.builder().name("Room 1009").recentMessageAuthor("lamp2")
							.recentMessageText("Hi~~~")
							.description("This is a welcome room 9.").build(),
					ChatRoom.builder().name("Room 10010").recentMessageAuthor("lamp2")
							.recentMessageText("Hi~~~")
							.description("This is a welcome room 10.").build());
			chatRoomRepository.save(rooms);
			log.info("chatRoomRepository.count(): {}", chatRoomRepository.count());
			Iterable<ChatRoom> chatRooms = chatRoomRepository.findAll();
			chatRooms.forEach(v -> log.info("{}", v));
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * page 는 0 부터 시작
	 * @throws Exception
	 */
	@Test
	public void ut1005_queryWithPageable() throws Exception {
		try {
			PageRequest pageable = new PageRequest(0, 5);
			Page<ChatRoom> page = chatRoomRepository.findAll(pageable);
			log.info("page number: {}", page.getNumber());
			log.info("page totalElements: {}", page.getTotalElements());
			log.info("page totalPages: {}", page.getTotalPages());
			log.info("page: {}", toJson(page.getContent()));

			PageRequest pageable2 = new PageRequest(2, 5);
			Page<ChatRoom> page2 = chatRoomRepository.findAll(pageable2);
			log.info("page2 number: {}", page2.getNumber());
			log.info("page2 totalElements: {}", page2.getTotalElements());
			log.info("page2 totalPages: {}", page2.getTotalPages());
			log.info("page2: {}", toJson(page2.getContent()));
			// page.forEach(v -> log.info("{}", v));
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1006_findByRecentMessageAuthor() throws Exception {
		try {
			// Sort sort = new Sort(Direction.ASC, "recentMessageDateTime");
			PageRequest pageable = new PageRequest(0, 5);
			Page<ChatRoom> page = chatRoomRepository.findByRecentMessageAuthor("lamp",
					pageable);
			log.info("page number: {}", page.getNumber());
			log.info("page totalElements: {}", page.getTotalElements());
			log.info("page totalPages: {}", page.getTotalPages());
			log.info("json: {}", toJson(page.getContent()));
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1007_findByRecentMessageAuthorAndRecentMessageText() throws Exception {
		try {
			// Sort sort = new Sort(Direction.ASC, "recentMessageDateTime");
			PageRequest pageable = new PageRequest(0, 5);
			Page<ChatRoom> page = chatRoomRepository
					.findByRecentMessageAuthorAndRecentMessageText("lamp", "Hi~~~",
							pageable);
			log.info("page number: {}", page.getNumber());
			log.info("page totalElements: {}", page.getTotalElements());
			log.info("page totalPages: {}", page.getTotalPages());
			log.info("json: {}", toJson(page.getContent()));
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1008_findByRecentMessageAuthorWithSort() throws Exception {
		try {
			final Sort sort = new Sort(Direction.ASC, "recentMessageDateTime");
			PageRequest pageable = new PageRequest(0, 5, sort);
			Page<ChatRoom> page = chatRoomRepository.findByRecentMessageAuthor("lamp",
					pageable);
			log.info("page number: {}", page.getNumber());
			log.info("page totalElements: {}", page.getTotalElements());
			log.info("page totalPages: {}", page.getTotalPages());
			log.info("json: {}", toJson(page.getContent()));
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1009_findByRecentMessageAuthorOrderByRecentMessageDateTime()
			throws Exception {
		try {
			List<ChatRoom> result = chatRoomRepository
					.findByRecentMessageAuthorOrderByRecentMessageDateTime("lamp");
			log.info("result.size(): {}", result.size());
			log.info("json: {}", toJson(result));
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
