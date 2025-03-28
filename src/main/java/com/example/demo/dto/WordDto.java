package com.example.demo.dto;

import com.example.demo.entity.Word;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WordDto {
    private String id;
    private String term;
    private String description;

    public WordDto(Word word){
        this.id = word.getId().toString();
        this.term = word.getTerm();
        this.description = word.getDescription();
    }
}
