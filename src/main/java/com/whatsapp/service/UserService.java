package com.whatsapp.service;

import java.util.List;

import com.whatsapp.exception.UserException;
import com.whatsapp.model.User;
import com.whatsapp.request.UpdateUserRequest;

public interface UserService {

	public User findUserById(Integer id) throws UserException;

	public User findUserProfile(String jwt) throws UserException;

	public User updateUser(Integer userId, UpdateUserRequest req) throws UserException;

	public List<User> searchUser(String query);

}
