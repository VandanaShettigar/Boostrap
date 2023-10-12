
@RestController
@RequestMapping("/api/urine-strips")
public class UrineStripController {
    @Autowired
    private UrineStripService urineStripService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            UrineStripImage image = urineStripService.saveImage(file);

            // You can add image processing logic here

            return ResponseEntity.ok("Image uploaded and processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Image upload and processing failed.");
        }
    }
}
