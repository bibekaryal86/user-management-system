package auth.service.app.util;

import static auth.service.app.util.CommonUtils.getHttpStatusForErrorResponse;
import static auth.service.app.util.CommonUtils.getHttpStatusForSingleResponse;
import static auth.service.app.util.CommonUtils.getResponseStatusInfoForSingleResponse;

import auth.service.app.model.dto.AddressTypeDto;
import auth.service.app.model.dto.PermissionDto;
import auth.service.app.model.dto.PermissionResponse;
import auth.service.app.model.dto.PlatformDto;
import auth.service.app.model.dto.PlatformResponse;
import auth.service.app.model.dto.ProfileAddressDto;
import auth.service.app.model.dto.ProfileDto;
import auth.service.app.model.dto.ProfilePasswordTokenResponse;
import auth.service.app.model.dto.ProfileResponse;
import auth.service.app.model.dto.ResponseCrudInfo;
import auth.service.app.model.dto.ResponseMetadata;
import auth.service.app.model.dto.ResponseStatusInfo;
import auth.service.app.model.dto.RoleDto;
import auth.service.app.model.dto.RoleResponse;
import auth.service.app.model.dto.StatusTypeDto;
import auth.service.app.model.entity.AddressTypeEntity;
import auth.service.app.model.entity.PermissionEntity;
import auth.service.app.model.entity.PlatformEntity;
import auth.service.app.model.entity.PlatformProfileRoleEntity;
import auth.service.app.model.entity.PlatformRolePermissionEntity;
import auth.service.app.model.entity.ProfileAddressEntity;
import auth.service.app.model.entity.ProfileEntity;
import auth.service.app.model.entity.RoleEntity;
import auth.service.app.model.entity.StatusTypeEntity;
import auth.service.app.service.PlatformProfileRoleService;
import auth.service.app.service.PlatformRolePermissionService;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

// TODO role in profile

@Component
@RequiredArgsConstructor
public class EntityDtoConvertUtils {
  private final PlatformProfileRoleService platformProfileRoleService;
  private final PlatformRolePermissionService platformRolePermissionService;

  public ResponseEntity<PlatformResponse> getResponseSinglePlatform(
      final PlatformEntity platformEntity) {
    final List<PlatformDto> platformDtos =
        platformEntity == null
            ? Collections.emptyList()
            : List.of(convertEntityToDtoPlatform(platformEntity));
    return new ResponseEntity<>(
        PlatformResponse.builder()
            .platforms(platformDtos)
            .responseMetadata(
                ResponseMetadata.builder()
                    .responseStatusInfo(getResponseStatusInfoForSingleResponse(platformEntity))
                    .build())
            .build(),
        getHttpStatusForSingleResponse(platformEntity));
  }

  public ResponseEntity<PlatformResponse> getResponseMultiplePlatforms(
      final List<PlatformEntity> platformEntities) {
    return ResponseEntity.ok(
        PlatformResponse.builder()
            .platforms(convertEntitiesToDtosPlatforms(platformEntities))
            .build());
  }

  public ResponseEntity<PlatformResponse> getResponseDeletePlatform() {
    return ResponseEntity.ok(
        PlatformResponse.builder()
            .platforms(Collections.emptyList())
            .responseMetadata(
                ResponseMetadata.builder()
                    .responseCrudInfo(ResponseCrudInfo.builder().deletedRowsCount(1).build())
                    .build())
            .build());
  }

  public ResponseEntity<PlatformResponse> getResponseErrorPlatform(final Exception exception) {
    return new ResponseEntity<>(
        PlatformResponse.builder()
            .platforms(Collections.emptyList())
            .responseMetadata(
                ResponseMetadata.builder()
                    .responseStatusInfo(
                        ResponseStatusInfo.builder().errMsg(exception.getMessage()).build())
                    .build())
            .build(),
        getHttpStatusForErrorResponse(exception));
  }

  public PlatformDto convertEntityToDtoPlatform(final PlatformEntity platformEntity) {
    if (platformEntity == null) {
      return null;
    }
    PlatformDto platformDto = new PlatformDto();
    BeanUtils.copyProperties(platformEntity, platformDto);
    return platformDto;
  }

