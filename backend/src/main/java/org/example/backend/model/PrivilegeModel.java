package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backend.dao.entity.user.Privilege;

@Getter
@AllArgsConstructor
public class PrivilegeModel {

    private Long id;
    private String name;

    public static PrivilegeModel fromPrivilege(Privilege privilege) {
        return new PrivilegeModel(privilege.getId(), privilege.getName());
    }
}
