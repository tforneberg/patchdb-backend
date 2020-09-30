package de.tforneberg.patchdb.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import de.tforneberg.patchdb.model.News;
import de.tforneberg.patchdb.model.Patch;
import de.tforneberg.patchdb.model.User;
import de.tforneberg.patchdb.model.User.UserStatus;
import de.tforneberg.patchdb.repo.NewsRepository;
import de.tforneberg.patchdb.repo.UserRepository;

@RestController
@RequestMapping("/api/news") 
public class NewsController {
	
	public static interface NewsAndUserDefaultView extends News.DefaultView, User.DefaultView {}
	
	@Autowired private UserRepository userRepo;
	@Autowired private NewsRepository newsRepo;
	
	//GET
	@GetMapping(Constants.ID_MAPPING)
	@JsonView(NewsAndUserDefaultView.class)
	public ResponseEntity<News> getById(@PathVariable("id") int id) {
		return ControllerUtil.getResponseOrNotFound(newsRepo.findById(id).orElse(null));
	}
	
	 @GetMapping
	 @JsonView(NewsAndUserDefaultView.class)
	 public ResponseEntity<List<News>> getAll(@RequestParam(name = Constants.PAGE) Optional<Integer> page,
											  @RequestParam(name = Constants.SIZE) Optional<Integer> size,
											  @RequestParam(name = Constants.SORTBY) Optional<String> sortBy,
											  @RequestParam(name = Constants.DIRECTION) Optional<String> direction) {
		 Page<News> result = newsRepo.findAll(ControllerUtil.getPageable(page, size, sortBy, direction));
		 return ResponseEntity.ok().body(result.getContent());
	 }
	 
	 //POST
	 @PostMapping
	 @PreAuthorize(Constants.AUTH_ADMIN_OR_MOD)
	public ResponseEntity<Void> createNews(@RequestBody News news, BindingResult result) {
		 //TODO validate? 
		 newsRepo.save(news);
		 return ResponseEntity.ok().build();
	}
	 
	//PUT
	 @PutMapping(Constants.ID_MAPPING)
	 @PreAuthorize(Constants.AUTH_ADMIN_OR_MOD)
	 public ResponseEntity<News> updateNews(@PathVariable("id") int id, String update, Authentication auth) {
		 Optional<News> newsOptional = newsRepo.findById(id);
			if (newsOptional.isPresent()) {
				News news = newsOptional.get();
				boolean createdByRequestingUser = wasCreatedByRequestingUser(news, auth);
				if (createdByRequestingUser && ControllerUtil.isUserAllowedToDoPATCHRequest(update, News.class, auth)) {
					ControllerUtil.updateObjectWithPatchString(update, news, News.class);
					return ResponseEntity.ok().body(newsRepo.save(news));
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
				}
			}
			return ResponseEntity.notFound().build();
	 }

	//DELETE
	@DeleteMapping(Constants.ID_MAPPING)
	@PreAuthorize(Constants.AUTH_ADMIN_OR_MOD)
	public ResponseEntity<Void> deleteNews(@PathVariable("id") int id, Authentication auth) {
		Optional<News> news = newsRepo.findById(id);
		if (news.isPresent()) {
			if (wasCreatedByRequestingUser(news.get(), auth) || ControllerUtil.hasUserStatus(auth, UserStatus.admin)) {
				newsRepo.delete(news.get());
				return ResponseEntity.ok().build();
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}
		return ResponseEntity.notFound().build();
	}

	private static boolean wasCreatedByRequestingUser(News news, Authentication authentication) {
		return StringUtils.equals(news.getCreator().getName(), authentication.getName());
	}
}