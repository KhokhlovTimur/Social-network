package ru.itis.controllers.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.other.ExceptionDto;
import ru.itis.dto.group.GroupDto;
import ru.itis.dto.group.NewOrUpdateGroupDto;

@RequestMapping("/api/groups")
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
    ResponseEntity<GroupDto> add(@ModelAttribute NewOrUpdateGroupDto groupDto,
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


    @PatchMapping("/{id}")
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
    ResponseEntity<GroupDto> update(@ModelAttribute NewOrUpdateGroupDto groupDto,
                                    @PathVariable("id") Long id,
                                    @RequestHeader("Authorization") String rawToken);

    @GetMapping("/{id}/users/{username}")
    ResponseEntity<Boolean> isUserExists(@PathVariable("id") Long id, @PathVariable("username") String username);
}
