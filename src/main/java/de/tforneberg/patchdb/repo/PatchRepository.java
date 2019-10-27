package de.tforneberg.patchdb.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import de.tforneberg.patchdb.model.Patch;
import de.tforneberg.patchdb.model.Patch.PatchState;
import de.tforneberg.patchdb.model.Patch.PatchType;

public interface PatchRepository extends JpaRepository<Patch, Integer> { 
	
	@Query(value="SELECT * FROM patches WHERE id IN (SELECT patch_id FROM collections WHERE user_id = ?1)", nativeQuery = true,
			countQuery="SELECT count(*) FROM  patches WHERE id IN (SELECT patch_id FROM collections WHERE user_id = ?1)")
	Page<Patch> findPatchesByUserId(Integer id, Pageable page); //TODO convert to JPQL
	
	@Query(value="SELECT * FROM patches WHERE id IN (SELECT patch_id FROM collections WHERE user_id = ?1) AND state = ?2", nativeQuery = true,
			countQuery="SELECT count(*) FROM  patches WHERE id IN (SELECT patch_id FROM collections WHERE user_id = ?1) AND state = ?2")
	Page<Patch> findPatchesByUserIdAndWithState(Integer id, PatchState state, Pageable page); //TODO convert to JPQL
	
	Optional<Patch> findByIdAndState(Integer id, PatchState state);
	
	Page<Patch> findByNameContainingIgnoreCaseAndState(String name, PatchState state, Pageable pageable);
	
	Page<Patch> findByState(PatchState state, Pageable page);
	
	@Query(value="SELECT p FROM Patch AS p WHERE p.band.id = ?1 AND p.state = ?2", 
			countQuery="SELECT COUNT(p) FROM Patch AS p WHERE p.band.id = ?1 AND p.state = ?2")
	Page<Patch> findByBandIdAndWithState(Integer bandId, PatchState state, Pageable page);
	
	@Query(value="SELECT p FROM Patch AS p WHERE p.userInserted.id = ?1 AND p.state = ?2",
			countQuery = "SELECT COUNT(p) FROM Patch AS p WHERE p.userInserted.id = ?1 AND p.state = ?2")
	Page<Patch> findPatchesByCreatorIdAndWithState(Integer id, PatchState state, Pageable page);
	
	@Query(value="SELECT p FROM Patch AS p WHERE p.type = ?1 AND p.state = ?2",
			countQuery = "SELECT COUNT(p) FROM Patch AS p WHERE p.type = ?1 AND p.state = ?2")
	Page<Patch> findPatchesByTypeAndWithState(PatchType type, PatchState state, Pageable page);
}

