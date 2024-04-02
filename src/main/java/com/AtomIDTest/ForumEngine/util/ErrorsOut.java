package com.AtomIDTest.ForumEngine.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.List;

public class ErrorsOut {
    public static void returnErrors(BindingResult bindingResult){
        StringBuilder errMessage = new StringBuilder();
        errMessage.append("Invalid input - ");
        List<FieldError> errors = Collections.singletonList(bindingResult.getFieldError());
        for (FieldError error: errors){
            errMessage.append(error.getDefaultMessage()).append("; ");
        }

        throw new NotCreatedException(errMessage.toString());
    }
}
