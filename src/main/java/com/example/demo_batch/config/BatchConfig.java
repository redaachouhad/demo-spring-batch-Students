package com.example.demo_batch.config;

import com.example.demo_batch.student.Student;
import com.example.demo_batch.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final StudentRepository repository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    //1. the reader: reading csv file and transform it into student structure
    @Bean
    public FlatFileItemReader<Student> itemReader(){
        FlatFileItemReader<Student> itemReader= new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/students.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Student> lineMapper() {
        DefaultLineMapper<Student> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstname", "lastname", "age");

        BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Student.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }
    //2. the processor: the logic of processing step
    @Bean
    public StudentProcessor processor(){
        return new StudentProcessor();
    }

    //3. the writer: for writing the data processed data in the database
    @Bean
    public RepositoryItemWriter<Student> writer(){
        RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    //4. defining step:
    @Bean
    public Step importStep(){
        return new StepBuilder("csvImport", jobRepository)
                .<Student, Student>chunk(10, platformTransactionManager) // the number of line to process
                .reader(itemReader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    //5. Creating job
    @Bean
    public Job runJob(){
        return new JobBuilder("importStudents", jobRepository)
                .start(importStep())
//                .next() // to pass the next step
                .build();
    }
}