  public List<PlatformDto> convertEntitiesToDtosPlatforms(
      final List<PlatformEntity> platformEntities) {
    if (CollectionUtils.isEmpty(platformEntities)) {
      return Collections.emptyList();
    }
    return platformEntities.stream().map(this::convertEntityToDtoPlatform).toList();
  }

  public ResponseEntity<PermissionResponse> getResponseSinglePermission(
      final PermissionEntity permissionEntity) {
    final List<PermissionDto> permissionDtos =
        permissionEntity == null
            ? Collections.emptyList()
            : List.of(convertEntityToDtoPermission(permissionEntity));
    return new ResponseEntity<>(
        PermissionResponse.builder()
            .permissions(permissionDtos)
            .responseMetadata(
                ResponseMetadata.builder()
                    .responseStatusInfo(getResponseStatusInfoForSingleResponse(permissionEntity))
                    .build())
            .build(),
        getHttpStatusForSingleResponse(permissionEntity));
  }

  public ResponseEntity<PermissionResponse> getResponseMultiplePermissions(
      final List<PermissionEntity> permissionEntities) {
    final List<PermissionDto> permissionDtos = convertEntitiesToDtosPermissions(permissionEntities);
    return ResponseEntity.ok(PermissionResponse.builder().permissions(permissionDtos).build());
  }

  public ResponseEntity<PermissionResponse> getResponseDeletePermission() {
    return ResponseEntity.ok(
        PermissionResponse.builder()
            .permissions(Collections.emptyList())
            .responseMetadata(
                ResponseMetadata.builder()
                    .responseCrudInfo(ResponseCrudInfo.builder().deletedRowsCount(1).build())
                    .build())
            .build());
  }

  public ResponseEntity<PermissionResponse> getResponseErrorPermission(final Exception exception) {
    return new ResponseEntity<>(
        PermissionResponse.builder()
            .permissions(Collections.emptyList())
            .responseMetadata(
                ResponseMetadata.builder()
                    .responseStatusInfo(
                        ResponseStatusInfo.builder().errMsg(exception.getMessage()).build())
                    .build())
            .build(),
        getHttpStatusForErrorResponse(exception));
  }

  public PermissionDto convertEntityToDtoPermission(final PermissionEntity permissionEntity) {
    if (permissionEntity == null) {
      return null;
    }
    PermissionDto permissionDto = new PermissionDto();
    BeanUtils.copyProperties(permissionEntity, permissionDto);
    return permissionDto;
  }

  public List<PermissionDto> convertEntitiesToDtosPermissions(
      final List<PermissionEntity> permissionEntities) {
    if (CollectionUtils.isEmpty(permissionEntities)) {
      return Collections.emptyList();
    }
    return permissionEntities.stream().map(this::convertEntityToDtoPermission).toList();
  }

  public ResponseEntity<RoleResponse> getResponseSingleRole(
      final RoleEntity roleEntity, final Long platformId) {
    final HttpStatus httpStatus = getHttpStatusForSingleResponse(roleEntity);
    final List<RoleDto> roleDtos =
        roleEntity == null
            ? Collections.emptyList()
            : List.of(convertEntityToDtoRole(roleEntity, platformId));
    return new ResponseEntity<>(
        RoleResponse.builder()
            .roles(roleDtos)
            .responseMetadata(
                ResponseMetadata.builder()
                    .responseStatusInfo(getResponseStatusInfoForSingleResponse(roleEntity))
                    .build())
            .build(),
        getHttpStatusForSingleResponse(roleEntity));
  }

  public ResponseEntity<RoleResponse> getResponseMultipleRoles(
      final List<RoleEntity> roleEntities, final Long platformId) {
    final List<RoleDto> roleDtos = convertEntitiesToDtosRoles(roleEntities, platformId);
    return ResponseEntity.ok(RoleResponse.builder().roles(roleDtos).build());
  }

  public ResponseEntity<RoleResponse> getResponseDeleteRole() {
    return ResponseEntity.ok(
        RoleResponse.builder()
            .roles(Collections.emptyList())
            .responseMetadata(
                ResponseMetadata.builder()
                    .responseCrudInfo(ResponseCrudInfo.builder().deletedRowsCount(1).build())
                    .build())
            .build());
  }

