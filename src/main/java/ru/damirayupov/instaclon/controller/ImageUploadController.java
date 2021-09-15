package ru.damirayupov.instaclon.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.damirayupov.instaclon.models.ImageModel;
import ru.damirayupov.instaclon.payload.response.MessageResponse;
import ru.damirayupov.instaclon.services.ImageUploadService;

import java.io.IOException;
import java.security.Principal;


@RestController
@RequestMapping("/api/image")
@CrossOrigin
public class ImageUploadController {

    @Autowired
    private ImageUploadService imageUploadService;

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadImageToUser(@RequestParam("file")MultipartFile file,
                                                             Principal principal) throws IOException {
        imageUploadService.uploadImageToUser(file, principal);
        return ResponseEntity.ok(new MessageResponse("Image upload successfully"));
    }

    @PostMapping("/{post_id}/upload")
    public ResponseEntity<MessageResponse> uploadImageToPost(@RequestParam("file") MultipartFile file,
                                                             @PathVariable("post_id") String postId,
                                                             Principal principal) throws IOException {
        imageUploadService.uploadImagePost(file, principal, Long.parseLong(postId));
        return ResponseEntity.ok(new MessageResponse("Image upload successfully"));
    }

    @GetMapping("/profileImage")
    public ResponseEntity<ImageModel> getImageForUser(Principal principal) {
        ImageModel imageModel = imageUploadService.getImageToUser(principal);
        return ResponseEntity.ok(imageModel);
    }

    @GetMapping("/{post_id}/image")
    public ResponseEntity<ImageModel> getImageForPost(@PathVariable("post_id") String postId) {
        ImageModel imageModel = imageUploadService.getImageToPost(Long.parseLong(postId));
        return ResponseEntity.ok(imageModel);
    }
}
