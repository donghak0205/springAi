package com.dh.springai.rag;

import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.List;


public class LengthTokenTextSplitter extends TextSplitter {

    @Override
    protected List<String> splitText(String text) {
        return List.of();
    }
}