  public ResponseEntity<RoleResponse> getResponseErrorRole(final Exception exception) {
    return new ResponseEntity<>(
        RoleResponse.builder()
            .roles(Collections.emptyList())
            .responseMetadata(
                ResponseMetadata.builder()
                    .responseStatusInfo(
                        ResponseStatusInfo.builder().errMsg(exception.getMessage()).build())
                    .build())
            .build(),
        getHttpStatusForErrorResponse(exception));
  }

  public RoleDto convertEntityToDtoRole(final RoleEntity roleEntity, final Long platformId) {
    if (roleEntity == null) {
      return null;
    }

    RoleDto roleDto = new RoleDto();
    BeanUtils.copyProperties(roleEntity, roleDto);

    if (platformId == null || platformId <= 0L) {
      return roleDto;
    }

    final List<PlatformRolePermissionEntity> platformRolePermissionEntities =
        platformRolePermissionService.readPlatformRolePermissions(platformId, roleEntity.getId());
    final List<PermissionEntity> permissionEntities =
        platformRolePermissionEntities.stream()
            .map(PlatformRolePermissionEntity::getPermission)
            .toList();
    final List<PermissionDto> permissionDtos = convertEntitiesToDtosPermissions(permissionEntities);
    roleDto.setPermissions(permissionDtos);

    return roleDto;
  }

  public List<RoleDto> convertEntitiesToDtosRoles(
      final List<RoleEntity> roleEntities, final Long platformId) {
    if (CollectionUtils.isEmpty(roleEntities)) {
      return Collections.emptyList();
    }

    if (platformId == null || platformId <= 0L) {
      return roleEntities.stream()
          .map(appRoleEntity -> convertEntityToDtoRole(appRoleEntity, platformId))
          .toList();
    }

    final List<Long> roleIds = roleEntities.stream().map(RoleEntity::getId).toList();
    final List<PlatformRolePermissionEntity> platformRolePermissionEntities =
        platformRolePermissionService.readPlatformRolePermissions(platformId, roleIds);

    Map<Long, List<PermissionDto>> rolePermissionsMap =
        platformRolePermissionEntities.stream()
            .collect(
                Collectors.groupingBy(
                    platformRolePermissionEntity -> platformRolePermissionEntity.getRole().getId(),
                    Collectors.mapping(
                        platformRolePermissionEntity ->
                            convertEntityToDtoPermission(
                                platformRolePermissionEntity.getPermission()),
                        Collectors.toList())));

    return roleEntities.stream()
        .map(
            roleEntity -> {
              RoleDto roleDto = new RoleDto();
              BeanUtils.copyProperties(roleEntity, roleDto);
              List<PermissionDto> permissions =
                  rolePermissionsMap.getOrDefault(roleDto.getId(), Collections.emptyList());
              roleDto.setPermissions(permissions);
              return roleDto;
            })
        .toList();
  }

  public ResponseEntity<ProfileResponse> getResponseSingleProfile(
      final ProfileEntity profileEntity, final Long platformId) {
    final List<ProfileDto> profileDtos =
        profileEntity == null
            ? Collections.emptyList()
            : List.of(convertEntityToDtoProfile(profileEntity, platformId));
    return new ResponseEntity<>(
        ProfileResponse.builder()
            .profiles(profileDtos)
            .responseMetadata(
                ResponseMetadata.builder()
                    .responseStatusInfo(getResponseStatusInfoForSingleResponse(profileEntity))
                    .build())
            .build(),
        getHttpStatusForSingleResponse(profileEntity));
  }

  public ResponseEntity<ProfileResponse> getResponseMultipleProfiles(
      final List<ProfileEntity> profileEntities, final Long platformId) {
    final List<ProfileDto> profileDtos = convertEntitiesToDtosProfiles(profileEntities, platformId);
    return ResponseEntity.ok(ProfileResponse.builder().profiles(profileDtos).build());
  }

