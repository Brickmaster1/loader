package com.basedloader.wrapper;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import net.fabricmc.loader.api.Version;

import org.apache.maven.artifact.versioning.VersionRange;

public class FabricFMLModContainer extends ModContainer {

	public FabricFMLModContainer(net.fabricmc.loader.api.ModContainer fabricMod) {
		super(new IModInfoWrapper(fabricMod));
		contextExtension = () -> null;
	}

	@Override
	public boolean matches(Object mod) {
		return false; // It (probably) isn't a fabric mod if forge is asking about it
	}

	@Override
	public Object getMod() {
		return null; // No? but probably possible
	}

	public static class IModInfoWrapper implements IModInfo, IConfigurable, IModFileInfo {
		private final net.fabricmc.loader.api.ModContainer fabricMod;
		private final ArtifactVersion version;

		public IModInfoWrapper(net.fabricmc.loader.api.ModContainer fabricMod) {
			this.fabricMod = fabricMod;
			this.version = convertVersion(this.fabricMod.getMetadata().getVersion());
		}

		private ArtifactVersion convertVersion(Version version) {
			return new DefaultArtifactVersion(version.getFriendlyString());
		}

		@Override
		public IModFileInfo getOwningFile() {
			return this;
		}

		@Override
		public String getModId() {
			return this.fabricMod.getMetadata().getId();
		}

		@Override
		public String getDisplayName() {
			return this.fabricMod.getMetadata().getName();
		}

		@Override
		public String getDescription() {
			return this.fabricMod.getMetadata().getDescription();
		}

		@Override
		public ArtifactVersion getVersion() {
			return this.version;
		}

		@Override
		public List<? extends ModVersion> getDependencies() {
			return null;
		}

		@Override
		public String getNamespace() {
			return getModId();
		}

		@Override
		public Map<String, Object> getModProperties() {
			return new HashMap<>();
		}

		@Override
		public Optional<URL> getUpdateURL() {
			return Optional.empty();
		}

		@Override
		public Optional<String> getLogoFile() {
			return Optional.empty();//this.fabricMod.getMetadata().getIconPath(1);
		}

		@Override
		public boolean getLogoBlur() {
			return false;
		}

		@Override
		public List<IModInfo> getMods() {
			return List.of(); // TODO:
		}

		@Override
		public List<LanguageSpec> requiredLanguageLoaders() {
			return List.of(new LanguageSpec("minecraft", VersionRange.createFromVersion("1.18.2"))); // TODO: pull mc version
		}

		@Override
		public boolean showAsResourcePack() {
			return true; // TODO:
		}

		@Override
		public Map<String, Object> getFileProperties() {
			return Map.of();
		}

		@Override
		public String getLicense() {
			return String.join("\n", this.fabricMod.getMetadata().getLicense());
		}

		@Override
		public String moduleName() {
			return ""; // Might be safe to return null here, not sure.
		}

		@Override
		public String versionString() {
			return this.fabricMod.getMetadata().getVersion().getFriendlyString();
		}

		@Override
		public List<String> usesServices() {
			return List.of();
		}

		@Override
		public IModFile getFile() {
			return new FabricFMLModFile(this.fabricMod, this);
		}

		@Override
		public IConfigurable getConfig() {
			return this;
		}

		@Override
		public <T> Optional<T> getConfigElement(String... key) {
			return Optional.empty();
		}

		@Override
		public List<? extends IConfigurable> getConfigList(String... key) {
			return List.of();
		}
	}
}
