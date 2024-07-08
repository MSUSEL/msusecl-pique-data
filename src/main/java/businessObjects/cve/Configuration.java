package businessObjects.cve;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public final class Configuration {
    private ArrayList<Node> nodes;
}