  public ResponseEntity<ProfileResponse> getResponseDeleteAppUser() {
    return ResponseEntity.ok(
        ProfileResponse.builder()
            .profiles(Collections.emptyList())
            .responseMetadata(
                ResponseMetadata.builder()
                    .responseCrudInfo(ResponseCrudInfo.builder().deletedRowsCount(1).build())
                    .build())
            .build());
  }

  public ResponseEntity<ProfileResponse> getResponseErrorProfile(final Exception exception) {
    final HttpStatus httpStatus = getHttpStatusForErrorResponse(exception);
    final ResponseStatusInfo responseStatusInfo =
        ResponseStatusInfo.builder().errMsg(exception.getMessage()).build();
    return new ResponseEntity<>(
        ProfileResponse.builder()
            .profiles(Collections.emptyList())
            .responseMetadata(
                ResponseMetadata.builder()
                    .responseStatusInfo(
                        ResponseStatusInfo.builder().errMsg(exception.getMessage()).build())
                    .build())
            .build(),
        getHttpStatusForErrorResponse(exception));
  }

  public ProfileDto convertEntityToDtoProfile(
      final ProfileEntity profileEntity, final Long platformId) {
    if (profileEntity == null) {
      return null;
    }
    ProfileDto profileDto = new ProfileDto();
    BeanUtils.copyProperties(profileEntity, profileDto, "password", "addresses", "status", "roles");

    profileDto.setStatus(convertEntityToDtoStatusType(profileEntity.getStatusType()));

    if (!CollectionUtils.isEmpty(profileEntity.getAddresses())) {
      final List<ProfileAddressDto> profileAddressDtos =
          convertEntitiesToDtosProfileAddress(profileEntity.getAddresses());
      profileDto.setAddresses(profileAddressDtos);
    }

    if (platformId == null || platformId <= 0) {
      return profileDto;
    }

    List<PlatformProfileRoleEntity> platformProfileRoleEntities =
        platformProfileRoleService.readPlatformProfileRoles(platformId, profileEntity.getId());
    List<RoleEntity> roleEntities =
        platformProfileRoleEntities.stream().map(PlatformProfileRoleEntity::getRole).toList();
    List<RoleDto> roleDtos = convertEntitiesToDtosRoles(roleEntities, platformId);
    profileDto.setRoles(roleDtos);

    return profileDto;
  }

  public List<ProfileDto> convertEntitiesToDtosProfiles(
      final List<ProfileEntity> profileEntities, final Long platformId) {
    if (CollectionUtils.isEmpty(profileEntities)) {
      return Collections.emptyList();
    }
    if (platformId == null || platformId <= 0L) {
      return profileEntities.stream()
          .map(profileEntity -> convertEntityToDtoProfile(profileEntity, platformId))
          .toList();
    }

    final List<Long> profileIds = profileEntities.stream().map(ProfileEntity::getId).toList();
    final List<PlatformProfileRoleEntity> platformProfileRoleEntities =
        platformProfileRoleService.readPlatformProfileRoles(platformId, profileIds);
    Map<Long, List<RoleDto>> profileRolesMap =
        platformProfileRoleEntities.stream()
            .collect(
                Collectors.groupingBy(
                    platformProfileRoleEntity -> platformProfileRoleEntity.getProfile().getId(),
                    Collectors.mapping(
                        platformProfileRoleEntity ->
                            convertEntityToDtoRole(platformProfileRoleEntity.getRole(), platformId),
                        Collectors.toList())));

    return profileEntities.stream()
        .map(
            profileEntity -> {
              ProfileDto profileDto = new ProfileDto();
              BeanUtils.copyProperties(
                  profileEntity, profileDto, "password", "addresses", "status", "roles");
              List<RoleDto> roles =
                  profileRolesMap.getOrDefault(profileDto.getId(), Collections.emptyList());
              profileDto.setRoles(roles);
              profileDto.setStatus(convertEntityToDtoStatusType(profileEntity.getStatusType()));

              if (!CollectionUtils.isEmpty(profileEntity.getAddresses())) {
                final List<ProfileAddressDto> profileAddressDtos =
                    convertEntitiesToDtosProfileAddress(profileEntity.getAddresses());
                profileDto.setAddresses(profileAddressDtos);
              }

              return profileDto;
            })
        .toList();
  }

