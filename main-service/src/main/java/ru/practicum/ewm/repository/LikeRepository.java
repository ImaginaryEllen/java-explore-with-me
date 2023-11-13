package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Like;
import ru.practicum.ewm.model.User;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long>, QuerydslPredicateExecutor<Like> {
    Like findByUserAndEvent(User user, Event event);

    List<Like> findAllByUser(User user);
}
