package com.example.demo_batch.config;

import com.example.demo_batch.student.Student;
import org.springframework.batch.item.ItemProcessor;


// the processor: the logic of processing the data (in our case the input is Student and the output is Student)
public class StudentProcessor implements ItemProcessor<Student, Student> {
    @Override
    public Student process(Student student) throws Exception {
        // all business logic to process data
        student.setFirstname(student.getFirstname().toUpperCase());
        student.setLastname(student.getLastname().toUpperCase());
        return student;
    }
}
