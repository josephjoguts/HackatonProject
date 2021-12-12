package com.hackaton.model;

import lombok.Data;

@Data
public class FromModel {
    private String verified;
    private Float mean_dist;
    private String emo;
    private Integer num_right_photo;
    private Integer num_emo_faces;
    private Float mean_sim_dist;
    private Float open_images_time;
    private Float face_recognition_time;
    private Float emotion_recognition_time;

}
