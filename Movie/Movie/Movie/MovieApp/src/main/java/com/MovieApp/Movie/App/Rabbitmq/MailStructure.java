package com.MovieApp.Movie.App.Rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MailStructure {
    private String subject;
    private String message;
}
