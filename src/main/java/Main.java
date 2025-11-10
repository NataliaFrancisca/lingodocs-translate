import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import service.TranslateService;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

public class Main implements RequestHandler<S3Event, String> {
    @Override
    public String handleRequest(S3Event event, Context context) {
        TranslateService service = new TranslateService();

        String key = event.getRecords().get(0).getS3().getObject().getKey();
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();

        S3Client s3 = S3Client.builder().build();

        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        HeadObjectResponse headResponse = s3.headObject(headRequest);
        String username = headResponse.metadata().get("username");

        try{
            var fileName = key.substring(key.lastIndexOf("/") + 1);

            var text = service.getFileFromBucket(key);
            service.insertFileIntoBucket(text, fileName, username);
        }catch (Exception e){
            context.getLogger().log(e.getMessage());
            return "Erro ao processar a requisição";
        }

        return key;
    }
}