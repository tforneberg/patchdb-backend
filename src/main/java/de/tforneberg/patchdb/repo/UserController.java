package de.tforneberg.patchdb.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import de.tforneberg.patchdb.error.BadRequestException;
import de.tforneberg.patchdb.model.User;
import de.tforneberg.patchdb.model.requests.AddOrRemovePatchRequest;
import de.tforneberg.patchdb.model.requests.ChangePasswordRequestData;
import de.tforneberg.patchdb.model.requests.RegisterRequestData;
import de.tforneberg.patchdb.validation.ChangePasswordRequestValidator;
import de.tforneberg.patchdb.validation.RegisterRequestValidator;

@RepositoryRestController
public class UserController {
	@Autowired private UserRepository repo;
	@Autowired private PatchRepository patchRepo;
	
	@Autowired private ChangePasswordRequestValidator changePWvalidator;
	@Autowired private RegisterRequestValidator registerValidator;
	@Autowired private PasswordEncoder passwordEncoder;
	
	public static class UserUtils {
		private UserRepository repo;
		public UserUtils(UserRepository repo) { this.repo = repo; }
		
		public String mapIDtoUsername(int id) {
			try { return repo.getOne(id).getName(); } 
			catch(javax.persistence.EntityNotFoundException e) { return null; }
		}
	}
	
	@Bean
	public UserUtils userUtils() { return new UserUtils(repo); }
	
	@PostMapping("/users/register")
	public ResponseEntity<String> processRegistrationForm(@RequestBody RegisterRequestData req, BindingResult result) {
		registerValidator.validate(req, result);
		
		if (result.hasErrors()) {
			throw new BadRequestException(result);
		}
		
		//create new User object
		User user = new User();
		user.setName(req.getName());
		user.setEmail(req.getEmail());
		user.setPassword(passwordEncoder.encode(req.getPassword()));
		user.setStatus(User.Status.user);
		
		// save user in the database
		repo.save(user);
		
		//todo: send confirmation email
		
		return ResponseEntity.ok("success");
	}

	@PatchMapping("users/{id}/changePassword")
	@PreAuthorize("@userUtils.mapIDtoUsername(#id) == authentication.principal.username")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestData data, @PathVariable("id") int id, BindingResult result) {
		changePWvalidator.validate(data, result);
		
		if (result.hasErrors()) {
			throw new BadRequestException(result);
		}
		
		User user = repo.getOne(id);
		user.setPassword(passwordEncoder.encode(data.getPassword()));
		repo.save(user);
		
		return ResponseEntity.ok("success");
	}
	
	@PatchMapping("users/{id}/patches")
	@PreAuthorize("@userUtils.mapIDtoUsername(#id) == authentication.principal.username")
	public ResponseEntity<String> addOrRemovePatch(@RequestBody AddOrRemovePatchRequest data, @PathVariable("id") int id) {
		User user = repo.getOne(id);
		
		if (data.getOp().equals("add")) {
			user.addPatch(patchRepo.getOne(data.getValue()));
		} else if (data.getOp().equals("remove")) {
			user.removePatch(patchRepo.getOne(data.getValue()));
		} else throw new BadRequestException(null);		

		repo.save(user);
		
		return ResponseEntity.ok("success");
	}

}
