package org.example.backend.controller;

import org.example.backend.dao.service.UserDataService;
import org.example.backend.model.PrivilegeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class PrivilegeController {

    private UserDataService userDataService;

    @Autowired
    public PrivilegeController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @GetMapping("/privileges")
    public ResponseEntity<List<PrivilegeModel>> getAllPrivileges(){

        return ResponseEntity.ok(userDataService.getAllPrivileges());
    }
}
