package com.jjangtrio.project1_back.trie;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = TrieNodeSerializer.class)
@JsonDeserialize(using = TrieNodeDeserializer.class)
public class TrieNode implements Serializable{

    Map<Character, TrieNode> children;
    boolean isEndOfWord;
    String word;  // 단어 저장
    Long id;      // 단어에 해당하는 ID 저장

    public TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
        word = null;
        id = null;
    }
}
