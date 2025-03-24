package com.lezhin.lezhinchallenge.domain.work.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lezhin.lezhinchallenge.common.config.JwtTokenUtil;
import com.lezhin.lezhinchallenge.common.exception.custom.WorkNotFoundException;
import com.lezhin.lezhinchallenge.domain.history.service.HistoryService;
import com.lezhin.lezhinchallenge.domain.work.dto.WorkDto;
import com.lezhin.lezhinchallenge.domain.work.entity.WorkType;
import com.lezhin.lezhinchallenge.domain.work.service.WorkService;
import com.lezhin.lezhinchallenge.structure.auth.SecurityUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WorkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkService workService;

    @MockitoBean
    private HistoryService historyService;

    @MockitoBean
    private JwtTokenUtil jwtTokenUtil;

    @MockitoBean
    private SecurityUserService securityUserService;

    @Test
    @DisplayName("작품 목록 조회 - 성공")
    @WithMockUser(username = "1", roles = {"USER"})
    void getWorks_Success() throws Exception {
        // given
        WorkDto.WorkResponseDto work1 = createWorkResponseDto(1L, "작품1", "작가1", BigDecimal.valueOf(1000), WorkType.WEBTOON);
        WorkDto.WorkResponseDto work2 = createWorkResponseDto(2L, "작품2", "작가2", BigDecimal.valueOf(2000), WorkType.MANGA);

        Page<WorkDto.WorkResponseDto> workPage = new PageImpl<>(
                Arrays.asList(work1, work2),
                PageRequest.of(0, 20),
                2
        );

        when(workService.getWorks(any(Pageable.class))).thenReturn(workPage);

        // when & then
        mockMvc.perform(get("/api/works")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("작품1")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].title", is("작품2")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(workService, times(1)).getWorks(any(Pageable.class));
    }

    @Test
    @DisplayName("특정 작품 조회 - 성공")
    @WithMockUser(username = "1", roles = {"USER"})
    void getWork_Success() throws Exception {
        // given
        Long workId = 1L;
        Long userId = 1L;
        WorkDto.WorkResponseDto workDto = createWorkResponseDto(
                workId, "블랙", "테스트 작가", BigDecimal.valueOf(1000), WorkType.WEBTOON);

        when(workService.getWork(workId)).thenReturn(workDto);

        doNothing().when(historyService).saveViewHistory(eq(workId), eq(userId));

        // when & then
        mockMvc.perform(get("/api/works/{workId}", workId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("테스트 작품")))
                .andExpect(jsonPath("$.author", is("테스트 작가")))
                .andExpect(jsonPath("$.price", is(1000)));

        verify(workService, times(1)).getWork(workId);
        verify(historyService, times(1)).saveViewHistory(eq(workId), eq(userId));
    }

    @Test
    @DisplayName("존재하지 않는 작품 조회 - 실패")
    @WithMockUser(username = "111", roles = {"USER"})
    void getWork_NotFound() throws Exception {
        // given
        Long workId = 999L;
        when(workService.getWork(workId)).thenThrow(new WorkNotFoundException("ID가 " + workId + "인 작품을 찾을 수 없습니다"));

        // when & then
        mockMvc.perform(get("/api/works/{workId}", workId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("작품을 찾을 수 없습니다")));
        verify(workService, times(1)).getWork(workId);
        verify(historyService, never()).saveViewHistory(anyLong(), anyLong());
    }

    @Test
    @DisplayName("인기 작품 목록 조회 - 성공")
    @WithMockUser(username = "1", roles = {"USER"})
    void getPopularWorks_Success() throws Exception {
        // given
        WorkDto.WorkResponseDto work1 = createWorkResponseDto(1L, "인기 작품1", "작가1", BigDecimal.valueOf(1000), WorkType.WEBTOON);
        work1.setViewCount(1000);
        WorkDto.WorkResponseDto work2 = createWorkResponseDto(2L, "인기 작품2", "작가2", BigDecimal.valueOf(2000), WorkType.MANGA);
        work2.setViewCount(800);

        List<WorkDto.WorkResponseDto> popularWorks = Arrays.asList(work1, work2);

        when(workService.getPopularWorks()).thenReturn(popularWorks);

        // when & then
        mockMvc.perform(get("/api/works/popular")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("인기 작품1")))
                .andExpect(jsonPath("$[0].viewCount", is(1000)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("인기 작품2")))
                .andExpect(jsonPath("$[1].viewCount", is(800)));

        verify(workService, times(1)).getPopularWorks();
    }

    @Test
    @DisplayName("작품 생성 - 관리자 성공")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createWork_AdminSuccess() throws Exception {
        // given
        WorkDto.WorkCreateRequestDto requestDto = new WorkDto.WorkCreateRequestDto();
        requestDto.setTitle("new 작품");
        requestDto.setAuthor("new 작가");
        requestDto.setDescription("new 작품 설명");
        requestDto.setPrice(new BigDecimal("500"));
        requestDto.setType(WorkType.MANGA);
        requestDto.setThumbnailUrl("http://lezhin.com/new-thumbnail.jpg");

        WorkDto.WorkResponseDto responseDto = createWorkResponseDto(1L, "new 작품", "new 작가", new BigDecimal("500"), WorkType.MANGA);
        responseDto.setDescription("new 작품 설명");
        responseDto.setThumbnailUrl("http://lezhin.com/new-thumbnail.jpg");

        when(workService.createWork(any(WorkDto.WorkCreateRequestDto.class))).thenReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/works")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/works/1")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("new 작품")))
                .andExpect(jsonPath("$.author", is("new 작가")))
                .andExpect(jsonPath("$.price", is(500)))
                .andExpect(jsonPath("$.type", is("MANGA")));

        verify(workService, times(1)).createWork(any(WorkDto.WorkCreateRequestDto.class));
    }

    @Test
    @DisplayName("작품 수정 - 관리자 성공")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateWork_AdminSuccess() throws Exception {
        // given
        Long workId = 1L;
        WorkDto.WorkUpdateRequestDto requestDto = new WorkDto.WorkUpdateRequestDto();
        requestDto.setTitle("수정된 작품");
        requestDto.setAuthor("수정된 작가");
        requestDto.setDescription("수정된 작품설");
        requestDto.setPrice(new BigDecimal("1500"));
        requestDto.setType(WorkType.WEBTOON);
        requestDto.setThumbnailUrl("http://lezhin.com/updated-thumbnail.jpg");

        WorkDto.WorkResponseDto responseDto = createWorkResponseDto(workId, "수정된 작품", "수정된 작가", new BigDecimal("1500"), WorkType.WEBTOON);
        responseDto.setDescription("수정된 작품 설명");
        responseDto.setThumbnailUrl("http://lezhin.com/updated-thumbnail.jpg");

        // updateWork 메서드는 @CacheEvict가 적용되어 있음
        when(workService.updateWork(eq(workId), any(WorkDto.WorkUpdateRequestDto.class))).thenReturn(responseDto);

        // when & then
        mockMvc.perform(put("/api/works/{workId}", workId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("수정된 작품")))
                .andExpect(jsonPath("$.author", is("수정된 작가")))
                .andExpect(jsonPath("$.price", is(1500)))
                .andExpect(jsonPath("$.type", is("WEBTOON")));

        verify(workService, times(1)).updateWork(eq(workId), any(WorkDto.WorkUpdateRequestDto.class));

        // TODO 수정 후 캐시가 갱신되었으므로, getWork 호출 시 캐시 미스가 발생해야 함
    }

    @Test
    @DisplayName("작품 삭제 - 관리자 성공 (캐시 갱신 및 이력 삭제)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteWork_AdminSuccess() throws Exception {
        // given
        Long workId = 1L;

        doNothing().when(workService).deleteWork(workId);

        // when & then
        mockMvc.perform(delete("/api/works/{workId}", workId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(workService, times(1)).deleteWork(workId);
    }

    @Test
    @DisplayName("권한 없는 사용자의 작품 생성 시도 - 실패")
    @WithMockUser(username = "user", roles = {"USER"})
    void createWork_Forbidden() throws Exception {
        // given
        WorkDto.WorkCreateRequestDto requestDto = new WorkDto.WorkCreateRequestDto();
        requestDto.setTitle("새 작품");
        requestDto.setAuthor("새 작가");
        requestDto.setDescription("새 작품 설명");
        requestDto.setPrice(new BigDecimal("500"));
        requestDto.setType(WorkType.MANGA);
        requestDto.setThumbnailUrl("http://lezhin.com/new-thumbnail.jpg");

        // when & then
        mockMvc.perform(post("/api/works")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());

        verify(workService, never()).createWork(any(WorkDto.WorkCreateRequestDto.class));
    }

    @Test
    @DisplayName("인증되지 않은 사용자 요청 - 실패")
    @WithAnonymousUser
    void unauthenticatedRequest_Failure() throws Exception {
        // when & then
        mockMvc.perform(get("/api/works")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(workService, never()).getWorks(any(Pageable.class));
    }

    /**
     * 인기 구매 작품 목록 조회 테스트
     */
    @Test
    @DisplayName("인기 구매 작품 목록 조회 - 성공 (캐시 적용)")
    @WithMockUser(username = "1", roles = {"USER"})
    void getPopularPurchaseWorks_Success() throws Exception {
        // given
        WorkDto.WorkResponseDto work1 = createWorkResponseDto(1L, "인기 구매 작품1", "작가1", BigDecimal.valueOf(1000), WorkType.WEBTOON);
        work1.setPurchaseCount(500);
        WorkDto.WorkResponseDto work2 = createWorkResponseDto(2L, "인기 구매 작품2", "작가2", BigDecimal.valueOf(2000), WorkType.MANGA);
        work2.setPurchaseCount(400);

        List<WorkDto.WorkResponseDto> popularPurchaseWorks = Arrays.asList(work1, work2);

        when(workService.getPopularPurchaseWorks()).thenReturn(popularPurchaseWorks);

        // when & then
        mockMvc.perform(get("/api/works/popular-purchase")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("인기 구매 작품1")))
                .andExpect(jsonPath("$[0].purchaseCount", is(500)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("인기 구매 작품2")))
                .andExpect(jsonPath("$[1].purchaseCount", is(400)));

        verify(workService, times(1)).getPopularPurchaseWorks();
    }

    private WorkDto.WorkResponseDto createWorkResponseDto(
            Long id, String title, String author, BigDecimal price, WorkType type) {
        WorkDto.WorkResponseDto dto = new WorkDto.WorkResponseDto();
        dto.setId(id);
        dto.setTitle(title);
        dto.setAuthor(author);
        dto.setPrice(price);
        dto.setType(type);
        dto.setViewCount(0);
        dto.setPurchaseCount(0);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        dto.setCreatedBy("admin");
        return dto;
    }
}