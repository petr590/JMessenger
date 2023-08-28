package x590.jmessenger.service;

import org.springframework.stereotype.Service;
import x590.jmessenger.entities.User;

import java.util.Optional;

@Service
public interface UserService {

	Optional<User> findById(int id);

	boolean updateById(int userId, User user);
}
