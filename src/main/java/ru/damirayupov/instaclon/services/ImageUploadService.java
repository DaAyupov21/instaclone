package ru.damirayupov.instaclon.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.damirayupov.instaclon.exceptions.ImageNotFoundException;
import ru.damirayupov.instaclon.models.ImageModel;
import ru.damirayupov.instaclon.models.Post;
import ru.damirayupov.instaclon.models.User;
import ru.damirayupov.instaclon.repositories.ImageRepository;
import ru.damirayupov.instaclon.repositories.PostRepository;
import ru.damirayupov.instaclon.repositories.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageUploadService {

    private static final Logger log = LoggerFactory.getLogger(ImageUploadService.class);

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    public ImageModel uploadImageToUser(MultipartFile file, Principal principal) throws IOException {
        User user = getUserByPrincipal(principal);
        log.info("Uploading profile to user " + user.getUsername());
        ImageModel userProfileImage = imageRepository.findByUserId(user.getId())
                .orElse(null);
        if (!ObjectUtils.isEmpty(userProfileImage)){
            imageRepository.delete(userProfileImage);
        }
        ImageModel imageModel = ImageModel.builder()
                .userId(user.getId())
                .imageBytes(compressBytes(file.getBytes()))
                .name(file.getOriginalFilename())
                .build();

        return imageRepository.save(imageModel);
    }

    public ImageModel uploadImagePost(MultipartFile file, Principal principal, Long postId) throws IOException {
        User user = getUserByPrincipal(principal);
        Post post = user.getPosts().stream().filter(p -> p.getId().equals(postId)).collect(toSinglePostCollector());

        ImageModel imageModel = ImageModel.builder()
                .postId(post.getId())
                .imageBytes(compressBytes(file.getBytes()))
                .name(file.getOriginalFilename())
                .build();

        log.info("Uploading image to Post {}", postId);
        return imageRepository.save(imageModel);
    }

    public ImageModel getImageToUser(Principal principal){
        User user = getUserByPrincipal(principal);
        ImageModel imageModel = imageRepository.findByUserId(user.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    public ImageModel getImageToPost(Long postId){
        ImageModel imageModel = imageRepository.findByPostId(postId).orElseThrow(
                () -> new ImageNotFoundException("Cannot find image to post:" + postId));
        if (!ObjectUtils.isEmpty(imageModel)){
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }


    private User getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username not found " + username));
    }

    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            log.error("Cannot compress Bytes");
        }
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    private static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException e) {
            log.error("Cannot decompress Bytes");
        }
        return outputStream.toByteArray();
    }

    private <T> Collector<T, ?, T> toSinglePostCollector() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }
}
