package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.MailingTask;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface  MailingTaskRepository extends JpaRepository<MailingTask, Long> {
    List<MailingTask> findByDateTime(LocalDateTime dateTime);

}
