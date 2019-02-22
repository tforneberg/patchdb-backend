package de.tforneberg.patchdb.repo;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import de.tforneberg.patchdb.error.BadRequestException;
import de.tforneberg.patchdb.model.Patch;
import de.tforneberg.patchdb.model.User;
import de.tforneberg.patchdb.service.AWSS3Client;

@RepositoryRestController
public class PatchController {
	
	@Autowired UserRepository userRepository;
	
	private AWSS3Client s3Client;
	
	 @Autowired
	public PatchController(AWSS3Client s3Client) {
		this.s3Client = s3Client;
	}
	
	@Autowired private PatchRepository patchRepo;
	
    @RequestMapping(method=RequestMethod.POST, value="/patches", consumes="multipart/form-data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> uploadFile(@RequestPart("name") String name, @RequestPart("file") MultipartFile file) {
    	//todo check form data for validity...
    	
    	//todo check file for validity...
    	String fileUrl = s3Client.uploadFile(file);
    	
    	if (fileUrl.equals("")) {
    		throw new BadRequestException(null);
    	}
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	User userInserted = userRepository.findByName(auth.getName());
    	
    	Patch patch = new Patch();
    	java.util.Date date = new java.util.Date();
    	patch.setDateInserted(new Date(date.getTime()));
    	patch.setUserInserted(userInserted);
    	patch.setImage(fileUrl);
    	patch.setName(name);
    	
    	patchRepo.save(patch);
        return ResponseEntity.ok("success");
    }

}
