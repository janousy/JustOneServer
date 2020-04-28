package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.rest.dto.user.UserGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.user.UserPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.user.UserPutDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g., UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for creating information (POST).
 */
@Mapper
public interface UserDTOMapper {

    UserDTOMapper INSTANCE = Mappers.getMapper(UserDTOMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "token", target = "token")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "overallScore", target = "overallScore")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "token", target = "token")
    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);

}
