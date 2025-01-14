package com.jjangtrio.project1_back.trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Trie implements Serializable {

    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // 단어 삽입
    public void insert(String word, Long id) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
        }
        // 해당 노드에 검색어와 ID 저장
        current.isEndOfWord = true;
        current.word = word;
        current.id = id;
    }

    // 단어 검색
    public Long search(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return null; // 단어가 없으면 null 반환
            }
            current = current.children.get(ch);
        }
        return current.isEndOfWord ? current.id : null; // 단어 끝이면 ID 반환
    }

    // 접두사로 시작하는 모든 단어를 반환 (중간에 끝나는 단어 포함)
    // 접두사로 시작하는 모든 단어를 반환 (중간에 끝나는 단어 포함)
    public List<Pair<String, Long>> getWordsWithPrefix(String prefix) {
        List<Pair<String, Long>> result = new ArrayList<>();
    
        // 예외 처리
        if (prefix == null || prefix.isEmpty()) {
            findWordsFromNode(root, result);
            return result;
        }
    
        TrieNode current = root;
    
        // 접두사를 따라 Trie 탐색
        for (char ch : prefix.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return result; // 접두사가 없으면 빈 리스트 반환
            }
            current = current.children.get(ch);
        }
    
        // 현재 노드부터 단어 찾기
        findWordsFromNode(current, result);
        return result;
    }

    // 주어진 노드부터 가능한 모든 단어를 찾는 메서드
    private void findWordsFromNode(TrieNode node, List<Pair<String, Long>> result) {
        // 만약 현재 노드가 단어의 끝이라면, 현재 단어와 ID를 결과 리스트에 추가
        if (node.isEndOfWord) {
            result.add(new Pair<>(node.word, node.id));
        }

        // 자식 노드를 재귀적으로 탐색
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            findWordsFromNode(entry.getValue(), result);
        }
    }

    // 단어 삭제
    public boolean delete(String word) {
        return deleteHelper(root, word, 0);
    }

    private boolean deleteHelper(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord) {
                return false; // 단어가 없음
            }
            current.isEndOfWord = false;
            return current.children.isEmpty(); // 자식이 없으면 삭제 가능
        }

        char ch = word.charAt(index);
        TrieNode node = current.children.get(ch);
        if (node == null) {
            return false; // 단어가 없음
        }

        boolean shouldDeleteCurrentNode = deleteHelper(node, word, index + 1);

        if (shouldDeleteCurrentNode) {
            current.children.remove(ch);
            return current.children.isEmpty() && !current.isEndOfWord;
        }

        return false;
    }
}
