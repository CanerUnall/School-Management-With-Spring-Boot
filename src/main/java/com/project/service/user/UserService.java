package com.project.service.user;

import com.project.entity.concretes.user.User;
import com.project.entity.enums.RoleType2;
import com.project.exception.BadRequestException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.user.UserRequest;
import com.project.payload.request.user.UserRequestWithoutPassword;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.user.UserResponse;
import com.project.repository.user.UserRepository;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final PageableHelper pageableHelper;
    private final MethodHelper methodHelper;

    public ResponseMessage<UserResponse> saveUser(UserRequest userRequest, String userRole) {

        //!!! Girilen username, email, phoneNumber, ssn
        uniquePropertyValidator.checkDuplicate(userRequest.getUsername(), userRequest.getSsn(),
                userRequest.getPhoneNumber(), userRequest.getEmail());
        //!!! DTO --> POJO
        User user = userMapper.mapUserRequestToUser(userRequest);
        //!!! Rol bilgisini setliyoruz
        if (userRole.equalsIgnoreCase(RoleType2.ADMIN.name())) {

            if (Objects.equals(userRequest.getUsername(), "Admin")) {
                user.setBuilt_in(true);
            }
            //!!! admin rolu veriliyor
            user.setUserRole(userRoleService.getUserRole(RoleType2.ADMIN));
        } else if (userRole.equalsIgnoreCase("Dean")) {
            user.setUserRole(userRoleService.getUserRole(RoleType2.MANAGER));
        } else if (userRole.equalsIgnoreCase("ViceDean")) {
            user.setUserRole(userRoleService.getUserRole(RoleType2.ASSISTANT_MANAGER));
        } else {
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_USERROLE_MESSAGE, userRole));
        }
        //!!! password encode
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //!!! isAdvisor degerini False yapiyoruz
        user.setIsAdvisor(Boolean.FALSE);
        User savedUser = userRepository.save(user);

        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.USER_CREATED)
                .object(userMapper.mapUserToUserResponse(savedUser))
                .build();
    }

    public Page<UserResponse> getUsersByPage(int page, int size, String sort, String type, String userRole) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        return userRepository.findByUserByRole(userRole, pageable)
                .map(userMapper::mapUserToUserResponse);
    }

    public ResponseMessage<BaseUserResponse> getUserById(Long userId) {

        BaseUserResponse baseUserResponse = null;

        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, userId)));

        if (user.getUserRole().getRoleType() == RoleType2.STUDENT) {
            baseUserResponse = userMapper.mapUserToStudentResponse(user);
        } else if (user.getUserRole().getRoleType() == RoleType2.TEACHER) {
            baseUserResponse = userMapper.mapUserToTeacherResponse(user);
        } else {
            baseUserResponse = userMapper.mapUserToUserResponse(user);
        }

        return ResponseMessage.<BaseUserResponse>builder()
                .message(SuccessMessages.USER_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(baseUserResponse)
                .build();


    }

    public String deleteUserById(Long id, HttpServletRequest request) {

        //!!! silinecek olan user var mi ? kontrolu
        User user = methodHelper.isUserExist(id);
        //!!! metodu tetikleyen kullanicinin ril bilgisini aliyoruz
        String userName = (String) request.getAttribute("username");
        User user2 = userRepository.findByUsernameEquals(userName);
        //!!! builtIn ve Role kontrolu
        if (Boolean.TRUE.equals(user.getBuilt_in())) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
            // MANAGER sadece Teacher, student, Assistant_Manager silebilir
        } else if (user2.getUserRole().getRoleType() == RoleType2.MANAGER) {
            if (!((user.getUserRole().getRoleType() == RoleType2.TEACHER) ||
                    (user.getUserRole().getRoleType() == RoleType2.STUDENT) ||
                    (user.getUserRole().getRoleType() == RoleType2.ASSISTANT_MANAGER))) {
                throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
            }
            // Mudur Yardimcisi sadece Teacher veya Student silebilir
        } else if (user2.getUserRole().getRoleType() == RoleType2.ASSISTANT_MANAGER) {
            if (!((user.getUserRole().getRoleType() == RoleType2.TEACHER) ||
                    (user.getUserRole().getRoleType() == RoleType2.STUDENT))) {
                throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
            }
        }

        /*if(Boolean.TRUE.equals(user.getBuilt_in())){
            throw  new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        } else if(!(user2.getUserRole().getRoleType().rank>2&&user2.getUserRole().getRoleType().rank>user.getUserRole().getRoleType().rank)){
            //kullanici seviye olarak sadece kendi altindakileri silebilir.
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }*/

        userRepository.deleteById(id);
        return SuccessMessages.USER_DELETE;

    }

    public ResponseMessage<BaseUserResponse> updateUser(UserRequest userRequest, Long userId) {
        //!!! id var mi kontrolu :
        User user = methodHelper.isUserExist(userId);
        //!!! built_IN kontrolu
        methodHelper.checkBuiltIn(user);
        //!!! unique kontrolu :
        uniquePropertyValidator.checkUniqueProperties(user, userRequest);
        //!!! DTO --> POJO
        User updatedUser = userMapper.mapUserRequestToUpdatedUser(userRequest, userId);
        //!!! password Hashlenecek
        updatedUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        updatedUser.setUserRole(user.getUserRole());
        User savedUser = userRepository.save(updatedUser);

        return ResponseMessage.<BaseUserResponse>builder()
                .message(SuccessMessages.USER_UPDATE_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .object(userMapper.mapUserToUserResponse(savedUser))
                .build();

    }

    public ResponseEntity<String> updateUserForUsers(UserRequestWithoutPassword userRequest,
                                                     HttpServletRequest request) {
        String userName = (String) request.getAttribute("username");
        User user = userRepository.findByUsernameEquals(userName);

        //!!! builtIn
        methodHelper.checkBuiltIn(user);
        // unique kontrolu
        uniquePropertyValidator.checkUniqueProperties(user, userRequest);
        //!!! DTO --> POJO
        user.setUsername(userRequest.getUsername());
        user.setBirthDay(userRequest.getBirthDay());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setBirthPlace(userRequest.getBirthPlace());
        user.setGender(userRequest.getGender());
        user.setName(userRequest.getName());
        user.setSurname(userRequest.getSurname());
        user.setSsn(userRequest.getSsn());

        userRepository.save(user);

        String message = SuccessMessages.USER_UPDATE;
        return ResponseEntity.ok(message);
    }
}