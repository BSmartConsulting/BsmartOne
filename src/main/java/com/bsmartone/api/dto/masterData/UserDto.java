package com.bsmartone.api.dto.masterData;

import com.bsmartone.api.model.*;
import com.bsmartone.api.model.masterData.Role;
import com.bsmartone.api.model.masterData.User;
import com.bsmartone.api.model.masterData.UserRole;
import com.bsmartone.api.model.masterData.UserStatus;
import com.bsmartone.api.service.masterData.RoleService;
import com.bsmartone.api.service.masterData.UserRoleService;
import com.bsmartone.api.service.masterData.UserStatusService;
import com.bsmartone.api.util.BeanUtil;
import com.bsmartone.api.util.GlobalExceptionHandler;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserDto extends BaseEntity implements Serializable {

    private static UserStatusService userStatusService = BeanUtil.getBean(UserStatusService.class);
    private static RoleService roleService = BeanUtil.getBean(RoleService.class);
    private static UserRoleService userRoleService = BeanUtil.getBean(UserRoleService.class);

    private Long id;
    private String username;
    private String password;
    private String email;
    private String taxid;
    private UserStatusDto status;
    private String fullname;
    private boolean requirespasswordchange;
    private Integer failedattempts;
    private Set<RoleDto> roles;
    private String cityofresidence;
    private String address;
    private String cellphone;
    private LocalDateTime startdate;
    private LocalDateTime enddate;

    public static User UserDtoToUser(UserDto recordDto) {
        Set<Role> roles = new HashSet<>();
        try {
            for (RoleDto role : recordDto.getRoles()) {
                roles.add(RoleDto.RoleDtoToRole(role));
            }
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);

        }
        return User.builder()
                .id(recordDto.getId())
                .username(recordDto.getUsername())
                .password(recordDto.getPassword())
                .email(recordDto.getEmail())
                .fullname(recordDto.getFullname())
                .taxid(recordDto.getTaxid())
                .status(UserStatusDto.UserStatusDtoToUserStatus(recordDto.getStatus()))
                .requirespasswordchange(recordDto.isRequirespasswordchange())
                .roles(roles)
                .cellphone(recordDto.getCellphone())
                .failedattempts(recordDto.getFailedattempts())
                .cityofresidence(recordDto.getCityofresidence())
                .address(recordDto.getAddress())
                .failedattempts(recordDto.getFailedattempts())
                .cellphone(recordDto.getCellphone())
                .startdate(recordDto.getStartdate())
                .enddate(recordDto.getEnddate())
                .creationDate(recordDto.getCreationDate())
                .lastUpdatedDate(recordDto.getLastUpdatedDate())
                .createdBy(recordDto.getCreatedBy())
                .lastUpdatedBy(recordDto.getLastUpdatedBy())
                .deleted(recordDto.getDeleted())
                .build();
    }

    public static UserDto UserToUserDto(User record) {

        Set<Role> currentRoles = getUserRolesById(record.getId());
//        Set<Role> currentRoles = new HashSet<>();
//        for (Role userRole : roleIds) {
//            for (Role r : roleService.getAllActive()) {
//                if (userRole.getId().equals(r.getId())) {
//                    currentRoles.add(r);
//                }
//            }
//        }
        Set<RoleDto> roles = currentRoles.stream()
                .map(RoleDto::RoleToRoleDto)
                .collect(Collectors.toSet());
        UserStatus currentUserStatus = new UserStatus();
        for (UserStatus us : userStatusService.getAllActive()) {
            if (record.getStatus().getId().equals(us.getId())) {
                currentUserStatus = us;
                break;
            }
        }

        UserStatusDto userstatus = UserStatusDto.UserStatusToUserStatusDto(currentUserStatus);


        return UserDto.builder()
                .id(record.getId())
                .username(record.getUsername())
                .password(record.getPassword())
                .email(record.getEmail())
                .fullname(record.getFullname())
                .taxid(record.getTaxid())
                .status(userstatus)
                .requirespasswordchange(record.isRequirespasswordchange())
                .roles(roles)
                .cellphone(record.getCellphone())
                .failedattempts(record.getFailedattempts())
                .cityofresidence(record.getCityofresidence())
                .address(record.getAddress())
                .failedattempts(record.getFailedattempts())
                .cellphone(record.getCellphone())
                .startdate(record.getStartdate())
                .enddate(record.getEnddate())
                .creationDate(record.getCreationDate())
                .lastUpdatedDate(record.getLastUpdatedDate())
                .createdBy(record.getCreatedBy())
                .lastUpdatedBy(record.getLastUpdatedBy())
                .deleted(record.getDeleted())
                .build();
    }

    private static Set<Role> getUserRolesById(Long id) {
        Set<UserRole> currentUserRole = userRoleService.findByUserId(id);
        Set<Role> allUserRoleId = new HashSet<>();
        for (UserRole ur : currentUserRole) {
            allUserRoleId.add(ur.getRole());
        }
        return allUserRoleId;
    }

    public UserDto() {
    }

    @Builder
    public UserDto(Long id, String username, String password, String email, String taxid, UserStatusDto status, String fullname, boolean requirespasswordchange, Integer failedattempts, Set<RoleDto> roles, String cityofresidence, String address, String cellphone, LocalDateTime startdate, LocalDateTime enddate, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.taxid = taxid;
        this.status = status;
        this.fullname = fullname;
        this.requirespasswordchange = requirespasswordchange;
        this.failedattempts = failedattempts;
        this.roles = roles;
        this.cityofresidence = cityofresidence;
        this.address = address;
        this.cellphone = cellphone;
        this.startdate = startdate;
        this.enddate = enddate;
    }
}
