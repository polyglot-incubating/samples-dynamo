package org.chiwooplatform.samples.dam.dynamo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.chiwooplatform.samples.entity.ChatRoom;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBPagingAndSortingRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;

@EnableScan
@EnableScanCount
public interface ChatRoomRepository
		extends DynamoDBPagingAndSortingRepository<ChatRoom, String> {
	Page<ChatRoom> findByRecentMessageAuthor(String recentMessageAuthor,
			Pageable pageable);

	Page<ChatRoom> findByRecentMessageAuthorAndRecentMessageText(
			String recentMessageAuthor, String recentMessageText, Pageable pageable);

	List<ChatRoom> findByRecentMessageAuthorOrderByRecentMessageDateTime(
			String recentMessageAuthor);

}
