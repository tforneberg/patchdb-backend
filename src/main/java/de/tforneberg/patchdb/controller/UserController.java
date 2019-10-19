package de.tforneberg.patchdb.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import de.tforneberg.patchdb.error.BadRequestException;
import de.tforneberg.patchdb.model.Collection;
import de.tforneberg.patchdb.model.Patch;
import de.tforneberg.patchdb.model.Patchable;
import de.tforneberg.patchdb.model.User;
import de.tforneberg.patchdb.model.Patch.PatchState;
import de.tforneberg.patchdb.model.User.UserStatus;
import de.tforneberg.patchdb.model.dto.ChangePasswordRequestData;
import de.tforneberg.patchdb.model.dto.RegisterRequestData;
import de.tforneberg.patchdb.repo.PatchRepository;
import de.tforneberg.patchdb.repo.UserRepository;
import de.tforneberg.patchdb.repo.utils.UserUtils;
import de.tforneberg.patchdb.validation.ChangePasswordRequestValidator;
import de.tforneberg.patchdb.validation.RegisterRequestValidator;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	public static interface UserAndPatchDefaultView extends Patch.DefaultView, User.DefaultView {};
	
	@Autowired private UserRepository repo;
	@Autowired private PatchRepository patchRepo;
	
	@Autowired private ChangePasswordRequestValidator changePWvalidator;
	@Autowired private RegisterRequestValidator registerValidator;
	@Autowired private PasswordEncoder passwordEncoder;
	
	@Autowired private UserUtils userUtils;
	
	//GET
	@GetMapping("/{id}")
	@JsonView(User.CompleteView.class)
	public ResponseEntity<User> getById(@PathVariable("id") int id) {
		User result = repo.findById(id).orElse(null);
		
		return ControllerUtil.getResponseOrNotFound(result);
	}

	@GetMapping("/findByName/{name}")
	@JsonView(User.CompleteView.class)
	public ResponseEntity<User> getByName(@PathVariable("name") String name) {
		User result = repo.findByName(name);
		
		return ControllerUtil.getResponseOrNotFound(result);
	}
	
	@GetMapping()
	@JsonView(User.DefaultView.class)
	public ResponseEntity<List<User>> getAll(@RequestParam(name = "page") Optional<Integer> page, @RequestParam(name = "size") Optional<Integer> size, 
			@RequestParam(name = "direction") Optional<String> direction, @RequestParam(name = "sortBy" ) Optional<String> sortBy) {
		Page<User> result = repo.findAll(ControllerUtil.getPageable(page, size, direction, sortBy));
		return ResponseEntity.ok().body(result.getContent());
	}
	
	//POST
	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody RegisterRequestData req, BindingResult result) {
		registerValidator.validate(req, result);
		if (result.hasErrors()) { throw new BadRequestException(result); }
		
		User user = new User();
		user.setName(req.getName());
		user.setEmail(req.getEmail());
		user.setPassword(passwordEncoder.encode(req.getPassword()));
		user.setStatus(User.UserStatus.user);
		
		repo.save(user);
		//todo: send confirmation email
		
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{id}/patches")
	@JsonView(UserAndPatchDefaultView.class)
	public ResponseEntity<Collection> getUserPatches(@PathVariable("id") int id, 
			@RequestParam(name = "page") Optional<Integer> page, @RequestParam(name = "size") Optional<Integer> size, 
			@RequestParam(name = "sortBy") Optional<String> sortBy, @RequestParam(name = "direction") Optional<String> direction, Authentication auth) {
		boolean showIfNotApproved = ControllerUtil.hasUserStatus(auth, UserStatus.admin) || ControllerUtil.hasUserStatus(auth, UserStatus.mod);
		User user = repo.findById(id).orElse(null);
		
		Page<Patch> patches;
		if (showIfNotApproved) {
			patches = patchRepo.findPatchesByUserId(id, ControllerUtil.getPageable(page, size, sortBy, direction));
		} else {
			patches = patchRepo.findPatchesByUserIdAndWithState(id, PatchState.approved, ControllerUtil.getPageable(page, size, sortBy, direction));
		}
		
		Collection result = user != null && patches != null ? new Collection(patches.getContent(), user.getName()) : null;
		
		return ControllerUtil.getResponseOrNotFound(result);
	}
	
	//PATCH
	@PatchMapping("/{id}")
	@JsonView(User.CompleteView.class)
	public ResponseEntity<User> updateUser(@PathVariable("id") int id, @RequestBody String update, Authentication auth) {
	    User user = repo.findById(id).orElse(null);
	    if (user != null) {
	    	boolean success = ControllerUtil.updateObjectWithPatchString(update, user, User.class, auth);
		    return success ? ResponseEntity.ok().body(repo.save(user)) : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	    }
		return ResponseEntity.notFound().build();
	}

	@PatchMapping("/{id}/changePassword")
	@PreAuthorize("@userUtils.mapIDtoUsername(#id) == authentication.principal.username")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestData data, @PathVariable("id") int id, BindingResult result) {
		changePWvalidator.validate(data, result);
		if (result.hasErrors()) { throw new BadRequestException(result); }
		
		User user = repo.findById(id).orElse(null);
		user.setPassword(passwordEncoder.encode(data.getPassword()));
		repo.save(user);
		
		return ResponseEntity.ok().build();
	}
	
	@PatchMapping("/{id}/patches")
	@PreAuthorize("@userUtils.mapIDtoUsername(#id) == authentication.principal.username")
	public ResponseEntity<String> addOrRemovePatch(@RequestBody Patch data, @PathVariable("id") int id) {
		if (Patchable.HttpPatchOperation.remove.equals(data.getOperation())) {
			repo.deletePatchFromCollection(data.getId(), id);
		} else if (Patchable.HttpPatchOperation.add.equals(data.getOperation())) {
			repo.insertIntoCollection(data.getId(), id);
		}
		return ResponseEntity.ok().build();
	}

}