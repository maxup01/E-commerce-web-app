package org.example.backend.controller;

import org.example.backend.dao.service.UserDataService;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.role.RoleNotFoundException;
import org.example.backend.model.RoleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class RoleController {

    private UserDataService userDataService;

    @Autowired
    public RoleController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @GetMapping("/role-by-id/{id}")
    public ResponseEntity<RoleModel> getRoleById(Long id) {

        RoleModel roleModel;

        try{
            roleModel = userDataService.getRoleById(id);
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RoleNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(roleModel);
    }

    @GetMapping("/role-by-name/{name}")
    public ResponseEntity<RoleModel> getRoleByName(String name) {

        RoleModel roleModel;

        try{
            roleModel = userDataService.getRoleByName(name);
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RoleNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(roleModel);
    }

    @GetMapping("/role-all")
    public ResponseEntity<List<RoleModel>> getAllRoles() {

        return ResponseEntity.status(HttpStatus.OK).body(userDataService.getAllRoles());
    }

    @DeleteMapping("/delete-role-and-assign-new-by-id/{id}")
    public ResponseEntity deleteRoleAndAssignNewById(@PathVariable("id") Long id,
                                                                @Param("id_of_role_to_assign") Long idOfRoleToAssign) {

        try{
            userDataService.deleteRoleById(id, idOfRoleToAssign);
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RoleNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
