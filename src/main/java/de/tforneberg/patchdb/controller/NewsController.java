package de.tforneberg.patchdb.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import de.tforneberg.patchdb.model.News;
import de.tforneberg.patchdb.model.User;
import de.tforneberg.patchdb.repo.NewsRepository;
import de.tforneberg.patchdb.repo.UserRepository;

@RestController
@RequestMapping("/api/news") 
public class NewsController {
	
	public static interface NewsAndUserDefaultView extends News.DefaultView, User.DefaultView {}
	
	@Autowired private UserRepository userRepo;
	@Autowired private NewsRepository newsRepo;
	
	//GET
	@GetMapping("/{id}")
	@JsonView(NewsAndUserDefaultView.class)
	public ResponseEntity<News> getById(@PathVariable("id") int id) {
		News result = newsRepo.findById(id).orElse(null);
		
		return ResponseEntity.ok().body(result);
	}
	
	 @GetMapping
	 @JsonView(NewsAndUserDefaultView.class)
	 public ResponseEntity<List<News>> getAll(@RequestParam(name = "page") Optional<Integer> page, @RequestParam(name = "size") Optional<Integer> size, 
			 @RequestParam(name = "sortBy") Optional<String> sortBy, @RequestParam(name = "direction") Optional<String> direction) {
		 Page<News> result = newsRepo.findAll(ControllerUtil.getPageable(page, size, sortBy, direction));
		 
		 return ResponseEntity.ok().body(result.getContent());
	 }
	 
	 //POST
	 @PostMapping
	 @PreAuthorize("hasAuthority('admin') || hasAuthority('mod')")
	 public ResponseEntity<Void> createNews(@RequestBody News news, BindingResult result) {
		 //TODO validate? 
		 newsRepo.save(news);
		 return ResponseEntity.ok().build();
	 }
}
