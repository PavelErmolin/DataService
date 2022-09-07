package com.example.redishamster.Model;

import DTO.Data;

import java.util.ArrayList;
import java.util.List;

public class HamsterData {
    private List<Data> data = new ArrayList<>();

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
