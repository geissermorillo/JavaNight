package com.javanight.clientservice.model;

import lombok.Data;

@Data
public class Freelancer {
    private int id;
    private int rate;

    private String name;
    private String email;
    private String profession;
}
