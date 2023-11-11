package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.model.App;

@Repository
public interface AppRepository extends JpaRepository<App, Long> {

}
