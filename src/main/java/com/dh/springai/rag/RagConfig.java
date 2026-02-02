package com.dh.springai.rag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.document.DocumentWriter;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.document.DocumentReader;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;

@Configuration
public class RagConfig {

    @Bean
    public DocumentReader[] documentReaders(@Value("classpath:rag.pdf") String documentsLocationPattern) throws IOException {

        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(documentsLocationPattern);
        return Arrays.stream(resources).map(TikaDocumentReader::new).toArray(DocumentReader[]::new);
    }

    @Bean
    public DocumentTransformer tokenSplitter(){
        return new LengthTokenTextSplitter(200, 100);
    }

    @Bean
    public DocumentTransformer keyMetadataEnricher(ChatModel chatModel){
        return new KeywordMetadataEnricher(chatModel, 4);
    }

    @Bean
    public DocumentWriter jsonConsoleDocumentWriter(ObjectMapper objectMapper){
        return documents -> {
            System.out.println("====[INFO] Writing JSON Console document=======");
            try{
                System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(documents));
            } catch (JsonProcessingException e){
                throw new RuntimeException(e);
            }
            System.out.println("=========================================");
        };
    }

    @Bean
    public DocumentWriter vectorStore(EmbeddingModel embeddingModel){
        return SimpleVectorStore.builder(embeddingModel).build();
    }

}
