package de.tforneberg.patchdb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import de.tforneberg.patchdb.model.Band;
import de.tforneberg.patchdb.repo.BandRepository;

@RestController
@RequestMapping("/api/bands")
public class BandController {

	@Autowired BandRepository repo;
	
	@GetMapping("/{id}")
	@JsonView(Band.CompleteView.class)
	public ResponseEntity<Band> getById(@PathVariable("id") int id) {
		Band result = repo.findById(id).orElse(null);
		
		return ResponseEntity.ok().body(result);
	}
	
	@GetMapping()
	@JsonView(Band.DefaultView.class)
	public ResponseEntity<List<Band>> getAll() {
		List<Band> result = repo.findAll();
		
		return ResponseEntity.ok().body(result);
	}
	
}
