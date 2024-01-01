package com.MovieApp.Movie.App.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Id;


@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class UserDto {

    @Id
    private String email;
    private String name;
    private String password;
    private String phoneNo;
    private String imageName;
}
