package org.tao.xml;
// Java 7 compatible XML mapping classes
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class Project {
    @XmlAttribute
    private String xmlns;
    
    @XmlAttribute(name = "xmlns:xsi")
    private String xmlnsXsi;
    
    @XmlAttribute(name = "xsi:schemaLocation")
    private String schemaLocation;
    
    private String modelVersion;
    private String groupId;
    private String artifactId;
    private String version;
    
    @XmlElementWrapper(name = "dependencies")
    @XmlElement(name = "dependency")
    private List<Dependency> dependencies;
    
    private Properties properties;

    // Standard getters and setters required for Java 7
    public String getXmlns() { return xmlns; }
    public void setXmlns(String xmlns) { this.xmlns = xmlns; }
    // ... other getters and setters

    public String getXmlnsXsi() {
        return xmlnsXsi;
    }

    public void setXmlnsXsi(String xmlnsXsi) {
        this.xmlnsXsi = xmlnsXsi;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}

