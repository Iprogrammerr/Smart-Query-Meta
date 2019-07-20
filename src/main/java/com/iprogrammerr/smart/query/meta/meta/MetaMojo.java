package com.iprogrammerr.smart.query.meta.meta;

import com.iprogrammerr.smart.query.meta.App;
import com.iprogrammerr.smart.query.meta.Configuration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "generate")
public class MetaMojo extends AbstractMojo {

    @Parameter
    private String jdbcUrl;
    @Parameter
    private String databaseUser;
    @Parameter
    private String databasePassword;
    @Parameter
    private String classesPackage;
    @Parameter
    private String classesPath;

    @Override
    public void execute() throws MojoFailureException {
        try {
            new App().execute(new Configuration(jdbcUrl, databaseUser, databasePassword, classesPackage, classesPath));
        } catch (Exception e) {
            throw new MojoFailureException("Failure while generating db representation", e);
        }
    }
}
