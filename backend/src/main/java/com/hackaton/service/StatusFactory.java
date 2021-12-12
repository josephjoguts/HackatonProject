package com.hackaton.service;

import com.hackaton.model.FromModel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StatusFactory {
    private volatile Status statusInstance;

    public Status getStatusInstance() {
        return statusInstance;
    }

    @PostConstruct
    private void constructFactory(){
        statusInstance = new Status();
    }
    public void refreshStatus(Statuses st, FromModel value, String taskEmotion){
        synchronized (this) {
            statusInstance = new Status();
            statusInstance.setStatus(st);
            statusInstance.setMessage(value);
        }
    }
}
