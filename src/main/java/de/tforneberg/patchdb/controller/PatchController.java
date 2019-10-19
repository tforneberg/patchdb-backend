package de.tforneberg.patchdb.controller;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;
import de.tforneberg.patchdb.model.Band;
import de.tforneberg.patchdb.model.Patch;
import de.tforneberg.patchdb.model.Patch.PatchState;
import de.tforneberg.patchdb.model.User;
import de.tforneberg.patchdb.model.User.UserStatus;
import de.tforneberg.patchdb.repo.PatchRepository;
import de.tforneberg.patchdb.repo.UserRepository;
import de.tforneberg.patchdb.repo.utils.UserUtils;
import de.tforneberg.patchdb.service.AWSS3Client;

@RestController
@RequestMapping("/api/patches") 
public class PatchController {
	private static interface PatchCompleteOthersDefaultView extends Patch.CompleteView, User.DefaultView, Band.DefaultView {};
	
	@Autowired private UserRepository userRepository;
	@Autowired private PatchRepository patchRepo;
	
	@Autowired private AWSS3Client s3Client;
	
	@Autowired private UserUtils userUtils;
	
	//GET 
	@GetMapping("/{id}")
	@JsonView(PatchCompleteOthersDefaultView.class)
	public ResponseEntity<Patch> getById(@PathVariable("id") int id, Authentication auth) {
		boolean showIfNotApproved = ControllerUtil.hasUserStatus(auth, UserStatus.admin) || ControllerUtil.hasUserStatus(auth, UserStatus.mod);
		Optional<Patch> result = showIfNotApproved ? patchRepo.findById(id) : patchRepo.findByIdAndState(id, PatchState.approved);
		return ResponseEntity.of(result);
	}
	
	 @GetMapping
	 @JsonView(Patch.DefaultView.class)
	 public ResponseEntity<List<Patch>> getApproved(@RequestParam(name = "page") Optional<Integer> page, @RequestParam(name = "size") Optional<Integer> size,
			 @RequestParam(name = "direction") Optional<String> direction, @RequestParam(name = "sortBy" ) Optional<String> sortBy) {
		 Page<Patch> result = patchRepo.findByState(PatchState.approved, ControllerUtil.getPageable(page, size, sortBy, direction));
		 return ResponseEntity.ok().body(result.getContent());
	 }
	
	 @GetMapping("/approvalNeeded")
	 @JsonView(Patch.DefaultView.class)
	 @PreAuthorize("hasAuthority('admin') || hasAuthority('mod')")
	 public ResponseEntity<List<Patch>> getWhereApprovalNeeded(@RequestParam(name = "page") Optional<Integer> page, @RequestParam(name = "size") Optional<Integer> size,
			 @RequestParam(name = "direction") Optional<String> direction, @RequestParam(name = "sortBy" ) Optional<String> sortBy) {
		 Page<Patch> result = patchRepo.findByState(PatchState.notApproved, ControllerUtil.getPageable(page, size, sortBy, direction));
		 return ResponseEntity.ok().body(result.getContent());
	 }
	
	//POST
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> uploadFile(@RequestPart("patchData") Patch patch, @RequestPart("file") MultipartFile file) {
    	//todo check form and file validity...
    	
    	String fileUrl = s3Client.uploadPatchImages(file);
    	if (fileUrl == null || fileUrl.isEmpty()) {
    		return ResponseEntity.badRequest().build(); 
		}
   
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	patch.setImage(fileUrl);
    	patch.setUserInserted(userRepository.findByName(auth.getName()));
    	patch.setDateInserted(new Date(new java.util.Date().getTime()));
    	
    	patchRepo.save(patch);
    	return ResponseEntity.ok().build();
    }
    
    //PATCH
	@PatchMapping("/{id}")
	@JsonView(PatchCompleteOthersDefaultView.class)
	public ResponseEntity<Patch> updatePatch(@PathVariable("id") int id, @RequestBody String update, Authentication auth) {
	    Patch patch = patchRepo.findById(id).orElse(null);
	    if (patch != null) {
	    	boolean success = ControllerUtil.updateObjectWithPatchString(update, patch, Patch.class, auth);
		    return success ? ResponseEntity.ok().body(patchRepo.save(patch)) : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	    }
		return ResponseEntity.notFound().build();
	}
	
	//DELETE
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('admin') || hasAuthority('mod')") //should a user be able to delete the patches that he added/created?
	public ResponseEntity<Void> deletePatch(@PathVariable("id") int id) {
		Patch patch = patchRepo.findById(id).orElse(null);
		if (patch != null) {
			boolean success = s3Client.deleteImageAndThumbnailFromBucket(patch.getImage());
			if (success) {
				patchRepo.delete(patch);
				return ResponseEntity.ok().build();
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}
		return ResponseEntity.notFound().build();
	}

}
