package com.keshe3.keshe3server.entity;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-09-04
 */
@Getter
@Setter
  public class User implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 用户编号
     */
        private String id;

      /**
     * 用户名称
     */
      private String userName;

      /**
     * 用户密码
     */
      private String userPassword;

      /**
     * 用户邮箱
     */
      private String userEmail;

      /**
     * 用户手机号
     */
      private String userPhone;

      /**
     * 用户权限
     */
      private String userPermission;
}
