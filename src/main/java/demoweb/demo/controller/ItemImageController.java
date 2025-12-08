package demoweb.demo.controller;

import demoweb.demo.entity.ItemImage;
import demoweb.demo.service.ItemImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
public class ItemImageController {

    private final ItemImageService itemImageService;

    @Autowired
    public ItemImageController(ItemImageService itemImageService) {
        this.itemImageService = itemImageService;
    }

    @GetMapping("/api/item-images/by-item/{itemId}")
    public ResponseEntity<List<ItemImage>> getImagesByItem(@PathVariable Integer itemId) {
        return ResponseEntity.ok(itemImageService.getImagesByItemId(itemId));
    }

    @GetMapping("/api/item-images/by-item/{itemId}/primary")
    public ResponseEntity<ItemImage> getPrimaryImage(@PathVariable Integer itemId) {
        return ResponseEntity.ok(itemImageService.getPrimaryImage(itemId));
    }

    @GetMapping("/api/item-images/blob/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer imageId) {
        ItemImage itemImage = itemImageService.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        MediaType mediaType = MediaType.IMAGE_JPEG;
        if (itemImage.getAlt() != null) {
            String lower = itemImage.getAlt().toLowerCase();
            if (lower.endsWith(".png")) mediaType = MediaType.IMAGE_PNG;
            else if (lower.endsWith(".gif")) mediaType = MediaType.IMAGE_GIF;
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=image_" + imageId)
                .contentType(mediaType)
                .body(itemImage.getImageBlob());
    }

    @PostMapping(value = "/api/item-images/upload/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemImage> uploadImage(
            @PathVariable Integer itemId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", required = false) Boolean isPrimary,
            @RequestParam(value = "alt", required = false) String alt
    ) throws IOException {
        ItemImage itemImage = new ItemImage();
        itemImage.setImageBlob(file.getBytes());
        itemImage.setIsPrimary(isPrimary != null ? isPrimary : false);
        itemImage.setAlt(alt != null ? alt : file.getOriginalFilename());
        if (Boolean.TRUE.equals(itemImage.getIsPrimary())) {
            List<ItemImage> oldImages = itemImageService.getImagesByItemId(itemId);
            oldImages.forEach(img -> {
                if (Boolean.TRUE.equals(img.getIsPrimary())) {
                    img.setIsPrimary(false);
                    itemImageService.saveForItem(itemId, img);
                }
            });
        }
        ItemImage savedImage = itemImageService.saveForItem(itemId, itemImage);
        return ResponseEntity.ok(savedImage);
    }

    @GetMapping("/item/image/{itemId}")
    public ResponseEntity<byte[]> getItemImageForDisplay(@PathVariable Integer itemId) {
        ItemImage itemImage = itemImageService.getPrimaryImage(itemId);
        if (itemImage == null || itemImage.getImageBlob() == null) {
            return ResponseEntity.notFound().build();
        }
        MediaType mediaType = MediaType.IMAGE_JPEG;
        if (itemImage.getAlt() != null) {
            String lower = itemImage.getAlt().toLowerCase();
            if (lower.endsWith(".png")) mediaType = MediaType.IMAGE_PNG;
            else if (lower.endsWith(".gif")) mediaType = MediaType.IMAGE_GIF;
            else if (lower.endsWith(".webp")) mediaType = MediaType.valueOf("image/webp");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setCacheControl("max-age=3600");
        return new ResponseEntity<>(itemImage.getImageBlob(), headers, HttpStatus.OK);
    }
}