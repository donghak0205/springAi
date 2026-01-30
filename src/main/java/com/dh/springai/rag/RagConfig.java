package com.dh.springai.rag;

import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.document.DocumentReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        return new TokenTextSplitter();
    }
}
