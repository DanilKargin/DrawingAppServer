package com.example.demo.service;

import com.example.demo.dto.WordDto;
import com.example.demo.entity.Word;
import com.example.demo.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordService {
    private final WordRepository wordRepository;

    private Word save(Word word){
        return wordRepository.save(word);
    }
    public Word findById(UUID id){
        return wordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Термин не найден!"));
    }
    public Optional<Word> findByTerm(String term){
        return wordRepository.findByTerm(term);
    }
    public List<WordDto> getRandomWords(){
        return wordRepository.getThreeRandomWord().stream().map(WordDto::new).collect(Collectors.toList());
    }
    public WordDto create(Word word){
        if(word.getTerm().isEmpty()){
            return null;
        }
        return new WordDto(save(word));
    }
}
