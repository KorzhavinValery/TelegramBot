package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.model.Update;

public interface MailingService {
   void process(Update update);

   void mailingTaskFromDataBase();
}
