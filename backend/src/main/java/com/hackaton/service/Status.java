package com.hackaton.service;

import lombok.Data;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Data
public class Status {
    private Statuses status;
    private String message;
}
