package ru.itis.controllers.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.other.ExceptionDto;
import ru.itis.dto.group.GroupDto;
import ru.itis.dto.group.UsersPage;
import ru.itis.dto.group.NewOrUpdateGroupDto;

@RequestMapping("/groups")
public interface GroupsApi {
    @GetMapping("/{id}")
    @Operation(summary = "Get group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group's info",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GroupDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Error's information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class))
                    })
    })
    ResponseEntity<GroupDto> get(@PathVariable("id") Long id);


    @PostMapping
    @Operation(summary = "Adding group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added group",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = NewOrUpdateGroupDto.class))
                    }),
            @ApiResponse(responseCode = "422", description = "Error information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class))
                    })
    })
    ResponseEntity<GroupDto> add(@RequestBody NewOrUpdateGroupDto newGroupDto,
                                 @RequestHeader("Authorization") String rawToken);


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Group deleted"),
            @ApiResponse(responseCode = "404", description = "Error's information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class))
                    })
    })
    ResponseEntity<?> delete(@PathVariable("id") Long id);


    @PutMapping("/{id}")
    @Operation(summary = "Update group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Updated group",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GroupDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Error's information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class))
                    })

    })
    ResponseEntity<GroupDto> update(@PathVariable("id") Long id, @RequestBody NewOrUpdateGroupDto groupDto);


    @GetMapping("/{id}/users")
    @Operation(summary = "Get users from group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All group's users",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UsersPage.class))})
    })
    ResponseEntity<UsersPage> getUsers(@PathVariable("id") Long id);

}
