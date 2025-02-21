package org.example.backend.dao.service;

import org.example.backend.dao.entity.image.UserImage;
import org.example.backend.dao.entity.user.Privilege;
import org.example.backend.dao.entity.user.Role;
import org.example.backend.dao.entity.user.User;
import org.example.backend.dao.repository.image.UserImageRepository;
import org.example.backend.dao.repository.user.PrivilegeRepository;
import org.example.backend.dao.repository.user.RoleRepository;
import org.example.backend.dao.repository.user.UserRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.privilege.PrivilegeNotFoundException;
import org.example.backend.exception.privilege.PrivilegeNotSavedException;
import org.example.backend.exception.role.RoleNotFoundException;
import org.example.backend.exception.role.RoleNotSavedException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.exception.user.UserNotSavedException;
import org.example.backend.model.PrivilegeModel;
import org.example.backend.model.RoleModel;
import org.example.backend.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDataServiceTest {


    private final Long ID_OF_FIRST_CREATED_ENTITY = 1L;
    private final Long ID_OF_SECOND_CREATED_ENTITY = 2L;
    private final Long OTHER_ID = 5L;
    private final Long NEGATIVE_ID = -1L;
    private final String RANDOM_PRIVILEGE_NAME = "RANDOM_PRIVILEGE";
    private final String OTHER_PRIVILEGE_NAME = "WRITE_PRIVILEGE";
    private final String RANDOM_WRONG_PRIVILEGE_NAME = "random privilege name";
    private final String RANDOM_ROLE_NAME = "ROLE_RANDOM";
    private final String ROLE_NAME_WHICH_NOT_EXIST = "ROLE_DIFFERENT";
    private final String OTHER_ROLE_NAME = "ROLE_OTHER";
    private final String WRONG_ROLE_NAME = "WRONG_ROLE";
    private final UUID RANDOM_USER_ID = UUID.randomUUID();
    private final UUID ID_OF_USER_WHICH_NOT_EXIST = UUID.randomUUID();
    private final String RANDOM_FIRST_NAME = "Name";
    private final String RANDOM_LAST_NAME = "LastName";
    private final String RANDOM_EMAIL = "email@email.com";
    private final String EMAIL_OF_USER_WHICH_NOT_EXIST = "not@exist.com";
    private final String WRONG_USER_EMAIL = "WrongEmail";
    private final String RANDOM_PASSWORD = "Password1234";
    private final String WRONG_RANDOM_PASSWORD = "wrongPassword";
    private final LocalDate RANDOM_BIRTH_DATE = LocalDate.of(2004, 4, 2);
    private final byte[] RANDOM_IMAGE = new byte[12];

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private PrivilegeRepository privilegeRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserImageRepository userImageRepository;

    @InjectMocks
    private UserDataService userDataService;

    private Privilege existingPrivilege;
    private Role role;
    private Role otherRole;
    private User firstUser;

    @BeforeEach
    public void setUp() {
        existingPrivilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        existingPrivilege.setId(ID_OF_FIRST_CREATED_ENTITY);

        role = Role
                .builder()
                .id(ID_OF_FIRST_CREATED_ENTITY)
                .name(RANDOM_ROLE_NAME)
                .privileges(new ArrayList<>())
                .build();

        role.getPrivileges().add(existingPrivilege);

        otherRole  = Role
                .builder()
                .id(ID_OF_SECOND_CREATED_ENTITY)
                .name(ROLE_NAME_WHICH_NOT_EXIST)
                .build();

        firstUser = User
                .builder()
                .id(RANDOM_USER_ID)
                .firstName(RANDOM_FIRST_NAME)
                .lastName(RANDOM_LAST_NAME)
                .email(RANDOM_EMAIL)
                .password(RANDOM_PASSWORD)
                .birthDate(RANDOM_BIRTH_DATE)
                .role(role)
                .profileImage(new UserImage(RANDOM_IMAGE))
                .build();
    }

    @Test
    public void testOfSaveNewPrivilege(){

        when(privilegeRepository.save(any())).thenReturn(existingPrivilege);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.saveNewPrivilege(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.saveNewPrivilege(RANDOM_WRONG_PRIVILEGE_NAME);
        });

        assertDoesNotThrow(() -> {
            userDataService.saveNewPrivilege(OTHER_PRIVILEGE_NAME);
        });

        when(privilegeRepository.findByName(RANDOM_PRIVILEGE_NAME)).thenReturn(existingPrivilege);

        Exception thirdException = assertThrows(PrivilegeNotSavedException.class, () -> {
            userDataService.saveNewPrivilege(RANDOM_PRIVILEGE_NAME);
        });

        assertEquals(firstException.getMessage(), "Privilege needs to match privilege name pattern and cannot be null");
        assertEquals(secondException.getMessage(), "Privilege needs to match privilege name pattern and cannot be null");
        assertEquals(thirdException.getMessage(), "Privilege with name " + RANDOM_PRIVILEGE_NAME + " already exists");
    }

    @Test
    public void testOfUpdateNameOfPrivilegeById(){

        when(privilegeRepository.findById(ID_OF_FIRST_CREATED_ENTITY))
                .thenReturn(Optional.ofNullable(existingPrivilege));

        when(privilegeRepository.findById(OTHER_ID)).thenReturn(Optional.empty());

        when(privilegeRepository.save(any())).thenReturn(existingPrivilege);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateNameOfPrivilegeById(null, RANDOM_WRONG_PRIVILEGE_NAME);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateNameOfPrivilegeById(NEGATIVE_ID, RANDOM_WRONG_PRIVILEGE_NAME);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateNameOfPrivilegeById(
                    ID_OF_FIRST_CREATED_ENTITY, null);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateNameOfPrivilegeById(
                    ID_OF_FIRST_CREATED_ENTITY, RANDOM_WRONG_PRIVILEGE_NAME);
        });

        assertThrows(PrivilegeNotFoundException.class, () -> {
            userDataService.updateNameOfPrivilegeById(OTHER_ID, OTHER_PRIVILEGE_NAME);
        });

        assertDoesNotThrow(() -> {
            userDataService.updateNameOfPrivilegeById(
                    ID_OF_FIRST_CREATED_ENTITY, RANDOM_PRIVILEGE_NAME);
            userDataService.updateNameOfPrivilegeById(
                    ID_OF_FIRST_CREATED_ENTITY, OTHER_PRIVILEGE_NAME);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: id");
        assertEquals(thirdException.getMessage(), "Incorrect argument: privilegeName");
        assertEquals(fourthException.getMessage(), "Incorrect argument: privilegeName");
    }

    @Test
    public void testOfFindPrivilegeById(){

        when(privilegeRepository.findById(ID_OF_FIRST_CREATED_ENTITY))
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
            userDataService.getPrivilegeById(ID_OF_FIRST_CREATED_ENTITY);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: id");
        assertEquals(thirdException.getMessage(), "Privilege with id " + OTHER_ID + " not found");
    }

    @Test
    public void testOfFindPrivilegeByName(){

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

        when(privilegeRepository.findById(ID_OF_FIRST_CREATED_ENTITY))
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
            userDataService.deletePrivilegeById(ID_OF_FIRST_CREATED_ENTITY);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: id");
        assertEquals(thirdException.getMessage(), "Privilege with id " + OTHER_ID + " not found");
    }

    @Test
    public void testOfSaveNewRole(){

        when(privilegeRepository.findByName(RANDOM_PRIVILEGE_NAME))
                .thenReturn(existingPrivilege);
        when(privilegeRepository.findByName(OTHER_PRIVILEGE_NAME)).thenReturn(null);

        Role role = Role
                .builder()
                .id(ID_OF_FIRST_CREATED_ENTITY)
                .name(RANDOM_ROLE_NAME)
                .privileges(new ArrayList<>())
                .build();

        when(roleRepository.findByName(RANDOM_ROLE_NAME)).thenReturn(role);
        when(roleRepository.findByName(ROLE_NAME_WHICH_NOT_EXIST)).thenReturn(null);

        when(roleRepository.save(any())).thenReturn(role);

        ArrayList<PrivilegeModel> privileges = new ArrayList<>();

        privileges.add(new PrivilegeModel(null, RANDOM_PRIVILEGE_NAME));

        RoleModel roleModel = new RoleModel(null, null, privileges);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.saveNewRole(roleModel);
        });

        RoleModel roleModel2 = new RoleModel(null, WRONG_ROLE_NAME, privileges);

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.saveNewRole(roleModel2);
        });

        RoleModel roleModel3 = new RoleModel(null, RANDOM_ROLE_NAME, privileges);

        Exception thirdException = assertThrows(RoleNotSavedException.class, () -> {
            userDataService.saveNewRole(roleModel3);
        });

        RoleModel roleModel4 = new RoleModel(null, ROLE_NAME_WHICH_NOT_EXIST, null);

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            userDataService.saveNewRole(roleModel4);
        });

        RoleModel roleModel5 = new RoleModel(null, ROLE_NAME_WHICH_NOT_EXIST, new ArrayList<>());

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            userDataService.saveNewRole(roleModel5);
        });

        ArrayList<PrivilegeModel> privileges2 = new ArrayList<>();
        privileges2.add(new PrivilegeModel(null, RANDOM_WRONG_PRIVILEGE_NAME));

        RoleModel roleModel6 = new RoleModel(null, ROLE_NAME_WHICH_NOT_EXIST, privileges2);

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            userDataService.saveNewRole(roleModel6);
        });

        ArrayList<PrivilegeModel> privileges3 = new ArrayList<>();
        privileges3.add(new PrivilegeModel(null, OTHER_PRIVILEGE_NAME));

        RoleModel roleModel7 = new RoleModel(null, ROLE_NAME_WHICH_NOT_EXIST, privileges3);

        Exception seventhException = assertThrows(PrivilegeNotFoundException.class, () -> {
            userDataService.saveNewRole(roleModel7);
        });

        RoleModel roleModel8 = new RoleModel(null, ROLE_NAME_WHICH_NOT_EXIST, privileges);

        assertDoesNotThrow(() -> {
            userDataService.saveNewRole(roleModel8);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument field: Role.name");
        assertEquals(secondException.getMessage(), "Incorrect argument field: Role.name");
        assertEquals(thirdException.getMessage(), "Role with name " + RANDOM_ROLE_NAME + " already exists");
        assertEquals(fourthException.getMessage(), "Incorrect argument field: Role.privileges");
        assertEquals(fifthException.getMessage(), "Incorrect argument field: Role.privileges");
        assertEquals(sixthException.getMessage(), "Incorrect argument field: Role.privileges.name");
        assertEquals(seventhException.getMessage(), "Privilege with name " + OTHER_PRIVILEGE_NAME + " not found");
    }

    @Test
    public void testOfUpdateRoleNameById(){

        when(roleRepository.findById(ID_OF_FIRST_CREATED_ENTITY)).thenReturn(Optional.ofNullable(role));
        when(roleRepository.findById(OTHER_ID)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateRoleNameById(null, ROLE_NAME_WHICH_NOT_EXIST);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateRoleNameById(NEGATIVE_ID, ROLE_NAME_WHICH_NOT_EXIST);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateRoleNameById(ID_OF_FIRST_CREATED_ENTITY, null);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateRoleNameById(ID_OF_FIRST_CREATED_ENTITY, WRONG_ROLE_NAME);
        });

        Exception fifthException = assertThrows(RoleNotFoundException.class, () -> {
            userDataService.updateRoleNameById(OTHER_ID, ROLE_NAME_WHICH_NOT_EXIST);
        });

        assertDoesNotThrow(() -> {
            userDataService.updateRoleNameById(ID_OF_FIRST_CREATED_ENTITY, ROLE_NAME_WHICH_NOT_EXIST);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: id");
        assertEquals(thirdException.getMessage(), "Incorrect argument: newName");
        assertEquals(fourthException.getMessage(), "Incorrect argument: newName");
        assertEquals(fifthException.getMessage(), "Role with id " + OTHER_ID + " not found");
    }

    @Test
    public void testOfDeleteRolePrivilegeByIdAndName(){

        when(roleRepository.findById(ID_OF_FIRST_CREATED_ENTITY)).thenReturn(Optional.ofNullable(role));
        when(roleRepository.findById(OTHER_ID)).thenReturn(Optional.empty());

        when(privilegeRepository.findByName(RANDOM_PRIVILEGE_NAME)).thenReturn(existingPrivilege);
        when(privilegeRepository.findByName(OTHER_PRIVILEGE_NAME)).thenReturn(null);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.deleteRolePrivilegeByIdAndName(null, RANDOM_PRIVILEGE_NAME);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.deleteRolePrivilegeByIdAndName(NEGATIVE_ID, ROLE_NAME_WHICH_NOT_EXIST);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            userDataService.deleteRolePrivilegeByIdAndName(ID_OF_FIRST_CREATED_ENTITY, null);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            userDataService.deleteRolePrivilegeByIdAndName(ID_OF_FIRST_CREATED_ENTITY, WRONG_ROLE_NAME);
        });

        Exception fifthException = assertThrows(RoleNotFoundException.class, () -> {
            userDataService.deleteRolePrivilegeByIdAndName(OTHER_ID, RANDOM_PRIVILEGE_NAME);
        });

        Exception sixthException = assertThrows(PrivilegeNotFoundException.class, () -> {
            userDataService.deleteRolePrivilegeByIdAndName(ID_OF_FIRST_CREATED_ENTITY, OTHER_PRIVILEGE_NAME);
        });

        assertDoesNotThrow(() -> {
            userDataService.deleteRolePrivilegeByIdAndName(ID_OF_FIRST_CREATED_ENTITY, RANDOM_PRIVILEGE_NAME);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: id");
        assertEquals(thirdException.getMessage(), "Incorrect argument: privilegeName");
        assertEquals(fourthException.getMessage(), "Incorrect argument: privilegeName");
        assertEquals(fifthException.getMessage(), "Role with id " + OTHER_ID + " not found");
        assertEquals(sixthException.getMessage(), "Privilege with name " + OTHER_PRIVILEGE_NAME + " not found");
    }

    @Test
    public void testOfAddPrivilegeToRoleByIdAndName(){

        when(roleRepository.findById(OTHER_ID)).thenReturn(Optional.empty());
        when(roleRepository.findById(ID_OF_FIRST_CREATED_ENTITY)).thenReturn(Optional.ofNullable(role));

        when(privilegeRepository.findByName(OTHER_PRIVILEGE_NAME)).thenReturn(null);
        when(privilegeRepository.findByName(RANDOM_PRIVILEGE_NAME)).thenReturn(existingPrivilege);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.addPrivilegeToRoleByIdAndName(null, RANDOM_PRIVILEGE_NAME);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.addPrivilegeToRoleByIdAndName(NEGATIVE_ID, RANDOM_PRIVILEGE_NAME);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            userDataService.addPrivilegeToRoleByIdAndName(ID_OF_FIRST_CREATED_ENTITY, null);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            userDataService.addPrivilegeToRoleByIdAndName(ID_OF_FIRST_CREATED_ENTITY, WRONG_ROLE_NAME);
        });

        Exception fifthException = assertThrows(RoleNotFoundException.class, () -> {
            userDataService.addPrivilegeToRoleByIdAndName(OTHER_ID, RANDOM_PRIVILEGE_NAME);
        });

        Exception sixthException = assertThrows(PrivilegeNotFoundException.class, () -> {
            userDataService.addPrivilegeToRoleByIdAndName(ID_OF_FIRST_CREATED_ENTITY, OTHER_PRIVILEGE_NAME);
        });

        assertDoesNotThrow(() -> {
            userDataService.addPrivilegeToRoleByIdAndName(ID_OF_FIRST_CREATED_ENTITY, RANDOM_PRIVILEGE_NAME);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: id");
        assertEquals(thirdException.getMessage(), "Incorrect argument: privilegeName");
        assertEquals(fourthException.getMessage(), "Incorrect argument: privilegeName");
        assertEquals(fifthException.getMessage(), "Role with id " + OTHER_ID + " not found");
        assertEquals(sixthException.getMessage(), "Privilege with name " + OTHER_PRIVILEGE_NAME + " not found");
    }

    @Test
    public void testOfGetRoleById(){

        Role role = Role
                .builder()
                .id(ID_OF_FIRST_CREATED_ENTITY)
                .name(RANDOM_ROLE_NAME)
                .privileges(new ArrayList<>())
                .build();

        when(roleRepository.findById(ID_OF_FIRST_CREATED_ENTITY)).thenReturn(Optional.ofNullable(role));
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
            userDataService.getRoleById(ID_OF_FIRST_CREATED_ENTITY);
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

    @Test
    public void testOfDeleteRoleById(){

        when(roleRepository.findById(ID_OF_FIRST_CREATED_ENTITY)).thenReturn(Optional.ofNullable(role));
        when(roleRepository.findById(ID_OF_SECOND_CREATED_ENTITY)).thenReturn(Optional.ofNullable(otherRole));
        when(roleRepository.findById(OTHER_ID)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.deleteRoleById(null, ID_OF_FIRST_CREATED_ENTITY);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.deleteRoleById(NEGATIVE_ID, ID_OF_FIRST_CREATED_ENTITY);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            userDataService.deleteRoleById(ID_OF_FIRST_CREATED_ENTITY, null);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            userDataService.deleteRoleById(ID_OF_FIRST_CREATED_ENTITY, NEGATIVE_ID);
        });

        Exception fifthException = assertThrows(RoleNotFoundException.class, () -> {
            userDataService.deleteRoleById(OTHER_ID, ID_OF_SECOND_CREATED_ENTITY);
        });

        Exception sixthException = assertThrows(RoleNotFoundException.class, () -> {
            userDataService.deleteRoleById(ID_OF_SECOND_CREATED_ENTITY, OTHER_ID);
        });

        assertDoesNotThrow(() -> {
            userDataService.deleteRoleById(ID_OF_FIRST_CREATED_ENTITY, ID_OF_SECOND_CREATED_ENTITY);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: idOfRoleToDelete");
        assertEquals(secondException.getMessage(), "Incorrect argument: idOfRoleToDelete");
        assertEquals(thirdException.getMessage(), "Incorrect argument: idOfRoleToAssignToUsers");
        assertEquals(fourthException.getMessage(), "Incorrect argument: idOfRoleToAssignToUsers");
        assertEquals(fifthException.getMessage(), "Role to delete with id " + OTHER_ID + " not found");
        assertEquals(sixthException.getMessage(), "Role to assign to users with id " + OTHER_ID + " not found");
    }

    @Test
    public void testOfSaveNewUser(){

        UserModel userModel = new UserModel(null, RANDOM_FIRST_NAME, RANDOM_LAST_NAME, EMAIL_OF_USER_WHICH_NOT_EXIST
                , RANDOM_PASSWORD, RANDOM_BIRTH_DATE, null);

        when(userRepository.findByEmail(RANDOM_EMAIL)).thenReturn(firstUser);
        when(userRepository.findByEmail(EMAIL_OF_USER_WHICH_NOT_EXIST)).thenReturn(null);

        when(roleRepository.findByName(RANDOM_ROLE_NAME)).thenReturn(role);
        when(roleRepository.findByName(ROLE_NAME_WHICH_NOT_EXIST)).thenReturn(null);

        when(bCryptPasswordEncoder.encode(RANDOM_PASSWORD)).thenReturn(RANDOM_PASSWORD);

        when(userRepository.save(any())).thenReturn(firstUser);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.saveNewUser(null, RANDOM_ROLE_NAME);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userModel.setFirstName(null);
            userDataService.saveNewUser(userModel, RANDOM_ROLE_NAME);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            userModel.setFirstName("");
            userDataService.saveNewUser(userModel, RANDOM_ROLE_NAME);
        });

        userModel.setFirstName(RANDOM_FIRST_NAME);

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            userModel.setLastName(null);
            userDataService.saveNewUser(userModel, RANDOM_ROLE_NAME);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            userModel.setLastName("");
            userDataService.saveNewUser(userModel, RANDOM_ROLE_NAME);
        });

        userModel.setLastName(RANDOM_LAST_NAME);

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            userModel.setEmail(null);
            userDataService.saveNewUser(userModel, RANDOM_ROLE_NAME);
        });

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            userModel.setEmail(WRONG_USER_EMAIL);
            userDataService.saveNewUser(userModel, RANDOM_ROLE_NAME);
        });

        userModel.setEmail(RANDOM_EMAIL);

        Exception eighthException = assertThrows(BadArgumentException.class, () -> {
            userModel.setPassword(null);
            userDataService.saveNewUser(userModel, RANDOM_ROLE_NAME);
        });

        Exception ninthException = assertThrows(BadArgumentException.class, () -> {
            userModel.setPassword(WRONG_RANDOM_PASSWORD);
            userDataService.saveNewUser(userModel, RANDOM_ROLE_NAME);
        });

        userModel.setPassword(RANDOM_PASSWORD);

        Exception tenthException = assertThrows(BadArgumentException.class, () -> {
            userModel.setBirthDate(null);
            userDataService.saveNewUser(userModel, RANDOM_ROLE_NAME);
        });

        Exception eleventhException = assertThrows(BadArgumentException.class, () -> {
            userModel.setBirthDate(LocalDate.of(2050, 10, 1));
            userDataService.saveNewUser(userModel, RANDOM_ROLE_NAME);
        });

        userModel.setBirthDate(RANDOM_BIRTH_DATE);

        Exception twelvethException = assertThrows(BadArgumentException.class, () -> {
            userDataService.saveNewUser(userModel, null);
        });

        Exception thirteenthException = assertThrows(BadArgumentException.class, () -> {
            userDataService.saveNewUser(userModel, WRONG_ROLE_NAME);
        });

        Exception fourteenthException = assertThrows(UserNotSavedException.class, () -> {
            userModel.setEmail(RANDOM_EMAIL);
            userDataService.saveNewUser(userModel, RANDOM_ROLE_NAME);
        });

        userModel.setEmail(EMAIL_OF_USER_WHICH_NOT_EXIST);

        Exception fifteenthException = assertThrows(RoleNotFoundException.class, () -> {
            userDataService.saveNewUser(userModel, ROLE_NAME_WHICH_NOT_EXIST);
        });

        assertDoesNotThrow(() -> {
            userDataService.saveNewUser(userModel, RANDOM_ROLE_NAME);
        });

        assertEquals(firstException.getMessage(), "Null argument: userModel");
        assertEquals(secondException.getMessage(), "Incorrect argument field: userModel.firstName");
        assertEquals(thirdException.getMessage(), "Incorrect argument field: userModel.firstName");
        assertEquals(fourthException.getMessage(), "Incorrect argument field: userModel.lastName");
        assertEquals(fifthException.getMessage(), "Incorrect argument field: userModel.lastName");
        assertEquals(sixthException.getMessage(), "Incorrect argument field: userModel.email");
        assertEquals(seventhException.getMessage(), "Incorrect argument field: userModel.email");
        assertEquals(eighthException.getMessage(), "Incorrect argument field: userModel.password");
        assertEquals(ninthException.getMessage(), "Incorrect argument field: userModel.password");
        assertEquals(tenthException.getMessage(), "Incorrect argument field: userModel.birthDate");
        assertEquals(eleventhException.getMessage(), "Incorrect argument field: userModel.birthDate");
        assertEquals(twelvethException.getMessage(), "Incorrect argument: roleName");
        assertEquals(thirteenthException.getMessage(), "Incorrect argument: roleName");
        assertEquals(fourteenthException.getMessage(), "User with email " + RANDOM_EMAIL + " already exists");
        assertEquals(fifteenthException.getMessage(), "Role with name " + ROLE_NAME_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfUpdateUserFirstNameByEmail(){

        when(userRepository.findById(RANDOM_USER_ID)).thenReturn(Optional.ofNullable(firstUser));
        when(userRepository.findById(ID_OF_USER_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateUserFirstNameById(null, RANDOM_FIRST_NAME);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateUserFirstNameById(RANDOM_USER_ID, null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateUserFirstNameById(RANDOM_USER_ID, "");
        });

        Exception fourthException = assertThrows(UserNotFoundException.class, () -> {
            userDataService.updateUserFirstNameById(ID_OF_USER_WHICH_NOT_EXIST, RANDOM_FIRST_NAME);
        });

        assertDoesNotThrow(() -> {
            userDataService.updateUserFirstNameById(RANDOM_USER_ID, RANDOM_FIRST_NAME);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: firstName");
        assertEquals(thirdException.getMessage(), "Incorrect argument: firstName");
        assertEquals(fourthException.getMessage(), "User with id " + ID_OF_USER_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfUpdateUserLastNameByEmail(){

        when(userRepository.findById(RANDOM_USER_ID)).thenReturn(Optional.ofNullable(firstUser));
        when(userRepository.findById(ID_OF_USER_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateUserLastNameById(null, RANDOM_LAST_NAME);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateUserLastNameById(RANDOM_USER_ID, null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateUserLastNameById(RANDOM_USER_ID, "");
        });

        Exception fourthException = assertThrows(UserNotFoundException.class, () -> {
            userDataService.updateUserLastNameById(ID_OF_USER_WHICH_NOT_EXIST, RANDOM_LAST_NAME);
        });

        assertDoesNotThrow(() -> {
            userDataService.updateUserLastNameById(RANDOM_USER_ID, RANDOM_LAST_NAME);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: lastName");
        assertEquals(thirdException.getMessage(), "Incorrect argument: lastName");
        assertEquals(fourthException.getMessage(), "User with id " + ID_OF_USER_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfUpdateUserPasswordByEmail(){

        when(userRepository.findById(RANDOM_USER_ID)).thenReturn(Optional.ofNullable(firstUser));
        when(userRepository.findById(ID_OF_USER_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateUserPasswordById(null, RANDOM_PASSWORD);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateUserPasswordById(RANDOM_USER_ID, null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateUserPasswordById(RANDOM_USER_ID, WRONG_RANDOM_PASSWORD);
        });

        Exception fourthException = assertThrows(UserNotFoundException.class, () -> {
            userDataService.updateUserPasswordById(ID_OF_USER_WHICH_NOT_EXIST, RANDOM_PASSWORD);
        });

        assertDoesNotThrow(() -> {
            userDataService.updateUserPasswordById(RANDOM_USER_ID, RANDOM_PASSWORD);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: password");
        assertEquals(thirdException.getMessage(), "Incorrect argument: password");
        assertEquals(fourthException.getMessage(), "User with id " + ID_OF_USER_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfUpdateUserImageByEmail(){

        when(userRepository.findById(RANDOM_USER_ID)).thenReturn(Optional.ofNullable(firstUser));
        when(userRepository.findById(ID_OF_USER_WHICH_NOT_EXIST)).thenReturn(Optional.empty());
        when(userImageRepository.save(any())).thenReturn(firstUser.getProfileImage());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateUserImageById(null, RANDOM_IMAGE);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.updateUserImageById(RANDOM_USER_ID, null);
        });

        Exception thirdException = assertThrows(UserNotFoundException.class, () -> {
            userDataService.updateUserImageById(ID_OF_USER_WHICH_NOT_EXIST, RANDOM_IMAGE);
        });

        assertDoesNotThrow(() -> {
            userDataService.updateUserImageById(RANDOM_USER_ID, RANDOM_IMAGE);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: image");
        assertEquals(thirdException.getMessage(), "User with id " + ID_OF_USER_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfGetUserById(){

        when(userRepository.findById(RANDOM_USER_ID)).thenReturn(Optional.ofNullable(firstUser));
        when(userRepository.findById(ID_OF_USER_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.getUserById(null);
        });

        Exception secondException = assertThrows(UserNotFoundException.class, () -> {
            userDataService.getUserById(ID_OF_USER_WHICH_NOT_EXIST);
        });

        assertDoesNotThrow(() -> {
            userDataService.getUserById(RANDOM_USER_ID);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "User with id " + ID_OF_USER_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfGetUserByEmail(){

        when(userRepository.findByEmail(RANDOM_EMAIL)).thenReturn(firstUser);
        when(userRepository.findByEmail(EMAIL_OF_USER_WHICH_NOT_EXIST)).thenReturn(null);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.getUserByEmail(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            userDataService.getUserByEmail(WRONG_USER_EMAIL);
        });

        Exception thirdException = assertThrows(UserNotFoundException.class, () -> {
            userDataService.getUserByEmail(EMAIL_OF_USER_WHICH_NOT_EXIST);
        });

        assertDoesNotThrow(() -> {
            userDataService.getUserByEmail(RANDOM_EMAIL);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: email");
        assertEquals(secondException.getMessage(), "Incorrect argument: email");
        assertEquals(thirdException.getMessage(), "User with email " + EMAIL_OF_USER_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfGetUserIdAndImageById(){

        when(userRepository.findById(RANDOM_USER_ID)).thenReturn(Optional.ofNullable(firstUser));
        when(userRepository.findById(ID_OF_USER_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.getUserIdAndImageById(null);
        });

        Exception secondException = assertThrows(UserNotFoundException.class, () -> {
            userDataService.getUserIdAndImageById(ID_OF_USER_WHICH_NOT_EXIST);
        });

        assertDoesNotThrow(() -> {
            userDataService.getUserIdAndImageById(RANDOM_USER_ID);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "User with id " + ID_OF_USER_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfDeleteUserById(){

        when(userRepository.findById(RANDOM_USER_ID)).thenReturn(Optional.ofNullable(firstUser));
        when(userRepository.findById(ID_OF_USER_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            userDataService.deleteUserById(null);
        });

        Exception secondException = assertThrows(UserNotFoundException.class, () -> {
            userDataService.deleteUserById(ID_OF_USER_WHICH_NOT_EXIST);
        });

        assertDoesNotThrow(() -> {
            userDataService.deleteUserById(RANDOM_USER_ID);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "User with id " + ID_OF_USER_WHICH_NOT_EXIST + " not found");
    }
}
