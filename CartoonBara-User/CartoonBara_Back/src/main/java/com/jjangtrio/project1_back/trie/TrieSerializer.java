package com.jjangtrio.project1_back.trie;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TrieSerializer extends JsonSerializer<Trie> {

    @Override
    public void serialize(Trie value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 트리를 JSON으로 변환할 때 루트부터 시작하여 재귀적으로 직렬화
        gen.writeStartObject();
        serializeNode(value.getRoot(), gen);
        gen.writeEndObject();
    }

    // TrieNode를 직렬화하는 함수
    private void serializeNode(TrieNode node, JsonGenerator gen) throws IOException {
        if (node == null) {
            return;
        }

        // 현재 노드가 단어 끝인지 확인
        gen.writeStartObject();
        
        // 단어 끝 여부
        gen.writeBooleanField("isEndOfWord", node.isEndOfWord);
        
        // 단어와 ID가 있으면 작성
        if (node.word != null) {
            gen.writeStringField("word", node.word);
        }
        if (node.id != null) {
            gen.writeNumberField("id", node.id);
        }

        // 자식 노드들 직렬화
        if (!node.children.isEmpty()) {
            gen.writeFieldName("children");
            gen.writeStartObject();
            for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                gen.writeFieldName(String.valueOf(entry.getKey()));
                serializeNode(entry.getValue(), gen);
            }
            gen.writeEndObject();
        }
        
        gen.writeEndObject();
    }
}
