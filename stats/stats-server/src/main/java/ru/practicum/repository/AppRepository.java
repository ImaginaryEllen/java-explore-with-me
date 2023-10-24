package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.App;

@Repository
public interface AppRepository extends JpaRepository<App, Long> {

}
