package de.tforneberg.patchdb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import de.tforneberg.patchdb.model.News;

public interface NewsRepository extends JpaRepository<News, Integer> {

}
