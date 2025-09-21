package com.tournament.app.footycup.backend.mapper;

import com.tournament.app.footycup.backend.dto.common.UserRef;
import com.tournament.app.footycup.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CommonMapper {
    @Named("toUserRef")
    default  UserRef toUserRef(User user) {
        if(user == null) return null;
        var name = (user.getFirstname() + " " + user.getLastname()).trim();
        return new UserRef(user.getId(), name, user.getEmail());
    }
}
