package me.atomishere.RPIBotSever;

import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.*;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.hopding.jrpicam.RPiCamera;
import marytts.LocalMaryInterface;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.AudioPlayer;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by archieoconnor on 18/12/17.
 */
public class PictureManager {
    private final RPiCamera camera;
    private final LocalMaryInterface maryInterface;

    public PictureManager(RPiCamera camera, LocalMaryInterface maryInterface) {
        this.camera = camera;
        this.maryInterface = maryInterface;
    }

    public void takePictureAndIdentify(Vision vision, int maxResults) throws Exception {
        try {
            camera.takeStill("image.jpg");
        } catch(InterruptedException ex) {
            System.out.println("Could not take picture. Camera is being used!");
            return;
        }

        Path path = Paths.get("image.jpg");
        byte[] data = Files.readAllBytes(path);

        AnnotateImageRequest request =
                new AnnotateImageRequest()
                .setImage(new Image().encodeContent(data))
                .setFeatures(ImmutableList.of(
                        new Feature()
                        .setType("LABEL_DETECTION")
                        .setMaxResults(maxResults)));

        Vision.Images.Annotate annotate =
                vision.images()
                .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));

        annotate.setDisableGZipContent(true);

        BatchAnnotateImagesResponse batchResponse = annotate.execute();
        assert batchResponse.getResponses().size() == 1;
        AnnotateImageResponse response = batchResponse.getResponses().get(0);
        if(response.getLabelAnnotations() == null) {
            throw new IOException(
                    response.getError() != null
                    ? response.getError().getMessage()
                    : "Unknown error getting image annotation");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Labels: \n");

        for(EntityAnnotation annotation : response.getLabelAnnotations()) {
            builder.append(annotation.getDescription())
                    .append("\n         Score:").append(annotation.getScore())
                    .append("\n");

            AudioPlayer ap = new AudioPlayer();
            AudioInputStream audioInputStream = maryInterface.generateAudio(annotation.getDescription());
            ap.setAudio(audioInputStream);
            ap.start();
        }

        System.out.println(builder.toString());
    }

    public void takePictureAndIdentifyText(Vision vision, int maxResults) {
        try {
            camera.takeStill("image.jpg");
            Path path = Paths.get("image.jpg");
            byte[] data;
            data = Files.readAllBytes(path);
            AnnotateImageRequest request = new AnnotateImageRequest()
                    .setImage(new Image().encodeContent(data))
                    .setFeatures(ImmutableList.of(
                            new Feature()
                            .setType("TEXT_DETECTION")
                            .setMaxResults(maxResults)));

            Vision.Images.Annotate annotate =
                    vision.images()
                    .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));

            annotate.setDisableGZipContent(true);
            BatchAnnotateImagesResponse batchResponse = annotate.execute();
            assert batchResponse.getResponses().size() == 1;

            AnnotateImageResponse response = batchResponse.getResponses().get(0);
            ImageText imageText = ImageText.builder()
                    .path(path)
                    .textAnnotations(
                            MoreObjects.firstNonNull(
                                    response.getTextAnnotations(),
                                    ImmutableList.<EntityAnnotation>of()))
                    .error(response.getError())
                    .build();

            StringBuilder builder = new StringBuilder();
            builder.append("Labels: \n");

            for(EntityAnnotation annotation : imageText.textAnnotation()) {
                builder.append("    ").append(annotation.getDescription())
                        .append("\n         Score:").append(annotation.getScore());

                AudioPlayer ap = new AudioPlayer();
                AudioInputStream audioInputStream = maryInterface.generateAudio(annotation.getDescription());
                ap.setAudio(audioInputStream);
                ap.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SynthesisException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
