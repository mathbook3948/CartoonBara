package com.jjangtrio.project1_back.trie;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Map;

public class TrieNodeSerializer extends JsonSerializer<TrieNode> {

    @Override
    public void serialize(TrieNode value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        
        // 필드 직렬화
        gen.writeBooleanField("isEndOfWord", value.isEndOfWord);
        
        if (value.word != null) {
            gen.writeStringField("word", value.word);
        }
        
        if (value.id != null) {
            gen.writeNumberField("id", value.id);
        }
        
        // children 맵 직렬화
        if (!value.children.isEmpty()) {
            gen.writeFieldName("children");
            gen.writeStartObject();
            for (Map.Entry<Character, TrieNode> entry : value.children.entrySet()) {
                gen.writeFieldName(String.valueOf(entry.getKey()));
                serialize(entry.getValue(), gen, serializers);
            }
            gen.writeEndObject();
        }

        gen.writeEndObject();
    }
}
