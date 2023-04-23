package ru.itis.controllers.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.itis.dto.group.GroupDto;
import ru.itis.dto.other.ExceptionDto;
import ru.itis.dto.user.GroupsPage;

public interface UsersGroupsApi {


    @Operation(summary = "Remove user from group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "User is removed"),
            @ApiResponse(responseCode = "404", description = "Error's information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class))
                    })
    })
    @DeleteMapping(value = {"/users/{user_id}/groups/{group_id}", "/groups/{group_id}/users/{user_id}"})
    ResponseEntity<?> deleteUserFromGroup(@PathVariable("user_id") Long userId,
                                                 @PathVariable("group_id") Long groupId);


    @PostMapping("/groups/{group_id}/users/{user_id}")
    @Operation(summary = "Add a group to the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All user's groups",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GroupsPage.class))})
    })
    ResponseEntity<GroupDto> addGroupToUser(@PathVariable("user_id") Long userId,
                                            @PathVariable("group_id") Long groupId,
                                            @RequestHeader("Authorization") String rawToken);
}
