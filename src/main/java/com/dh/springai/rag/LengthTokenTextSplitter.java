package com.dh.springai.rag;

import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class LengthTokenTextSplitter extends TextSplitter {

    private final int chunkSize;
    private final int chunkOverlap;

    public LengthTokenTextSplitter(int chunkSize, int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }


    @Override
    protected List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();

        if(!StringUtils.hasLength(text)){
            return chunks;
        }

        int textLength = text.length();
        if(textLength <= chunkOverlap){
            chunks.add(text);
            return chunks;
        }

        int position = 0;
        while(position < textLength){
            int end = Math.min(position + chunkSize, textLength);
            chunks.add(text.substring(position, end));
            int nextPosition = end - chunkOverlap;
            if(nextPosition <= position){
                break;
            }
            position = nextPosition;
        }
        return chunks;
    }
}
