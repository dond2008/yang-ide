/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ui.wizards;

import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Repository;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.m2e.core.ui.internal.wizards.MavenProjectWizard;
import org.eclipse.ui.IWorkbench;

import com.cisco.yangide.ui.YangUIPlugin;

/**
 * @author Konstantin Zaitsev
 * @date Jun 26, 2014
 */
@SuppressWarnings("restriction")
public class YangProjectWizard extends MavenProjectWizard {

    /** Default source location. */
    public static final String SRC_MAIN_YANG = "src/main/yang";

    /** YANG tools configuration page. */
    private YangProjectWizardPage yangPage;

    /**
     * Constructor.
     */
    public YangProjectWizard() {
        super();
        setWindowTitle("New YANG Project");
    }

    @Override
    public void addPages() {
        yangPage = new YangProjectWizardPage();
        addPage(yangPage);
        super.addPages();
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        super.init(workbench, selection);
    }

    @Override
    public boolean performFinish() {
        boolean res = super.performFinish();
        if (res) {
            IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getModel().getArtifactId());
            IFolder folder = project.getFolder(yangPage.getRootDir());
            if (!folder.exists()) {
                try {
                    folder.create(true, true, null);
                    if (yangPage.createExampleFile()) {
                        folder.getFile("acme-system.yang").create(
                                FileLocator.openStream(YangUIPlugin.getDefault().getBundle(), new Path(
                                        "resources/yang/acme-system.yang"), false), true, null);
                    }
                } catch (CoreException e) {
                    YangUIPlugin.log(e.getMessage(), e);
                    return false;
                } catch (IOException e) {
                    YangUIPlugin.log(e.getMessage(), e);
                    return false;
                }
            }
        }
        return res;
    }

    @Override
    public Model getModel() {
        Model model = super.getModel();
        model.setBuild(new Build());
        Plugin plugin = new Plugin();
        plugin.setGroupId("org.opendaylight.yangtools");
        plugin.setArtifactId("yang-maven-plugin");
        plugin.setVersion(yangPage.getYangVersion());

        // add generators
        List<CodeGeneratorConfig> generators = yangPage.getCodeGenerators();

        for (CodeGeneratorConfig genConf : generators) {
            Dependency dependency = new Dependency();
            dependency.setGroupId(genConf.getGroupId());
            dependency.setArtifactId(genConf.getArtifactId());
            dependency.setVersion(genConf.getVersion());
            dependency.setType("jar");
            plugin.addDependency(dependency);
        }

        PluginExecution pluginExecution = new PluginExecution();
        pluginExecution.setId("generate-sources");
        pluginExecution.addGoal("generate-sources");
        Xpp3Dom config = new Xpp3Dom("configuration");

        Xpp3Dom codeGenerators = new Xpp3Dom("codeGenerators");
        for (CodeGeneratorConfig genConf : generators) {
            Xpp3Dom generator = new Xpp3Dom("generator");
            generator.addChild(createSingleParameter("codeGeneratorClass", genConf.getGenClassName()));
            generator.addChild(createSingleParameter("outputBaseDir", genConf.getGenOutputDirectory()));
            codeGenerators.addChild(generator);
        }
        config.addChild(createSingleParameter("yangFilesRootDir", yangPage.getRootDir()));
        config.addChild(codeGenerators);
        config.addChild(createSingleParameter("inspectDependencies", "false"));
        pluginExecution.setConfiguration(config);

        plugin.addExecution(pluginExecution);
        model.getBuild().addPlugin(plugin);
        model.addPluginRepository(createRepoParameter("opendaylight-release",
                "http://nexus.opendaylight.org/content/repositories/opendaylight.release/"));
        model.addPluginRepository(createRepoParameter("opendaylight-snapshot",
                "http://nexus.opendaylight.org/content/repositories/opendaylight.snapshot/"));
        model.addRepository(createRepoParameter("opendaylight-release",
                "http://nexus.opendaylight.org/content/repositories/opendaylight.release/"));
        model.addRepository(createRepoParameter("opendaylight-snapshot",
                "http://nexus.opendaylight.org/content/repositories/opendaylight.snapshot/"));

        model.getProperties().put("maven.compiler.source", "1.7");
        model.getProperties().put("maven.compiler.target", "1.7");

        Dependency dependency2 = new Dependency();
        dependency2.setGroupId("org.opendaylight.yangtools");
        dependency2.setArtifactId("yang-binding");
        dependency2.setVersion(yangPage.getYangVersion());
        dependency2.setType("jar");
        model.addDependency(dependency2);
        return model;
    }

    /**
     * Creates single configuration parameter.
     * 
     * @param name name
     * @param value value
     * @return config parameter
     */
    private Xpp3Dom createSingleParameter(String name, String value) {
        Xpp3Dom parameter = new Xpp3Dom(name);
        parameter.setValue(value);
        return parameter;
    }

    /**
     * @param name name
     * @param url url
     * @return repository configuration by name and url
     */
    private Repository createRepoParameter(String name, String url) {
        Repository r = new Repository();
        r.setId(name);
        r.setName(name);
        r.setUrl(url);
        return r;
    }
}
