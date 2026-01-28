package com.dh.springai.service;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder, Advisor[] advisors) {
        this.chatClient = chatClientBuilder.defaultAdvisors(advisors).build();
    }

    public Flux<String> stream(Prompt prompt, String conversationId){
        return buildChatClientRequestSpec(prompt, conversationId).stream().content();
    }

    private ChatClient.ChatClientRequestSpec buildChatClientRequestSpec(Prompt prompt, String conversationId) {
        return chatClient.prompt(prompt)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId));
    }

    public ChatResponse call(Prompt prompt, String conversationId){
        return buildChatClientRequestSpec(prompt, conversationId).call().chatResponse();
    }

    //Structured Output Converter
    public enum Emotion {VERY_NEGATIVE, NEGATIVE, NEUTRAL, POSITIVE, VERY_POSITIVE}

    public record EmotionEvaluation(Emotion emotion, List<String> reason){
    }

    public EmotionEvaluation callEmotionEvaluation(Prompt prompt, String conversationId){
        return buildChatClientRequestSpec(prompt, conversationId).call().entity(EmotionEvaluation.class);
    }
}
