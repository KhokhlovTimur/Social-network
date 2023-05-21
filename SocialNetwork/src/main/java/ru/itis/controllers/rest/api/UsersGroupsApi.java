package ru.itis.controllers.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.group.GroupDto;
import ru.itis.dto.other.ExceptionDto;
import ru.itis.dto.group.GroupsPage;
import ru.itis.dto.user.UsersPage;

@RequestMapping("/api")
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
    @DeleteMapping(value = {"/groups/{group_id}/users"})
    ResponseEntity<?> deleteUserFromGroup(@PathVariable("group_id") Long groupId,
                                          @RequestHeader("Authorization") String token);


    @PostMapping("/groups/{group_id}/users")
    @Operation(summary = "Add a group to the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All user's groups",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GroupsPage.class))})
    })
    ResponseEntity<GroupDto> addGroupToUser(@PathVariable("group_id") Long groupId,
                                            @RequestHeader("Authorization") String rawToken);


    @GetMapping("/groups/{id}/users")
    ResponseEntity<UsersPage> getUsers(@PathVariable("id") Long groupId, @RequestParam("page") int pageNumber);

    @GetMapping("/groups")
    ResponseEntity<GroupsPage> getGroups(@RequestParam("name") String name, @RequestParam("page") int pageNumber);

    @GetMapping("/users/{username}/groups")
    ResponseEntity<GroupsPage> getGroups(@RequestParam("page") int pageNumber, @PathVariable("username") String username);
}
