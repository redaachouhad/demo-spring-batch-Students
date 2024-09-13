package com.example.demo_batch.student;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Student {
    @Id
    @GeneratedValue
    private Integer id;
    private String firstname;
    private String lastname;
    private int age;
}
