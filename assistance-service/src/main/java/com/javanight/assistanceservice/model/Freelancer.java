package com.javanight.assistanceservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Freelancer {
    private int id;
    private int rate;

    private String name;
    private String email;
    private String profession;
}
