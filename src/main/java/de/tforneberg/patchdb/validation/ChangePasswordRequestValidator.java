package de.tforneberg.patchdb.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import de.tforneberg.patchdb.model.requests.ChangePasswordRequestData;

@Component
public class ChangePasswordRequestValidator implements Validator {
	
    @Override
    public boolean supports(Class<?> aClass) {
        return ChangePasswordRequestData.class.equals(aClass);
    }
	
	@Override
    public void validate(Object o, Errors errors) {
        ChangePasswordRequestData req = (ChangePasswordRequestData) o;

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
    }

}
