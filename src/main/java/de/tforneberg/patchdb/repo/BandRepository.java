package de.tforneberg.patchdb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import de.tforneberg.patchdb.model.Band;

public interface BandRepository extends JpaRepository<Band, Integer> {
	

	
}
