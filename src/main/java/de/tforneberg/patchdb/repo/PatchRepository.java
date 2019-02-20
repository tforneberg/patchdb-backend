package de.tforneberg.patchdb.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.web.bind.annotation.CrossOrigin;

import de.tforneberg.patchdb.model.Band;
import de.tforneberg.patchdb.model.Patch;
import de.tforneberg.patchdb.model.User;

@RepositoryRestResource(excerptProjection = InlineEverything.class)
//@CrossOrigin
public interface PatchRepository extends JpaRepository<Patch, Integer> { }

@Projection(name = "inlineEverything", types = { Patch.class }) 
interface InlineEverything {
	int getId();
	Band getBand();
	Date getDateInserted();
	Date getDatePublished();
	String getDescription();
	String getImage();
	String getManufacturer();
	String getName();
	int getNumOfCopies();
	Patch.Type getType();
	User getUserInserted();
}

