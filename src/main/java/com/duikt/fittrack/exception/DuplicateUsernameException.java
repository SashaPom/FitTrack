package com.duikt.fittrack.exception;

public class DuplicateUsernameException extends RuntimeException{
  public static final String USER_WITH_USERNAME_EXIST_MESSAGE = "User with username %s already exists";

  public DuplicateUsernameException(String productName) {
    super(String.format(USER_WITH_USERNAME_EXIST_MESSAGE, productName));
  }
}
