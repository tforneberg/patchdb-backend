package de.tforneberg.patchdb.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import de.tforneberg.patchdb.model.dto.RegisterRequestData;
import de.tforneberg.patchdb.repo.UserRepository;

@Component
public class RegisterRequestValidator implements Validator {
    
    @Autowired
    private UserRepository userRepo;

    @Override
    public boolean supports(Class<?> aClass) {
        return RegisterRequestData.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        RegisterRequestData req = (RegisterRequestData) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "notEmpty");
        if (req.getName().length() < 6 || req.getName().length() > 32) {
            errors.rejectValue("name", "size");
        }
        
        if (userRepo.findByName(req.getName()) != null) {
            errors.rejectValue("name", "duplicate");
        }
        
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "notEmpty");
        if (userRepo.findByEmail(req.getEmail()) != null) {
            errors.rejectValue("email", "duplicate");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "notEmpty");
        if (req.getPassword().length() < 6 || req.getPassword().length() > 32) {
            errors.rejectValue("password", "size");
        }
        
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password2", "notEmpty");
        if (req.getPassword().length() < 6 || req.getPassword().length() > 32) {
            errors.rejectValue("password2", "size");
        }

        if (!req.getPassword().equals(req.getPassword2())) {
            errors.rejectValue("password2", "passwordConfirm");
        }
        
        if (!req.isAcceptedTerms()) {
        	errors.rejectValue("acceptedTerms", "notAccepted");	
        }
    }
}