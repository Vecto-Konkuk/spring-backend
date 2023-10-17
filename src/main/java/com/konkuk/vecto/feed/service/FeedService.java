package com.konkuk.vecto.feed.service;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.konkuk.vecto.feed.common.TimeDifferenceCalcuator;
import com.konkuk.vecto.feed.domain.FeedImage;
import com.konkuk.vecto.feed.domain.FeedMovement;
import com.konkuk.vecto.feed.domain.Feed;
import com.konkuk.vecto.feed.domain.FeedPlace;
import com.konkuk.vecto.feed.dto.request.FeedSaveRequest;
import com.konkuk.vecto.feed.dto.response.FeedResponse;
import com.konkuk.vecto.feed.repository.FeedRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

	private final FeedRepository feedRepository;
	private final TimeDifferenceCalcuator timeDifferenceCalcuator;

	@Transactional
	public Long saveFeed(FeedSaveRequest feedSaveRequest) {
		// TODO: 현재는 매번 날려서 저장하는 방식. 이를 Bulk Insert 형태로 변경해야함.
		List<FeedMovement> feedMovements = dtoToEntityIncludeIndex(feedSaveRequest.getMovements(), FeedMovement::new);
		List<FeedImage> feedImages = dtoToEntityIncludeIndex(feedSaveRequest.getImages(), FeedImage::new);
		List<FeedPlace> feedPlaces = dtoToEntityIncludeIndex(feedSaveRequest.getPlaces(), FeedPlace::new);

		Feed feed = Feed.builder()
			.title(feedSaveRequest.getTitle())
			.content(feedSaveRequest.getContent())
			.uploadTime(feedSaveRequest.getUploadTime())
			.feedMovements(feedMovements)
			.feedImages(feedImages)
			.feedPlaces(feedPlaces)
			.build();

		return feedRepository.save(feed).getId();
	}

	public FeedResponse getFeed(Long feedId) {
		Feed feed = feedRepository.findById(feedId).orElseThrow();

		String differ = timeDifferenceCalcuator.formatTimeDifferenceKorean(feed.getUploadTime());

		List<FeedResponse.Place> places = feed.getFeedPlaces().stream()
			.map(FeedResponse.Place::new).toList();

		List<FeedResponse.Movement> movements = feed.getFeedMovements().stream()
			.map(FeedResponse.Movement::new).toList();

		List<String> images = feed.getFeedImages().stream()
			.map(FeedImage::getUrl).toList();

		return FeedResponse.builder()
			.title(feed.getTitle())
			.content(feed.getContent())
			.timeDifference(differ)
			.places(places)
			.movements(movements)
			.images(images)
			.build();
	}

	// 리스트의 순서를 껴넣어서, DTO를 엔티티로 변환해주는 함수
	private static <T, R> List<R> dtoToEntityIncludeIndex(List<T> items, BiFunction<Long, T, R> mapper) {
		return IntStream.range(0, items.size())
			.mapToObj(index -> mapper.apply((long)index, items.get(index)))
			.toList();
	}
}