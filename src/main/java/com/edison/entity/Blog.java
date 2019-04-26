package com.edison.entity;

public class Blog {
    public Blog() {
    }
    public Blog(Integer bid, String name, Integer authorId) {
        this.bid = bid;
        this.name = name;
        this.authorId = authorId;
    }

    private Integer bid;

    private String name;

    private Integer authorId;

    public Integer getBid() {
        return bid;
    }

    public void setBid(Integer bid) {
        this.bid = bid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }
}