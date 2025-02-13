package ti4.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;

@Data
public class AttachmentModel implements ModelInterface {
    private String id;
    private List<String> techSpeciality = new ArrayList<>();
    private List<String> planetTypes = new ArrayList<>();
    private int resourcesModifier;
    private int influenceModifier;
    private String token;
    private Boolean isLegendary;
    private String imagePath;
    private Boolean isFakeAttachment; // is an attachment on backend, but should not be displayed as one

    private int spaceCannonHitsOn;
    private int spaceCannonDieCount;

    @Override
    public boolean isValid() {
        return id != null
            && imagePath != null;
    }

    @Override
    public String getAlias() {
        return getImagePath(); // looks like were using the attachment_<name>.png for identification for now.
    }

    public boolean isFakeAttachment() {
        return Optional.ofNullable(isFakeAttachment).orElse(false);
    }

    public boolean isLegendary() {
        return Optional.ofNullable(isLegendary).orElse(false);
    }

    public Optional<String> getToken() {
        return Optional.ofNullable(token);
    }
}
