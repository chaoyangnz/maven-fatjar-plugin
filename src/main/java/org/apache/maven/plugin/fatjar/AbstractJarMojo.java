/**
 * 
 */
package org.apache.maven.plugin.fatjar;

/**
 * @author Ñî³¬  <ychao@bankcomm.com>
 *
 * 2010-1-23
 */
import java.io.File;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;

/**
 * Base class for creating a jar from project classes.
 */
public abstract class AbstractJarMojo extends AbstractMojo {

	private static final String[] DEFAULT_EXCLUDES = new String[] { "**/package.html" };

	private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };

	/**
	 * List of files to include. Specified as fileset patterns.
	 * 
	 * @parameter
	 */
	protected String[] includes;

	/**
	 * List of files to exclude. Specified as fileset patterns.
	 * 
	 * @parameter
	 */
	protected String[] excludes;

	/**
	 * Directory containing the generated JAR.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	protected File outputDirectory;

	/**
	 * Name of the generated JAR.
	 * 
	 * @parameter alias="jarName" expression="${jar.finalName}"
	 *            default-value="${project.build.finalName}"
	 * @required
	 */
	protected String finalName;

	/**
	 * The Jar archiver.
	 * 
	 * @parameter 
	 *            expression="${component.org.codehaus.plexus.archiver.Archiver#jar}"
	 * @required
	 */
	protected JarArchiver jarArchiver;

	/**
	 * The Maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * The archive configuration to use.
	 * 
	 * See <a
	 * href="http://maven.apache.org/shared/maven-archiver/index.html">the
	 * documentation for Maven Archiver</a>.
	 * 
	 * @parameter
	 */
	protected MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

	/**
	 * Path to the default MANIFEST file to use. It will be used if
	 * <code>useDefaultManifestFile</code> is set to <code>true</code>.
	 * 
	 * @parameter 
	 *            expression="${project.build.outputDirectory}/META-INF/MANIFEST.MF"
	 * @required
	 * @readonly
	 * @since 2.2
	 */
	protected File defaultManifestFile;

	/**
	 * Set this to <code>true</code> to enable the use of the
	 * <code>defaultManifestFile</code>.
	 * 
	 * @parameter expression="${jar.useDefaultManifestFile}"
	 *            default-value="false"
	 * 
	 * @since 2.2
	 */
	protected boolean useDefaultManifestFile;

	/**
	 * @component
	 */
	protected MavenProjectHelper projectHelper;

	/**
	 * Whether creating the archive should be forced.
	 * 
	 * @parameter expression="${jar.forceCreation}" default-value="false"
	 */
	protected boolean forceCreation;

	/**
	 * Return the specific output directory to serve as the root for the
	 * archive.
	 */
	protected abstract File getClassesDirectory();

	protected final MavenProject getProject() {
		return project;
	}

	/**
	 * Overload this to produce a jar with another classifier, for example a
	 * test-jar.
	 */
	protected abstract String getClassifier();

	/**
	 * Overload this to produce a test-jar, for example.
	 */
	protected abstract String getType();

	protected static File getJarFile(File basedir, String finalName,
			String classifier) {
		if (classifier == null) {
			classifier = "";
		} else if (classifier.trim().length() > 0
				&& !classifier.startsWith("-")) {
			classifier = "-" + classifier;
		}

		return new File(basedir, finalName + classifier + ".jar");
	}

	/**
	 * Default Manifest location. Can point to a non existing file. Cannot
	 * return null.
	 */
	protected File getDefaultManifestFile() {
		return defaultManifestFile;
	}

	/**
	 * Generates the JAR.
	 * 
	 * @todo Add license files in META-INF directory.
	 */
	public File createArchive() throws MojoExecutionException {
		File jarFile = getJarFile(outputDirectory, finalName, getClassifier());

		MavenArchiver archiver = new MavenArchiver();

		archiver.setArchiver(jarArchiver);

		archiver.setOutputFile(jarFile);

		// archive.setForced( forceCreation );

		try {
			File contentDirectory = getClassesDirectory();
			if (!contentDirectory.exists()) {
				getLog()
						.warn(
								"JAR will be empty - no content was marked for inclusion!");
			} else {
				archiver.getArchiver().addDirectory(contentDirectory,
						getIncludes(), getExcludes());
			}

			File existingManifest = getDefaultManifestFile();

			if (useDefaultManifestFile && existingManifest.exists()
					&& archive.getManifestFile() == null) {
				getLog().info(
						"Adding existing MANIFEST to archive. Found under: "
								+ existingManifest.getPath());
				archive.setManifestFile(existingManifest);
			}

			archiver.createArchive(project, archive);

			return jarFile;
		} catch (Exception e) {
			// TODO: improve error handling
			throw new MojoExecutionException("Error assembling JAR", e);
		}
	}

	/**
	 * Generates the JAR.
	 * 
	 * @todo Add license files in META-INF directory.
	 */
	public void execute() throws MojoExecutionException {
		File jarFile = createArchive();

		String classifier = getClassifier();
		if (classifier != null) {
			projectHelper.attachArtifact(getProject(), getType(), classifier,
					jarFile);
		} else {
			getProject().getArtifact().setFile(jarFile);
		}
	}

	private String[] getIncludes() {
		if (includes != null && includes.length > 0) {
			return includes;
		}
		return DEFAULT_INCLUDES;
	}

	private String[] getExcludes() {
		if (excludes != null && excludes.length > 0) {
			return excludes;
		}
		return DEFAULT_EXCLUDES;
	}
}
