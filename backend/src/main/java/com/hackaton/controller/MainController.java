package com.hackaton.controller;

import com.hackaton.Tools;
import com.hackaton.model.ClientRequest;
import com.hackaton.model.DefaultPhotoModel;
import com.hackaton.proccessor.PythonConnectProccesor;
import com.hackaton.service.Status;
import com.hackaton.service.StatusFactory;
import com.hackaton.service.Statuses;
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
    @Autowired
    Status status;
    @Autowired
    StatusFactory statusFactory;
    @CrossOrigin
    @PostMapping(value = "/receivePhoto", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String>> controller(@RequestBody ClientRequest clientRequest) throws IOException, InterruptedException {
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
    @CrossOrigin
    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String>> statusController(){
        while (statusFactory.getStatusInstance().getStatus() != Statuses.READY){
            var st = statusFactory.getStatusInstance().getStatus();
            if(st == Statuses.READY){
                LinkedHashMap<String,String> res = new LinkedHashMap<>();
                res.put("Message", status.getMessage());
                return new ResponseEntity<>(res, HttpStatus.OK);
            }
        }
        LinkedHashMap<String,String> res = new LinkedHashMap<>();
        res.put("Message", statusFactory.getStatusInstance().getMessage());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
