package x590.jmessenger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import x590.jmessenger.entity.Role;
import x590.jmessenger.entity.User;
import x590.jmessenger.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder encoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder) {
		this.userRepository = userRepository;
		this.encoder = encoder;
	}

	/**
	 * @return Пользователя с именем {@code username}.
	 * @throws UsernameNotFoundException если пользователь с таким именем не найден
	 */
	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = userRepository.findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}

		return user;
	}

	/**
	 * @return Пользователя по {@code id}.
	 * @throws UsernameNotFoundException если пользователь с таким id не найден
	 */
	@Override
	public User findUserById(int id) {
		return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public boolean saveUser(User user, BindingResult bindingResult) {
		if (userRepository.existsByUsername(user.getUsername())) {
			bindingResult.addError(new ObjectError("username", "Пользователь с таким именем уже существует"));
			return false;
		}

		if (!user.checkPasswords(bindingResult)) {
			return false;
		}

		user.setId(0);
		user.setRole(Role.USER);
		user.encodePassword(encoder);
		user.setRepeatedPassword(null);
		userRepository.save(user);
		return true;
	}

	@Override
	public void updateUser(User existingUser, User newUserData) {
		newUserData.encodePassword(encoder);
		existingUser.update(newUserData);
		userRepository.save(existingUser);
	}

	@Override
	public void deleteUser(User user) {
		userRepository.delete(user);
	}
}
