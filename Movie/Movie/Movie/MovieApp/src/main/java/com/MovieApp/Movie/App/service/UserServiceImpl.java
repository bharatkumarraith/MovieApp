package com.MovieApp.Movie.App.service;

import com.MovieApp.Movie.App.Rabbitmq.MailStructure;
import com.MovieApp.Movie.App.config.MovieDTO;
import com.MovieApp.Movie.App.domain.Movie;
import com.MovieApp.Movie.App.domain.User;
import com.MovieApp.Movie.App.domain.UserDto;
import com.MovieApp.Movie.App.exception.MovieAlreadyExist;
import com.MovieApp.Movie.App.exception.MovieException;
import com.MovieApp.Movie.App.exception.UserAlreadyException;
import com.MovieApp.Movie.App.exception.UserException;
import com.MovieApp.Movie.App.proxy.MovieProxy;
import com.MovieApp.Movie.App.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.bson.json.JsonObject;
import org.json.simple.JSONObject;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private MovieProxy movieProxy;
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MovieProxy movieProxy, RabbitTemplate rabbitTemplate, DirectExchange directExchange) {
        this.userRepository = userRepository;
        this.movieProxy = movieProxy;
    }


    @Override
    public String uploadImage(MultipartFile file) throws IOException {

        // Check if the file is empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        // Construct the complete path to store the file
        String directoryPath = "E:\\Project\\Movieplex\\movieflex\\src\\assets";
        String fileName = file.getOriginalFilename();
        String filePath = directoryPath + File.separator + fileName;

        // Create the directory if it does not exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdir();
        }

        // Save the file to the specified directory
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        // Return the complete path of the saved file
        System.out.println(fileName);
        return fileName;
    }

    @Override
    public User addUser(User user) throws UserAlreadyException {
        if (userRepository.findById(user.getEmail()).isEmpty()) {
            UserDto userDto = new UserDto(user.getName(), user.getEmail(),
                    user.getPassword(), user.getPhoneNo(), user.getImageName());
            movieProxy.registerUser(userDto);
            return userRepository.save(user);







        } else {
            throw new UserAlreadyException();
        }
    }


//    private void sendRegistrationEmail(String to, String subject, String body) {
//        // Replace these values with your actual email server details
//        String host = "smtp.gmail.com";
//        String username = "bharatkumarraithi5@gmail.com";
//        String password = "dybxxscadwkgkrbd";
//
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", host);
//        props.put("mail.smtp.port", "587");
//
//        Session session = Session.getInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username, password);
//            }
//        });
//
//        try {
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(username));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
//            message.setSubject(subject);
//            message.setText(body);
//
//            Transport.send(message);
//
//            System.out.println("Email sent successfully!");
//
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            // Handle the exception appropriately
//        }
//    }



