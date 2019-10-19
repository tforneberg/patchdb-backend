package de.tforneberg.patchdb.config;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.tforneberg.patchdb.model.Band;
import de.tforneberg.patchdb.model.Patch;
import de.tforneberg.patchdb.model.User;


@Configuration
public class RepoMvcConfig extends RepositoryRestMvcConfiguration {
	
	@Autowired private EntityManager entityManager;
	private ApplicationContext context;

	public RepoMvcConfig(ApplicationContext context, ObjectFactory<ConversionService> conversionService) {
		super(context, conversionService);
		this.context = context;
	}
	
	@Bean public RepositoryRestConfigurer repositoryRestConfigurer() {
	    return new RepositoryRestConfigurer() {
		    @Override public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		  		config.exposeIdsFor(User.class);
				config.exposeIdsFor(Band.class);
				config.exposeIdsFor(Patch.class);
				
				config.disableDefaultExposure();
				
				config.setBasePath("/api");
		    }
	    };
	}

}
