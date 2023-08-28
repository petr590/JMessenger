package x590.jmessenger.service;

import org.springframework.stereotype.Service;
import x590.jmessenger.entities.Chat;

import java.util.Optional;

@Service
public interface ChatService {
	Optional<Chat> findById(int id);
}
