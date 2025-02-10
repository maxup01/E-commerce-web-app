package org.example.backend.dao.service.user;

import org.example.backend.dao.entity.user.Privilege;
import org.example.backend.dao.entity.user.Role;
import org.example.backend.dao.repository.user.PrivilegeRepository;
import org.example.backend.dao.repository.user.RoleRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.privilege.PrivilegeNotFoundException;
import org.example.backend.exception.privilege.PrivilegeNotSavedException;
import org.example.backend.exception.role.RoleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDataServiceTest {

    private final Long ID_OF_FIRST_CREATED_PRIVILEGE = 1L;
    private final Long OTHER_ID = 5L;
    private final Long NEGATIVE_ID = -1L;
    private final String RANDOM_PRIVILEGE_NAME = "RANDOM_PRIVILEGE";
    private final String OTHER_PRIVILEGE_NAME = "WRITE_PRIVILEGE";
    private final String RANDOM_WRONG_PRIVILEGE_NAME = "random privilege name";
    private final String RANDOM_ROLE_NAME = "ROLE_RANDOM";
    private final String OTHER_ROLE_NAME = "ROLE_OTHER";
    private final String WRONG_ROLE_NAME = "WRONG_ROLE";

    @Mock
    private PrivilegeRepository privilegeRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserDataService userDataService;

    private Privilege existingPrivilege;
    private Role role;

    @BeforeEach
    public void setUp() {
        existingPrivilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        existingPrivilege.setId(ID_OF_FIRST_CREATED_PRIVILEGE);

        role = Role
                .builder()
                .id(ID_OF_FIRST_CREATED_PRIVILEGE)
                .name(RANDOM_ROLE_NAME)
                .build();
    }

    @Test
    public void testOfSaveNewPrivilege(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.saveNewPrivilege(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
            privilege.setId(1L);

            userDataService.saveNewPrivilege(privilege);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            Privilege privilege = new Privilege(null);

            userDataService.saveNewPrivilege(privilege);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            Privilege privilege = new Privilege(RANDOM_WRONG_PRIVILEGE_NAME);

            userDataService.saveNewPrivilege(privilege);
        });

        assertDoesNotThrow(() -> {
            Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
            userDataService.saveNewPrivilege(privilege);
        });

        when(privilegeRepository.findByName(RANDOM_PRIVILEGE_NAME)).thenReturn(existingPrivilege);

        Exception fifthException = assertThrows(PrivilegeNotSavedException.class, () -> {

            Privilege incorrectlyInitializedPrivilege = new Privilege(RANDOM_PRIVILEGE_NAME);
            userDataService.saveNewPrivilege(incorrectlyInitializedPrivilege);
        });

        assertEquals(firstException.getMessage(), "Privilege cannot be null");
        assertEquals(secondException.getMessage(), "Privilege id cannot be not null");
        assertEquals(thirdException.getMessage(), "Privilege needs to match privilege name pattern and cannot be null");
        assertEquals(fourthException.getMessage(), "Privilege needs to match privilege name pattern and cannot be null");
        assertEquals(fifthException.getMessage(), "Privilege name cannot be the same as one of the existed privileges");
    }

    @Test
    public void testOfUpdateNameOfPrivilegeById(){

        when(privilegeRepository.findById(ID_OF_FIRST_CREATED_PRIVILEGE))
                .thenReturn(Optional.ofNullable(existingPrivilege));

        when(privilegeRepository.findById(OTHER_ID)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateNameOfPrivilegeById(null, RANDOM_WRONG_PRIVILEGE_NAME);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateNameOfPrivilegeById(NEGATIVE_ID, RANDOM_WRONG_PRIVILEGE_NAME);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateNameOfPrivilegeById(
                    ID_OF_FIRST_CREATED_PRIVILEGE, null);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateNameOfPrivilegeById(
                    ID_OF_FIRST_CREATED_PRIVILEGE, RANDOM_WRONG_PRIVILEGE_NAME);
        });

        assertThrows(PrivilegeNotFoundException.class, () -> {
            userDataService.updateNameOfPrivilegeById(OTHER_ID, OTHER_PRIVILEGE_NAME);
        });

        assertDoesNotThrow(() -> {
            userDataService.updateNameOfPrivilegeById(
                    ID_OF_FIRST_CREATED_PRIVILEGE, RANDOM_PRIVILEGE_NAME);
            userDataService.updateNameOfPrivilegeById(
                    ID_OF_FIRST_CREATED_PRIVILEGE, OTHER_PRIVILEGE_NAME);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: id");
        assertEquals(thirdException.getMessage(), "Incorrect argument: privilegeName");
        assertEquals(fourthException.getMessage(), "Incorrect argument: privilegeName");
    }

    @Test
    public void testOfFindById(){

        when(privilegeRepository.findById(ID_OF_FIRST_CREATED_PRIVILEGE))
                .thenReturn(Optional.ofNullable(existingPrivilege));

        when(privilegeRepository.findById(OTHER_ID)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.getPrivilegeById(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.getPrivilegeById(NEGATIVE_ID);
        });

        Exception thirdException = assertThrows(PrivilegeNotFoundException.class, () -> {
            userDataService.getPrivilegeById(OTHER_ID);
        });

        assertDoesNotThrow(() -> {
            userDataService.getPrivilegeById(ID_OF_FIRST_CREATED_PRIVILEGE);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: id");
        assertEquals(thirdException.getMessage(), "Privilege with id " + OTHER_ID + " not found");
    }

    @Test
    public void testOfFindByName(){

        when(privilegeRepository.findByName(RANDOM_PRIVILEGE_NAME)).thenReturn(existingPrivilege);
        when(privilegeRepository.findByName(OTHER_PRIVILEGE_NAME)).thenReturn(null);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.getPrivilegeByName(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.getPrivilegeByName(RANDOM_WRONG_PRIVILEGE_NAME);
        });

        Exception thirdException = assertThrows(PrivilegeNotFoundException.class, () -> {
            userDataService.getPrivilegeByName(OTHER_PRIVILEGE_NAME);
        });

        assertDoesNotThrow(() -> {
            userDataService.getPrivilegeByName(RANDOM_PRIVILEGE_NAME);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: name");
        assertEquals(secondException.getMessage(), "Incorrect argument: name");
        assertEquals(thirdException.getMessage(), "Privilege with name " + OTHER_PRIVILEGE_NAME + " not found");
    }

    @Test
    public void testOfDeletePrivilegeById(){

        when(privilegeRepository.findById(ID_OF_FIRST_CREATED_PRIVILEGE))
                .thenReturn(Optional.ofNullable(existingPrivilege));
        when(privilegeRepository.findById(OTHER_ID)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.deletePrivilegeById(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.deletePrivilegeById(NEGATIVE_ID);
        });

        Exception thirdException = assertThrows(PrivilegeNotFoundException.class, () -> {
            userDataService.deletePrivilegeById(OTHER_ID);
        });

        assertDoesNotThrow(() -> {
            userDataService.deletePrivilegeById(ID_OF_FIRST_CREATED_PRIVILEGE);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: id");
        assertEquals(thirdException.getMessage(), "Privilege with id " + OTHER_ID + " not found");
    }

    @Test
    public void testOfGetRoleById(){

        Role role = Role
                .builder()
                .id(ID_OF_FIRST_CREATED_PRIVILEGE)
                .name(RANDOM_ROLE_NAME)
                .build();

        when(roleRepository.findById(ID_OF_FIRST_CREATED_PRIVILEGE)).thenReturn(Optional.ofNullable(role));
        when(roleRepository.findById(OTHER_ID)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.getRoleById(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.getRoleById(NEGATIVE_ID);
        });

        Exception thirdException = assertThrows(RoleNotFoundException.class, () -> {
            userDataService.getRoleById(OTHER_ID);
        });

        assertDoesNotThrow(() -> {
            userDataService.getRoleById(ID_OF_FIRST_CREATED_PRIVILEGE);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: id");
        assertEquals(thirdException.getMessage(), "Role with id " + OTHER_ID + " not found");
    }

    @Test
    public void testOfGetRoleByName(){

        when(roleRepository.findByName(RANDOM_ROLE_NAME)).thenReturn(role);
        when(roleRepository.findByName(OTHER_ROLE_NAME)).thenReturn(null);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.getRoleByName(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.getRoleByName(WRONG_ROLE_NAME);
        });

        Exception thirdException = assertThrows(RoleNotFoundException.class, () -> {
            userDataService.getRoleByName(OTHER_ROLE_NAME);
        });

        assertDoesNotThrow(() -> {
            userDataService.getRoleByName(RANDOM_ROLE_NAME);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: name");
        assertEquals(secondException.getMessage(), "Incorrect argument: name");
        assertEquals(thirdException.getMessage(), "Role with name " + OTHER_ROLE_NAME + " not found");
    }
}
