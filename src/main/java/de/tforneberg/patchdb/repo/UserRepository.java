package de.tforneberg.patchdb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import de.tforneberg.patchdb.controller.Constants;
import de.tforneberg.patchdb.controller.ControllerUtil;
import de.tforneberg.patchdb.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	User findByName(String name);
	
	User findByEmail(String email);
	
	@Override @PreAuthorize(Constants.AUTH_ADMIN_OR_MOD) 
	void deleteById(Integer id);
	
	@Override @PreAuthorize(Constants.AUTH_ADMIN_OR_MOD)
	void delete(User user);
	
	@Modifying @Transactional @Query(value="INSERT INTO collections (patch_id, user_id) VALUES (?1, ?2)", nativeQuery = true)
	void insertIntoCollection(Integer patchId, Integer userId);
	
	@Modifying @Transactional @Query(value="DELETE FROM collections WHERE patch_id = ?1 AND user_id = ?2 LIMIT 1", nativeQuery = true)
	void deletePatchFromCollection(Integer patchId, Integer userId); //TODO return value boolean or Integer?
}
