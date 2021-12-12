package com.hackaton.service;

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
    public void refreshStatus(Statuses st, String value){
        synchronized (this) {
            statusInstance = new Status();
            statusInstance.setStatus(st);
            statusInstance.setMessage(value);
        }
    }
}
