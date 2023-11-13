package ru.practicum.ewm.controller.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.like.LikeRequestDto;
import ru.practicum.ewm.dto.like.LikeResponseDto;
import ru.practicum.ewm.service.like.LikeService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/likes")
@RequiredArgsConstructor
@Slf4j
@Validated
public class LikePrivateController {
    private final LikeService likeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LikeResponseDto> getAllByUser(@PathVariable Long userId,
                                              @RequestParam(required = false) Boolean like,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting likes by user id: {}", userId);
        return likeService.getLikesByUser(userId, like, PageRequest.of(from > 0 ? from / size : 0, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LikeResponseDto post(@PathVariable Long userId,
                                @Valid @RequestBody LikeRequestDto requestDto) {
        log.info("Saving new like to event with id: {}, from user with id: {} - STARTED", requestDto.getEvent(), userId);
        LikeResponseDto likeResponseDto = likeService.saveLikeByUser(userId, requestDto);
        log.info("Saving new like: {} - FINISHED", likeResponseDto);
        return likeResponseDto;
    }

    @DeleteMapping("/{likeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long likeId) {
        log.info("Deleting user by id: {}", likeId);
        likeService.deleteLikeById(userId, likeId);
    }

    @PatchMapping("/{likeId}")
    @ResponseStatus(HttpStatus.OK)
    public LikeResponseDto patch(@PathVariable Long userId,
                                 @PathVariable Long likeId,
                                 @RequestParam Boolean like) {
        log.info("Updating like by id: {} from user with id: {}", likeId, userId);
        return likeService.updateLikeByUser(userId, likeId, like);
    }
}