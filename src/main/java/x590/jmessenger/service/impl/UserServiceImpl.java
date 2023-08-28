package x590.jmessenger.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import x590.jmessenger.entities.User;
import x590.jmessenger.repository.UserRepository;
import x590.jmessenger.service.UserService;

import java.util.Optional;

@Component
public class UserServiceImpl extends AbstractServiceImpl<User, Integer, UserRepository> implements UserService {

	@Autowired
	private UserServiceImpl(UserRepository repository) {
		super(repository);
	}

	@Override
	public Optional<User> findById(int id) {
		return getRepository().findById(id);
	}

	@Override
	public boolean updateById(int userId, User user) {
		return getRepository().updateById(userId, user.getName());
	}
}
