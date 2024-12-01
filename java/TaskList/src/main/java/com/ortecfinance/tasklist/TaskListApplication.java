package com.ortecfinance.tasklist;

import com.ortecfinance.tasklist.console.TaskList;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TaskListApplication {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Starting console Application");
            ApplicationContext context = new SpringApplicationBuilder(TaskListApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
            TaskList taskList = context.getBean(TaskList.class);
            TaskList.startConsole(taskList);
        } else {
            SpringApplication.run(TaskListApplication.class, args);
            System.out.println("localhost:8080/tasks");
        }
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
