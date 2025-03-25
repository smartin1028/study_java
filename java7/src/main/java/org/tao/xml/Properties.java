package org.tao.xml;

import javax.xml.bind.annotation.XmlElement;

public class Properties {

    private String mavenCompilerSource;
    private String mavenCompilerTarget;
    private String projectBuildSourceEncoding;

    @XmlElement(name = "maven.compiler.source")
    public String getMavenCompilerSource() {
        return mavenCompilerSource;
    }

    public void setMavenCompilerSource(String mavenCompilerSource) {
        this.mavenCompilerSource = mavenCompilerSource;
    }

    @XmlElement(name = "maven.compiler.target")
    public String getMavenCompilerTarget() {
        return mavenCompilerTarget;
    }

    public void setMavenCompilerTarget(String mavenCompilerTarget) {
        this.mavenCompilerTarget = mavenCompilerTarget;
    }

    @XmlElement(name = "project.build.sourceEncoding")
    public String getProjectBuildSourceEncoding() {
        return projectBuildSourceEncoding;
    }

    public void setProjectBuildSourceEncoding(String projectBuildSourceEncoding) {
        this.projectBuildSourceEncoding = projectBuildSourceEncoding;
    }
}
