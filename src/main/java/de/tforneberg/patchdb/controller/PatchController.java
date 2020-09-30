package de.tforneberg.patchdb.controller;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	@GetMapping(Constants.ID_MAPPING)
	@JsonView(PatchCompleteOthersDefaultView.class)
	public ResponseEntity<Patch> getById(@PathVariable("id") int id, Authentication auth) {
		boolean showEvenIfNotApproved = ControllerUtil.hasUserAnyStatus(auth, UserStatus.admin, UserStatus.mod);
		if (showEvenIfNotApproved) {
			return ResponseEntity.of(patchRepo.findById(id));
		} else {
			return ResponseEntity.of(patchRepo.findByIdAndState(id, PatchState.approved));
		}
	}
	
	@GetMapping("/findByName/{name}")
	@JsonView(Patch.DefaultView.class)
	public ResponseEntity<List<Patch>> getByNameApproved(@RequestParam(name = Constants.PAGE) Optional<Integer> page,
														 @RequestParam(name = Constants.SIZE) Optional<Integer> size,
														 @RequestParam(name = Constants.DIRECTION) Optional<String> direction,
														 @RequestParam(name = Constants.SORTBY) Optional<String> sortBy,
														 @PathVariable("name") String name) {
		Pageable pageable = ControllerUtil.getPageable(page, size, sortBy, direction);
		Page<Patch> result = patchRepo.findByNameContainingIgnoreCaseAndState(name, PatchState.approved, pageable);
		return ResponseEntity.ok().body(result.getContent());
	}
	
	@GetMapping("/findByBand/{id}")
	@JsonView(Patch.DefaultView.class)
	public ResponseEntity<List<Patch>> getByBandApproved(@RequestParam(name = Constants.PAGE) Optional<Integer> page,
														 @RequestParam(name = Constants.SIZE) Optional<Integer> size,
														 @RequestParam(name = Constants.DIRECTION) Optional<String> direction,
														 @RequestParam(name = Constants.SORTBY) Optional<String> sortBy,
														 @PathVariable("id") int bandId) {
		Pageable pageable = ControllerUtil.getPageable(page, size, sortBy, direction);
		Page<Patch> result = patchRepo.findByBandIdAndWithState(bandId, PatchState.approved, pageable);
		return ResponseEntity.ok().body(result.getContent());
	}
	
	@GetMapping("/findByUserCreated/{id}")
	@JsonView(Patch.DefaultView.class)
	public ResponseEntity<List<Patch>> getByUserCreatedAndApproved(@RequestParam(name = Constants.PAGE) Optional<Integer> page,
																   @RequestParam(name = Constants.SIZE) Optional<Integer> size,
																   @RequestParam(name = Constants.DIRECTION) Optional<String> direction,
																   @RequestParam(name = Constants.SORTBY) Optional<String> sortBy,
																   @PathVariable("id") int userCreatedId) {
		Pageable pageable = ControllerUtil.getPageable(page, size, sortBy, direction);
		Page<Patch> result = patchRepo.findPatchesByCreatorIdAndWithState(userCreatedId, PatchState.approved, pageable);
		return ResponseEntity.ok().body(result.getContent()); 
	}
	
	@GetMapping("/findByType/{type}")
	@JsonView(Patch.DefaultView.class)
	public ResponseEntity<List<Patch>> getByTypeAndApproved(@RequestParam(name = Constants.PAGE) Optional<Integer> page,
															@RequestParam(name = Constants.SIZE) Optional<Integer> size,
															@RequestParam(name = Constants.DIRECTION) Optional<String> direction,
															@RequestParam(name = Constants.SORTBY) Optional<String> sortBy,
															@PathVariable("type") String type) {
		Pageable pageable = ControllerUtil.getPageable(page, size, sortBy, direction);
		Patch.PatchType patchType = Patch.PatchType.valueOf(type);
		Page<Patch> result = patchRepo.findPatchesByTypeAndWithState(patchType, PatchState.approved, pageable);
		return ResponseEntity.ok().body(result.getContent());
	}
	
	 @GetMapping
	 @JsonView(Patch.DefaultView.class)
	 public ResponseEntity<List<Patch>> getApproved(@RequestParam(name = Constants.PAGE) Optional<Integer> page,
													@RequestParam(name = Constants.SIZE) Optional<Integer> size,
													@RequestParam(name = Constants.DIRECTION) Optional<String> direction,
													@RequestParam(name = Constants.SORTBY) Optional<String> sortBy) {
		 Pageable pageable = ControllerUtil.getPageable(page, size, sortBy, direction);
		 Page<Patch> result = patchRepo.findByState(PatchState.approved, pageable);
		 return ResponseEntity.ok().body(result.getContent());
	 }
	
	 @GetMapping("/approvalNeeded")
	 @JsonView(Patch.DefaultView.class)
	 @PreAuthorize(Constants.AUTH_ADMIN_OR_MOD)
	 public ResponseEntity<List<Patch>> getWhereApprovalNeeded(@RequestParam(name = Constants.PAGE) Optional<Integer> page,
															   @RequestParam(name = Constants.SIZE) Optional<Integer> size,
															   @RequestParam(name = Constants.DIRECTION) Optional<String> direction,
															   @RequestParam(name = Constants.SORTBY) Optional<String> sortBy) {
	 	Pageable pageable = ControllerUtil.getPageable(page, size, sortBy, direction);
	 	Page<Patch> result = patchRepo.findByState(PatchState.notApproved, pageable);
	 	return ResponseEntity.ok().body(result.getContent());
	 }
	
	//POST
    @PostMapping
    @PreAuthorize(Constants.LOGGED_IN)
    public ResponseEntity<Void> uploadFile(@RequestPart("patchData") Patch patch,
										   @RequestPart("file") MultipartFile file) {
    	//todo check form and file validity...
    	
    	String fileUrl = s3Client.uploadPatchImages(file);
    	if (fileUrl == null || fileUrl.isEmpty()) {
    		return ResponseEntity.badRequest().build(); 
		}
   
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	patch.setImage(fileUrl);
    	patch.setUserInserted(userRepository.findByName(auth.getName()));
    	patch.setDateInserted(new Date(new java.util.Date().getTime()));
    	patch.setState(PatchState.notApproved);
    	
    	patchRepo.save(patch);
    	return ResponseEntity.ok().build();
    }
    
    //PATCH
	@PatchMapping(Constants.ID_MAPPING)
	@JsonView(PatchCompleteOthersDefaultView.class)
	public ResponseEntity<Patch> updatePatch(@PathVariable("id") int id, @RequestBody String update, Authentication auth) {
	    Optional<Patch> patch = patchRepo.findById(id);
	    if (patch.isPresent()) {
	    	if (ControllerUtil.isUserAllowedToDoPATCHRequest(update, Patch.class, auth)) {
				ControllerUtil.updateObjectWithPatchString(update, patch.get(), Patch.class);
				return ResponseEntity.ok().body(patchRepo.save(patch.get()));
			} else {
	    		ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
	    }
		return ResponseEntity.notFound().build();
	}
	
	//DELETE
	@DeleteMapping(Constants.ID_MAPPING)
	@PreAuthorize(Constants.AUTH_ADMIN_OR_MOD) //should a user be able to delete the patches that he added/created?
	public ResponseEntity<Void> deletePatch(@PathVariable("id") int id) {
		Optional<Patch> patch = patchRepo.findById(id);
		if (patch.isPresent()) {
			boolean success = s3Client.deleteImageAndThumbnailFromBucket(patch.get().getImage());
			if (success) {
				patchRepo.delete(patch.get());
				return ResponseEntity.ok().build();
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}
		return ResponseEntity.notFound().build();
	}

}
