package com.lezhin.lezhinchallenge.domain.work.service;

import com.lezhin.lezhinchallenge.common.exception.ErrorCode;
import com.lezhin.lezhinchallenge.common.exception.custom.DuplicateValueException;
import com.lezhin.lezhinchallenge.common.exception.custom.EpisodeNotFoundException;
import com.lezhin.lezhinchallenge.common.exception.custom.WorkNotFoundException;
import com.lezhin.lezhinchallenge.domain.work.dto.EpisodeDto;
import com.lezhin.lezhinchallenge.domain.work.entity.Episode;
import com.lezhin.lezhinchallenge.domain.work.entity.Work;
import com.lezhin.lezhinchallenge.domain.work.repository.EpisodeRepository;
import com.lezhin.lezhinchallenge.domain.work.repository.WorkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 에피소드 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final WorkRepository workRepository;

    /**
     * 작품별 에피소드 목록 조회
     */
    @Cacheable(value = "episodes", key = "'work_' + #workId")
    public Page<EpisodeDto.EpisodeResponseDto> getEpisodesByWorkId(Long workId, Pageable pageable) {
        // 작품 존재 여부 확인
        if (!workRepository.existsById(workId)) {
            throw new WorkNotFoundException("ID가 " + workId + "인 작품을 찾을 수 없습니다");
        }

        return episodeRepository.findByWorkId(workId, pageable)
                .map(EpisodeDto.EpisodeResponseDto::from);
    }

    /**
     * 특정 에피소드 조회
     */
    @Cacheable(value = "episodes", key = "#episodeId")
    public EpisodeDto.EpisodeResponseDto getEpisode(Long episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new EpisodeNotFoundException("ID가 " + episodeId + "인 에피소드를 찾을 수 없습니다"));

        return EpisodeDto.EpisodeResponseDto.from(episode);
    }

    /**
     * 작품의 특정 에피소드 조회
     */
    @Cacheable(value = "episodes", key = "'work_' + #workId + '_episode_' + #episodeNumber")
    public EpisodeDto.EpisodeResponseDto getEpisodeByWorkIdAndNumber(Long workId, int episodeNumber) {
        Episode episode = episodeRepository.findByWorkIdAndEpisodeNumber(workId, episodeNumber)
                .orElseThrow(() -> new EpisodeNotFoundException(
                        "작품 ID " + workId + "의 " + episodeNumber + "화 에피소드를 찾을 수 없습니다"));

        return EpisodeDto.EpisodeResponseDto.from(episode);
    }

    /**
     * 무료 에피소드 목록 조회
     */
    @Cacheable(value = "freeEpisodes", key = "#workId")
    public List<EpisodeDto.EpisodeResponseDto> getFreeEpisodes(Long workId) {
        // 작품 존재 여부 확인
        if (!workRepository.existsById(workId)) {
            throw new WorkNotFoundException("ID가 " + workId + "인 작품을 찾을 수 없습니다");
        }

        return episodeRepository.findByWorkIdAndIsFreeTrue(workId).stream()
                .map(EpisodeDto.EpisodeResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 에피소드 생성
     */
    @Transactional
    @CacheEvict(value = {"episodes", "freeEpisodes"}, allEntries = true)
    public EpisodeDto.EpisodeResponseDto createEpisode(EpisodeDto.EpisodeCreateRequestDto requestDto) {
        Work work = workRepository.findById(requestDto.getWorkId())
                .orElseThrow(() -> new WorkNotFoundException("ID가 " + requestDto.getWorkId() + "인 작품을 찾을 수 없습니다"));

        // 이미 해당 에피소드 번호가 존재하는지 확인
        if (episodeRepository.findByWorkIdAndEpisodeNumber(work.getId(), requestDto.getEpisodeNumber()).isPresent()) {
            throw new DuplicateValueException(ErrorCode.DUPLICATE_EPISODE_NUMBER,
                    "작품 ID " + work.getId() + "의 " + requestDto.getEpisodeNumber() + "화 에피소드가 이미 존재합니다");
        }

        Episode episode = Episode.builder()
                .work(work)
                .title(requestDto.getTitle())
                .episodeNumber(requestDto.getEpisodeNumber())
                .content(requestDto.getContent())
                .price(requestDto.getPrice())
                .isFree(requestDto.isFree())
                .build();

        Episode savedEpisode = episodeRepository.save(episode);

        // 작품에 에피소드 추가
        work.addEpisode(savedEpisode);

        return EpisodeDto.EpisodeResponseDto.from(savedEpisode);
    }

    /**
     * 에피소드 수정
     */
    @Transactional
    @CacheEvict(value = {"episodes", "freeEpisodes"}, key = "#episodeId")
    public EpisodeDto.EpisodeResponseDto updateEpisode(Long episodeId, EpisodeDto.EpisodeUpdateRequestDto requestDto) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new EpisodeNotFoundException("ID가 " + episodeId + "인 에피소드를 찾을 수 없습니다"));

        episode.update(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getPrice(),
                requestDto.isFree()
        );

        return EpisodeDto.EpisodeResponseDto.from(episode);
    }

    /**
     * 에피소드 삭제
     */
    @Transactional
    @CacheEvict(value = {"episodes", "freeEpisodes"}, allEntries = true)
    public void deleteEpisode(Long episodeId) {
        if (!episodeRepository.existsById(episodeId)) {
            throw new EpisodeNotFoundException("ID가 " + episodeId + "인 에피소드를 찾을 수 없습니다");
        }

        episodeRepository.deleteById(episodeId);
    }

    /**
     * 에피소드 조회수 증가
     */
    @Transactional
    @CacheEvict(value = "episodes", key = "#episodeId")
    public void increaseViewCount(Long episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new EpisodeNotFoundException("ID가 " + episodeId + "인 에피소드를 찾을 수 없습니다"));

        episode.increaseViewCount();
    }

    /**
     * 인기 에피소드 목록 조회
     */
    @Cacheable(value = "popularEpisodes", key = "#workId")
    public List<EpisodeDto.EpisodeResponseDto> getPopularEpisodes(Long workId, int limit) {
        // 작품 존재 여부 확인
        if (!workRepository.existsById(workId)) {
            throw new WorkNotFoundException("ID가 " + workId + "인 작품을 찾을 수 없습니다");
        }

        return episodeRepository.findTopEpisodesByViewCount(workId, limit).stream()
                .map(EpisodeDto.EpisodeResponseDto::from)
                .collect(Collectors.toList());
    }
}