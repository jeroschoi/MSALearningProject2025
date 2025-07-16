package com.event.msalearningproject.example.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
public class SampleDto {
    private long id;
    private String content;
}
