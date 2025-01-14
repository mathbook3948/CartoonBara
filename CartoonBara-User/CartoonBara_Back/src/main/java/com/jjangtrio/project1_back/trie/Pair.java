package com.jjangtrio.project1_back.trie;

import java.io.Serializable;

import lombok.Data;

@Data
public class Pair<T, U>  implements Serializable{

    public T first;
    public U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }
}