  private StatusTypeDto convertEntityToDtoStatusType(final StatusTypeEntity statusTypeEntity) {
    if (statusTypeEntity == null) {
      return null;
    }
    final StatusTypeDto statusTypeDto = new StatusTypeDto();
    BeanUtils.copyProperties(statusTypeEntity, statusTypeDto);
    return statusTypeDto;
  }

  private List<StatusTypeDto> convertEntitiesToDtosStatusTypes(
      final List<StatusTypeEntity> statusTypeEntities) {
    if (CollectionUtils.isEmpty(statusTypeEntities)) {
      return Collections.emptyList();
    }
    return statusTypeEntities.stream().map(this::convertEntityToDtoStatusType).toList();
  }

  private AddressTypeDto convertEntityToDtoAddressType(final AddressTypeEntity addressTypeEntity) {
    if (addressTypeEntity == null) {
      return null;
    }
    final AddressTypeDto addressTypeDto = new AddressTypeDto();
    BeanUtils.copyProperties(addressTypeEntity, addressTypeDto);
    return addressTypeDto;
  }

  private List<AddressTypeDto> convertEntitiesToDtosAddressTypes(
      final List<AddressTypeEntity> addressTypeEntities) {
    if (CollectionUtils.isEmpty(addressTypeEntities)) {
      return Collections.emptyList();
    }
    return addressTypeEntities.stream().map(this::convertEntityToDtoAddressType).toList();
  }

  private ProfileAddressDto convertEntityToDtoProfileAddress(
      final ProfileAddressEntity profileAddressEntity) {
    if (profileAddressEntity == null) {
      return null;
    }

    final ProfileAddressDto profileAddressDto = new ProfileAddressDto();
    BeanUtils.copyProperties(profileAddressEntity, profileAddressDto, "profile", "type");
    AddressTypeDto addressTypeDto = convertEntityToDtoAddressType(profileAddressEntity.getType());
    profileAddressDto.setType(addressTypeDto);

    return profileAddressDto;
  }

  private List<ProfileAddressDto> convertEntitiesToDtosProfileAddress(
      final List<ProfileAddressEntity> profileAddressEntities) {
    if (CollectionUtils.isEmpty(profileAddressEntities)) {
      return Collections.emptyList();
    }
    return profileAddressEntities.stream().map(this::convertEntityToDtoProfileAddress).toList();
  }

  public ResponseEntity<ProfilePasswordTokenResponse> getResponseErrorProfilePassword(
      final Exception exception) {
    return new ResponseEntity<>(
        ProfilePasswordTokenResponse.builder()
            .responseMetadata(
                ResponseMetadata.builder()
                    .responseStatusInfo(
                        ResponseStatusInfo.builder().errMsg(exception.getMessage()).build())
                    .build())
            .build(),
        getHttpStatusForErrorResponse(exception));
  }

  public ResponseEntity<ResponseStatusInfo> getResponseErrorResponseStatusInfo(
      final Exception exception) {
    final HttpStatus httpStatus = getHttpStatusForErrorResponse(exception);
    final ResponseStatusInfo responseStatusInfo =
        ResponseStatusInfo.builder().errMsg(exception.getMessage()).build();
    return new ResponseEntity<>(responseStatusInfo, httpStatus);
  }

  public ResponseEntity<Void> getResponseValidateProfile(
      final String redirectUrl, final boolean isValidated) {
    if (!StringUtils.hasText(redirectUrl)) {
      throw new IllegalStateException("Redirect URL cannot be null or empty...");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(
        URI.create(redirectUrl + (isValidated ? "?is_validated=true" : "?is_validated=false")));
    return new ResponseEntity<>(headers, HttpStatus.FOUND);
  }

  public ResponseEntity<Void> getResponseResetProfile(
      final String redirectUrl, final boolean isReset, final String email) {
    if (!StringUtils.hasText(redirectUrl)) {
      throw new IllegalStateException("Redirect URL cannot be null or empty...");
    }

    final String url =
        redirectUrl + (isReset ? "?is_reset=true&to_reset=" + email : "?is_reset=false");
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(url));
    return new ResponseEntity<>(headers, HttpStatus.FOUND);
  }
}
