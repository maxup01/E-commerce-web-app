package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backend.dao.entity.user.Role;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class RoleModel {

    private Long id;
    private String name;
    private List<PrivilegeModel> privileges;

    public static RoleModel fromRole(Role role) {

        ArrayList<PrivilegeModel> privilegeModelsList = new ArrayList<>();

        role.getPrivileges().forEach(privilege -> {
            privilegeModelsList.add(PrivilegeModel.fromPrivilege(privilege));
        });

        return new RoleModel(role.getId(), role.getName(), privilegeModelsList);
    }
}
