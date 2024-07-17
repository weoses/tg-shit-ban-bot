package com.weoses.telegram_ban_bot;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.origin.MessageOrigin;
import com.pengrad.telegrambot.model.message.origin.MessageOriginChannel;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class BanBot {
    private final String token;
    private final List<String> channelsForwardBan;
    private TelegramBot bot;

    @PostConstruct
    void init(){
        bot = new TelegramBot(token);
        bot.setUpdatesListener(this::updatesListener);
        log.info("Init bot {}", bot);
    }

    int updatesListener(List<Update> updates){
        for (Update update : updates) {
            try {
                processUpdate(update);
            } catch (MessageBanned e){
                log.info("Update banned: id={}", update.updateId());
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


    void processUpdate(Update update){
        Message message = update.message();
        log.debug("Received message {}", message);
        if(message == null) return;
        processChannelForwarded(message);
    }

    void processChannelForwarded(Message message){
        MessageOrigin messageOrigin = message.forwardOrigin();
        if (!(messageOrigin instanceof MessageOriginChannel channelForwardedMessage)) return;
        Long chatFromId = channelForwardedMessage.chat().id();
        log.info("Check forward ban for message id={} forwardedId={}", message.messageId(), chatFromId);

        if (channelsForwardBan.contains(chatFromId.toString())){
            banMessage(message);
            throw new MessageBanned();
        }

    }

    void banMessage(Message m){
        log.info("Ban message id={} text={}", m.messageId(), m.text());
        BaseResponse deleteResponseResult = bot.execute(new DeleteMessage(m.chat().id(), m.messageId()));
        log.info("Ban message result: status={}, description={} ", deleteResponseResult.isOk(), deleteResponseResult.description());
    }

    static class MessageBanned extends RuntimeException{}

}
