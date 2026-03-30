package com.rubilia.exercise201.dto;

import com.rubilia.exercise201.entity.SentimentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SentimentAnalysisResult {
    private SentimentType sentiment;
    private Double score;
    private String explanation;
}
