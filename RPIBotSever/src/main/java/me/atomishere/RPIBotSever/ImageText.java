package me.atomishere.RPIBotSever;

import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Status;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by archieoconnor on 18/12/17.
 */
public class ImageText {
    private Path pth;
    private List<EntityAnnotation> ts;
    private Status err;

    public static Builder builder() {
        return new Builder();
    }

    private ImageText() {}

    public Path path() {
        return this.pth;
    }

    public List<EntityAnnotation> textAnnotation() {
        return this.ts;
    }

    @Nullable
    public Status error() {
        return this.err;
    }

    public static class Builder {
        private Path pth;
        private List<EntityAnnotation> ts;
        private Status err;

        Builder() {}

        public Builder path(Path path) {
            this.pth = path;
            return this;
        }

        public Builder textAnnotations(List<EntityAnnotation> ts) {
            this.ts = ts;
            return this;
        }

        public Builder error(@Nullable Status err) {
            this.err = err;
            return this;
        }

        public ImageText build() {
            ImageText out = new ImageText();
            out.pth = this.pth;
            out.ts = this.ts;
            out.err = this.err;
            return out;
        }
    }
}
