package org.example.backend.controller;

import org.example.backend.dao.service.UserDataService;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
public class UserController {

    private final UserDataService userDataService;

    @Autowired
    public UserController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @PutMapping("/user/update-first-name")
    public ResponseEntity<UserModel> updateUserFirstName(@RequestBody UserModel userModel) {

        UserModel user;

        try{
            user = userDataService.updateUserFirstNameById(userModel.getId(), userModel.getFirstName());
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/user/update-last-name")
    public ResponseEntity<UserModel> updateUserLastName(@RequestBody UserModel userModel) {

        UserModel user;

        try{
            user = userDataService.updateUserLastNameById(userModel.getId(), userModel.getLastName());
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/user/update-password")
    public ResponseEntity<UserModel> updateUserPassword(@RequestBody UserModel userModel) {

        UserModel user;

        try{
            user = userDataService.updateUserPasswordById(userModel.getId(), userModel.getPassword());
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/admin/user/{email}")
    public ResponseEntity<UserModel> getUserByEmail(@PathVariable("email") String email) {

        UserModel foundUser;

        try{
            foundUser = userDataService.getUserByEmail(email);
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(foundUser);
    }

    @DeleteMapping("/delete-user-by-id/{id}")
    public ResponseEntity deleteUserById(@PathVariable("id") UUID id) {

        try{
            userDataService.deleteUserById(id);
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
