package de.tforneberg.patchdb.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.tforneberg.patchdb.model.News;

public interface NewsRepository extends JpaRepository<News, Integer> {
	
	@Query(value="SELECT n FROM News as n WHERE creator.id = ?1",
			countQuery="SELECT count(n) FROM News as n WHERE creator.id = ?1")
	Page<News> findByCreatorId(Integer creatorId, Pageable pageable);

}
