package org.osehra.cpe.hub;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Arrays;

public class VistaAccountValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return VistaAccount.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "vistaId", "default.blank.message", new Object[]{"vistaId", VistaAccount.class});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "division", "default.blank.message", new Object[]{"division", VistaAccount.class});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "host", "default.blank.message", new Object[]{"host", VistaAccount.class});
        ValidationUtils.rejectIfEmpty(errors, "port", "default.null.message", new Object[]{"port", VistaAccount.class});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "default.blank.message", new Object[]{"name", VistaAccount.class});
    }

}
