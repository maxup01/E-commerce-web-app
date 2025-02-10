package org.example.backend.dao.service.user;

import org.example.backend.dao.entity.user.Privilege;
import org.example.backend.dao.repository.user.PrivilegeRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.privilege.PrivilegeNotFoundException;
import org.example.backend.exception.privilege.PrivilegeNotSavedException;
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
public class PrivilegeServiceTest {

    private final Long ID_OF_FIRST_CREATED_PRIVILEGE = 1L;
    private final Long OTHER_ID = 5L;
    private final Long NEGATIVE_ID = -1L;
    private final String RANDOM_PRIVILEGE_NAME = "RANDOM_PRIVILEGE";
    private final String OTHER_PRIVILEGE_NAME = "WRITE_PRIVILEGE";
    private final String RANDOM_WRONG_PRIVILEGE_NAME = "random privilege name";

    @Mock
    private PrivilegeRepository privilegeRepository;

    @InjectMocks
    private PrivilegeService privilegeService;

    private Privilege existingPrivilege;

    @BeforeEach
    public void setUp() {
        existingPrivilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        existingPrivilege.setId(ID_OF_FIRST_CREATED_PRIVILEGE);
    }

    @Test
    public void testOfSaveNewPrivilege(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            privilegeService.saveNewPrivilege(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
            privilege.setId(1L);

            privilegeService.saveNewPrivilege(privilege);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            Privilege privilege = new Privilege(null);

            privilegeService.saveNewPrivilege(privilege);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            Privilege privilege = new Privilege(RANDOM_WRONG_PRIVILEGE_NAME);

            privilegeService.saveNewPrivilege(privilege);
        });

        assertDoesNotThrow(() -> {
            Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
            privilegeService.saveNewPrivilege(privilege);
        });

        when(privilegeRepository.findByName(RANDOM_PRIVILEGE_NAME)).thenReturn(existingPrivilege);

        Exception fifthException = assertThrows(PrivilegeNotSavedException.class, () -> {

            Privilege incorrectlyInitializedPrivilege = new Privilege(RANDOM_PRIVILEGE_NAME);
            privilegeService.saveNewPrivilege(incorrectlyInitializedPrivilege);
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
            privilegeService.updateNameOfPrivilegeById(null, RANDOM_WRONG_PRIVILEGE_NAME);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            privilegeService.updateNameOfPrivilegeById(NEGATIVE_ID, RANDOM_WRONG_PRIVILEGE_NAME);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            privilegeService.updateNameOfPrivilegeById(
                    ID_OF_FIRST_CREATED_PRIVILEGE, null);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            privilegeService.updateNameOfPrivilegeById(
                    ID_OF_FIRST_CREATED_PRIVILEGE, RANDOM_WRONG_PRIVILEGE_NAME);
        });

        assertThrows(PrivilegeNotFoundException.class, () -> {
            privilegeService.updateNameOfPrivilegeById(OTHER_ID, OTHER_PRIVILEGE_NAME);
        });

        assertDoesNotThrow(() -> {
            privilegeService.updateNameOfPrivilegeById(
                    ID_OF_FIRST_CREATED_PRIVILEGE, RANDOM_PRIVILEGE_NAME);
            privilegeService.updateNameOfPrivilegeById(
                    ID_OF_FIRST_CREATED_PRIVILEGE, OTHER_PRIVILEGE_NAME);
        });

        assertEquals(firstException.getMessage(), "Argument id is incorrect");
        assertEquals(secondException.getMessage(), "Argument id is incorrect");
        assertEquals(thirdException.getMessage(), "Argument privilegeName is incorrect");
        assertEquals(fourthException.getMessage(), "Argument privilegeName is incorrect");
    }

    @Test
    public void testOfFindById(){

        when(privilegeRepository.findById(ID_OF_FIRST_CREATED_PRIVILEGE))
                .thenReturn(Optional.ofNullable(existingPrivilege));

        when(privilegeRepository.findById(OTHER_ID)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            privilegeService.getPrivilegeById(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            privilegeService.getPrivilegeById(NEGATIVE_ID);
        });

        Exception thirdException = assertThrows(PrivilegeNotFoundException.class, () -> {
            privilegeService.getPrivilegeById(OTHER_ID);
        });

        assertDoesNotThrow(() -> {
            privilegeService.getPrivilegeById(ID_OF_FIRST_CREATED_PRIVILEGE);
        });

        assertEquals(firstException.getMessage(), "Argument id is incorrect");
        assertEquals(secondException.getMessage(), "Argument id is incorrect");
        assertEquals(thirdException.getMessage(), "Privilege with id " + OTHER_ID + " not found");
    }

    @Test
    public void testOfFindByName(){

        when(privilegeRepository.findByName(RANDOM_PRIVILEGE_NAME)).thenReturn(existingPrivilege);
        when(privilegeRepository.findByName(OTHER_PRIVILEGE_NAME)).thenReturn(null);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            privilegeService.getPrivilegeByName(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            privilegeService.getPrivilegeByName(RANDOM_WRONG_PRIVILEGE_NAME);
        });

        Exception thirdException = assertThrows(PrivilegeNotFoundException.class, () -> {
            privilegeService.getPrivilegeByName(OTHER_PRIVILEGE_NAME);
        });

        assertDoesNotThrow(() -> {
            privilegeService.getPrivilegeByName(RANDOM_PRIVILEGE_NAME);
        });

        assertEquals(firstException.getMessage(), "Argument name is incorrect");
        assertEquals(secondException.getMessage(), "Argument name is incorrect");
        assertEquals(thirdException.getMessage(), "Privilege with name " + OTHER_PRIVILEGE_NAME + " not found");
    }
}
