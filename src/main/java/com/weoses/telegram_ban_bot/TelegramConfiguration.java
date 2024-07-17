package com.weoses.telegram_ban_bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TelegramConfiguration {
    @Value("${bot.token}")
    private String token;

    @Value("${ban.forwarded.channel-ids}")
    private List<String> bannedChannels;


    @Bean
    BanBot botObject(){
        return new BanBot(token, bannedChannels);
    }

}
