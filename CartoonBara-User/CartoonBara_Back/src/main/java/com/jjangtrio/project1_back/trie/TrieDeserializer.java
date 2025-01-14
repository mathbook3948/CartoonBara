package com.jjangtrio.project1_back.trie;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class TrieDeserializer extends JsonDeserializer<Trie> {

    @Override
    public Trie deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        Trie trie = new Trie();

        if (rootNode != null) {
            TrieNode root = deserializeNode(rootNode);
            trie.setRoot(root);
        }

        return trie;
    }

    private TrieNode deserializeNode(JsonNode node) {
        TrieNode trieNode = new TrieNode();

        // `isEndOfWord` 필드 처리
        JsonNode isEndOfWordNode = node.get("isEndOfWord");
        if (isEndOfWordNode != null) {
            trieNode.isEndOfWord = isEndOfWordNode.asBoolean();
        }

        // `word` 필드 처리
        JsonNode wordNode = node.get("word");
        if (wordNode != null) {
            trieNode.word = wordNode.asText();
        }

        // `id` 필드 처리
        JsonNode idNode = node.get("id");
        if (idNode != null) {
            trieNode.id = idNode.asLong();
        }

        // `children` 필드 처리
        JsonNode childrenNode = node.get("children");
        if (childrenNode != null) {
            Iterator<Map.Entry<String, JsonNode>> fields = childrenNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                char key = entry.getKey().charAt(0);
                TrieNode childNode = deserializeNode(entry.getValue());
                trieNode.children.put(key, childNode);
            }
        }

        return trieNode;
    }
}
