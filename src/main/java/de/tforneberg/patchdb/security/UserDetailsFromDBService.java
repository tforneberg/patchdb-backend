package de.tforneberg.patchdb.security;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.User;
import de.tforneberg.patchdb.repo.UserRepository;

@Service
public class UserDetailsFromDBService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		
		//Get active user from database (own model User object)
		de.tforneberg.patchdb.model.User user = userRepo.findByName(userName);
		
		//Get the users' authority
		GrantedAuthority authority = new SimpleGrantedAuthority(user.getStatus().toString());
		
		//Construct and return a UserDetails implementation object
		UserDetails userDetails = (UserDetails) new User(user.getName(), user.getPassword(), Arrays.asList(authority));
		return userDetails;
	}
}