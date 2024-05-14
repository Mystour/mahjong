package org.example.mahjong.dto;

import lombok.Data;

import java.util.List;

@Data
public class DetermineEntity {

    private List<Option> options;

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> value) {
        options = value;
    }

}
