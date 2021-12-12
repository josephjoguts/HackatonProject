package com.hackaton.service;

import com.hackaton.model.FromModel;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Data
public class Status {
    private Statuses status;
    private FromModel message;
    private String taskEmotion;
}
