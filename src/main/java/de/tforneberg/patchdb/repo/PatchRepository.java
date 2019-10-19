package de.tforneberg.patchdb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PreAuthorize;

import de.tforneberg.patchdb.model.Patch;
import de.tforneberg.patchdb.model.Patch.PatchState;

public interface PatchRepository extends JpaRepository<Patch, Integer> { 
	
	@Query(value="SELECT * FROM patches WHERE id IN (SELECT patch_id FROM collections WHERE user_id = ?1)", nativeQuery = true,
			countQuery="SELECT count(*) FROM  patches WHERE id IN (SELECT patch_id FROM collections WHERE user_id = ?1)")
	Page<Patch> findPatchesByUserId(Integer id, Pageable page); //TODO convert to jpql?
	
	@Query(value="SELECT * FROM patches WHERE id IN (SELECT patch_id FROM collections WHERE user_id = ?1) AND state = ?2", nativeQuery = true,
			countQuery="SELECT count(*) FROM  patches WHERE id IN (SELECT patch_id FROM collections WHERE user_id = ?1) AND state = ?2")
	Page<Patch> findPatchesByUserIdAndWithState(Integer id, PatchState state, Pageable page); //TODO convert to jpql?
	
	Optional<Patch> findByIdAndState(Integer id, PatchState state);
	
	Page<Patch> findByState(PatchState state, Pageable page);
}

