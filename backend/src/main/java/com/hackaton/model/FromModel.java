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

}
