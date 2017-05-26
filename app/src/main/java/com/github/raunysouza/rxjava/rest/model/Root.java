package com.github.raunysouza.rxjava.rest.model;

import java.util.List;

/**
 * @author raunysouza
 */
public class Root {

    private int count;
    private String next;
    private String previous;
    private List<Character> results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<Character> getResults() {
        return results;
    }

    public void setResults(List<Character> results) {
        this.results = results;
    }
}
