package com.AtomIDTest.ForumEngine.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UUIDValidator {
    public boolean checkingForIncorrectUUID(String id){
        Pattern UUID_REGEX =
                Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        if (!UUID_REGEX.matcher(id).matches()){
            return true;
//            throw new InvalidIdException("Invalid  UUID");
        }
        return false;
    }
}
