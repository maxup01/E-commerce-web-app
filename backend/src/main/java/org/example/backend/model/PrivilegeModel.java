package org.example.backend.model;

import lombok.AllArgsConstructor;
import org.example.backend.dao.entity.user.Privilege;

@AllArgsConstructor
public class PrivilegeModel {

    private Long id;
    private String name;

    public static PrivilegeModel fromPrivilege(Privilege privilege) {
        return new PrivilegeModel(privilege.getId(), privilege.getName());
    }
}
