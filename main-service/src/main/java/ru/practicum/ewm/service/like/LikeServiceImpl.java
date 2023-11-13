package ru.practicum.ewm.service.like;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.like.LikeRequestDto;
import ru.practicum.ewm.dto.like.LikeResponseDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.LikeMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Like;
import ru.practicum.ewm.model.QLike;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LikeRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    @Override
    public List<LikeResponseDto> getLikesByUser(Long userId, Boolean like, Pageable page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        QLike qLike = QLike.like;
        List<BooleanExpression> expressions = new ArrayList<>();
        expressions.add(qLike.user.eq(user));
        if (like != null) {
            expressions.add(qLike.likeEvent.eq(like));
        }
        Optional<BooleanExpression> predicate = expressions.stream()
                .reduce(BooleanExpression::and);
        List<Like> likes = predicate.map(BooleanExpression -> likeRepository.findAll(BooleanExpression, page).toList())
                .orElseGet(() -> likeRepository.findAllByUser(user));
        return likes.stream()
                .map(LikeMapper::toLikeResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public LikeResponseDto saveLikeByUser(Long userId, LikeRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(requestDto.getEvent())
                .orElseThrow(() -> new NotFoundException("Event with id=" + requestDto.getEvent() + " was not found"));
        if (event.getInitiator().equals(user)) {
            throw new ConflictException("Initiator cannot like their events");
        }
        Like checkLike = likeRepository.findByUserAndEvent(user, event);
        if (checkLike != null) {
            throw new ConflictException("User has already liked this event");
        }
        Like like = likeRepository.save(LikeMapper.toLike(requestDto, user, event));
        List<Like> likes = event.getLikes();
        likes.add(like);
        event.setLikes(likes);
        return LikeMapper.toLikeResponseDto(like);
    }

    @Override
    public void deleteLikeById(Long userId, Long likeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new NotFoundException("Like with id=" + likeId + " was not found"));
        if (!user.equals(like.getUser())) {
            throw new ConflictException("User can delete only their like");
        }
        likeRepository.delete(like);
        Event event = eventRepository.findById(like.getEvent().getId())
                .orElseThrow(() -> new NotFoundException("Event with id=" + like.getEvent().getId() + " was not found"));
        List<Like> likes = event.getLikes();
        likes.remove(like);
        event.setLikes(likes);
    }

    @Override
    public LikeResponseDto updateLikeByUser(Long userId, Long likeId, Boolean newLike) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new NotFoundException("Like with id=" + likeId + " was not found"));
        if (!user.equals(like.getUser())) {
            throw new ConflictException("User can update only their like");
        }
        like.setLikeEvent(newLike);
        Event event = eventRepository.findById(like.getEvent().getId())
                .orElseThrow(() -> new NotFoundException("Event with id=" + like.getEvent().getId() + " was not found"));
        List<Like> likes = event.getLikes();
        likes.stream().filter(l -> l.getUser().equals(user)).forEach(l -> {
            event.getLikes().remove(l);
            event.getLikes().add(like);
        });
        event.setLikes(likes);
        return LikeMapper.toLikeResponseDto(like);
    }
}
