package com.luke.maven.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

/**
 * Goal which touches a timestamp file.
 *
 * @goal discover
 *
 * @phase process-sources
 */
public class Dependencies extends AbstractMojo
{
    /**
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    protected ArtifactRepository local;
    /**
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    protected List<ArtifactRepository> remoteRepos;
    
    /**
     * @parameter default-value="${project}" @required @readonly
     */
    private MavenProject project;
    
    /**
     * @component role="org.apache.maven.project.MavenProjectBuilder"
     * @required
     * @readonly
     */
    protected MavenProjectBuilder mavenProjectBuilder;
    /**
     * @component
     */
    protected ArtifactFactory factory;
    
    private int count;

    public void execute() throws MojoExecutionException
    {     
        try
        {
            count = 0;
            this.resolveDependencies(project);
        } 
        catch(ProjectBuildingException ex)
        {
            Logger.getLogger(Dependencies.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void resolveDependencies(MavenProject p) throws ProjectBuildingException
    {
        count++;
        Set<Artifact> artifacts = p.createArtifacts(factory, Artifact.LATEST_VERSION, null);
        for(Artifact a: artifacts)
        {
            System.out.println(tabGen(count) + "+- " + a.getGroupId() + ":" + a.getArtifactId() + ":" + a.getVersion());
            MavenProject pomProject = mavenProjectBuilder.buildFromRepository(a, remoteRepos, local);
            resolveDependencies(pomProject);
        }
        count--;
    }
    
    private String tabGen(int count)
    {
        StringBuilder s = new StringBuilder();
        
        for(int i=count-1; i>0;i--)
            s.append("|  ");
        
        return s.toString();
    }
}
