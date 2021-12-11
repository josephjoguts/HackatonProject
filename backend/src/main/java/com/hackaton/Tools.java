package com.hackaton;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Base64;

@Configuration
public class Tools {
    private JsonMapper mapper;
    private Base64.Decoder decoder;
    private Base64.Encoder encoder;
    @PostConstruct
    private void generateConfig(){
        mapper = new JsonMapper();
        decoder =  Base64.getDecoder();
        encoder = Base64.getEncoder();
    }

    public JsonMapper getMapper() {
        return mapper;
    }

    public Base64.Decoder getDecoder() {
        return decoder;
    }

    public Base64.Encoder getEncoder() {
        return encoder;
    }
}
