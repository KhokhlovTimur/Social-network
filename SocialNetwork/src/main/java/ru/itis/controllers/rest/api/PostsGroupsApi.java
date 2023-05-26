package ru.itis.controllers.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.dto.other.ExceptionDto;
import ru.itis.dto.other.LikesPage;
import ru.itis.dto.posts.NewOrUpdateGroupPostDto;
import ru.itis.dto.posts.PostDto;
import ru.itis.dto.posts.PostsPage;

@RequestMapping("/api/groups")
public interface PostsGroupsApi {

    @PostMapping("/{id}/posts")
    @Operation(summary = "Adding post to group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added post",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = NewOrUpdateGroupPostDto.class))
                    }),
            @ApiResponse(responseCode = "422", description = "Error information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class))
                    })
    })
    ResponseEntity<PostDto> addPost(@PathVariable("id") Long id, @RequestParam(value = "files", required = false) MultipartFile[] files,
                                    @RequestParam("text") String text,
                                    @RequestHeader(name = "Authorization") String rawToken);


    @GetMapping("/{group_id}/posts")
    @Operation(summary = "Get group's posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All group's posts",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PostsPage.class))})
    })
    ResponseEntity<PostsPage> getPosts(@PathVariable("group_id") Long id, @RequestParam("page") int pageNumber,
                                       @RequestHeader(name = "Authorization") String rawToken);


    @GetMapping("/{group_id}/posts/{post_id}")
    @Operation(summary = "Get post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post's info",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PostDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Error's information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class))
                    })
    })
    ResponseEntity<PostDto> getPost(@PathVariable("group_id") Long groupId, @PathVariable("post_id") Long postId);


    @Operation(summary = "Update post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Updated post",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PostDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Error's information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class))
                    })

    })
    @PutMapping("/{group_id}/posts/{post_id}")
    ResponseEntity<PostDto> updatePost(@PathVariable("group_id") Long groupId, @PathVariable("post_id") Long postId,
                                       @RequestBody NewOrUpdateGroupPostDto postDto);


    @Operation(summary = "Get post's likes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All post's likes",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LikesPage.class))})
    })
    @GetMapping("/{group_id}/posts/{post_id}/likes")
    ResponseEntity<LikesPage> getLikes(@PathVariable("group_id") Long groupId,
                                       @PathVariable("post_id") Long postId);


    @Operation(summary = "Put a like")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"
            ),
            @ApiResponse(responseCode = "422", description = "Error information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class))
                    })
    })
    @PostMapping("/{group_id}/posts/{post_id}/likes")
    ResponseEntity<?> putLike(@PathVariable("group_id") Long groupId,
                              @PathVariable("post_id") Long postId,
                              @RequestHeader(name = "Authorization") String rawToken);


    @DeleteMapping("/{group_id}/posts/{post_id}/likes")
    ResponseEntity<?> removeLike(@PathVariable("group_id") Long groupId,
                                 @PathVariable("post_id") Long postId,
                                 @RequestHeader(name = "Authorization") String rawToken);


    @GetMapping("/{group_id}/posts/{post_id}/likes/count")
    ResponseEntity<Long> getLikesCount(@PathVariable("group_id") Long groupId,
                                       @PathVariable("post_id") Long postId);

    @GetMapping("/{group_id}/posts/{post_id}/likes/{username}")
    ResponseEntity<Boolean> isUserPutLikeToPost(@PathVariable("group_id") Long groupId,
                                                @PathVariable("post_id") Long postId,
                                                @PathVariable("username") String username);

    @DeleteMapping("/{group_id}/posts/{post_id}")
    ResponseEntity<?> delete(@PathVariable("group_id") Long groupId,
                             @PathVariable("post_id") Long postId);
}

