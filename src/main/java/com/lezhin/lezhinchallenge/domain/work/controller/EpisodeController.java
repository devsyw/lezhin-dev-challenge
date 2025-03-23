package com.lezhin.lezhinchallenge.domain.work.controller;

import com.lezhin.lezhinchallenge.domain.work.dto.EpisodeDto;
import com.lezhin.lezhinchallenge.domain.work.service.EpisodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * 에피소드 관련 API 엔드포인트
 */
@RestController
@RequestMapping("/api/works/{workId}/episodes")
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeService episodeService;

    /**
     * 작품별 에피소드 목록 조회
     * @param workId 작품 ID
     * @param pageable 페이징 정보
     * @return 에피소드 목록
     */
    @GetMapping
    public ResponseEntity<Page<EpisodeDto.EpisodeResponseDto>> getEpisodes(
            @PathVariable Long workId,
            @PageableDefault(size = 20, sort = "episodeNumber") Pageable pageable) {

        return ResponseEntity.ok(episodeService.getEpisodesByWorkId(workId, pageable));
    }

    /**
     * 특정 에피소드 조회
     * @param workId 작품 ID
     * @param episodeId 에피소드 ID
     * @return 에피소드 정보
     */
    @GetMapping("/{episodeId}")
    public ResponseEntity<EpisodeDto.EpisodeResponseDto> getEpisode(
            @PathVariable Long workId,
            @PathVariable Long episodeId) {

        EpisodeDto.EpisodeResponseDto episode = episodeService.getEpisode(episodeId);

        // 요청한 작품 ID와 에피소드의 작품 ID가 일치하는지 확인
        if (!episode.getWorkId().equals(workId)) {
            return ResponseEntity.notFound().build();
        }

        // 에피소드 조회수 증가
        episodeService.increaseViewCount(episodeId);

        return ResponseEntity.ok(episode);
    }

    /**
     * 특정 에피소드 번호로 에피소드 조회
     * @param workId 작품 ID
     * @param episodeNumber 에피소드 번호
     * @return 에피소드 정보
     */
    @GetMapping("/number/{episodeNumber}")
    public ResponseEntity<EpisodeDto.EpisodeResponseDto> getEpisodeByNumber(
            @PathVariable Long workId,
            @PathVariable int episodeNumber) {

        EpisodeDto.EpisodeResponseDto episode = episodeService.getEpisodeByWorkIdAndNumber(workId, episodeNumber);

        // 에피소드 조회수 증가
        episodeService.increaseViewCount(episode.getId());

        return ResponseEntity.ok(episode);
    }

    /**
     * 무료 에피소드 목록 조회
     * @param workId 작품 ID
     * @return 무료 에피소드 목록
     */
    @GetMapping("/free")
    public ResponseEntity<List<EpisodeDto.EpisodeResponseDto>> getFreeEpisodes(@PathVariable Long workId) {
        return ResponseEntity.ok(episodeService.getFreeEpisodes(workId));
    }

    /**
     * 인기 에피소드 목록 조회
     * @param workId 작품 ID
     * @param limit 조회 개수
     * @return 인기 에피소드 목록
     */
    @GetMapping("/popular")
    public ResponseEntity<List<EpisodeDto.EpisodeResponseDto>> getPopularEpisodes(
            @PathVariable Long workId,
            @RequestParam(defaultValue = "5") int limit) {

        return ResponseEntity.ok(episodeService.getPopularEpisodes(workId, limit));
    }

    /**
     * 에피소드 생성
     * @param workId 작품 ID
     * @param requestDto 에피소드 생성 요청 정보
     * @return 생성된 에피소드 정보
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    public ResponseEntity<EpisodeDto.EpisodeResponseDto> createEpisode(
            @PathVariable Long workId,
            @Valid @RequestBody EpisodeDto.EpisodeCreateRequestDto requestDto) {

        // 요청한 작품 ID와 DTO의 작품 ID가 일치하는지 확인
        if (!workId.equals(requestDto.getWorkId())) {
            return ResponseEntity.badRequest().build();
        }

        EpisodeDto.EpisodeResponseDto createdEpisode = episodeService.createEpisode(requestDto);

        return ResponseEntity
                .created(URI.create("/api/works/" + workId + "/episodes/" + createdEpisode.getId()))
                .body(createdEpisode);
    }

    /**
     * 에피소드 수정
     * @param workId 작품 ID
     * @param episodeId 에피소드 ID
     * @param requestDto 에피소드 수정 요청 정보
     * @return 수정된 에피소드 정보
     */
    @PutMapping("/{episodeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    public ResponseEntity<EpisodeDto.EpisodeResponseDto> updateEpisode(
            @PathVariable Long workId,
            @PathVariable Long episodeId,
            @Valid @RequestBody EpisodeDto.EpisodeUpdateRequestDto requestDto) {

        EpisodeDto.EpisodeResponseDto episode = episodeService.getEpisode(episodeId);

        // 요청한 작품 ID와 에피소드의 작품 ID가 일치하는지 확인
        if (!episode.getWorkId().equals(workId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(episodeService.updateEpisode(episodeId, requestDto));
    }

    /**
     * 에피소드 삭제
     * @param workId 작품 ID
     * @param episodeId 에피소드 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{episodeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    public ResponseEntity<Void> deleteEpisode(
            @PathVariable Long workId,
            @PathVariable Long episodeId) {

        EpisodeDto.EpisodeResponseDto episode = episodeService.getEpisode(episodeId);

        // 요청한 작품 ID와 에피소드의 작품 ID가 일치하는지 확인
        if (!episode.getWorkId().equals(workId)) {
            return ResponseEntity.notFound().build();
        }

        episodeService.deleteEpisode(episodeId);

        return ResponseEntity.noContent().build();
    }
}