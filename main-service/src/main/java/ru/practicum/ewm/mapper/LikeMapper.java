package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.like.LikeRequestDto;
import ru.practicum.ewm.dto.like.LikeResponseDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Like;
import ru.practicum.ewm.model.User;

@UtilityClass
public class LikeMapper {
    public static Like toLike(LikeRequestDto requestDto, User user, Event event) {
        Like like = new Like();
        like.setUser(user);
        like.setEvent(event);
        like.setLikeEvent(requestDto.getEventLike());
        return like;
    }

    public static LikeResponseDto toLikeResponseDto(Like like) {
        LikeResponseDto dto = new LikeResponseDto();
        dto.setId(like.getId());
        dto.setUser(like.getUser().getId());
        dto.setEvent(like.getEvent().getId());
        dto.setEventLike(like.getLikeEvent());
        return dto;
    }
}
