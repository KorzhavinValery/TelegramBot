package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "mailing_task")
public class MailingTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "message")
    private String message;
    @Column(name = "date_time")
    private LocalDateTime dateTime;

    public MailingTask() {
    }

    public MailingTask(Long chatId, String message, LocalDateTime dateTime) {
        this.chatId = chatId;
        this.message = message;
        this.dateTime = dateTime;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MailingTask that = (MailingTask) o;
        return id == that.id && Objects.equals(chatId, that.chatId) && Objects.equals(message, that.message) && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, message, dateTime);
    }

}
