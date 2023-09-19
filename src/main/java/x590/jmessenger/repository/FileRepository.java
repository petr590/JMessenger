package x590.jmessenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import x590.jmessenger.entity.File;

@Repository
public interface ImageRepository extends JpaRepository<File, Integer> {}
