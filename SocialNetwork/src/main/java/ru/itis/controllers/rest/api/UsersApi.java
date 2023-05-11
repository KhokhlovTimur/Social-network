package ru.itis.controllers.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.group.GroupsPage;
import ru.itis.dto.other.ExceptionDto;
import ru.itis.dto.user.*;

import java.util.List;

@RequestMapping("/api/users")
public interface UsersApi  {

    @PostMapping
    @Operation(summary = "User registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registered user",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PrivateUserDto.class))
                    }),
            @ApiResponse(responseCode = "422", description = "Error information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class))
                    })
    })
    ResponseEntity<PrivateUserDto> signUp(@RequestBody UserSignUpDto userSignUpDto);


    @GetMapping("/{id}")
    @Operation(summary = "Get user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's info",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PublicUserDto.class)),
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PrivateUserDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Error's information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class))
                    })
    })
    ResponseEntity<? extends PublicUserDto> get(@PathVariable("id") Long id, @RequestHeader("Authorization") String rawToken);


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "Error's information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class))
                    })
    })
    ResponseEntity<?> delete(@PathVariable("id") Long id, @RequestHeader(name = "Authorization") String rawToken);


    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Updated user",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PrivateUserDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Error's information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class))
                    })

    })
    ResponseEntity<PrivateUserDto> update(@PathVariable("id") Long id,
                                   @RequestBody UserUpdateDto updateUserDto,
                                   @RequestHeader(name = "Authorization") String rawToken);

    @GetMapping("/{id}/groups")
    ResponseEntity<GroupsPage> getGroups(@PathVariable("id") Long id);

//    @GetMapping("/{id}/friends")
//    ResponseEntity<List<PublicUserDto>> getFriends(@PathVariable("id") Long id);

}
