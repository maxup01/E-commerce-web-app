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

        return ResponseEntity.status(HttpStatus.OK).body(privilegeModel);
    }

    @PutMapping("/update-privilege")
    public ResponseEntity updatePrivilege(@RequestBody PrivilegeModel privilegeModel) {

        try{
            userDataService.updateNameOfPrivilegeById(privilegeModel.getId(), privilegeModel.getName());
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (PrivilegeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/privilege-by-id/{id}")
    public ResponseEntity<PrivilegeModel> getPrivilegeById(@PathVariable("id") Long id) {

        PrivilegeModel privilegeModel;

        try{
            privilegeModel = userDataService.getPrivilegeById(id);
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (PrivilegeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(privilegeModel);
    }

    @GetMapping("/privilege-by-name/{privilegeName}")
    public ResponseEntity<PrivilegeModel> getPrivilegeByName(@PathVariable("privilegeName") String privilegeName) {

        PrivilegeModel privilegeModel;

        try{
            privilegeModel = userDataService.getPrivilegeByName(privilegeName);
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (PrivilegeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(privilegeModel);
    }

    @GetMapping("/privileges")
    public ResponseEntity<List<PrivilegeModel>> getAllPrivileges(){

        return ResponseEntity.status(HttpStatus.OK).body(userDataService.getAllPrivileges());
    }

    @DeleteMapping("/delete-privilege/{id}")
    public ResponseEntity deletePrivilege(@PathVariable("id") Long id){

        try{
            userDataService.deletePrivilegeById(id);
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (PrivilegeNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