//    private boolean sendWelcomeEmail(String email) {
//        try {
//            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//            simpleMailMessage.setFrom("bharatkumarraithi5@gmail.com");
//            simpleMailMessage.setText("Hii Dear,");
//            simpleMailMessage.setSubject("Welcome To MovieApp");
//            simpleMailMessage.setTo(email);
//            javaMailSender.send(simpleMailMessage);
//            return true;
//        } catch (Exception e) {
//            // Log the exception for debugging
//            e.printStackTrace();
//            return false;
//        }
//    }

    @Override
    public User updateUserWithEmail(MultipartFile file, String userEmail, String userJson) throws IOException {
        // Find the user by email
        User existingUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("user error"));

        // Update user details
        User updatedUser = new ObjectMapper().readValue(userJson, User.class);
        existingUser.setName(updatedUser.getName());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setPhoneNo(updatedUser.getPhoneNo());
        // ... other fields you want to update

        // Update favorite movies
        List<Movie> favoriteMovies = updatedUser.getFavouriteMovielist();
        if (favoriteMovies != null) {
            existingUser.setFavouriteMovielist(favoriteMovies);
        }

        // Update image details
        byte[] imageBytes = file.getBytes();
        existingUser.setImagePath(imageBytes);

        String originalFilename = file.getOriginalFilename();
        String newFileName = FilenameUtils.getBaseName(originalFilename)
                + "_" + Instant.now().toEpochMilli()
                + "." + FilenameUtils.getExtension(originalFilename);

        existingUser.setImageName(newFileName);

        return userRepository.save(existingUser);
    }



    @Override
    public boolean deleteUser(String id) throws UserException {
        if (!userRepository.findById(id).isEmpty()) {
            movieProxy.deleteUser(id);
            userRepository.deleteById(id);
            return true;
        } else {
            throw new UserException();
        }
    }

    @Override
    public List<Movie> getAllFavouriteMovie(String id) throws UserException {
        if (userRepository.findById(id).isPresent()) {
            return userRepository.findById(id).get().getFavouriteMovielist();
        } else {
            throw new UserException();
        }
    }

    @Override
    public User addMovieToFavorite(String email, Movie movie) throws MovieAlreadyExist, UserException {
        Optional<User> optionalUser = userRepository.findById(email);
        if (!optionalUser.isPresent()) {
            throw new UserException();
        }

        User user = optionalUser.get();
        if (user.getFavouriteMovielist() != null) {
            List<Movie> movieList = user.getFavouriteMovielist();
            for (Movie movieIterator : movieList) {
                if (movieIterator.getId() == movie.getId()) {
                    throw new MovieAlreadyExist();
                }
            }

            movieList.add(movie);
            user.setFavouriteMovielist(movieList);
        }
        return userRepository.save(user);
    }


    @Override
    public boolean deleteFavoriteMovie(String email, Movie movie) throws MovieException, UserException {
        Optional<User> optionalUser = userRepository.findById(email);
        if (!optionalUser.isPresent()) {
            throw new UserException();
        }

        User user = optionalUser.get();
        List<Movie> movieList = user.getFavouriteMovielist();

        boolean movieFound = false;
        for (Movie movieIterator : movieList) {
            if (movieIterator.getId() == movie.getId()) {
                movieList.remove(movieIterator);
                user.setFavouriteMovielist(movieList);
                userRepository.save(user);
                movieFound = true;
                break;
            }
        }

        if (!movieFound) {
            throw new MovieException();
        }

        return true;
    }


    @Override
    public User getUser(String email) throws UserException {
        if (userRepository.findById(email).isPresent()) {
            return userRepository.findById(email).get();
        } else {
            throw new UserException();
        }
    }


    @Override
    public byte[] getUserProfileImg(String email) {
        User user = userRepository.findById(email).get();
        if (user.getEmail() != null) {
            System.out.println(user.getImagePath());
            return user.getImagePath();
        } else {
            return null;
        }
    }

    @Override
    public String updatePassword(String email, String password) throws UserException {
        Optional<User> userOptional = userRepository.findById(email);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            if (password != null) {
                existingUser.setPassword(password);
                movieProxy.updatePassword(email, password);
                userRepository.save(existingUser);

                return "Password updated Successfully.";
            } else {
                return "Please provide a valid password.";
            }

        } else {
            throw new UserException();
        }
    }



@Override
    public User updateFullUserDetails(User updatedUser) {
    if(userRepository.findById(updatedUser.getEmail()).isPresent()) {
        return userRepository.save(updatedUser);
    }
    else{
        return null;
    }
    }
//
@Override
    public boolean sendMail(String mail)
    {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();

        simpleMailMessage.setText("Hello Dear,\n\n"
                + "Seamless Video Playback - Our adaptive video streaming technology ensures that the best possible video quality is played back automatically based on the available bandwidth, therefore making it a great video experience on both mobile networks as well as WiFi internet connections. Our video is optimized to play on mobile networks with inconsistent throughput so that our users don't have to compromise on their experience on the low end, and play HD quality video on the top end of bandwidth availability. Additionally, our users can manually select the quality of video that suits their taste.\n");

        simpleMailMessage.setSubject("Welcome To MovieApp (:");
        simpleMailMessage.setCc("kumarprincelkh@gmail.com");

        simpleMailMessage.setTo(mail);
        javaMailSender.send(simpleMailMessage);
        return true;

    }
}
