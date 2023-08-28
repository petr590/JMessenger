package x590.jmessenger.service.impl;

import org.springframework.data.jpa.repository.JpaRepository;

public class AbstractServiceImpl<T, ID, R extends JpaRepository<T, ID>> {

	private final R repository;

	public AbstractServiceImpl(R repository) {
		this.repository = repository;
	}

	public R getRepository() {
		return repository;
	}
}
