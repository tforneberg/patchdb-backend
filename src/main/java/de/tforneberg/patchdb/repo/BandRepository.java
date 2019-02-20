package de.tforneberg.patchdb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import de.tforneberg.patchdb.model.Band;

@PreAuthorize("hasRole('ROLE_user')")
public interface BandRepository extends JpaRepository<Band, Integer> {
	
	@PreAuthorize("hasRole('ROLE_admin')") 
	@Override
	void deleteById(Integer id);

	@PreAuthorize("hasRole('ROLE_admin')")
	@Override
	void delete(Band band);

	@PreAuthorize("hasRole('ROLE_admin')")
	@Override
	void deleteAll(Iterable<? extends Band> bands);

	@PreAuthorize("hasRole('ROLE_admin')")
	@Override
	void deleteAll();
}
