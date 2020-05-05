package de.tforneberg.patchdb.repo.utils;

import org.springframework.stereotype.Component;

import de.tforneberg.patchdb.repo.UserRepository;

import javax.persistence.EntityNotFoundException;

@Component
public class UserUtils {
	private UserRepository repo;
	public UserUtils(UserRepository repo) { this.repo = repo; }
	
	public String mapIDtoUsername(int id) {
		try { 
			return repo.getOne(id).getName(); 
		} catch(EntityNotFoundException e) { 
			return null; 
		}
	}
}