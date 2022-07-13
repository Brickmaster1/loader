package com.basedloader.loader.impl.metadata;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModEnvironment;
import net.fabricmc.loader.api.metadata.Person;
import net.fabricmc.loader.impl.metadata.EntrypointMetadata;
import net.fabricmc.loader.impl.metadata.LoaderModMetadata;
import net.fabricmc.loader.impl.metadata.NestedJarEntry;
import net.fabricmc.loader.impl.metadata.SimplePerson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ForgeModMetadata implements LoaderModMetadata {

	private String modid;
	private Version version;
	private String displayName;
	private String description;
	private Person authors;

	/**
	 * For now, lets use a dodgy system to find the name and version of the mod. TODO: use a toml parser
	 */
	public ForgeModMetadata(InputStream is) {
		try {
			String modsToml = new String(is.readAllBytes());
			String[] modInfoLines = modsToml.split("\\[\\[.+]]")[1].split("\n");
			for (String infoLine : modInfoLines) {
				if (infoLine.contains("=")) {
					String[] property = infoLine.split("=");
					String key = property[0].replace("\t", "").replace("    ", "");
					String value = property[1].replace("\"", "");
					switch (key) {
						case "modId" -> this.modid = value;
						case "authors" -> this.authors = new SimplePerson(value);
						case "displayName" -> this.displayName = value;
						case "description" -> this.description = value;
						case "version" -> {
							try {
								this.version = Version.parse(value);
							} catch (VersionParsingException e) {
								throw new RuntimeException("Failed to read forge mod version", e);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read \"mods.toml\"", e);
		}
	}

	@Override
	public String getType() {
		return "forge";
	}

	@Override
	public String getId() {
		return this.modid;
	}

	@Override
	public Collection<String> getProvides() {
		return Collections.emptyList();
	}

	@Override
	public Version getVersion() {
		return this.version;
	}

	@Override
	public ModEnvironment getEnvironment() {
		return ModEnvironment.UNIVERSAL;
	}

	@Override
	public Collection<ModDependency> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		return this.displayName;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public Collection<Person> getAuthors() {
		return List.of(this.authors);
	}

	@Override
	public Collection<Person> getContributors() {
		return Collections.emptyList();
	}

	@Override
	public ContactInformation getContact() {
		return ContactInformation.EMPTY;
	}

	@Override
	public Collection<String> getLicense() {
		return Collections.emptyList();
	}

	@Override
	public Optional<String> getIconPath(int size) {
		return Optional.empty();
	}

	@Override
	public boolean containsCustomValue(String key) {
		return false;
	}

	@Override
	public CustomValue getCustomValue(String key) {
		return null;
	}

	@Override
	public Map<String, CustomValue> getCustomValues() {
		return Map.of();
	}

	@Override
	public boolean containsCustomElement(String key) {
		return false;
	}

	@Override
	public int getSchemaVersion() {
		return 1;
	}

	@Override
	public Map<String, String> getLanguageAdapterDefinitions() {
		return Map.of();
	}

	@Override
	public Collection<NestedJarEntry> getJars() {
		return Collections.emptyList();
	}

	@Override
	public Collection<String> getMixinConfigs(EnvType type) {
		return Collections.emptyList();
	}

	@Override
	public String getAccessWidener() {
		return null;
	}

	@Override
	public boolean loadsInEnvironment(EnvType type) {
		return true;
	}

	@Override
	public Collection<String> getOldInitializers() {
		return Collections.emptyList();
	}

	@Override
	public List<EntrypointMetadata> getEntrypoints(String type) {
		return Collections.emptyList();
	}

	@Override
	public Collection<String> getEntrypointKeys() {
		return Collections.emptyList();
	}

	@Override
	public void emitFormatWarnings() {

	}

	@Override
	public void setVersion(Version version) {
		this.version = version;
	}

	@Override
	public void setDependencies(Collection<ModDependency> dependencies) {

	}
}
