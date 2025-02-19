package org.example.backend.controller;

import org.example.backend.dao.service.UserDataService;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.privilege.PrivilegeNotFoundException;
import org.example.backend.exception.privilege.PrivilegeNotSavedException;
import org.example.backend.model.PrivilegeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class PrivilegeController {

    private UserDataService userDataService;

    @Autowired
    public PrivilegeController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @PostMapping("/create-privilege/{privilegeName}")
    public ResponseEntity<PrivilegeModel> createPrivilege(
            @PathVariable("privilegeName") String privilegeName) {

        PrivilegeModel privilegeModel;

        try{
            privilegeModel = userDataService.saveNewPrivilege(privilegeName);
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (PrivilegeNotSavedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.ok().body(privilegeModel);
    }

    @GetMapping("/privileges")
    public ResponseEntity<List<PrivilegeModel>> getAllPrivileges(){

        return ResponseEntity.ok(userDataService.getAllPrivileges());
    }

    @DeleteMapping("/delete-privilege/{id}")
    public ResponseEntity deletePrivilege(@PathVariable("id") Long id){

        try{
            userDataService.deletePrivilegeById(id);
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (PrivilegeNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
