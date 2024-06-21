package com.example.backend.telegram;

import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repositoy.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

@Component
public class Bot extends TelegramLongPollingBot {
    private final UserRepository userRepository;

    @SneakyThrows
    @Autowired
    public Bot(TelegramBotsApi api, UserRepository userRepository) throws TelegramApiException {
        api.registerBot(this);
        this.userRepository = userRepository;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

//            Config

            Message message = update.getMessage();
            User currentUser = registerUser(message.getChatId());
            BotStep currentStep = currentUser.getBotStep();

            SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(message.getChatId());
            sendMessage.enableHtml(true);

//            If Block

            if (message.hasText()) {
                if (currentStep.equals(BotStep.START)) {
                    sendMessage.setText("Assalomu Alekum <b>" + update.getMessage().getFrom().getFirstName() + "</b> \uD83D\uDC4B! \n Tizimga Kirish Uchun Kontakt Uzotishni Bosing!");
                    currentUser.setBotStep(BotStep.SHARE_CONTACT);
                }else if(currentStep.equals(BotStep.MENU)) {

                }
            } else if (message.hasContact()) {
                if (currentStep.equals(BotStep.SHARE_CONTACT)) {
                    String phoneNumber = message.getContact().getPhoneNumber();
                    currentUser.setPhone(phoneNumber);

                    Optional<User> byPhone = userRepository.findByPhone(phoneNumber);

                    if(byPhone.isPresent()) {
                        User user = byPhone.get();
                        List<Role> roles = user.getRoles();
                        Boolean found = false;
                        for (Role role : roles) {
                            if(role.equals("ROlE_AGENT")) {
                                found = true;
                            }
                        }
                        if(found) {
                            sendMessage.setText("Assalomu Alekum Hurmatli Agent "+ message.getFrom().getFirstName()+" \uD83D\uDC4B! \n Joblink botiga Xush Kelibsiz!");
                        }
                    }else {
                        sendMessage.setText("Foydalanuvchi topilmadi! ❌");
                    }

                }
            }

//            Ending Of Block
            System.out.println(currentUser.getPhone()==null);
            userRepository.save(currentUser);
            execute(sendMessage);
        }
    }

    public User registerUser(Long chatId) {
        Optional<User> byChatId = userRepository.findByChatId(chatId);
        if(byChatId.isPresent()) {
            return byChatId.get();
        }else {
         return userRepository.save(new User(
                 null,
                 "",
                 null,
                 BotStep.START,
                 false,
                 chatId,
                 null,
                 null,
                 null
         ));
        }
    }


    @Override
    public String getBotUsername() {
        return "@JOBLINKBuX_bot";
    }

    @Override
    public String getBotToken() {
        return "6711743966:AAHwR0Vo1XJ_aHEjF8YUgbyY7wvPztsfoR0";
    }
}
