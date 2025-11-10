package service;

import com.google.common.collect.ImmutableList;
import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.types.*;
import com.google.gson.Gson;
import dto.TranslateResponse;

import java.util.List;

public class GeminiService {
    private Client client;

    public GeminiService(){
        var geminiApiKey = System.getenv("GEMINI_API_KEY");
        this.client = Client.builder().apiKey(geminiApiKey).build();
    }

    private GenerateContentConfig config(){
        var action = """
            Translate the user's text into Portuguese - Brazil.
            If the text is already in Portuguese, you should translate to English.
            The input text can be in any language. Return only the translated text, with no extra commentary or explanations.
        """;

        try{
            return GenerateContentConfig
                    .builder()
                    .topP(1f)
                    .responseMimeType("application/json")
                    .responseSchema(Schema.fromJson("""
                            {
                              "type": "object",
                              "properties": {
                                "response": {
                                  "type": "string"
                                }
                              },
                              "required": [
                                "response"
                              ],
                              "propertyOrdering": [
                                "response"
                              ]
                           }
                           """))
                    .systemInstruction(Content.fromParts(Part.fromText(action))).build();
        }catch (Exception exception){
            System.out.println("Erro ao tentar configurar a API do Gemini.");
            throw new RuntimeException(exception.getMessage());
        }
    }

    public String translate(String textToTranslate){
        var MODEL_NAME = "gemini-2.0-flash";

        List<Content> contents = ImmutableList.of(
                Content.builder()
                        .role("user")
                        .parts(ImmutableList.of(
                                Part.fromText(textToTranslate)))
                        .build()
        );

        ResponseStream<GenerateContentResponse> responseStream = this.client.models
                .generateContentStream(MODEL_NAME, contents, this.config());

        StringBuilder responseString = new StringBuilder();

        for (GenerateContentResponse res : responseStream) {
            if (res.candidates().isEmpty() || res.candidates().get().get(0).content().isEmpty() || res.candidates().get().get(0).content().get().parts().isEmpty()) {
                continue;
            }

            List<Part> parts = res.candidates().get().get(0).content().get().parts().get();
            for (Part part : parts) {
                part.text().ifPresent(responseString::append);
            }
        }

        responseStream.close();
        TranslateResponse translateResponse = new Gson()
                .fromJson(responseString.toString(), TranslateResponse.class);
        return translateResponse.response();
    }

}
