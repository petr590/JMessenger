package x590.jmessenger.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import x590.jmessenger.entity.User;

import java.util.List;

public interface UserService extends UserDetailsService {

	User findUserById(int id);

	List<User> getUsers();

	boolean saveUser(User user, BindingResult bindingResult);

	void updateUser(User user);

	boolean updateUser(User existingUser, User newUserData, BindingResult bindingResult);

	void deleteUser(User user);
}
