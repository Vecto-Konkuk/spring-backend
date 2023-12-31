package com.konkuk.vecto.feed.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.konkuk.vecto.feed.domain.Feed;
import com.konkuk.vecto.feed.domain.FeedImage;
import com.konkuk.vecto.feed.domain.FeedQueue;

@Repository
public interface FeedQueueRepository extends JpaRepository<FeedQueue, Long> {

	@Query("SELECT fq FROM FeedQueue fq WHERE fq.userId = :userId AND fq.createdAt >= :dateTime "
		+ "ORDER BY fq.createdAt DESC")
	List<FeedQueue> findFeedIdByUserId(Pageable pageable, Long userId, LocalDateTime dateTime);
}
