package service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class TranslateService {
    private S3Service s3Service = new S3Service();
    private GeminiService geminiService = new GeminiService();

    public File createFile(String text){
        try{
            File output = File.createTempFile("output", ".txt");
            Files.writeString(output.toPath(), text, StandardCharsets.UTF_8);
            return output;
        }catch (IOException ex){
            throw new RuntimeException("Erro ao tentar criar arquivo.");
        }
    }

    public String getFileFromBucket(String key){
        String originalText = this.s3Service.get(key);
        return this.geminiService.translate(originalText);
    }

    public void insertFileIntoBucket(String text, String fileName, String username){
        var file = this.createFile(text);
        this.s3Service.upload(file, fileName , username);
    }
}
