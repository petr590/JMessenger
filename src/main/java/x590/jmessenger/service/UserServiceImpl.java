package x590.jmessenger.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import x590.jmessenger.entity.Role;
import x590.jmessenger.entity.User;
import x590.jmessenger.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository repository;

	private final PasswordEncoder encoder;


	/**
	 * @return Пользователя с именем {@code username}.
	 * @throws UsernameNotFoundException если пользователь с таким именем не найден
	 */
	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = repository.findByUsername(username);

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
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
	}

	@Override
	public List<User> getUsers() {
		return repository.findAll();
	}

	@Override
	public boolean saveUser(User user, BindingResult bindingResult) {
		checkUsername(user, bindingResult);
		user.checkPasswords(bindingResult);

		if (bindingResult.hasErrors()) {
			return false;
		}

		user.setId(0);
		user.setRole(Role.USER);
		user.encodePassword(encoder);
		user.setRepeatedPassword(null);
		repository.save(user);
		return true;
	}

	@Override
	public void updateUser(User user) {
		repository.save(user);
	}

	@Override
	public boolean updateUser(User existingUser, User newUserData, BindingResult bindingResult) {
		var username = newUserData.getUsername();

		if (username != null && !existingUser.getUsername().equals(username)) {
			checkUsername(newUserData, bindingResult);

			if (bindingResult.hasErrors()) {
				return false;
			}
		}

		newUserData.encodePassword(encoder);
		existingUser.update(newUserData);
		repository.save(existingUser);

		return true;
	}

	private void checkUsername(User user, BindingResult bindingResult) {
		if (repository.existsByUsername(user.getUsername())) {
			bindingResult.rejectValue("username", "error.user.alreadyExists");
		}
	}

	@Override
	public void deleteUser(User user) {
		user.setDeleted(true);
		repository.save(user);
	}
}
