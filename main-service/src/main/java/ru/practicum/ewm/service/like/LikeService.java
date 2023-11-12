package ru.practicum.ewm.service.like;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.like.LikeRequestDto;
import ru.practicum.ewm.dto.like.LikeResponseDto;

import java.util.List;

public interface LikeService {
    List<LikeResponseDto> getLikesByUser(Long userId, Boolean like, Pageable page);

    LikeResponseDto saveLikeByUser(Long userId, LikeRequestDto requestDto);

    void deleteLikeById(Long userId, Long likeId);

    LikeResponseDto updateLikeByUser(Long userId, Long likeId, Boolean like);
}
