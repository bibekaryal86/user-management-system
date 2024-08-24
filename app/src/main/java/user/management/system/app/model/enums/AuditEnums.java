package user.management.system.app.model.enums;

public class AuditEnums {
  public enum AuditUsers {
    CREATE_USER,
    UPDATE_USER,
    UPDATE_USER_EMAIL,
    UPDATE_USER_PASSWORD,
    SOFT_DELETE_USER,
    HARD_DELETE_USER,
    RESTORE_USER,
    ASSIGN_ROLE,
    UNASSIGN_ROLE,
    ASSIGN_APP,
    UNASSIGN_APP,
    USER_LOGIN,
    USER_LOGIN_ERROR,
    USER_LOGOUT,
    USER_LOGOUT_ERROR,
    USER_VALIDATE_INIT,
    USER_VALIDATE_EXIT,
    USER_VALIDATE_ERROR,
    USER_RESET_INIT,
    USER_RESET_EXIT,
    USER_RESET,
    USER_RESET_ERROR,
    TOKEN_REFRESH,
    TOKEN_REFRESH_ERROR
  }

  public enum AuditRoles {
    CREATE_ROLE,
    UPDATE_ROLE,
    SOFT_DELETE_ROLE,
    HARD_DELETE_ROLE,
    RESTORE_ROLE,
    ASSIGN_PERMISSION,
    UNASSIGN_PERMISSION
  }

  public enum AuditPermissions {
    CREATE_PERMISSION,
    UPDATE_PERMISSION,
    SOFT_DELETE_PERMISSION,
    HARD_DELETE_PERMISSION,
    RESTORE_PERMISSION
  }

  public enum AuditApps {
    CREATE_APP,
    UPDATE_APP,
    SOFT_DELETE_APP,
    HARD_DELETE_APP,
    RESTORE_APP
  }
}
