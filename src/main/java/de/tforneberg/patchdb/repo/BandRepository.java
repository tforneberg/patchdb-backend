package de.tforneberg.patchdb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import de.tforneberg.patchdb.model.Band;

public interface BandRepository extends JpaRepository<Band, Integer> {
	
	@PreAuthorize("hasAuthority('admin') || hasAuthority('mod')") 
	@Override
	void deleteById(Integer id);

	@PreAuthorize("hasAuthority('admin') || hasAuthority('mod')")
	@Override
	void delete(Band band);

	@PreAuthorize("hasAuthority('admin') || hasAuthority('mod')")
	@Override
	void deleteAll(Iterable<? extends Band> bands);

	@PreAuthorize("hasAuthority('admin') || hasAuthority('mod')")
	@Override
	void deleteAll();
}
