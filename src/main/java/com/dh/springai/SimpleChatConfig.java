package com.dh.springai;


import ch.qos.logback.classic.LoggerContext;
import com.dh.springai.service.ChatService;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Valid;
import java.util.Scanner;


import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class SimpleChatConfig {

    @Bean
    public SimpleLoggerAdvisor simpleLoggerAdvisor() { ///요청, 응답과정을 콘솔에 남겨서 디버깅하려는 용도
        return SimpleLoggerAdvisor.builder().build();
    }


    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().maxMessages(10).build();
    }

    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        //요청이 들어오면 chatMemory에서 검색 후 모델에게 보냄
        //모델이 그걸 기반으로 답변을 주면 저장함
        return MessageChatMemoryAdvisor.builder(chatMemory).build();
    }

    @ConditionalOnProperty(prefix = "spring.application", name = "cli", havingValue = "true")
    @Bean
    public CommandLineRunner cli(@Value("${spring.application.name}") String applicationName, ChatService chatService) {
        return args -> {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));

            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            context.getLogger("ROOT").detachAppender("CONSOLE");

            System.out.println("\n" + applicationName + "CLI Chat Bot");

            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    System.out.print("\nUser: ");
                    String userMessage = scanner.nextLine();
                    chatService.stream(Prompt.builder().content(userMessage).build(), "cli")
                            .doFirst(() -> System.out.print("\nAssistant: "))
                            .doOnNext(System.out::print).doOnComplete(System.out::println).blockLast();
                }
            }
        };

    }

}
