package de.tforneberg.patchdb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;

import de.tforneberg.patchdb.model.User;

@RepositoryRestResource(excerptProjection = Compact.class)
public interface UserRepository extends JpaRepository<User, Integer> /*, UserRepositoryCustom */ {
	User findByName(String name);
	
	User findByEmail(String email);
}


@Projection(name = "compact", types = { User.class }) 
interface Compact {
	int getId();
	String getName();
	User.Status getStatus();
	String getImage();
	List<Integer> getPatchIDs();
	
}