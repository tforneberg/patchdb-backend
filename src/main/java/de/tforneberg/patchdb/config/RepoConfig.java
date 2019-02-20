package de.tforneberg.patchdb.config;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;

import de.tforneberg.patchdb.model.Band;
import de.tforneberg.patchdb.model.Patch;
import de.tforneberg.patchdb.model.User;

@Component
public class RepoConfig implements RepositoryRestConfigurer {
	
	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.exposeIdsFor(User.class);
		config.exposeIdsFor(Band.class);
		config.exposeIdsFor(Patch.class);
	}
}
