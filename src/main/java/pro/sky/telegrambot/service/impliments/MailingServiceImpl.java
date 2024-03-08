package pro.sky.telegrambot.service.impliments;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.MailingTask;
import pro.sky.telegrambot.repository.MailingTaskRepository;
import pro.sky.telegrambot.service.MailingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MailingServiceImpl implements MailingService {
    private final TelegramBot telegramBot;
    private final MailingTaskRepository mailingTaskRepository;
    private final Logger logger = LoggerFactory.getLogger(MailingServiceImpl.class);
    private final Pattern MESSAGE_PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public MailingServiceImpl(TelegramBot telegramBot, MailingTaskRepository mailingTaskRepository) {
        this.telegramBot = telegramBot;
        this.mailingTaskRepository = mailingTaskRepository;
    }

    @Override
    public void process(Update update) {
    long chatId = update.message().chat().id();
    String messageFromUser = update.message().text();
        if (messageFromUser == null) {
            telegramBot.execute(new SendMessage(chatId, "Чтобы начать отправьте /start"));
            return;
        }
        if (messageFromUser.equals("/start")) {
            logger.info("Отправка ответа на сообщение /start");
            sendMessageToUserAfterStart(chatId);
            return;
        }
        Matcher matcher = MESSAGE_PATTERN.matcher(messageFromUser);
        if (matcher.find()) {
            formatValidationDate(chatId, matcher);
        } else {
            telegramBot.execute(new SendMessage(chatId, "Напоминание не соответствует формату - 'dd.MM.yyyy HH:mm текст напоминания'"));
            return;
        }
        LocalDateTime dateTime = LocalDateTime.parse(matcher.group(1), DATE_TIME_FORMATTER);
        String mailing = matcher.group(3);
        saveMessage(chatId, mailing, dateTime);

    }

    private void sendMessageToUserAfterStart(long chatId) {
        telegramBot
                .execute(new SendMessage(chatId, "Я могу напоминать Вам о запланированных делах! Добавьте напоминание в формате: 'dd.MM.yyyy HH:mm текст напоминания'"));
    }

    private void formatValidationDate(long chatId, Matcher matcher) {
        String dateOfMailing = matcher.group(1);
        LocalDateTime dateTime = LocalDateTime.parse(dateOfMailing, DATE_TIME_FORMATTER);
    }

    private void saveMessage(Long chatId, String mailing, LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            logger.warn("Напоминание не может быть создано в прошлом");
            telegramBot.execute(new  SendMessage(chatId, "По прошедшему времени нельзя отправить напоминание"));
            return;

        }
        MailingTask mailingTask = new MailingTask(chatId, mailing, dateTime);
        mailingTaskRepository.save(mailingTask);
        logger.info("Напоминание " + mailingTask + " успешно сохранено");
        telegramBot.execute(new SendMessage(chatId, "Напоминание " + " ' " + mailingTask + " ' " + " успешно сохранено"));
    }
    @Scheduled(cron = "0 0/1 * * * *")
    @Override
    public void mailingTaskFromDataBase() {
        List<MailingTask> tasks = mailingTaskRepository.
                findByDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        tasks.forEach(task -> {
            logger.info("Напоминание было отправлено");
            telegramBot.execute(new SendMessage(task.getChatId(), String.format("Привет! Не забудь:\n%s" + " , в %s",
                    task.getMessage(), task.getDateTime())));
    });
}}
