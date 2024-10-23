package mo.core.preferences;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("project")
public class AppProjectPreferencesWrapper {

    private String location;

    public AppProjectPreferencesWrapper(String location) {
        this.location = location;
    }

    public void setLocation(String l) {
        this.location = l;
    }

    public String getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return location;
    }

}
