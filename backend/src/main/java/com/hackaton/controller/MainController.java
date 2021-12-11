package com.hackaton.controller;

import com.hackaton.Tools;
import com.hackaton.model.ClientRequest;
import com.hackaton.model.DefaultPhotoModel;
import com.hackaton.proccessor.PythonConnectProccesor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class MainController {
    @Autowired
    Tools config;
    @Autowired
    PythonConnectProccesor proccesor;
    @CrossOrigin
    @PostMapping(value = "/receivePhoto", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String>> controller(@RequestBody ClientRequest clientRequest) throws IOException {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("Status","Working");
        proccesor.processClientData(clientRequest.getPhotoCount(), clientRequest.getImageString());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
    @CrossOrigin
    @PostMapping(value = "/receiveDefaultPhoto", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String>> controller1(@RequestBody DefaultPhotoModel clientRequest) throws IOException {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("Status","Working");
        proccesor.setDefPhoto(clientRequest.getImageString());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}
