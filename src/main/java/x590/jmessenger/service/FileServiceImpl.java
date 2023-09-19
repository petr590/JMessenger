package x590.jmessenger.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x590.jmessenger.entity.File;
import x590.jmessenger.repository.ImageRepository;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

	private final ImageRepository repository;

	@Override
	public File findImageById(int id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cannot find image"));
	}
}
